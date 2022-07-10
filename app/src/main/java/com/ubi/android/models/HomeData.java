package com.ubi.android.models;

import java.io.Serializable;
import java.util.ArrayList;

public class HomeData implements Serializable {
    private ArrayList<AutomobileModel> automobile;

    private ArrayList<DealsModel> deals_of_the_day;

    private ArrayList<ImportantSupplies> important_supplies;

    private ArrayList<ProductInterest> product_interest;

    private ArrayList<CategoriesModel> categories;

    private ArrayList<TopBanners> top_banners;

    private ArrayList<ShoppingModel> shopping;

    private ArrayList<HotelStay> hotel_stay;

    public ArrayList<AutomobileModel> getAutomobile() {
        return automobile;
    }

    public void setAutomobile(ArrayList<AutomobileModel> automobile) {
        this.automobile = automobile;
    }

    public ArrayList<DealsModel> getDeals() {
        return deals_of_the_day;
    }

    public void setDeals(ArrayList<DealsModel> deals_of_the_day) {
        this.deals_of_the_day = deals_of_the_day;
    }

    public ArrayList<ImportantSupplies> getImportant_supplies() {
        return important_supplies;
    }

    public void setImportant_supplies(ArrayList<ImportantSupplies> important_supplies) {
        this.important_supplies = important_supplies;
    }

    public ArrayList<ProductInterest> getProduct_interest() {
        return product_interest;
    }

    public void setProduct_interest(ArrayList<ProductInterest> product_interest) {
        this.product_interest = product_interest;
    }

    public ArrayList<CategoriesModel> getCategories() {
        return categories;
    }

    public void setCategories(ArrayList<CategoriesModel> categories) {
        this.categories = categories;
    }

    public ArrayList<TopBanners> getTop_banners() {
        return top_banners;
    }

    public void setTop_banners(ArrayList<TopBanners> top_banners) {
        this.top_banners = top_banners;
    }

    public ArrayList<ShoppingModel> getShopping() {
        return shopping;
    }

    public void setShopping(ArrayList<ShoppingModel> shopping) {
        this.shopping = shopping;
    }

    public ArrayList<HotelStay> getHotelStay() {
        return hotel_stay;
    }

    public void setHotelStay(ArrayList<HotelStay> hotelstay) {
        this.hotel_stay = hotel_stay;
    }
}
