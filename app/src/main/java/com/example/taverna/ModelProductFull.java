package com.example.taverna;

import android.support.annotation.Nullable;

import java.io.IOException;
import java.util.ArrayList;

public class ModelProductFull extends ModelProduct {
    public ArrayList<ModelPrice> prices;
    public ArrayList<ModelComment> comments;
    public ArrayList<String> images_links;

    public ModelProductFull(ModelProduct product) {
        this.id = product.id;
        this.name = product.name;
        this.price = product.price;
        this.price_min = product.price_min;
        this.price_max = product.price_max;
        this.raiting = product.raiting;
    }


    //Конструктор класса по ID
    @Nullable
    public static ModelProductFull getProductByID(int idProduct){
        HTTPService ConnectHTTP = new HTTPService();
        try {
            ModelProductFull res = ConnectHTTP.getTovarFullById(idProduct);
            return res;
        } catch (IOException e) {
            return null;
        }

    }

    @Nullable
    public static ModelProductFull getProductByEAN(String EAN){
        HTTPService ConnectHTTP = new HTTPService();
        try {
            ModelProductFull res = ConnectHTTP.getTovarFullByEAN(EAN);
            return res;
        } catch (IOException e) {
            return null;
        }

    }



}
