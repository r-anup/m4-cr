package org.consumerreports.pagespeed.models;

public class Diagnostics {

    public DiagnosticsMetricsMeta numRequests;
    public DiagnosticsMetricsMeta numScripts;
    public DiagnosticsMetricsMeta numStylesheets;
    public DiagnosticsMetricsMeta numFonts;
    public DiagnosticsMetricsMeta numTasks;
    public DiagnosticsMetricsMeta numTasksOver10ms;
    public DiagnosticsMetricsMeta numTasksOver25ms;
    public DiagnosticsMetricsMeta numTasksOver50ms;
    public DiagnosticsMetricsMeta numTasksOver100ms;
    public DiagnosticsMetricsMeta numTasksOver500ms;
    public DiagnosticsMetricsMeta rtt;
    public DiagnosticsMetricsMeta throughput;
    public DiagnosticsMetricsMeta maxRtt;
    public DiagnosticsMetricsMeta maxServerLatency;
    public DiagnosticsMetricsMeta totalByteWeight;
    public DiagnosticsMetricsMeta totalTaskTime;
    public DiagnosticsMetricsMeta mainDocumentTransferSize;

    public Diagnostics() {
    }


    public Diagnostics(DiagnosticsMetricsMeta numRequests, DiagnosticsMetricsMeta numScripts, DiagnosticsMetricsMeta numStylesheets, DiagnosticsMetricsMeta numFonts, DiagnosticsMetricsMeta numTasks, DiagnosticsMetricsMeta numTasksOver10ms, DiagnosticsMetricsMeta numTasksOver25ms, DiagnosticsMetricsMeta numTasksOver50ms, DiagnosticsMetricsMeta numTasksOver100ms, DiagnosticsMetricsMeta numTasksOver500ms, DiagnosticsMetricsMeta rtt, DiagnosticsMetricsMeta throughput, DiagnosticsMetricsMeta maxRtt, DiagnosticsMetricsMeta maxServerLatency, DiagnosticsMetricsMeta totalByteWeight, DiagnosticsMetricsMeta totalTaskTime, DiagnosticsMetricsMeta mainDocumentTransferSize) {
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


    public DiagnosticsMetricsMeta getNumRequests() {
        return numRequests;
    }

    public void setNumRequests(DiagnosticsMetricsMeta numRequests) {
        this.numRequests = numRequests;
    }

    public DiagnosticsMetricsMeta getNumScripts() {
        return numScripts;
    }

    public void setNumScripts(DiagnosticsMetricsMeta numScripts) {
        this.numScripts = numScripts;
    }

    public DiagnosticsMetricsMeta getNumStylesheets() {
        return numStylesheets;
    }

    public void setNumStylesheets(DiagnosticsMetricsMeta numStylesheets) {
        this.numStylesheets = numStylesheets;
    }

    public DiagnosticsMetricsMeta getNumFonts() {
        return numFonts;
    }

    public void setNumFonts(DiagnosticsMetricsMeta numFonts) {
        this.numFonts = numFonts;
    }

    public DiagnosticsMetricsMeta getNumTasks() {
        return numTasks;
    }

    public void setNumTasks(DiagnosticsMetricsMeta numTasks) {
        this.numTasks = numTasks;
    }

    public DiagnosticsMetricsMeta getNumTasksOver10ms() {
        return numTasksOver10ms;
    }

    public void setNumTasksOver10ms(DiagnosticsMetricsMeta numTasksOver10ms) {
        this.numTasksOver10ms = numTasksOver10ms;
    }

    public DiagnosticsMetricsMeta getNumTasksOver25ms() {
        return numTasksOver25ms;
    }

    public void setNumTasksOver25ms(DiagnosticsMetricsMeta numTasksOver25ms) {
        this.numTasksOver25ms = numTasksOver25ms;
    }

    public DiagnosticsMetricsMeta getNumTasksOver50ms() {
        return numTasksOver50ms;
    }

    public void setNumTasksOver50ms(DiagnosticsMetricsMeta numTasksOver50ms) {
        this.numTasksOver50ms = numTasksOver50ms;
    }

    public DiagnosticsMetricsMeta getNumTasksOver100ms() {
        return numTasksOver100ms;
    }

    public void setNumTasksOver100ms(DiagnosticsMetricsMeta numTasksOver100ms) {
        this.numTasksOver100ms = numTasksOver100ms;
    }

    public DiagnosticsMetricsMeta getNumTasksOver500ms() {
        return numTasksOver500ms;
    }

    public void setNumTasksOver500ms(DiagnosticsMetricsMeta numTasksOver500ms) {
        this.numTasksOver500ms = numTasksOver500ms;
    }

    public DiagnosticsMetricsMeta getRtt() {
        return rtt;
    }

    public void setRtt(DiagnosticsMetricsMeta rtt) {
        this.rtt = rtt;
    }

    public DiagnosticsMetricsMeta getThroughput() {
        return throughput;
    }

    public void setThroughput(DiagnosticsMetricsMeta throughput) {
        this.throughput = throughput;
    }

    public DiagnosticsMetricsMeta getMaxRtt() {
        return maxRtt;
    }

    public void setMaxRtt(DiagnosticsMetricsMeta maxRtt) {
        this.maxRtt = maxRtt;
    }

    public DiagnosticsMetricsMeta getMaxServerLatency() {
        return maxServerLatency;
    }

    public void setMaxServerLatency(DiagnosticsMetricsMeta maxServerLatency) {
        this.maxServerLatency = maxServerLatency;
    }

    public DiagnosticsMetricsMeta getTotalByteWeight() {
        return totalByteWeight;
    }

    public void setTotalByteWeight(DiagnosticsMetricsMeta totalByteWeight) {
        this.totalByteWeight = totalByteWeight;
    }

    public DiagnosticsMetricsMeta getTotalTaskTime() {
        return totalTaskTime;
    }

    public void setTotalTaskTime(DiagnosticsMetricsMeta totalTaskTime) {
        this.totalTaskTime = totalTaskTime;
    }

    public DiagnosticsMetricsMeta getMainDocumentTransferSize() {
        return mainDocumentTransferSize;
    }

    public void setMainDocumentTransferSize(DiagnosticsMetricsMeta mainDocumentTransferSize) {
        this.mainDocumentTransferSize = mainDocumentTransferSize;
    }
}