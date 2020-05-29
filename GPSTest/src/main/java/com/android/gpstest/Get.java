package com.android.gpstest;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Get {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("Username")
    @Expose
    private String nome;

    public String getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }
}
