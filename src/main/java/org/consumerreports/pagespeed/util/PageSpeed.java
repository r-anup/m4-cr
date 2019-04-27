package org.consumerreports.pagespeed.util;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.consumerreports.pagespeed.Main;
import org.consumerreports.pagespeed.config.ConfigProperties;
import org.consumerreports.pagespeed.models.*;
import org.consumerreports.pagespeed.repositories.MetricsRepository;
import org.consumerreports.pagespeed.repositories.UrlsRepository;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.data.domain.PageRequest;

import javax.xml.bind.DatatypeConverter;
import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class PageSpeed {
    private static final Logger LOG = LogManager.getLogger(PageSpeed.class);

    private static final HttpClient httpClient;

    private ConfigProperties configProperties;

    private String PAGE_SPEED_LOCAL_API;

    private String PAGE_SPEED_GOOGLE_API;

    private String PAGE_SPEED_GOOGLE_KEY;

    public ConfigProperties getConfigProperties() {
        return configProperties;
    }

    public void setConfigProperties(ConfigProperties configProperties) {
        this.configProperties = configProperties;
        this.PAGE_SPEED_LOCAL_API = configProperties.getLocalApi();
        this.PAGE_SPEED_GOOGLE_API = configProperties.getGoogleApi();
        this.PAGE_SPEED_GOOGLE_KEY = configProperties.getGoogleKey();
    }

    public PageSpeed() {

    }

    public PageSpeed(ConfigProperties configProperties) {
        setConfigProperties(configProperties);
    }

    private static final ObjectMapper objectMapper =
            new ObjectMapper()
                    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    static {
        httpClient = HttpClientBuilder.create().build();
    }


    private static String getFileContent(String fileName) {
        InputStream inputStream = null;
        try {
            inputStream = TypeReference.class.getResourceAsStream(fileName);
            String data = PageSpeed.readFromInputStream(inputStream);

            return data;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return null;
    }


    private static String readFromInputStream(InputStream inputStream)
            throws IOException {
        StringBuilder resultStringBuilder = new StringBuilder();
        try (BufferedReader br
                     = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = br.readLine()) != null) {
                resultStringBuilder.append(line).append("\n");
            }
        }
        return resultStringBuilder.toString();
    }


    public JSONObject processRequest(String url, Main.Strategy strategy, Main.FetchSource fetchSource, MetricsRepository metricsRepository, UrlsRepository urlsRepository) {
        if (strategy == null) {
            strategy = Main.Strategy.mobile;
        }

        if (fetchSource == null) {
            fetchSource = Main.FetchSource.repository;
        }

        String api = String.format(PAGE_SPEED_LOCAL_API, url, strategy, "");

        if (fetchSource != null && fetchSource.equals(Main.FetchSource.googleNoSave)) {
            api = String.format(PAGE_SPEED_GOOGLE_API, url, strategy, PAGE_SPEED_GOOGLE_KEY);
        }

        HttpGet request = new HttpGet(api);
        JSONObject formattedData = new JSONObject();

        JSONObject data;
        try {

            JSONObject metricsProperties = PageSpeed.getMetricsProperties();

            HttpResponse response = httpClient.execute(request);
            String responseString = EntityUtils.toString(response.getEntity() == null ?
                    new StringEntity(StringUtils.EMPTY) : response.getEntity());
            if (!responseString.isEmpty()) {
                data = new JSONObject(responseString);
            } else {
                return null;
            }
            //JSONObject data = new JSONObject(PageSpeed.getFileContent("/public/data.json"));
            if (data.has("loadingExperience")) {
                formattedData.put("FIRST_CONTENTFUL_PAINT_MS", data.getJSONObject("loadingExperience").getJSONObject("metrics").getJSONObject("FIRST_CONTENTFUL_PAINT_MS"));
                formattedData.put("FIRST_INPUT_DELAY_MS", data.getJSONObject("loadingExperience").getJSONObject("metrics").getJSONObject("FIRST_INPUT_DELAY_MS"));
                formattedData.put("showLoadingExperience", true);
            }

            if (data.has("lighthouseResult")) {
                data = data.getJSONObject("lighthouseResult");
            }

            JSONObject lighthouseData = processLighthouseData(data, "lighthouse");
            String score = data.getJSONObject("categories").getJSONObject("performance").getString("score");

            lighthouseData.put("score", score);
            List<Metrics> metrics = metricsRepository.findByUrlEqualsAndDeviceTypeEqualsOrderByFetchTimeDesc(url, strategy.name(), PageRequest.of(0, 6));
            List<Integer> emaScores = CommonUtil.getEMAScores(CommonUtil.getScores(metrics, score, url), 7);
            lighthouseData.put("ema-score", emaScores.get(emaScores.size()-1));

            JSONObject lighthouseMiscData = processLighthouseData(data, "lighthouseMisc");


            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
            objectMapper.disable(DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES);
            LighthouseResult lighthouseResult = objectMapper.readValue(lighthouseData.toString(), LighthouseResult.class);
            LighthouseMisc lighthouseMisc = objectMapper.readValue(lighthouseMiscData.toString(), LighthouseMisc.class);

            formattedData.put("fetchTime", data.getString("fetchTime"));
            formattedData.put("screenshots", data.getJSONObject("audits").getJSONObject("screenshot-thumbnails").getJSONObject("details").getJSONArray("items"));
            formattedData.put("finalScreenshot", data.getJSONObject("audits").getJSONObject("final-screenshot").getJSONObject("details"));

            List<Screenshot> list = new ArrayList<Screenshot>();
            JSONArray jsonArr = formattedData.getJSONArray("screenshots");
            for (int i = 0; i < jsonArr.length(); i++) {
                JSONObject jsonObj = jsonArr.getJSONObject(i);
                Screenshot screenshot = objectMapper.readValue(jsonObj.toString(), Screenshot.class);
                list.add(screenshot);
            }

            Screenshot finalScreenshot = objectMapper.readValue(formattedData.getJSONObject("finalScreenshot").toString(), Screenshot.class);

            Diagnostics diagnostics = null;
            JSONObject diagnosticsData = new JSONObject();
            if (data.getJSONObject("audits").has("diagnostics")) {
                JSONObject diagnosticsDataElements = (data.getJSONObject("audits").getJSONObject("diagnostics").getJSONObject("details").getJSONArray("items")).getJSONObject(0);
                Iterator<String> keys = diagnosticsDataElements.keys();

                JSONObject diagnosticProperties = metricsProperties.getJSONObject("diagnostics");
                while (keys.hasNext()) {
                    String key = keys.next();
                    JSONObject diagnosticsMetricsMeta = new JSONObject();
                    diagnosticsMetricsMeta.put("value", diagnosticsDataElements.get(key));
                    diagnosticsMetricsMeta.put("title", diagnosticProperties.getJSONObject(key).getString("title"));
                    diagnosticsMetricsMeta.put("description", diagnosticProperties.getJSONObject(key).getString("description"));
                    diagnosticsData.put(key, diagnosticsMetricsMeta);
                }
                diagnostics = (new ObjectMapper()).readValue(diagnosticsData.toString(), Diagnostics.class);
            }

            if (fetchSource.equals(Main.FetchSource.lightHouseAndSave)) {
                Metrics m = new Metrics(
                        url,
                        strategy.name(),
                        (DatatypeConverter.parseDateTime(formattedData.getString("fetchTime")).getTime()),
                        true,
                        lighthouseResult,
                        lighthouseMisc,
                        diagnostics,
                        list,
                        finalScreenshot);
                metricsRepository.save(m);

                CroUrl croUrl = urlsRepository.findFirstByUrl(url);
                if (croUrl != null) {
                    if (strategy.name().equals("mobile")) {
                        croUrl.setMobilePreviousScore(croUrl.getMobileLatestScore());
                        croUrl.setMobileLatestScore(lighthouseData.getString("score"));

                        croUrl.setMobilePreviousScoreDate(croUrl.getMobileLatestScoreDate());
                        croUrl.setMobileLatestScoreDate(DatatypeConverter.parseDateTime(formattedData.getString("fetchTime")).getTime());

                        croUrl.setMobilePreviousEMAScore(croUrl.getMobileLatestEMAScore());
                        croUrl.setMobileLatestEMAScore(lighthouseData.getString("ema-score"));
                    } else {
                        croUrl.setDesktopPreviousScore(croUrl.getDesktopLatestScore());
                        croUrl.setDesktopLatestScore(lighthouseData.getString("score"));

                        croUrl.setDesktopPreviousScoreDate(croUrl.getDesktopLatestScoreDate());
                        croUrl.setDesktopLatestScoreDate(DatatypeConverter.parseDateTime(formattedData.getString("fetchTime")).getTime());

                        croUrl.setDesktopPreviousEMAScore(croUrl.getDesktopLatestEMAScore());
                        croUrl.setDesktopLatestEMAScore(lighthouseData.getString("ema-score"));
                    }
                    urlsRepository.save(croUrl);
                }
            }
            formattedData.put("deviceType", strategy.name());
            formattedData.put("lighthouseResult", lighthouseData);
            formattedData.put("lighthouseMisc", lighthouseMiscData);
            if (diagnostics != null) formattedData.put("diagnostics", diagnosticsData);
            formattedData.put("url", url);
            formattedData.put("isDataFormatted", true);

            return formattedData;
        } catch (JSONException e) {
            LOG.error("JSONException " + e.getMessage());
        } catch (JsonParseException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    private JSONObject processLighthouseData(JSONObject data, String type) throws JSONException {
        JSONObject metricsProperties = PageSpeed.getMetricsProperties();
        JSONObject lighthouseData = new JSONObject();
        JSONObject lighthouseProperties = metricsProperties.getJSONObject(type);

        Iterator<String> lighthouseKeys = lighthouseProperties.keys();
        while (lighthouseKeys.hasNext()) {
            String lighthouseKey = lighthouseKeys.next();
            JSONObject jo = new JSONObject();
            jo.put("displayValue", data.getJSONObject("audits").getJSONObject(lighthouseKey).getString("displayValue"));
            jo.put("score", data.getJSONObject("audits").getJSONObject(lighthouseKey).getString("score"));
            jo.put("title", lighthouseProperties.getJSONObject(lighthouseKey).getString("title"));
            jo.put("description", lighthouseProperties.getJSONObject(lighthouseKey).getString("description"));
            if (data.getJSONObject("audits").getJSONObject(lighthouseKey).has("details")) {
                jo.put("details", data.getJSONObject("audits").getJSONObject(lighthouseKey).getJSONObject("details"));
            }
            lighthouseData.put(lighthouseKey, jo);
        }

        return lighthouseData;
    }


    private static JSONObject getMetricsProperties() throws JSONException {
        return new JSONObject(PageSpeed.getFileContent("/metrics.json"));
    }
}