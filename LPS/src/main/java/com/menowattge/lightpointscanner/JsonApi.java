package com.menowattge.lightpointscanner;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/**
 * Preleva Inserisce o aggiorna i dati nel portale tramite chiamate alle API opportune
 */
public interface JsonApi {


    @POST("/Account/Login?ReturnUrl=%2F")
    //Call<ResponseBody> loginWithCredentials(@Body LoginCredentials data);
    Call<ResponseBody> loginWithCredentials(@Header("Authorization") String authkey);


    @GET("/api/Comuni")
    Call<JsonObject> getJson(@Header("Authorization") String authkey);

    @GET("/api/Dispositivi")
    Call<JsonArray> getDeviceList(@Header("Authorization") String authkey);

    // aggiorno il punto luce, gli passo i dati ed il token
    @PUT("/api/PuntoLuce")//("/LightPoint/Insert?createAtLat=null&createAtLng=null")
    Call<Void> putData(@Body Post data, @Header("Authorization") String authkey);

    // inserisco il punto luce, gli passo i dati ed il token
    @POST("/api/PuntoLuce")//("/LightPoint/Insert?createAtLat=null&createAtLng=null")
    Call<Void> postData(@Body Post data, @Header("Authorization") String authkey);

    //405
    //@DELETE("/api/PuntoLuce")
    //Call<Void> deleteDevices(@Header("Authorization") String authkey,@Header("id") String id);

    //405
    @DELETE("/api/PuntoLuce/{id}")
    Call<Void> deleteDevices(@Header("Authorization") String authkey,@Path("id") String id);




   // @POST("/api/ComandoStatoPuntoLuce/")
   // Call<Post> onOff(@Body OnOff data,@Header("Authorization") String authkey);



}