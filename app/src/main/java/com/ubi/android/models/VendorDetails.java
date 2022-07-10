package com.ubi.android.models;

import java.io.Serializable;

public class VendorDetails implements Serializable {
    private String phone;
    public String first_name, last_name, vendor_id;
    private String email;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}
