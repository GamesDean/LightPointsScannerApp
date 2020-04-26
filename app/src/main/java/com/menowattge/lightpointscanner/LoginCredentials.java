package com.menowattge.lightpointscanner;


import android.util.Base64;

import java.io.UnsupportedEncodingException;

public class LoginCredentials {

    private String name;
    private String pass;

    public LoginCredentials(String name, String pass) {
        this.name = name;
        this.pass = pass;
    }


    //creo un token a partire dalle credenziali
    public static String getAuthToken(String username,String password) {
        byte[] data = new byte[0];
        try {
            data = (username + ":" + password).getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "Basic " + Base64.encodeToString(data, Base64.NO_WRAP);
    }

}
