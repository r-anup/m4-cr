package org.consumerreports.pagespeed.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.data.annotation.Id;

public class Diagnostics {

    public Integer numRequests;
    public Integer numScripts;
    public Integer numStylesheets;
    public Integer numFonts;
    public Integer numTasks;
    public Integer numTasksOver10ms;
    public Integer numTasksOver25ms;
    public Integer numTasksOver50ms;
    public Integer numTasksOver100ms;
    public Integer numTasksOver500ms;
    public String rtt;
    public String throughput;
    public String maxRtt;
    public String maxServerLatency;
    public String totalByteWeight;
    public String totalTaskTime;
    public String mainDocumentTransferSize;

    public Diagnostics() {
    }


    public Diagnostics(Integer numRequests, Integer numScripts, Integer numStylesheets, Integer numFonts, Integer numTasks, Integer numTasksOver10ms, Integer numTasksOver25ms, Integer numTasksOver50ms, Integer numTasksOver100ms, Integer numTasksOver500ms, String rtt, String throughput, String maxRtt, String maxServerLatency, String totalByteWeight, String totalTaskTime, String mainDocumentTransferSize) {
        this.numRequests = numRequests;
        this.numScripts = numScripts;
        this.numStylesheets = numStylesheets;
        this.numFonts = numFonts;
        this.numTasks = numTasks;
        this.numTasksOver10ms = numTasksOver10ms;
        this.numTasksOver25ms = numTasksOver25ms;
        this.numTasksOver50ms = numTasksOver50ms;
        this.numTasksOver100ms = numTasksOver100ms;
        this.numTasksOver500ms = numTasksOver500ms;
        this.rtt = rtt;
        this.throughput = throughput;
        this.maxRtt = maxRtt;
        this.maxServerLatency = maxServerLatency;
        this.totalByteWeight = totalByteWeight;
        this.totalTaskTime = totalTaskTime;
        this.mainDocumentTransferSize = mainDocumentTransferSize;
    }

    public Integer getNumRequests() {
        return numRequests;
    }

    public void setNumRequests(Integer numRequests) {
        this.numRequests = numRequests;
    }

    public Integer getNumScripts() {
        return numScripts;
    }

    public void setNumScripts(Integer numScripts) {
        this.numScripts = numScripts;
    }

    public Integer getNumStylesheets() {
        return numStylesheets;
    }

    public void setNumStylesheets(Integer numStylesheets) {
        this.numStylesheets = numStylesheets;
    }

    public Integer getNumFonts() {
        return numFonts;
    }

    public void setNumFonts(Integer numFonts) {
        this.numFonts = numFonts;
    }

    public Integer getNumTasks() {
        return numTasks;
    }

    public void setNumTasks(Integer numTasks) {
        this.numTasks = numTasks;
    }

    public Integer getNumTasksOver10ms() {
        return numTasksOver10ms;
    }

    public void setNumTasksOver10ms(Integer numTasksOver10ms) {
        this.numTasksOver10ms = numTasksOver10ms;
    }

    public Integer getNumTasksOver25ms() {
        return numTasksOver25ms;
    }

    public void setNumTasksOver25ms(Integer numTasksOver25ms) {
        this.numTasksOver25ms = numTasksOver25ms;
    }

    public Integer getNumTasksOver50ms() {
        return numTasksOver50ms;
    }

    public void setNumTasksOver50ms(Integer numTasksOver50ms) {
        this.numTasksOver50ms = numTasksOver50ms;
    }

    public Integer getNumTasksOver100ms() {
        return numTasksOver100ms;
    }

    public void setNumTasksOver100ms(Integer numTasksOver100ms) {
        this.numTasksOver100ms = numTasksOver100ms;
    }

    public Integer getNumTasksOver500ms() {
        return numTasksOver500ms;
    }

    public void setNumTasksOver500ms(Integer numTasksOver500ms) {
        this.numTasksOver500ms = numTasksOver500ms;
    }

    public String getRtt() {
        return rtt;
    }

    public void setRtt(String rtt) {
        this.rtt = rtt;
    }

    public String getThroughput() {
        return throughput;
    }

    public void setThroughput(String throughput) {
        this.throughput = throughput;
    }

    public String getMaxRtt() {
        return maxRtt;
    }

    public void setMaxRtt(String maxRtt) {
        this.maxRtt = maxRtt;
    }

    public String getMaxServerLatency() {
        return maxServerLatency;
    }

    public void setMaxServerLatency(String maxServerLatency) {
        this.maxServerLatency = maxServerLatency;
    }

    public String getTotalByteWeight() {
        return totalByteWeight;
    }

    public void setTotalByteWeight(String totalByteWeight) {
        this.totalByteWeight = totalByteWeight;
    }

    public String getTotalTaskTime() {
        return totalTaskTime;
    }

    public void setTotalTaskTime(String totalTaskTime) {
        this.totalTaskTime = totalTaskTime;
    }

    public String getMainDocumentTransferSize() {
        return mainDocumentTransferSize;
    }

    public void setMainDocumentTransferSize(String mainDocumentTransferSize) {
        this.mainDocumentTransferSize = mainDocumentTransferSize;
    }
}