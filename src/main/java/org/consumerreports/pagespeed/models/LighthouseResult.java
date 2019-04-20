package org.consumerreports.pagespeed.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.consumerreports.pagespeed.util.CommonUtil;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.*;

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

    public Map<String, String> getResult() {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("Score", CommonUtil.toPercentage(this.score));
        map.put(this.speedIndex.title, CommonUtil.toMiliSeconds(this.speedIndex.displayValue));
        map.put(this.interactive.title, CommonUtil.toMiliSeconds(this.interactive.displayValue));
        map.put(this.firstMeaningfulPaint.title, CommonUtil.toMiliSeconds(this.firstMeaningfulPaint.displayValue));
        map.put(this.firstContentfulPaint.title, CommonUtil.toMiliSeconds(this.firstContentfulPaint.displayValue));
        map.put(this.firstCpuIdle.title, CommonUtil.toMiliSeconds(this.firstCpuIdle.displayValue));
        map.put(this.estimatedInputLatency.title, CommonUtil.toMiliSeconds(this.estimatedInputLatency.displayValue));
        return map;
    }
}