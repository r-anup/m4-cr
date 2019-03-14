package org.consumerreports.pagespeed.models;

public class LightHouseResultsMetricsMeta {

    public String displayValue;

    public String score;


    public LightHouseResultsMetricsMeta() {
    }


    public LightHouseResultsMetricsMeta(String displayValue, String score) {
        this.displayValue = displayValue;
        this.score = score;
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
}