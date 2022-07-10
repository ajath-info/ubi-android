package com.ubi.android.models;

import java.io.Serializable;

public class SubCategoryDetailModel implements Serializable {
    public String category_id, sub_category_id, id, name, image, price, location, distance, totla_rating, avg_rating, is_fav, is_payment;
    public VendorDetails vendor_details;
}
