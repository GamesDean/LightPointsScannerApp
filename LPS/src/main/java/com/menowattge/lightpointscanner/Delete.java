package com.menowattge.lightpointscanner;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Classe fondamentale dove costruisco il JSON da inviare al portale
 */
public class Delete {


    @SerializedName("ID")
    @Expose
    private String iD;

    /*

    Devo generare questo JSON

    {
    "ID": "D735DC6DB42A0102"
    }

    */

// costruttore vuoto per la serializzazione degli oggetti

    public Delete(){

    }

    /**
     * @param iD
     */


    public Delete(String iD) {

        this.iD = iD;

    }



}
