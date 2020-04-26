package com.menowattge.lightpointscanner;

import com.google.gson.JsonObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface JsonApi {


    @POST("/Account/Login?ReturnUrl=%2F")
    Call<ResponseBody> loginWithCredentials(@Body LoginCredentials data);

    // come parametro c'è il token che ho generato nella classe LoginCredential a partire da user e pass
    @GET("/api/Comuni")
    Call<Get> getData(@Header("Authorization") String authkey);

    @GET("/api/Comuni")
    Call<JsonObject> getJson(@Header("Authorization") String authkey);


    // inserisco il punto luce, gli passo i dati ed il token
    @POST("/api/PuntoLuce")//("/LightPoint/Insert?createAtLat=null&createAtLng=null")
    Call<Post> putData(@Body Post data , @Header("Authorization") String authkey);


    @POST("/api/ComandoStatoPuntoLuce/")
    Call<Post> onOff(@Body OnOff data,@Header("Authorization") String authkey);



}