package org.consumerreports.pagespeed.models;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

public class Urls {
    @Id
    public ObjectId _id;

    public String url;
    public ObjectId associatedCompetitorId;
    public String title;
    public Integer sortOrder;
    public String mobileLatestScore;
    public String mobilePreviousScore;
    public String desktopLatestScore;
    public String desktopPreviousScore;

    // Constructors
    public Urls() {
    }

    public Urls(String url, String title, String mobileLatestScore, String mobilePreviousScore, String desktopLatestScore, String desktopPreviousScore) {
        this.url = url;
        this.title = title;
        this.mobileLatestScore = mobileLatestScore;
        this.mobilePreviousScore = mobilePreviousScore;
        this.desktopLatestScore = desktopLatestScore;
        this.desktopPreviousScore = desktopPreviousScore;
    }

    public Urls(String url, String title) {
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


    public ObjectId getAssociatedCompetitorId() {
        return associatedCompetitorId;
    }

    public void setAssociatedCompetitorId(ObjectId associatedCompetitorId) {
        this.associatedCompetitorId = associatedCompetitorId;
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
}