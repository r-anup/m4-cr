package org.consumerreports.pagespeed.models;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.util.Date;

public class CroUrl {
    @Id
    public ObjectId _id;

    public String url;

    @DBRef
    public CompetitorUrl competitorUrl;
    public String competitorUrlValue;
    public String title;
    public Integer sortOrder;
    public String mobileLatestScore;
    public String mobilePreviousScore;
    public String desktopLatestScore;
    public String desktopPreviousScore;
    public String mobileLatestEMAScore;
    public String mobilePreviousEMAScore;
    public String desktopLatestEMAScore;
    public String desktopPreviousEMAScore;
    public Date mobileLatestScoreDate;
    public Date mobilePreviousScoreDate;
    public Date desktopLatestScoreDate;
    public Date desktopPreviousScoreDate;

    // Constructors
    public CroUrl() {
    }

    public CroUrl(String url, String title, String mobileLatestScore, String mobilePreviousScore, String desktopLatestScore, String desktopPreviousScore) {
        this.url = url;
        this.title = title;
        this.mobileLatestScore = mobileLatestScore;
        this.mobilePreviousScore = mobilePreviousScore;
        this.desktopLatestScore = desktopLatestScore;
        this.desktopPreviousScore = desktopPreviousScore;
    }

    public CroUrl(String url, String title) {
        this.url = url;
        this.title = title;
        this.sortOrder = sortOrder;
    }

    // ObjectId needs to be converted to string
    public String get_id() {
        return _id.toHexString();
    }

    public void set_id(ObjectId _id) {
        this._id = _id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public CompetitorUrl getCompetitorUrl() {
        return competitorUrl;
    }

    public void setCompetitorUrl(CompetitorUrl competitorUrl) {
        this.competitorUrl = competitorUrl;
    }

    public String getCompetitorUrlValue() {
        return competitorUrlValue;
    }

    public void setCompetitorUrlValue(String competitorUrlValue) {
        this.competitorUrlValue = competitorUrlValue;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    public String getMobileLatestScore() {
        return mobileLatestScore;
    }

    public void setMobileLatestScore(String mobileLatestScore) {
        this.mobileLatestScore = mobileLatestScore;
    }

    public String getMobilePreviousScore() {
        return mobilePreviousScore;
    }

    public void setMobilePreviousScore(String mobilePreviousScore) {
        this.mobilePreviousScore = mobilePreviousScore;
    }

    public String getDesktopLatestScore() {
        return desktopLatestScore;
    }

    public void setDesktopLatestScore(String desktopLatestScore) {
        this.desktopLatestScore = desktopLatestScore;
    }

    public String getDesktopPreviousScore() {
        return desktopPreviousScore;
    }

    public void setDesktopPreviousScore(String desktopPreviousScore) {
        this.desktopPreviousScore = desktopPreviousScore;
    }


    public Date getMobileLatestScoreDate() {
        return mobileLatestScoreDate;
    }

    public void setMobileLatestScoreDate(Date mobileLatestScoreDate) {
        this.mobileLatestScoreDate = mobileLatestScoreDate;
    }

    public Date getMobilePreviousScoreDate() {
        return mobilePreviousScoreDate;
    }

    public void setMobilePreviousScoreDate(Date mobilePreviousScoreDate) {
        this.mobilePreviousScoreDate = mobilePreviousScoreDate;
    }

    public Date getDesktopLatestScoreDate() {
        return desktopLatestScoreDate;
    }

    public void setDesktopLatestScoreDate(Date desktopLatestScoreDate) {
        this.desktopLatestScoreDate = desktopLatestScoreDate;
    }

    public Date getDesktopPreviousScoreDate() {
        return desktopPreviousScoreDate;
    }

    public void setDesktopPreviousScoreDate(Date desktopPreviousScoreDate) {
        this.desktopPreviousScoreDate = desktopPreviousScoreDate;
    }

    public String getMobileLatestEMAScore() {
        return mobileLatestEMAScore;
    }

    public void setMobileLatestEMAScore(String mobileLatestEMAScore) {
        this.mobileLatestEMAScore = mobileLatestEMAScore;
    }

    public String getMobilePreviousEMAScore() {
        return mobilePreviousEMAScore;
    }

    public void setMobilePreviousEMAScore(String mobilePreviousEMAScore) {
        this.mobilePreviousEMAScore = mobilePreviousEMAScore;
    }

    public String getDesktopLatestEMAScore() {
        return desktopLatestEMAScore;
    }

    public void setDesktopLatestEMAScore(String desktopLatestEMAScore) {
        this.desktopLatestEMAScore = desktopLatestEMAScore;
    }

    public String getDesktopPreviousEMAScore() {
        return desktopPreviousEMAScore;
    }

    public void setDesktopPreviousEMAScore(String desktopPreviousEMAScore) {
        this.desktopPreviousEMAScore = desktopPreviousEMAScore;
    }
}