package com.example.taverna;

import android.graphics.Bitmap;

public class ModelMarket {
    public String name;
    public String city;
    public String id;
    public String adress;
    public double latitude;
    public double longitude;
    public Bitmap logo;
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
}
