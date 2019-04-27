package org.consumerreports.pagespeed.models;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

import java.util.Date;

public class CompetitorUrl {
    @Id
    public ObjectId _id;
    public String url;
    public String title;
    public String brand;
    public String mobileLatestScore;
    public String mobilePreviousScore;
    public String desktopLatestScore;
    public String desktopPreviousScore;
    public Date mobileLatestScoreDate;
    public Date mobilePreviousScoreDate;
    public Date desktopLatestScoreDate;
    public Date desktopPreviousScoreDate;

    // Constructors
    public CompetitorUrl() {
    }

    public CompetitorUrl(String url, String title, String brand, String mobileLatestScore, String mobilePreviousScore, String desktopLatestScore, String desktopPreviousScore) {
        this.url = url;
        this.title = title;
        this.brand = brand;
        this.mobileLatestScore = mobileLatestScore;
        this.mobilePreviousScore = mobilePreviousScore;
        this.desktopLatestScore = desktopLatestScore;
        this.desktopPreviousScore = desktopPreviousScore;
    }

    public CompetitorUrl(String url, String title, String brand) {
        this.url = url;
        this.title = title;
        this.brand = brand;
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

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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
}