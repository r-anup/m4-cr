package org.consumerreports.pagespeed.models;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import java.util.Date;
import java.util.List;

public class Metrics {
    @Id
    public ObjectId _id;
    public String url;
    public String deviceType = "mobile";
    public Date fetchTime;
    public Boolean isDataFormatted;
    public LighthouseResult lighthouseResult;
    public Diagnostics diagnostics;
    public List<Screenshot> screenshots;
    public Screenshot finalScreenshot;


    public Metrics() {
    }

    public Metrics(String url) {
        this.url = url;
    }


    public Metrics(String url, String deviceType, Date fetchTime, Boolean isDataFormatted, LighthouseResult lighthouseResult, Diagnostics diagnostics, List<Screenshot> screenshots, Screenshot finalScreenshot) {
        this.url = url;
        this.deviceType = deviceType;
        this.fetchTime = fetchTime;
        this.isDataFormatted = isDataFormatted;
        this.lighthouseResult = lighthouseResult;
        this.diagnostics = diagnostics;
        this.screenshots = screenshots;
        this.finalScreenshot = finalScreenshot;
    }



    public String get_id() {
        return _id.toHexString();
    }

    public void set_id(ObjectId _id) {
        this._id = _id;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public Boolean getDataFormatted() {
        return isDataFormatted;
    }

    public void setDataFormatted(Boolean dataFormatted) {
        isDataFormatted = dataFormatted;
    }

    public void setFetchTime(Date fetchTime) {
        this.fetchTime = fetchTime;
    }

    public Date getFetchTime() {
        return fetchTime;
    }


    public List<Screenshot> getScreenshots() {
        return screenshots;
    }

    public void setScreenshots(List<Screenshot> screenshots) {
        this.screenshots = screenshots;
    }

    public Screenshot getFinalScreenshot() {
        return finalScreenshot;
    }

    public void setFinalScreenshot(Screenshot finalScreenshot) {
        this.finalScreenshot = finalScreenshot;
    }

    public LighthouseResult getLighthouseResult() {
        return lighthouseResult;
    }

    public void setLighthouseResult(LighthouseResult lighthouseResult) {
        this.lighthouseResult = lighthouseResult;
    }

    public Diagnostics getDiagnostics() {
        return diagnostics;
    }

    public void setDiagnostics(Diagnostics diagnostics) {
        this.diagnostics = diagnostics;
    }
}