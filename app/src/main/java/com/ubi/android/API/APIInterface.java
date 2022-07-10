package com.ubi.android.API;

import com.ubi.android.models.AboutResponse;
import com.ubi.android.models.AutoMobileResponse;
import com.ubi.android.models.BaseResponse;
import com.ubi.android.models.CategoriesResponse;
import com.ubi.android.models.CategoryPageResponse;
import com.ubi.android.models.ContactUsResponse;
import com.ubi.android.models.DealsResponse;
import com.ubi.android.models.HomeResponse;
import com.ubi.android.models.HotelStayResponse;
import com.ubi.android.models.ImportantSupplyProductResponse;
import com.ubi.android.models.LocationsResponse;
import com.ubi.android.models.LookingforallResponse;
import com.ubi.android.models.MessageRepliesModel;
import com.ubi.android.models.MessageRepliesResponse;
import com.ubi.android.models.MyAdsResponse;
import com.ubi.android.models.MyFavouriteResponse;
import com.ubi.android.models.MyLeadResponse;
import com.ubi.android.models.NotificationReponse;
import com.ubi.android.models.PackageResponse;
import com.ubi.android.models.PopularDestinationResponse;
import com.ubi.android.models.PostAdResponse;
import com.ubi.android.models.PostRequirementtypeResponse;
import com.ubi.android.models.PreviewAdReponse;
import com.ubi.android.models.ProductDetailResponse;
import com.ubi.android.models.ProductMessageResponse;
import com.ubi.android.models.SearchResponse;
import com.ubi.android.models.ShoppingResponse;
import com.ubi.android.models.StateResponse;
import com.ubi.android.models.SubCategoryResponse;
import com.ubi.android.models.SuggestionResponse;
import com.ubi.android.models.SuppliesResponse;
import com.ubi.android.models.UserResponse;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface APIInterface {
    @POST("userLogin")
    @FormUrlEncoded
    Call<UserResponse> loginUser(@FieldMap Map<String, String> params);

    @POST("userRegister")
    @FormUrlEncoded
    Call<UserResponse> registerUser(@FieldMap Map<String, String> params);

    @POST("verifyOtpLogin")
    @FormUrlEncoded
    Call<UserResponse> verifyOtpLogin(@FieldMap Map<String, String> params);

    @POST("reSendOtp")
    @FormUrlEncoded
    Call<UserResponse> reSendOtp(@FieldMap Map<String, String> params);

    @POST("forgotPasswordEmail")
    @FormUrlEncoded
    Call<UserResponse> forgotPasswordEmail(@FieldMap Map<String, String> params);

    @POST("forgotPasswordOtpVerified")
    @FormUrlEncoded
    Call<UserResponse> forgotPasswordOtpVerified(@FieldMap Map<String, String> params);

    @POST("resetPasswordEmail")
    @FormUrlEncoded
    Call<UserResponse> resetPasswordEmail(@FieldMap Map<String, String> params);

    @POST("user/getHomePage")
    @FormUrlEncoded
    Call<HomeResponse> getHomePage(@Header("auth-token") String authtoken, @FieldMap Map<String, String> params);

    @GET("aboutInfo")
    Call<AboutResponse> aboutInfo();

    @GET("contact")
    Call<ContactUsResponse> contact();

    @POST("user/categoriesAll")
    @FormUrlEncoded
    Call<CategoriesResponse> categoriesAll(@Header("auth-token") String authtoken, @FieldMap Map<String, String> params);

    @POST("user/lookingForAll")
    @FormUrlEncoded
    Call<LookingforallResponse> lookingForAll(@Header("auth-token") String authtoken, @FieldMap Map<String, String> params);

    @POST("user/suppliesAll")
    @FormUrlEncoded
    Call<SuppliesResponse> suppliesAll(@Header("auth-token") String authtoken, @FieldMap Map<String, String> params);

    @POST("user/dealsDayAll")
    @FormUrlEncoded
    Call<DealsResponse> dealsDayAll(@Header("auth-token") String authtoken, @FieldMap Map<String, String> params);

    @POST("user/shoppingAll")
    @FormUrlEncoded
    Call<ShoppingResponse> shoppingAll(@Header("auth-token") String authtoken, @FieldMap Map<String, String> params);

    @POST("user/hotelStayAll")
    @FormUrlEncoded
    Call<HotelStayResponse> hotelStayAll(@Header("auth-token") String authtoken, @FieldMap Map<String, String> params);

    @POST("user/automobileAll")
    @FormUrlEncoded
    Call<AutoMobileResponse> automobileAll(@Header("auth-token") String authtoken, @FieldMap Map<String, String> params);

    @POST("user/search")
    @FormUrlEncoded
    Call<SearchResponse> search(@Header("auth-token") String authtoken, @FieldMap Map<String, String> params);

    @POST("user/getCategoryPage")
    @FormUrlEncoded
    Call<CategoryPageResponse> getCategoryPage(@Header("auth-token") String authtoken, @FieldMap Map<String, String> params);

    @POST("user/popularDestinationAll")
    @FormUrlEncoded
    Call<PopularDestinationResponse> popularDestinationAll(@Header("auth-token") String authtoken, @FieldMap Map<String, String> params);

    @POST("user/categorSupplyAll")
    @FormUrlEncoded
    Call<SuppliesResponse> categorSupplyAll(@Header("auth-token") String authtoken, @FieldMap Map<String, String> params);

    @POST("user/suggestionAll")
    @FormUrlEncoded
    Call<SuggestionResponse> suggestionAll(@Header("auth-token") String authtoken, @FieldMap Map<String, String> params);

    @POST("user/productDetails")
    @FormUrlEncoded
    Call<ProductDetailResponse> productDetails(@Header("auth-token") String authtoken, @FieldMap Map<String, String> params);

    @POST("user/productListAll")
    @FormUrlEncoded
    Call<SubCategoryResponse> productListAll(@Header("auth-token") String authtoken, @FieldMap Map<String, String> params);

    @POST("user/productShorting")
    @FormUrlEncoded
    Call<SubCategoryResponse> productShorting(@Header("auth-token") String authtoken, @FieldMap Map<String, String> params);

    @POST("user/productFilters")
    @FormUrlEncoded
    Call<SubCategoryResponse> productFilters(@Header("auth-token") String authtoken, @FieldMap Map<String, String> params);

    @POST("countryList")
    @FormUrlEncoded
    Call<LocationsResponse> countryList(@Header("auth-token") String authtoken, @FieldMap Map<String, String> params);

    @POST("stateList")
    @FormUrlEncoded
    Call<StateResponse> stateList(@Header("auth-token") String authtoken, @FieldMap Map<String, String> params);

    @POST("user/myAds")
    @FormUrlEncoded
    Call<MyAdsResponse> myAds(@Header("auth-token") String authtoken, @FieldMap Map<String, String> params);

    @GET("category")
    Call<CategoriesResponse> category();

    @POST("subCategory")
    @FormUrlEncoded
    Call<CategoriesResponse> subCategory(@Header("auth-token") String authtoken, @FieldMap Map<String, String> params);

    @POST("user/postAds")
    @FormUrlEncoded
    Call<PostAdResponse> postAds(@Header("auth-token") String authtoken, @FieldMap Map<String, String> params);

    @POST("user/getNotificationLog")
    @FormUrlEncoded
    Call<NotificationReponse> getNotificationLog(@Header("auth-token") String authtoken, @FieldMap Map<String, String> params);

    @POST("user/myFavorite")
    @FormUrlEncoded
    Call<MyFavouriteResponse> myFavorite(@Header("auth-token") String authtoken, @FieldMap Map<String, String> params);

    @POST("user/favorite")
    @FormUrlEncoded
    Call<BaseResponse> favorite(@Header("auth-token") String authtoken, @FieldMap Map<String, String> params);

    @GET("package")
    Call<PackageResponse> packages();

    @POST("user/previewAds")
    @FormUrlEncoded
    Call<PreviewAdReponse> previewAds(@Header("auth-token") String authtoken, @FieldMap Map<String, String> params);

    @POST("user/importantSupplyProducts")
    @FormUrlEncoded
    Call<ImportantSupplyProductResponse> getImportantSuppliesAll(@Header("auth-token") String authtoken, @FieldMap Map<String, String> params);

    @POST("user/shoppingProducts")
    @FormUrlEncoded
    Call<ImportantSupplyProductResponse> shoppingProducts(@Header("auth-token") String authtoken, @FieldMap Map<String, String> params);

    @POST("user/hotelStayProducts")
    @FormUrlEncoded
    Call<ImportantSupplyProductResponse> hotelStayProducts(@Header("auth-token") String authtoken, @FieldMap Map<String, String> params);

    @POST("user/automobileProducts")
    @FormUrlEncoded
    Call<ImportantSupplyProductResponse> automobileProducts(@Header("auth-token") String authtoken, @FieldMap Map<String, String> params);

    @POST("user/popularDestinationsProducts")
    @FormUrlEncoded
    Call<ImportantSupplyProductResponse> popularDestinationsProducts(@Header("auth-token") String authtoken, @FieldMap Map<String, String> params);

    @POST("user/productComments")
    @FormUrlEncoded
    Call<BaseResponse> productComments(@Header("auth-token") String authtoken, @FieldMap Map<String, String> params);

    @POST("user/getProductComments")
    @FormUrlEncoded
    Call<ProductMessageResponse> getProductComments(@Header("auth-token") String authtoken, @FieldMap Map<String, String> params);

    @POST("user/getProductReply")
    @FormUrlEncoded
    Call<MessageRepliesResponse> getProductReply(@Header("auth-token") String authtoken, @FieldMap Map<String, String> params);

    @POST("user/productReply")
    @FormUrlEncoded
    Call<BaseResponse> productReply(@Header("auth-token") String authtoken, @FieldMap Map<String, String> params);

    @POST("user/myProfile")
    @FormUrlEncoded
    Call<UserResponse> myProfile(@Header("auth-token") String authtoken, @FieldMap Map<String, String> params);

    @POST("user/deleteNotification")
    @FormUrlEncoded
    Call<BaseResponse> deleteNotification(@Header("auth-token") String authtoken, @FieldMap Map<String, String> params);

    @POST("user/deleteNotificationLog")
    @FormUrlEncoded
    Call<BaseResponse> deleteNotificationLog(@Header("auth-token") String authtoken, @FieldMap Map<String, String> params);

    @POST("user/readLog")
    @FormUrlEncoded
    Call<BaseResponse> readLog(@Header("auth-token") String authtoken, @FieldMap Map<String, String> params);

    @POST("user/addPayment")
    @FormUrlEncoded
    Call<BaseResponse> addPayment(@Header("auth-token") String authtoken, @FieldMap Map<String, String> params);

    @POST("user/rateProduct")
    @FormUrlEncoded
    Call<BaseResponse> rateProduct(@Header("auth-token") String authtoken, @FieldMap Map<String, String> params);

    @POST("user/help")
    @FormUrlEncoded
    Call<BaseResponse> help(@Header("auth-token") String authtoken, @FieldMap Map<String, String> params);

    @POST("user/rateVendor")
    @FormUrlEncoded
    Call<BaseResponse> rateVendor(@Header("auth-token") String authtoken, @FieldMap Map<String, String> params);

    @GET("postRequirementType")
    Call<PostRequirementtypeResponse> postRequirementType(@Header("auth-token") String authtoken);

    @POST("user/postRequirement")
    @FormUrlEncoded
    Call<BaseResponse> postRequirement(@Header("auth-token") String authtoken, @FieldMap Map<String, String> params);

    @POST("user/myLead")
    @FormUrlEncoded
    Call<MyLeadResponse> myLead(@Header("auth-token") String authtoken, @FieldMap Map<String, String> params);

    @POST("user/updateProfile")
    @FormUrlEncoded
    Call<BaseResponse> updateProfile(@Header("auth-token") String authtoken,@FieldMap Map<String, String> params);

}
