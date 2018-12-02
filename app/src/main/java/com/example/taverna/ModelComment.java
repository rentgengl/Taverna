package com.example.taverna;

import java.util.ArrayList;

public class ModelComment {
    public ModelUser user;
    public String comment;
    public float raiting = 0;


    public ModelComment(ModelUser user, String comment, float raiting){
        this.user = user;
        this.comment = comment;
        this.raiting = raiting;
    }

    public ArrayList<ModelComment> getCommetListByProduct(ModelProduct product){

        HTTPService http = new HTTPService();
        return null;

    }

    public static ArrayList<ModelComment> getTestData(){

        ArrayList<ModelComment> res = new ArrayList<>();

        res.add(new ModelComment(new ModelUser("Иван",null,null),
                "Отличая штука всем советую!",4.3f));
        res.add(new ModelComment(new ModelUser("Григорий Леонидович",null,null),
                "Ерунда, не берите это дерьмо - полная хрень!",2.3f));

        res.add(new ModelComment(new ModelUser("Григорий Леонидович",null,null),
                "Ерунда, не берите это дерьмо - полная хрень!",2.3f));

        res.add(new ModelComment(new ModelUser("Григорий Леонидович",null,null),
                "Ерунда, не берите это дерьмо - полная хрень!",2.3f));
        return res;

    }
}
