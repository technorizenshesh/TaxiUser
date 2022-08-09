package com.taxiuser.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CountryModel {

    @SerializedName("result")
    @Expose
    private List<Result> result = null;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("message")
    @Expose
    private String message;

    public List<Result> getResult() {
        return result;
    }

    public void setResult(List<Result> result) {
        this.result = result;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


    public class Result {

        @SerializedName("id")
        @Expose
        private String id;
        @SerializedName("name")
        @Expose
        private String name;
       /* @SerializedName("iso3")
        @Expose
        private String iso3;
        @SerializedName("numeric_code")
        @Expose
        private String numericCode;
        @SerializedName("iso2")
        @Expose
        private String iso2;
        @SerializedName("phone_code")
        @Expose
        private String phoneCode;
        @SerializedName("capital")
        @Expose
        private String capital;
        @SerializedName("currency")
        @Expose
        private String currency;
        @SerializedName("currency_name")
        @Expose
        private String currencyName;
        @SerializedName("currency_symbol")
        @Expose
        private String currencySymbol;
        @SerializedName("tld")
        @Expose
        private String tld;
        @SerializedName("native")
        @Expose
        private String _native;
        @SerializedName("region")
        @Expose
        private String region;
        @SerializedName("subregion")
        @Expose
        private String subregion;
        @SerializedName("timezones")
        @Expose
        private String timezones;
        @SerializedName("translations")
        @Expose
        private String translations;*/
        @SerializedName("latitude")
        @Expose
        private String latitude;
        @SerializedName("longitude")
        @Expose
        private String longitude;
/*
        @SerializedName("emoji")
        @Expose
        private String emoji;
        @SerializedName("emojiU")
        @Expose
        private String emojiU;
        @SerializedName("created_at")
        @Expose
        private String createdAt;
        @SerializedName("updated_at")
        @Expose
        private String updatedAt;
        @SerializedName("flag")
        @Expose
        private String flag;
        @SerializedName("wikiDataId")
        @Expose
        private Object wikiDataId;
        @SerializedName("emergencyNumber")
        @Expose
        private String emergencyNumber;
*/

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

/*
        public String getIso3() {
            return iso3;
        }

        public void setIso3(String iso3) {
            this.iso3 = iso3;
        }

        public String getNumericCode() {
            return numericCode;
        }

        public void setNumericCode(String numericCode) {
            this.numericCode = numericCode;
        }

        public String getIso2() {
            return iso2;
        }

        public void setIso2(String iso2) {
            this.iso2 = iso2;
        }

        public String getPhoneCode() {
            return phoneCode;
        }

        public void setPhoneCode(String phoneCode) {
            this.phoneCode = phoneCode;
        }

        public String getCapital() {
            return capital;
        }

        public void setCapital(String capital) {
            this.capital = capital;
        }

        public String getCurrency() {
            return currency;
        }

        public void setCurrency(String currency) {
            this.currency = currency;
        }

        public String getCurrencyName() {
            return currencyName;
        }

        public void setCurrencyName(String currencyName) {
            this.currencyName = currencyName;
        }

        public String getCurrencySymbol() {
            return currencySymbol;
        }

        public void setCurrencySymbol(String currencySymbol) {
            this.currencySymbol = currencySymbol;
        }

        public String getTld() {
            return tld;
        }

        public void setTld(String tld) {
            this.tld = tld;
        }

        public String getNative() {
            return _native;
        }

        public void setNative(String _native) {
            this._native = _native;
        }

        public String getRegion() {
            return region;
        }

        public void setRegion(String region) {
            this.region = region;
        }

        public String getSubregion() {
            return subregion;
        }

        public void setSubregion(String subregion) {
            this.subregion = subregion;
        }

        public String getTimezones() {
            return timezones;
        }

        public void setTimezones(String timezones) {
            this.timezones = timezones;
        }

        public String getTranslations() {
            return translations;
        }

        public void setTranslations(String translations) {
            this.translations = translations;
        }
*/

        public String getLatitude() {
            return latitude;
        }

        public void setLatitude(String latitude) {
            this.latitude = latitude;
        }

        public String getLongitude() {
            return longitude;
        }

        public void setLongitude(String longitude) {
            this.longitude = longitude;
        }

      /*  public String getEmoji() {
            return emoji;
        }

        public void setEmoji(String emoji) {
            this.emoji = emoji;
        }

        public String getEmojiU() {
            return emojiU;
        }

        public void setEmojiU(String emojiU) {
            this.emojiU = emojiU;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }

        public String getUpdatedAt() {
            return updatedAt;
        }

        public void setUpdatedAt(String updatedAt) {
            this.updatedAt = updatedAt;
        }

        public String getFlag() {
            return flag;
        }

        public void setFlag(String flag) {
            this.flag = flag;
        }

        public Object getWikiDataId() {
            return wikiDataId;
        }

        public void setWikiDataId(Object wikiDataId) {
            this.wikiDataId = wikiDataId;
        }

        public String getEmergencyNumber() {
            return emergencyNumber;
        }

        public void setEmergencyNumber(String emergencyNumber) {
            this.emergencyNumber = emergencyNumber;
        }*/

    }

}


