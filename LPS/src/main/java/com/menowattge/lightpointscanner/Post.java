package com.menowattge.lightpointscanner;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Classe fondamentale dove costruisco il JSON da inviare al portale
 */
public class Post {

// TODO aggiungere campi mancanti, in attesa API

    @SerializedName("ID")
    @Expose
    private String iD;
    @SerializedName("Nome")
    @Expose
    private String nome;
    @SerializedName("TipoLuce")
    @Expose
    private String tipoLuce;
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
    @SerializedName("TipoApparecchiatura")
    @Expose
    private String tipoApparecchiatura;
    @SerializedName("Marca")
    @Expose
    private String marca;
    @SerializedName("Modello")
    @Expose
    private String modello;
    @SerializedName("InfoQuadroElettrico")
    @Expose
    private String infoQuadroElettrico;
    @SerializedName("Palo")
    @Expose
    private String palo;
    @SerializedName("AltezzaPaloMm")
    @Expose
    private Integer altezzaPaloMm;
    @SerializedName("Portella")
    @Expose
    private Boolean portella;
    @SerializedName("Pozzetto")
    @Expose
    private Boolean pozzetto;
    @SerializedName("Terra")
    @Expose
    private Boolean terra;
    @SerializedName("TecnologiaLampada")
    @Expose
    private String tecnologiaLampada;
    @SerializedName("PotenzaLampadaWatt")
    @Expose
    private Double potenzaLampadaWatt;
    @SerializedName("Alimentatore")
    @Expose
    private String alimentatore;
    @SerializedName("LineaAlimentazione")
    @Expose
    private String lineaAlimentazione;
    @SerializedName("Telecontrollo")
    @Expose
    private Boolean telecontrollo;


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
        "TipoApparecchiatura": "",
            "Marca": "",
            "Modello": "Meridio",
            "InfoQuadroElettrico": "",
            "Palo": "",
            "AltezzaPaloMm": 0,
            "Portella": true,
            "Pozzetto": true,
            "Terra": true,
            "TecnologiaLampada": "led",
            "PotenzaLampadaWatt": 1800.00,
            "Alimentatore": "",
            "LineaAlimentazione": "",
            "Telecontrollo": true
    }

    */




// costruttore vuoto per la serializzazione degli oggetti

    public Post(){

    }

    /**
     *
     * @param ripetitore
     * @param palo
     * @param note
     * @param terra
     * @param alimentatore
     * @param pozzetto
     * @param tecnologiaLampada
     * @param idComune
     * @param indirizzo
     * @param nome
     * @param tipoLuce
     * @param chiaviCrittografia
     * @param telecontrollo
     * @param portella
     * @param modello
     * @param lineaAlimentazione
     * @param tipoApparecchiatura
     * @param marca
     * @param potenzaLampadaWatt
     * @param iD
     * @param infoQuadroElettrico
     * @param coordinateGps
     * @param altezzaPaloMm
     */


    public Post(String iD, String nome, String tipoLuce, Boolean ripetitore, String note, List<String> chiaviCrittografia, String idComune, String indirizzo, CoordinateGps coordinateGps, String tipoApparecchiatura, String marca, String modello, String infoQuadroElettrico, String palo, Integer altezzaPaloMm, Boolean portella, Boolean pozzetto, Boolean terra, String tecnologiaLampada, Double potenzaLampadaWatt, String alimentatore, String lineaAlimentazione, Boolean telecontrollo) {

        this.iD = iD;
        this.nome = nome;
        this.tipoLuce = tipoLuce;
        this.ripetitore = ripetitore;
        this.note = note;
        this.chiaviCrittografia = chiaviCrittografia;
        this.idComune = idComune;
        this.indirizzo = indirizzo;
        this.coordinateGps = coordinateGps;
        this.tipoApparecchiatura = tipoApparecchiatura;
        this.marca = marca;
        this.modello = modello;
        this.infoQuadroElettrico = infoQuadroElettrico;
        this.palo = palo;
        this.altezzaPaloMm = altezzaPaloMm;
        this.portella = portella;
        this.pozzetto = pozzetto;
        this.terra = terra;
        this.tecnologiaLampada = tecnologiaLampada;
        this.potenzaLampadaWatt = potenzaLampadaWatt;
        this.alimentatore = alimentatore;
        this.lineaAlimentazione = lineaAlimentazione;
        this.telecontrollo = telecontrollo;
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
