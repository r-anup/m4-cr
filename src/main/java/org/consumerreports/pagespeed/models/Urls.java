package org.consumerreports.pagespeed.models;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

public class Urls {
    @Id
    public ObjectId _id;

    public String url;
    public String title;

    // Constructors
    public Urls() {}

    public Urls(ObjectId _id, String url, String title) {
        this._id = _id;
        this.url = url;
        this.title = title;
    }

    // ObjectId needs to be converted to string
    public String get_id() { return _id.toHexString(); }
    public void set_id(ObjectId _id) { this._id = _id; }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}