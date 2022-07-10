package com.ubi.android.models;

import java.io.Serializable;

public class SuggestionModel implements Serializable {
    private String product_id;
    private String is_fav;
    public String getIs_fav() {
        return is_fav;
    }

    public void setIs_fav(String is_fav) {
        this.is_fav = is_fav;
    }
    private String avg_rating;

    private String name;

    private String icon;

    private String totla_rating;

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
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

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getTotla_rating() {
        return totla_rating;
    }

    public void setTotla_rating(String totla_rating) {
        this.totla_rating = totla_rating;
    }
}
