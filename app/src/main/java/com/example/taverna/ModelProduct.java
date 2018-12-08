package com.example.taverna;

import android.graphics.Bitmap;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ModelProduct {
    public int id;
    public int price = 250;
    public int price_min = 0;
    public int price_max = 0;
    public int comment_count = 0;
    public int userID = 0;

    public String name;
    public String EAN;
    public String imageSmall_link;//Ссылка на маленькую основную привьюху
    public String newComment;

    public float raiting = 0;

    //Конструктор класса по данным из json
    public ModelProduct(String nID, String nName){

        this.id = Integer.parseInt(nID);
        this.name = nName;

    }

    public ModelProduct() {
    }

    //Сохраняет продук на бэкенде
    public void createOnServer(){
        HTTPService ConnectHTTP = new HTTPService();
        try {
            ConnectHTTP.createNewProduct(this);
        } catch (IOException e) {
            // Ну что еще такое
        }
    }

    public void setImageLink(String small){
        if (small !=null & small!="null"){
            imageSmall_link = small;
        }
    }
}
