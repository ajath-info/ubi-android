package com.ubi.android.models;

import java.io.Serializable;

public class ProductInterest implements Serializable {
    private String image;
    private String is_fav;
    private String price;
    public String vendor_id;
    private String name;
    public String is_payment, category_id, sub_category_id;

    public String getIs_fav() {
        return is_fav;
    }

    public void setIs_fav(String is_fav) {
        this.is_fav = is_fav;
    }

    private String location;

    private VendorDetails vendor_details;

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
