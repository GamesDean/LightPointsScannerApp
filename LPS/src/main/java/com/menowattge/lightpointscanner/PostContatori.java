package com.menowattge.lightpointscanner;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Classe fondamentale dove costruisco il JSON da inviare al portale
 */
public class PostContatori {

// TODO aggiungere campi mancanti, in attesa API

    @SerializedName("ID")
    @Expose
    private String iD;
    @SerializedName("Nome")
    @Expose
    private String nome;

    @SerializedName("Ripetitore")
    @Expose
    private Boolean ripetitore;
    @SerializedName("Note")
    @Expose
    private String note;
    @SerializedName("ChiaviCrittografia")
    @Expose
    private List<String> chiaviCrittografia = null;
    @SerializedName("IdComune")
    @Expose
    private  String idComune;
    @SerializedName("Indirizzo")
    @Expose
    private String indirizzo;
    @SerializedName("CoordinateGps")
    @Expose
    private CoordinateGps coordinateGps;

    @SerializedName("NomeUtente")
    @Expose
    private String nomeUtente;

    @SerializedName("NumeroUtente")
    @Expose
    private String numeroUtente;

    @SerializedName("NumeroContratto")
    @Expose
    private String numeroContratto;

    @SerializedName("Matricola")
    @Expose
    private String matricola;

    @SerializedName("NumeroSerialeRadio")
    @Expose
    private String numeroSerialeRadio;


    /*

    Devo generare questo JSON

    {
        "ID": "D735DC6DB42A0102",
            "Nome": "prova_post",
            "TipoLuce": "LED",
            "Ripetitore": false,
            "Note": "",
            "ChiaviCrittografia": [
        "12e259cb9966d73a09cafa751e995555"
  ],
        "IdComune": "Grottammare",
        "Indirizzo": "Via x",
            "CoordinateGps": {
                "Lat": 44.12345,
                "Long": 34.985432
    },
            "NomeUtente": "",
            "NumeroUtente": "",
            "NumeroContratto": "",
            "InfoQuadroElettrico": "",
            "Matricola": "",
            "NumeroSerialeRadio": ""

    }

    */




// costruttore vuoto per la serializzazione degli oggetti

    public PostContatori(){

    }

    /**
     * @param iD
     * @param nome
     * @param ripetitore
     * @param note
     * @param chiaviCrittografia
     * @param idComune
     * @param indirizzo
     * @param coordinateGps
     * @param nomeUtente
     * @param numeroUtente
     * @param numeroContratto
     * @param matricola
     * @param numeroSerialeRadio
     */


    public PostContatori(String iD, String nome, Boolean ripetitore, String note, List<String> chiaviCrittografia, String idComune, String indirizzo, CoordinateGps coordinateGps,
                         String nomeUtente, String numeroUtente, String numeroContratto, String matricola, String numeroSerialeRadio) {

        this.iD = iD;
        this.nome = nome;
        this.ripetitore = ripetitore;
        this.note = note;
        this.chiaviCrittografia = chiaviCrittografia;
        this.idComune = idComune;
        this.indirizzo = indirizzo;
        this.coordinateGps = coordinateGps;
        this.nomeUtente = nomeUtente;
        this.numeroUtente = numeroUtente;
        this.numeroContratto = numeroContratto;
        this.matricola = matricola;
        this.numeroSerialeRadio = numeroSerialeRadio;
    }



    /*

    Inner Class dove costruisco questo oggetto json

    "CoordinateGps": {
                "Lat": 44.12345,
                "Long": 34.985432
    },
     */

    public static class CoordinateGps {


        @SerializedName("Lat")
        @Expose
        private Double lat;
        @SerializedName("Long")
        @Expose
        private Double _long;


        public CoordinateGps() {
        }

        /**
         *
         * @param _long
         * @param lat
         */
        public CoordinateGps(Double lat, Double _long) {
            super();
            this.lat = lat;
            this._long = _long;
        }

        public Double getLat() {
            return lat;
        }

        public void setLat(Double lat) {
            this.lat = lat;
        }

        public Double getLong() {
            return _long;
        }

        public void setLong(Double _long) {
            this._long = _long;
        }
    }


}
