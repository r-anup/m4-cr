package org.consumerreports;

import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;


public class PageSpeed {
    private static final Logger LOG = LogManager.getLogger(PageSpeed.class);

    private static final HttpClient httpClient;
//    private static final String PAGE_SPEED_API = "https://www.googleapis.com/pagespeedonline/v5/runPagespeed?url=%s&strategy=%s&key=%s";
    private static final String PAGE_SPEED_API = "https://www.googleapis.com/pagespeedonline/v5/runPagespeed?category=performance&prettyPrint=true&url=%s&strategy=%s&key=%s";

    private static final String KEY = "AIzaSyAQp8vshwJq1nwhsryxOfK__GshqnpXvUA";

    static {
        httpClient = HttpClientBuilder.create().build();
    }



    private String getFileContent(String fileName) {
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



    public JSONObject processRequest(String url) {
        String api = String.format(PAGE_SPEED_API, url, "mobile", KEY);
        HttpGet request = new HttpGet(api);


        try {
            return new JSONObject(new PageSpeed().getFileContent("/data.json"));
        } catch (JSONException e) {
            LOG.error("JSONException " + e.getMessage());
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
        }*/


        return null;


    }
}
