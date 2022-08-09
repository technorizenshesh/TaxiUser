package com.taxiuser.utils.retrofitutils;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface Api {

    @FormUrlEncoded
    @POST("change_password")
    Call<ResponseBody> changePass(@FieldMap Map<String, String> params);

    @POST("get_faq")
    Call<ResponseBody> getAllFAQInformation();

    @FormUrlEncoded
    @POST("insert_chat")
    Call<ResponseBody> insertChatApiCall(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("get_chat")
    Call<ResponseBody> getConversationApiCAll(@FieldMap Map<String, String> params);

    @POST("get_donation")
    Call<ResponseBody> gteDonationApiCall();

    @FormUrlEncoded
    @POST("get_schedule_booking")
    Call<ResponseBody> getScheduleBooking(@FieldMap Map<String, String> params);

    @POST("get_admin_notification")
    Call<ResponseBody> getNotificationAdmin();

    @FormUrlEncoded
    @POST("delete_account")
    Call<ResponseBody> deleteAccountApiCall(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("update_user_address")
    Call<ResponseBody> updateAddressApiCall(@FieldMap Map<String, String> params);

    @Multipart
    @POST("update_profile")
    Call<ResponseBody> updateDriverCallApi(@Part("user_id") RequestBody user_id,
                                           @Part("first_name") RequestBody first_name,
                                           @Part("last_name") RequestBody last_name,
                                           @Part("mobile") RequestBody mobile,
                                           @Part("email") RequestBody email,
                                           @Part("address") RequestBody address,
                                           @Part("lat") RequestBody lat,
                                           @Part("lon") RequestBody lon,
                                           @Part("workplace") RequestBody workplace,
                                           @Part("work_lon") RequestBody work_lon,
                                           @Part("work_lat") RequestBody work_lat,
                                           @Part("city") RequestBody city,
                                           @Part MultipartBody.Part file1);

    @FormUrlEncoded
    @POST("add_wallet")
    Call<ResponseBody> addWalletAmount(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("wallet_transfer")
    Call<ResponseBody> walletTransferApiCall(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("get_my_transaction")
    Call<ResponseBody> getTransactionApiCall(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("check_valid_login")
    Call<ResponseBody> checkLoginValidCall(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("check_credential")
    Call<ResponseBody> checkCredentialsApi(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("get_schedule_booking_count")
    Call<ResponseBody> getScheduleBookingCount(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("get_booking_details")
    Call<ResponseBody> getCurrentBookingDetails(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("cancel_ride")
    Call<ResponseBody> cancelRideApi(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("get_user_history")
    Call<ResponseBody> getHistoryApiCall(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("offer_pool_to_booking_request")
    Call<ResponseBody> sendPoolRequestToDriver(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("case_payment")
    Call<ResponseBody> paymentApiCall(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("strips_payment")
    Call<ResponseBody> stripePaymentApiCAll(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("emergency_number")
    Call<ResponseBody> addContactApiCall(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("get_emergency")
    Call<ResponseBody> getAllEmerContacts(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("add_rating_review")
    Call<ResponseBody> addReviewsAndRating(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("update_lat_lon")
    Call<Map<String, String>> updateLocation(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("forgot_password")
    Call<ResponseBody> forgotPass(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("add_card_data")
    Call<ResponseBody> addCardApiCall(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("favorite_driver")
    Call<ResponseBody> favouriteDriverApiCall(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("get_favorite_all_driver")
    Call<ResponseBody> getAllDrivers(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("delete_emergency")
    Call<ResponseBody> deleteEmergencyContactApi(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("add_contact_us")
    Call<ResponseBody> contactUsApiCall(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("get_user_address")
    Call<ResponseBody> getRecentLocationApi(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("update_user_address")
    Call<ResponseBody> updateUserAddressApi(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("add_user_address")
    Call<ResponseBody> addRecentLocationApi(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("get_booking_history")
    Call<ResponseBody> getTaxiHistory(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("update_online_status")
    Call<ResponseBody> updateOnOffApi(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("get_profile")
    Call<ResponseBody> getProfileCall(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("update_currency_parm")
    Call<ResponseBody> updateCurrencyApiCall(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("login")
    Call<ResponseBody> loginApiCall(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("update_business_email")
    Call<ResponseBody> businessEmailUpdateApi(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("available_car_driver")
    Call<ResponseBody> getAvailableDrivers(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("get_available_driver")
    Call<ResponseBody> getAvailableCarDriversHome(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("get_car_type_list")
    Call<ResponseBody> getCarTypeListApi(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("get_dev_order")
    Call<ResponseBody> getDevOrdersApiCall(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("social_login")
    Call<ResponseBody> socialLogin(@FieldMap Map<String,String> params);

    @FormUrlEncoded
    @POST("add_bank_account")
    Call<ResponseBody> addBankAccount(@FieldMap Map<String,String> params);

    @FormUrlEncoded
    @POST("driver_accept_and_Cancel_request")
    Call<ResponseBody> acceptCancelOrderCall(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("get_neareast_offer_pool_request")
    Call<ResponseBody> getAvailablePoolDriver(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("cancel_ride")
    Call<ResponseBody> cancelRequestForUser(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("get_current_booking")
    Call<ResponseBody> getCurrentTaxiBooking(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("booking_request")
    Call<ResponseBody> bookingRequestApi(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("driver_accept_and_Cancel_request")
    Call<ResponseBody> acceptCancelOrderCallTaxi(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("place_order")
    Call<ResponseBody> placeDevOrderApiCall(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("signup")
    Call<ResponseBody> signUpApiCall(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("available_car_driver")
    Call<ResponseBody> getAvailableCarCall(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("add_to_cart")
    Call<ResponseBody> updateOrderStatusApiCall(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("get_count_cart")
    Call<ResponseBody> getCartCountApiCall(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("get_current_booking")
    Call<ResponseBody> getCurrentBooking(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("get_lat_lon")
    Call<ResponseBody> getLatLonDriver(@FieldMap Map<String, String> params);

    @Multipart
    @POST("add_document")
    Call<ResponseBody> addDriverDocumentApiCall(@Part("user_id") RequestBody user_id,
                                                @Part MultipartBody.Part file1,
                                                @Part MultipartBody.Part file2,
                                                @Part MultipartBody.Part file3);

    @Multipart
    @POST("signup")
    Call<ResponseBody> signUpDriverCallApi(@Part("first_name") RequestBody first_name,
                                           @Part("last_name") RequestBody last_name,
                                           @Part("email") RequestBody email,
                                           @Part("mobile") RequestBody mobile,
                                           @Part("country") RequestBody country,
                                           @Part("state") RequestBody state,
                                           @Part("city") RequestBody city,                                           @Part("address") RequestBody address,
                                           @Part("register_id") RequestBody register_id,
                                           @Part("lat") RequestBody lat,
                                           @Part("lon") RequestBody lon,
                                           @Part("password") RequestBody password,
                                           @Part("type") RequestBody type,
                                           @Part("step") RequestBody step,
                                           @Part("user_name") RequestBody username,
                                           @Part("workplace") RequestBody workplace,
                                           @Part("work_lat") RequestBody work_lat,
                                           @Part("work_lon") RequestBody work_lon,
                                           @Part MultipartBody.Part file1);

    @Multipart
    @POST("add_vehicle")
    Call<ResponseBody> addDriverVehicle(@Part("user_id") RequestBody user_id,
                                     @Part("car_type_id") RequestBody car_type,
                                     @Part("brand") RequestBody car_brand,
                                     @Part("car_model") RequestBody car_model,
                                     @Part("car_number") RequestBody carNumber,
                                     @Part("year_of_manufacture") RequestBody year_of_manufacture,
                                     @Part MultipartBody.Part file1);



    @GET("country_list")
    Call<ResponseBody> getAllCountry();

    @FormUrlEncoded
    @POST("state_list")
    Call<ResponseBody> getAllState(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("city_list")
    Call<ResponseBody> getAllCity(@FieldMap Map<String, String> params);


    @FormUrlEncoded
    @POST("car_list")
    Call<ResponseBody> getAllServices(@FieldMap Map<String, String> params);





}


