package com.taxiuser.models;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ModelServicess {

    @SerializedName("result")
    @Expose
    private List<Result> result = null;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("status")
    @Expose
    private String status;

    public List<Result> getResult() {
        return result;
    }

    public void setResult(List<Result> result) {
        this.result = result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    public class Result {

        @SerializedName("id")
        @Expose
        private String id;
        @SerializedName("car_name")
        @Expose
        private String carName;
        @SerializedName("car_image")
        @Expose
        private String carImage;
        @SerializedName("per_km")
        @Expose
        private String perKm;
        @SerializedName("charge")
        @Expose
        private String charge;
        @SerializedName("ride_time_charge_permin")
        @Expose
        private String rideTimeChargePermin;
        @SerializedName("waiting_charge")
        @Expose
        private String waitingCharge;
        @SerializedName("cancellation_charge")
        @Expose
        private String cancellationCharge;
        @SerializedName("no_of_seats")
        @Expose
        private String noOfSeats;
        @SerializedName("country")
        @Expose
        private String country;
        @SerializedName("state")
        @Expose
        private String state;
        @SerializedName("city")
        @Expose
        private String city;
        @SerializedName("currency_money")
        @Expose
        private String currencyMoney;
        @SerializedName("price_for_km")
        @Expose
        private String priceForKm;
        @SerializedName("price_per_min")
        @Expose
        private String pricePerMin;
        @SerializedName("nxn_fee")
        @Expose
        private String nxnFee;
        @SerializedName("admin_fee")
        @Expose
        private String adminFee;
        @SerializedName("driver_fee")
        @Expose
        private String driverFee;
        @SerializedName("tax")
        @Expose
        private String tax;
        @SerializedName("other_charge")
        @Expose
        private String otherCharge;
        @SerializedName("status")
        @Expose
        private String status;
        @SerializedName("service_tax")
        @Expose
        private String serviceTax;
        @SerializedName("location")
        @Expose
        private String location;
        @SerializedName("lat")
        @Expose
        private String lat;
        @SerializedName("lon")
        @Expose
        private String lon;

        private boolean isSelected = false;

        public boolean isSelected() {
            return isSelected;
        }

        public void setSelected(boolean selected) {
            isSelected = selected;
        }



        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getCarName() {
            return carName;
        }

        public void setCarName(String carName) {
            this.carName = carName;
        }

        public String getCarImage() {
            return carImage;
        }

        public void setCarImage(String carImage) {
            this.carImage = carImage;
        }

        public String getPerKm() {
            return perKm;
        }

        public void setPerKm(String perKm) {
            this.perKm = perKm;
        }

        public String getCharge() {
            return charge;
        }

        public void setCharge(String charge) {
            this.charge = charge;
        }

        public String getRideTimeChargePermin() {
            return rideTimeChargePermin;
        }

        public void setRideTimeChargePermin(String rideTimeChargePermin) {
            this.rideTimeChargePermin = rideTimeChargePermin;
        }

        public String getWaitingCharge() {
            return waitingCharge;
        }

        public void setWaitingCharge(String waitingCharge) {
            this.waitingCharge = waitingCharge;
        }

        public String getCancellationCharge() {
            return cancellationCharge;
        }

        public void setCancellationCharge(String cancellationCharge) {
            this.cancellationCharge = cancellationCharge;
        }

        public String getNoOfSeats() {
            return noOfSeats;
        }

        public void setNoOfSeats(String noOfSeats) {
            this.noOfSeats = noOfSeats;
        }

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getCurrencyMoney() {
            return currencyMoney;
        }

        public void setCurrencyMoney(String currencyMoney) {
            this.currencyMoney = currencyMoney;
        }

        public String getPriceForKm() {
            return priceForKm;
        }

        public void setPriceForKm(String priceForKm) {
            this.priceForKm = priceForKm;
        }

        public String getPricePerMin() {
            return pricePerMin;
        }

        public void setPricePerMin(String pricePerMin) {
            this.pricePerMin = pricePerMin;
        }

        public String getNxnFee() {
            return nxnFee;
        }

        public void setNxnFee(String nxnFee) {
            this.nxnFee = nxnFee;
        }

        public String getAdminFee() {
            return adminFee;
        }

        public void setAdminFee(String adminFee) {
            this.adminFee = adminFee;
        }

        public String getDriverFee() {
            return driverFee;
        }

        public void setDriverFee(String driverFee) {
            this.driverFee = driverFee;
        }

        public String getTax() {
            return tax;
        }

        public void setTax(String tax) {
            this.tax = tax;
        }

        public String getOtherCharge() {
            return otherCharge;
        }

        public void setOtherCharge(String otherCharge) {
            this.otherCharge = otherCharge;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getServiceTax() {
            return serviceTax;
        }

        public void setServiceTax(String serviceTax) {
            this.serviceTax = serviceTax;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public String getLat() {
            return lat;
        }

        public void setLat(String lat) {
            this.lat = lat;
        }

        public String getLon() {
            return lon;
        }

        public void setLon(String lon) {
            this.lon = lon;
        }

    }

}

