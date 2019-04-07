package org.consumerreports.pagespeed.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.json.JSONObject;

@JsonIgnoreProperties(ignoreUnknown=true)
public class LightHouseResultsMetricsMeta {

    public String displayValue;
    public String score;
    public String title;
    public String description;

    @JsonIgnoreProperties(ignoreUnknown=true)
    public BasicDBObject details;

    public LightHouseResultsMetricsMeta() {
    }


    public LightHouseResultsMetricsMeta(String displayValue, String score, String title, String description, BasicDBObject details) {
        this.displayValue = displayValue;
        this.score = score;
        this.title = title;
        this.description = description;
        this.details = details;
    }

    public String getDisplayValue() {
        return displayValue;
    }

    public void setDisplayValue(String displayValue) {
        this.displayValue = displayValue;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BasicDBObject getDetails() {
        return details;
    }

    public void setDetails(BasicDBObject details) {
        this.details = details;
    }
}