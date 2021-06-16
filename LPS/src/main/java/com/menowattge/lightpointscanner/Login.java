package com.menowattge.lightpointscanner;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Login {

    @SerializedName("Username")
    @Expose
    private String Username;

    @SerializedName("Password")
    @Expose
    private String Password;


    public Login(String Username, String Password){

        this.Username = Username;
        this.Password = Password;
    }

}
