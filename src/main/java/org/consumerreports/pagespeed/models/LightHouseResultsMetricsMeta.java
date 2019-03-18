package org.consumerreports.pagespeed.models;

public class LightHouseResultsMetricsMeta {

    public String displayValue;
    public String score;
    public String title;
    public String description;


    public LightHouseResultsMetricsMeta() {
    }


    public LightHouseResultsMetricsMeta(String displayValue, String score, String title, String description) {
        this.displayValue = displayValue;
        this.score = score;
        this.title = title;
        this.description = description;
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
}