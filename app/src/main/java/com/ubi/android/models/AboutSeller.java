package com.ubi.android.models;

import java.io.Serializable;

public class AboutSeller implements Serializable {
    public String profile_img, phone;
    private String totla_seller_rating;

    private String firstname;

    private String avg_seller_rating;

    private String last_seen;

    private String company_name;

    private String company_video;

    private String location;

    private String id;

    private String lastname;

    public String getTotla_seller_rating() {
        return totla_seller_rating;
    }

    public void setTotla_seller_rating(String totla_seller_rating) {
        this.totla_seller_rating = totla_seller_rating;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getAvg_seller_rating() {
        return avg_seller_rating;
    }

    public void setAvg_seller_rating(String avg_seller_rating) {
        this.avg_seller_rating = avg_seller_rating;
    }

    public String getLast_seen() {
        return last_seen;
    }

    public void setLast_seen(String last_seen) {
        this.last_seen = last_seen;
    }

    public String getCompany_name() {
        return company_name;
    }

    public void setCompany_name(String company_name) {
        this.company_name = company_name;
    }

    public String getCompany_video() {
        return company_video;
    }

    public void setCompany_video(String company_video) {
        this.company_video = company_video;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }
}
