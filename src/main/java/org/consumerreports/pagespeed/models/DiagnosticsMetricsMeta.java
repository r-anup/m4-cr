package org.consumerreports.pagespeed.models;


public class DiagnosticsMetricsMeta {

    public Object value = 0;
    public String title;
    public String description = "";

    public DiagnosticsMetricsMeta() {
    }

    public DiagnosticsMetricsMeta(Object value, String title, String description) {
        this.value = value;
        this.title = title;
        this.description = description;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
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
