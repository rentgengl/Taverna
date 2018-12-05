package com.example.taverna;

import android.graphics.Bitmap;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ModelProduct {
    public int id;
    public String name;
    public String EAN;
    public int price = 250;
    public int price_min = 0;
    public int price_max = 0;
    public int comment_count = 0;
    public float raiting = 0;

    public String imageSmall_link;//Ссылка на маленькую основную привьюху

//Для создания нового товара
    public int userID = 0;
    public String newComment;


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


    public static ArrayList<ModelProduct> GetProductListByGroup(String idGroup){

        HTTPService ConnectHTTP = new HTTPService();

        try {
            ArrayList<ModelProduct> res = ConnectHTTP.GetProductListByGroup(idGroup);
            return res;
        } catch (IOException e) {
            //Хз в чем беда
            return new ArrayList<ModelProduct>();
        }


    }

    public void setImageLink(String small){
        if (small !=null & small!="null"){
            imageSmall_link = small;
        }
    }

    public static HashMap<String,ArrayList> GetProductGroupListByName(String name){

        HTTPService ConnectHTTP = new HTTPService();

        try {
            HashMap<String,ArrayList> res = ConnectHTTP.GetProductGroupListByName(name);
            return res;
        } catch (IOException e) {
            //Хз в чем беда
            return null;
        }


    }
}
