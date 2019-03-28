package org.consumerreports.pagespeed.models;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

public class Email {
    @Id
    public ObjectId _id;

    public String name;
    public String email;
    public Boolean active;

    // Constructors
    public Email() {
    }

    public Email(String name, String email, Boolean active) {
        this._id = _id;
        this.name = name;
        this.email = email;
        this.active = active;
    }

    // ObjectId needs to be converted to string
    public String get_id() {
        return _id.toHexString();
    }

    public void set_id(ObjectId _id) {
        this._id = _id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}