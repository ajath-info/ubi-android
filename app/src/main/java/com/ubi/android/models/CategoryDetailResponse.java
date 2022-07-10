package com.ubi.android.models;

import java.io.Serializable;
import java.util.ArrayList;

public class CategoryDetailResponse implements Serializable {
    public ArrayList<CategoryLookingModel> looking_for;
    public ArrayList<PopularDestinationModel> popular_destinations;
    public ArrayList<ImportantSupplies> category_important_supplies;
    public ArrayList<SuggestionModel> suggestions;
    public String category_name;
}
