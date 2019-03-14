package org.consumerreports.pagespeed.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.bson.types.ObjectId;
import org.json.JSONObject;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.annotation.AliasFor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Reference;

import java.util.List;

public class LighthouseResult {

    public String score;

    @JsonProperty(value = "speed-index")
    public LightHouseResultsMetricsMeta speedIndex;

    public LightHouseResultsMetricsMeta interactive;

    @JsonProperty(value = "first-meaningful-paint")
    public LightHouseResultsMetricsMeta firstMeaningfulPaint;

    @JsonProperty(value = "first-contentful-paint")
    public LightHouseResultsMetricsMeta firstContentfulPaint;

    @JsonProperty(value = "first-cpu-idle")
    public LightHouseResultsMetricsMeta firstCpuIdle;

    @JsonProperty(value = "estimated-input-latency")
    public LightHouseResultsMetricsMeta estimatedInputLatency;


    public LighthouseResult() {
    }

    public LighthouseResult(String score, LightHouseResultsMetricsMeta speedIndex, LightHouseResultsMetricsMeta interactive, LightHouseResultsMetricsMeta firstMeaningfulPaint, LightHouseResultsMetricsMeta firstContentfulPaint, LightHouseResultsMetricsMeta firstCpuIdle, LightHouseResultsMetricsMeta estimatedInputLatency) {
        this.score = score;
        this.speedIndex = speedIndex;
        this.interactive = interactive;
        this.firstMeaningfulPaint = firstMeaningfulPaint;
        this.firstContentfulPaint = firstContentfulPaint;
        this.firstCpuIdle = firstCpuIdle;
        this.estimatedInputLatency = estimatedInputLatency;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public LightHouseResultsMetricsMeta getSpeedIndex() {
        return speedIndex;
    }

    public void setSpeedIndex(LightHouseResultsMetricsMeta speedIndex) {
        this.speedIndex = speedIndex;
    }

    public LightHouseResultsMetricsMeta getInteractive() {
        return interactive;
    }

    public void setInteractive(LightHouseResultsMetricsMeta interactive) {
        this.interactive = interactive;
    }

    public LightHouseResultsMetricsMeta getFirstMeaningfulPaint() {
        return firstMeaningfulPaint;
    }

    public void setFirstMeaningfulPaint(LightHouseResultsMetricsMeta firstMeaningfulPaint) {
        this.firstMeaningfulPaint = firstMeaningfulPaint;
    }

    public LightHouseResultsMetricsMeta getFirstContentfulPaint() {
        return firstContentfulPaint;
    }

    public void setFirstContentfulPaint(LightHouseResultsMetricsMeta firstContentfulPaint) {
        this.firstContentfulPaint = firstContentfulPaint;
    }

    public LightHouseResultsMetricsMeta getFirstCpuIdle() {
        return firstCpuIdle;
    }

    public void setFirstCpuIdle(LightHouseResultsMetricsMeta firstCpuIdle) {
        this.firstCpuIdle = firstCpuIdle;
    }

    public LightHouseResultsMetricsMeta getEstimatedInputLatency() {
        return estimatedInputLatency;
    }

    public void setEstimatedInputLatency(LightHouseResultsMetricsMeta estimatedInputLatency) {
        this.estimatedInputLatency = estimatedInputLatency;
    }
}