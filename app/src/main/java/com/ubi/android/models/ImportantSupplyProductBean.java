package com.ubi.android.models;

import java.io.Serializable;

public class ImportantSupplyProductBean implements Serializable {
    private String image;
    public String vendor_id;
    private String category_name;
    public String is_payment;
    private String distance;

    private String is_fav;

    private String totla_rating;

    private String category_id;

    private String sub_category_id;

    private String price;

    private String avg_rating;

    private String sub_category_name;

    private String name;

    private String location;

    private VendorDetails vendor_details;

    private String id;

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCategory_name() {
        return category_name;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getIs_fav() {
        return is_fav;
    }

    public void setIs_fav(String is_fav) {
        this.is_fav = is_fav;
    }

    public String getTotla_rating() {
        return totla_rating;
    }

    public void setTotla_rating(String totla_rating) {
        this.totla_rating = totla_rating;
    }

    public String getCategory_id() {
        return category_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }

    public String getSub_category_id() {
        return sub_category_id;
    }

    public void setSub_category_id(String sub_category_id) {
        this.sub_category_id = sub_category_id;
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

    public String getSub_category_name() {
        return sub_category_name;
    }

    public void setSub_category_name(String sub_category_name) {
        this.sub_category_name = sub_category_name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public VendorDetails getVendor_details() {
        return vendor_details;
    }

    public void setVendor_details(VendorDetails vendor_details) {
        this.vendor_details = vendor_details;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
