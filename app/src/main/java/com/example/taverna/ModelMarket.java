package com.example.taverna;


import android.os.Parcel;
import android.os.Parcelable;

public class ModelMarket implements Parcelable {
    public String name;
    public String city;
    public String id;
    public String adress;
    public double latitude;
    public double longitude;
    public String logo_link;


    public ModelMarket(String name, String adress, String logo_link){

        this.name = name;
        this.adress = adress;
        this.logo_link = logo_link;

    }

    public void setLatLng(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public static ModelMarket getMarketById(String idMark){
        ModelMarket res = new ModelMarket("Гипермаркет Ашан яблоновка",
                "Яблоновский Гагарина 159/1",
                Constants.SERVICE_GET_IMAGE + "logo_auchan.png");
        res.id = idMark;
        res.setLatLng(45.04484,38.97603);
        return res;

    }

    public static ModelMarket getDefaultMarket(){

        ModelMarket res = new ModelMarket("Магазин не определен",
                "Адрес не указан",
                Constants.SERVICE_GET_IMAGE + "logo_auchan.png");

        return res;
    }

    protected ModelMarket(Parcel in) {
        name = in.readString();
        city = in.readString();
        id = in.readString();
        adress = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
        logo_link = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(city);
        dest.writeString(id);
        dest.writeString(adress);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeString(logo_link);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<ModelMarket> CREATOR = new Parcelable.Creator<ModelMarket>() {
        @Override
        public ModelMarket createFromParcel(Parcel in) {
            return new ModelMarket(in);
        }

        @Override
        public ModelMarket[] newArray(int size) {
            return new ModelMarket[size];
        }
    };

}
