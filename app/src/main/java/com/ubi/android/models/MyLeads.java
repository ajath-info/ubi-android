package com.ubi.android.models;

import java.io.Serializable;

public class MyLeads implements Serializable {

    private String category_name;

    private String category_id;

    private String sub_category_id;

    private String product_image;

    private MyLeadUserDetails user_details;

    private String product_id;

    private String sub_category_name;

    private String product_location;

    private String lead_type;

    private String product_price;

    private String lead_quantity;

    private String product_name;

    public String getCategory_name() {
        return category_name;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
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

    public String getProduct_image() {
        return product_image;
    }

    public void setProduct_image(String product_image) {
        this.product_image = product_image;
    }

    public MyLeadUserDetails getUser_details() {
        return user_details;
    }

    public void setUser_details(MyLeadUserDetails user_details) {
        this.user_details = user_details;
    }

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public String getSub_category_name() {
        return sub_category_name;
    }

    public void setSub_category_name(String sub_category_name) {
        this.sub_category_name = sub_category_name;
    }

    public String getProduct_location() {
        return product_location;
    }

    public void setProduct_location(String product_location) {
        this.product_location = product_location;
    }

    public String getLead_type() {
        return lead_type;
    }

    public void setLead_type(String lead_type) {
        this.lead_type = lead_type;
    }

    public String getProduct_price() {
        return product_price;
    }

    public void setProduct_price(String product_price) {
        this.product_price = product_price;
    }

    public String getLead_quantity() {
        return lead_quantity;
    }

    public void setLead_quantity(String lead_quantity) {
        this.lead_quantity = lead_quantity;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }
}
