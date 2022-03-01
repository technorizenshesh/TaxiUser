package com.taxiuser.models;

import java.io.Serializable;
import java.util.ArrayList;

public class ModelFavourite implements Serializable {

    private ArrayList<Result> result;
    private String message;
    private String status;

    public void setResult(ArrayList<Result> result){
        this.result = result;
    }
    public ArrayList<Result> getResult(){
        return this.result;
    }
    public void setMessage(String message){
        this.message = message;
    }
    public String getMessage(){
        return this.message;
    }
    public void setStatus(String status){
        this.status = status;
    }
    public String getStatus(){
        return this.status;
    }

    public class Result implements Serializable
    {
        private String id;

        private String first_name;

        private String last_name;

        private String user_name;

        private String car_type_id;

        private String email;

        private String type;

        private String mobile;

        private String password;

        private String city;

        private String image;

        private String step;

        private String register_id;

        private String date_time;

        private String social_id;

        private String lat;

        private String lon;

        private String address;

        private String zipcode;

        private String country;

        private String state;

        private String ios_register_id;

        private String stripe_account_login_link;

        private String wallet;

        private String verify_code;

        private String phone_code;

        private String cust_id;

        private String lang;

        private String stripe_account_id;

        private String promo_code;

        private String amount;

        private String car_document;

        private String insurance;

        private String document;

        private String status;

        private String online_status;

        private String driver_lisence;

        private String pan_card_imag;

        private String workplace;

        private String work_lat;

        private String work_lon;

        private String basic_car;

        private String normal_car;

        private String luxurious_car;

        private String pool_car;

        private String favorite_driver_status;

        private String user_review_rating;

        public String getUser_review_rating() {
            return user_review_rating;
        }

        public void setUser_review_rating(String user_review_rating) {
            this.user_review_rating = user_review_rating;
        }

        public void setId(String id){
            this.id = id;
        }
        public String getId(){
            return this.id;
        }
        public void setFirst_name(String first_name){
            this.first_name = first_name;
        }
        public String getFirst_name(){
            return this.first_name;
        }
        public void setLast_name(String last_name){
            this.last_name = last_name;
        }
        public String getLast_name(){
            return this.last_name;
        }
        public void setUser_name(String user_name){
            this.user_name = user_name;
        }
        public String getUser_name(){
            return this.user_name;
        }
        public void setCar_type_id(String car_type_id){
            this.car_type_id = car_type_id;
        }
        public String getCar_type_id(){
            return this.car_type_id;
        }
        public void setEmail(String email){
            this.email = email;
        }
        public String getEmail(){
            return this.email;
        }
        public void setType(String type){
            this.type = type;
        }
        public String getType(){
            return this.type;
        }
        public void setMobile(String mobile){
            this.mobile = mobile;
        }
        public String getMobile(){
            return this.mobile;
        }
        public void setPassword(String password){
            this.password = password;
        }
        public String getPassword(){
            return this.password;
        }
        public void setCity(String city){
            this.city = city;
        }
        public String getCity(){
            return this.city;
        }
        public void setImage(String image){
            this.image = image;
        }
        public String getImage(){
            return this.image;
        }
        public void setStep(String step){
            this.step = step;
        }
        public String getStep(){
            return this.step;
        }
        public void setRegister_id(String register_id){
            this.register_id = register_id;
        }
        public String getRegister_id(){
            return this.register_id;
        }
        public void setDate_time(String date_time){
            this.date_time = date_time;
        }
        public String getDate_time(){
            return this.date_time;
        }
        public void setSocial_id(String social_id){
            this.social_id = social_id;
        }
        public String getSocial_id(){
            return this.social_id;
        }
        public void setLat(String lat){
            this.lat = lat;
        }
        public String getLat(){
            return this.lat;
        }
        public void setLon(String lon){
            this.lon = lon;
        }
        public String getLon(){
            return this.lon;
        }
        public void setAddress(String address){
            this.address = address;
        }
        public String getAddress(){
            return this.address;
        }
        public void setZipcode(String zipcode){
            this.zipcode = zipcode;
        }
        public String getZipcode(){
            return this.zipcode;
        }
        public void setCountry(String country){
            this.country = country;
        }
        public String getCountry(){
            return this.country;
        }
        public void setState(String state){
            this.state = state;
        }
        public String getState(){
            return this.state;
        }
        public void setIos_register_id(String ios_register_id){
            this.ios_register_id = ios_register_id;
        }
        public String getIos_register_id(){
            return this.ios_register_id;
        }
        public void setStripe_account_login_link(String stripe_account_login_link){
            this.stripe_account_login_link = stripe_account_login_link;
        }
        public String getStripe_account_login_link(){
            return this.stripe_account_login_link;
        }
        public void setWallet(String wallet){
            this.wallet = wallet;
        }
        public String getWallet(){
            return this.wallet;
        }
        public void setVerify_code(String verify_code){
            this.verify_code = verify_code;
        }
        public String getVerify_code(){
            return this.verify_code;
        }
        public void setPhone_code(String phone_code){
            this.phone_code = phone_code;
        }
        public String getPhone_code(){
            return this.phone_code;
        }
        public void setCust_id(String cust_id){
            this.cust_id = cust_id;
        }
        public String getCust_id(){
            return this.cust_id;
        }
        public void setLang(String lang){
            this.lang = lang;
        }
        public String getLang(){
            return this.lang;
        }
        public void setStripe_account_id(String stripe_account_id){
            this.stripe_account_id = stripe_account_id;
        }
        public String getStripe_account_id(){
            return this.stripe_account_id;
        }
        public void setPromo_code(String promo_code){
            this.promo_code = promo_code;
        }
        public String getPromo_code(){
            return this.promo_code;
        }
        public void setAmount(String amount){
            this.amount = amount;
        }
        public String getAmount(){
            return this.amount;
        }
        public void setCar_document(String car_document){
            this.car_document = car_document;
        }
        public String getCar_document(){
            return this.car_document;
        }
        public void setInsurance(String insurance){
            this.insurance = insurance;
        }
        public String getInsurance(){
            return this.insurance;
        }
        public void setDocument(String document){
            this.document = document;
        }
        public String getDocument(){
            return this.document;
        }
        public void setStatus(String status){
            this.status = status;
        }
        public String getStatus(){
            return this.status;
        }
        public void setOnline_status(String online_status){
            this.online_status = online_status;
        }
        public String getOnline_status(){
            return this.online_status;
        }
        public void setDriver_lisence(String driver_lisence){
            this.driver_lisence = driver_lisence;
        }
        public String getDriver_lisence(){
            return this.driver_lisence;
        }
        public void setPan_card_imag(String pan_card_imag){
            this.pan_card_imag = pan_card_imag;
        }
        public String getPan_card_imag(){
            return this.pan_card_imag;
        }
        public void setWorkplace(String workplace){
            this.workplace = workplace;
        }
        public String getWorkplace(){
            return this.workplace;
        }
        public void setWork_lat(String work_lat){
            this.work_lat = work_lat;
        }
        public String getWork_lat(){
            return this.work_lat;
        }
        public void setWork_lon(String work_lon){
            this.work_lon = work_lon;
        }
        public String getWork_lon(){
            return this.work_lon;
        }
        public void setBasic_car(String basic_car){
            this.basic_car = basic_car;
        }
        public String getBasic_car(){
            return this.basic_car;
        }
        public void setNormal_car(String normal_car){
            this.normal_car = normal_car;
        }
        public String getNormal_car(){
            return this.normal_car;
        }
        public void setLuxurious_car(String luxurious_car){
            this.luxurious_car = luxurious_car;
        }
        public String getLuxurious_car(){
            return this.luxurious_car;
        }
        public void setPool_car(String pool_car){
            this.pool_car = pool_car;
        }
        public String getPool_car(){
            return this.pool_car;
        }
        public void setFavorite_driver_status(String favorite_driver_status){
            this.favorite_driver_status = favorite_driver_status;
        }
        public String getFavorite_driver_status(){
            return this.favorite_driver_status;
        }
    }

}
