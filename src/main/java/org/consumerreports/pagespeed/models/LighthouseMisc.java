package org.consumerreports.pagespeed.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LighthouseMisc {

    @JsonProperty(value = "critical-request-chains")
    public LightHouseResultsMetricsMeta criticalRequestChains;

    @JsonProperty(value = "mainthread-work-breakdown")
    public LightHouseResultsMetricsMeta mainthreadWorkBreakdown;

    @JsonProperty(value = "render-blocking-resources")
    public LightHouseResultsMetricsMeta renderBlockingResources;

    @JsonProperty(value = "dom-size")
    public LightHouseResultsMetricsMeta domSize;

    @JsonProperty(value = "bootup-time")
    public LightHouseResultsMetricsMeta bootupTime;

    public LighthouseMisc() {
    }

    public LighthouseMisc(LightHouseResultsMetricsMeta criticalRequestChains, LightHouseResultsMetricsMeta mainthreadWorkBreakdown, LightHouseResultsMetricsMeta renderBlockingResources, LightHouseResultsMetricsMeta domSize, LightHouseResultsMetricsMeta bootupTime) {
        this.criticalRequestChains = criticalRequestChains;
        this.mainthreadWorkBreakdown = mainthreadWorkBreakdown;
        this.renderBlockingResources = renderBlockingResources;
        this.domSize = domSize;
        this.bootupTime = bootupTime;
    }

    public LightHouseResultsMetricsMeta getCriticalRequestChains() {
        return criticalRequestChains;
    }

    public void setCriticalRequestChains(LightHouseResultsMetricsMeta criticalRequestChains) {
        this.criticalRequestChains = criticalRequestChains;
    }

    public LightHouseResultsMetricsMeta getMainthreadWorkBreakdown() {
        return mainthreadWorkBreakdown;
    }

    public void setMainthreadWorkBreakdown(LightHouseResultsMetricsMeta mainthreadWorkBreakdown) {
        this.mainthreadWorkBreakdown = mainthreadWorkBreakdown;
    }

    public LightHouseResultsMetricsMeta getRenderBlockingResources() {
        return renderBlockingResources;
    }

    public void setRenderBlockingResources(LightHouseResultsMetricsMeta renderBlockingResources) {
        this.renderBlockingResources = renderBlockingResources;
    }

    public LightHouseResultsMetricsMeta getDomSize() {
        return domSize;
    }

    public void setDomSize(LightHouseResultsMetricsMeta domSize) {
        this.domSize = domSize;
    }

    public LightHouseResultsMetricsMeta getBootupTime() {
        return bootupTime;
    }

    public void setBootupTime(LightHouseResultsMetricsMeta bootupTime) {
        this.bootupTime = bootupTime;
    }
}