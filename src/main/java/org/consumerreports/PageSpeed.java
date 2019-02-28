package org.consumerreports;

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

import java.io.IOException;
import java.util.*;


public class PageSpeed {
    private static final Logger LOG = LogManager.getLogger(PageSpeedService.class);

    private static final HttpClient httpClient;
    private static final String PAGE_SPEED_API = System.getenv("PAGE_SPEED_API");
    private static final String KEY = System.getenv("PAGE_SPEED_API_KEY");

    static {
        httpClient = HttpClientBuilder.create().build();
    }


    public String processRequest(String url) {
        return "";
    }
}
