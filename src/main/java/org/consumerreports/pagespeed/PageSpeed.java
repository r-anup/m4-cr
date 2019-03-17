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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
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


    public JSONObject processRequest(String url, String strategy, String fetchSource, MetricsRepository metricsRepository) {
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

            String[] lighthouseDataMetrics = {"first-contentful-paint", "first-meaningful-paint", "interactive", "first-cpu-idle", "estimated-input-latency", "speed-index"};
            for (String lighthouseDataMetric:lighthouseDataMetrics) {
                JSONObject jo = new JSONObject();
                jo.put("displayValue", data.getJSONObject("audits").getJSONObject(lighthouseDataMetric).getString("displayValue"));
                jo.put("score", data.getJSONObject("audits").getJSONObject(lighthouseDataMetric).getString("score"));
                lighthouseData.put(lighthouseDataMetric, jo);
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

                 JSONObject metricsProperties = PageSpeed.getMetricsProperties();
                 while (keys.hasNext()) {
                     String key = keys.next();
                     JSONObject diagnosticsMetricsMeta = new JSONObject();
                     diagnosticsMetricsMeta.put("value", diagnosticsDataElements.get(key));
                     diagnosticsMetricsMeta.put("title", metricsProperties.getJSONObject(key).getString("title"));
                     diagnosticsMetricsMeta.put("description", metricsProperties.getJSONObject(key).getString("description"));
                     diagnosticsData.put(key, diagnosticsMetricsMeta);
                 }
                 diagnostics = (new ObjectMapper()).readValue(diagnosticsData.toString(), Diagnostics.class);
             }

            if (fetchSource.equals("lightHouseAndSave")) {
                Metrics m = new Metrics(
                        url,
                        strategy,
                        (DatatypeConverter.parseDateTime(formattedData.getString("fetchTime")).getTime()),
                        true,
                        lighthouseResult,
                        diagnostics,
                        list);
                metricsRepository.save(m);
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
