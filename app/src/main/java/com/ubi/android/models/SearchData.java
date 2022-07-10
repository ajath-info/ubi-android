package com.ubi.android.models;

import java.io.Serializable;

public class SearchData implements Serializable {
    private String image;
    public String is_fav;
    private String price;

    private String avg_rating;

    private String name;

    private String totla_rating;

    private String location;

    private String id;

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getAvg_rating() {
        return avg_rating;
    }

    public void setAvg_rating(String avg_rating) {
        this.avg_rating = avg_rating;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTotla_rating() {
        return totla_rating;
    }

    public void setTotla_rating(String totla_rating) {
        this.totla_rating = totla_rating;
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

}
