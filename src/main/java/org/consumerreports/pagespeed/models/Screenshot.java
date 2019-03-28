package org.consumerreports.pagespeed.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

public class Screenshot {

    public String data;

    @JsonIgnoreProperties
    public String timing;
    public String timestamp;

    // Constructors
    public Screenshot() {}

    public Screenshot(String data, String timing, String timestamp) {
        this.data = data;
        this.timing = timing;
        this.timestamp = timestamp;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getTiming() {
        return timing;
    }

    public void setTiming(String timing) {
        this.timing = timing;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}