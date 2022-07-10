package com.ubi.android.models;

import java.io.Serializable;
import java.util.ArrayList;

public class ProductDetailModel implements Serializable {
    private ArrayList<String> image;
    public String is_fav;
    private String category_name;

    private String totla_rating;

    private String description;

    private String features;

    private String category_id;

    private String price;

    private String vendor_id;

    private String avg_rating;

    private String name;

    private AboutSeller about_the_seller;

    private ArrayList<RecommendedProducts> recommended_products;

    private String location;

    private String id;

    public ArrayList<String> getImage() {
        return image;
    }

    public void setImage(ArrayList<String> image) {
        this.image = image;
    }

    public String getCategory_name() {
        return category_name;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }

    public String getTotla_rating() {
        return totla_rating;
    }

    public void setTotla_rating(String totla_rating) {
        this.totla_rating = totla_rating;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFeatures() {
        return features;
    }

    public void setFeatures(String features) {
        this.features = features;
    }

    public String getCategory_id() {
        return category_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getVendor_id() {
        return vendor_id;
    }

    public void setVendor_id(String vendor_id) {
        this.vendor_id = vendor_id;
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

    public AboutSeller getAbout_the_seller() {
        return about_the_seller;
    }

    public void setAbout_the_seller(AboutSeller about_the_seller) {
        this.about_the_seller = about_the_seller;
    }

    public ArrayList<RecommendedProducts> getRecommended_products() {
        return recommended_products;
    }

    public void setRecommended_products(ArrayList<RecommendedProducts> recommended_products) {
        this.recommended_products = recommended_products;
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

    public String sub_category_id, is_payment;
}
