package com.example.taverna;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface DataApi {

    @GET("GetProductListByGroup.php")
    Call<List<ModelProduct>> getProductListByGroup(@Query("id") int groupId);

    @GET("GetProductGroupListByName.php")
    Call<ModelSearchResult> getProductGroupListByName(@Query("name") String name);

    @GET("GetProductFullById.php")
    Call<ModelProductFull> getProductFullById(@Query("id") int id);

    @GET("GetProductFullByEAN.php")
    Call<ModelProductFull> getProductFullByEAN(@Query("EAN") String ean);

}
