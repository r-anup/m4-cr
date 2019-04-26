package org.consumerreports.pagespeed.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@ConfigurationProperties(prefix = "pagespeed")
public class ConfigProperties {

    private String localApi;
    private String googleApi;
    private String googleKey;

    public String getLocalApi() {
        return localApi;
    }

    public void setLocalApi(String localApi) {
        this.localApi = localApi;
    }

    public String getGoogleApi() {
        return googleApi;
    }

    public void setGoogleApi(String googleApi) {
        this.googleApi = googleApi;
    }

    public String getGoogleKey() {
        return googleKey;
    }

    public void setGoogleKey(String googleKey) {
        this.googleKey = googleKey;
    }
}