package org.consumerreports.pagespeed;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
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
import org.consumerreports.pagespeed.models.*;
import org.consumerreports.pagespeed.repositories.MetricsRepository;
import org.consumerreports.pagespeed.repositories.UrlsRepository;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import javax.xml.bind.DatatypeConverter;
import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class PageSpeed {
    private static final Logger LOG = LogManager.getLogger(PageSpeed.class);

    private static final HttpClient httpClient;
//  private static final String PAGE_SPEED_API = "https://www.googleapis.com/pagespeedonline/v5/runPagespeed?url=%s&strategy=%s&key=%s";
//  private static final String PAGE_SPEED_API = "https://www.googleapis.com/pagespeedonline/v5/runPagespeed?category=performance&prettyPrint=true&url=%s&strategy=%s&key=%s";
    private static final String PAGE_SPEED_API_LOCAL = "https://m4-lh.herokuapp.com/?category=performance&prettyPrint=true&locale=en_US&url=%s&strategy=%s&key=%s";
    private static final String PAGE_SPEED_API_GOOGLE =  "https://www.googleapis.com/pagespeedonline/v5/runPagespeed?category=performance&prettyPrint=true&url=%s&strategy=%s&key=%s";
    private static final String KEY = "AIzaSyAQp8vshwJq1nwhsryxOfK__GshqnpXvUA";


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


    public JSONObject processRequest(String url, String strategy, String fetchSource, MetricsRepository metricsRepository, UrlsRepository urlsRepository) {
        if (strategy == null) {
            strategy = "mobile";
        }

        if (fetchSource == null) {
            fetchSource = "repository";
        }

        String api = String.format(PAGE_SPEED_API_LOCAL, url, strategy, "");

        if (fetchSource != null && fetchSource.equals("googleNoSave")) {
            api = String.format(PAGE_SPEED_API_GOOGLE, url, strategy, KEY);
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
                formattedData.put("FIRST_CONTENTFUL_PAINT_MS" ,  data.getJSONObject("loadingExperience").getJSONObject("metrics").getJSONObject("FIRST_CONTENTFUL_PAINT_MS"));
                formattedData.put("FIRST_INPUT_DELAY_MS" ,  data.getJSONObject("loadingExperience").getJSONObject("metrics").getJSONObject("FIRST_INPUT_DELAY_MS"));
                formattedData.put("showLoadingExperience", true);
            }

            if (data.has("lighthouseResult")) {
                data = data.getJSONObject("lighthouseResult");
            }

            JSONObject lighthouseData = new JSONObject();
            JSONObject lighthouseProperties = metricsProperties.getJSONObject("lighthouse");

            Iterator<String> lighthouseKeys = lighthouseProperties.keys();
            while (lighthouseKeys.hasNext()) {
                String lighthouseKey = lighthouseKeys.next();
                JSONObject jo = new JSONObject();
                jo.put("displayValue", data.getJSONObject("audits").getJSONObject(lighthouseKey).getString("displayValue"));
                jo.put("score", data.getJSONObject("audits").getJSONObject(lighthouseKey).getString("score"));
                jo.put("title", lighthouseProperties.getJSONObject(lighthouseKey).getString("title"));
                jo.put("description", lighthouseProperties.getJSONObject(lighthouseKey).getString("description"));
                lighthouseData.put(lighthouseKey, jo);
            }

            lighthouseData.put("score", data.getJSONObject("categories").getJSONObject("performance").getString("score"));
            LighthouseResult lighthouseResult = (new ObjectMapper()).readValue(lighthouseData.toString(), LighthouseResult.class);


            formattedData.put("fetchTime",               data.getString("fetchTime"));
            formattedData.put("screenshots",             data.getJSONObject("audits").getJSONObject("screenshot-thumbnails").getJSONObject("details").getJSONArray("items"));
            List<Screenshot> list = new ArrayList<Screenshot>();
            JSONArray jsonArr = formattedData.getJSONArray("screenshots");

            for (int i = 0; i < jsonArr.length(); i++) {
                JSONObject jsonObj = jsonArr.getJSONObject(i);
                ObjectMapper mapper = new ObjectMapper();
                Screenshot screenshot = mapper.readValue(jsonObj.toString(), Screenshot.class);
                list.add(screenshot);
            }


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

            if (fetchSource.equalsIgnoreCase("lightHouseAndSave")) {
                Metrics m = new Metrics(
                        url,
                        strategy,
                        (DatatypeConverter.parseDateTime(formattedData.getString("fetchTime")).getTime()),
                        true,
                        lighthouseResult,
                        diagnostics,
                        list);
                metricsRepository.save(m);

                Urls urls = urlsRepository.findFirstByUrl(url);
                if (strategy.equals("mobile")) {
                    urls.setMobilePreviousScore(urls.getMobileLatestScore());
                    urls.setMobileLatestScore(lighthouseData.getString("score"));
                } else {
                    urls.setDesktopPreviousScore(urls.getDesktopLatestScore());
                    urls.setDesktopLatestScore(lighthouseData.getString("score"));
                }
                urlsRepository.save(urls);
            }
            formattedData.put("lighthouseResult", lighthouseData);
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

        /*
        try {
            HttpResponse response = httpClient.execute(request);
            String responseString = EntityUtils.toString(response.getEntity() == null ?
                    new StringEntity(StringUtils.EMPTY) : response.getEntity());
            if (!responseString.isEmpty()) {
                JSONObject data = new JSONObject(responseString);






                return data;

            }
        } catch (IOException e) {
            LOG.error("IOException " + e.getMessage());
        } catch (JSONException e) {
            LOG.error("JSONException " + e.getMessage());
        }

        */


        return null;


    }


    private static JSONObject getMetricsProperties() throws JSONException {
        return new JSONObject(PageSpeed.getFileContent("/metrics.json"));
    }
}
