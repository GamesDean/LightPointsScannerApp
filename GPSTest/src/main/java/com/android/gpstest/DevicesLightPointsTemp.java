package com.android.gpstest;

/**
 * Rappresenta un elemento che verra inserito nella tabella DevicesLightPointsTemp sul DB Azure Citymonitor
 *
 * DevicesLightPointsTemp è il nome della tabella sul Database Citymonitor, gestibile anche sul portale con le easytable
 */

public class DevicesLightPointsTemp {

    /**
     * Item text
     */
    @com.google.gson.annotations.SerializedName("conn_string")
    private String mConn_string;

    /**
     * Item Id
     */
    @com.google.gson.annotations.SerializedName("id")
    private String mId;

    /**
     * Item name
     */
    @com.google.gson.annotations.SerializedName("nome")
    private String mName;

    /**
     * Item city
     */
    @com.google.gson.annotations.SerializedName("city")
    private String mCity;

    /**
     * Item via
     */
    @com.google.gson.annotations.SerializedName("via")
    private String mVia;

    /**
     * Item latitude
     */
    @com.google.gson.annotations.SerializedName("latitude")
    private Double mLatitude;

    /**
     * Item longitude
     */
    @com.google.gson.annotations.SerializedName("longitude")
    private Double mLongitude;

    /**
     * Item corrente
     */
    @com.google.gson.annotations.SerializedName("corrente")
    private int mCorrente;

    /**
     * Indicates if the item is completed
     */
    @com.google.gson.annotations.SerializedName("complete")
    private boolean mComplete;

    /**
     * ToDoItem constructor
     */
    public DevicesLightPointsTemp() {

    }

    // non lo uso

   // @Override
   // public String toString() {
   //     return getText();
   // }

    /**
     * Initializes a new ToDoItem
     *
    // * @param text
     *            The item text
     * @param id
     *            The item id
     */
    public DevicesLightPointsTemp(/*String text,*/ String id) { // non lo uso
    //    this.setText(text);
        this.setId(id);
    }

    /**
     * Returns the item text
     */
  //  public String getText() {  // non lo uso
  //      return mText;
  //  }

    /**
     * Sets the item text
     *
     * @param text
     *            text to set
     */
  //  public final void setText(String text) { // non lo uso
  //      mText = text;
  //  }




    /**
     * Returns the item id
     */
    public String getId() {
        return mId;
    }

    /**
     * Returns the item conn_string
     */
    public String getConn_string() {
        return mConn_string;
    }



    /**
     * Returns the item name
     */
    public String getName() {
        return mName;
    }


    /**
     * Sets the item name
     *
     * @param name
     *            name to set
     */
    public final void setName(String name) {
        mName = name;
    }

    /**
     * Sets the item via
     *
     * @param via
     *            via to set
     */
    public final void setVia(String via) {
        mVia = via;
    }


    /**
     * Returns the item via
     */
    public String getVia() {
        return mVia;
    }

    /**
     * Sets the item city
     *
     * @param city
     *            city to set
     */
    public final void setCity(String city) {
        mCity = city;
    }


    /**
     * Returns the item text
     */
    public String getCity() {
        return mCity;
    }


    /**
     * Sets the item name
     *
     * @param latitude
     *            latitude to set
     */
    public final void setLatitude(Double latitude) {
        mLatitude = latitude;
    }

    /**
     * Returns the item latitude
     */
    public Double getLatitude() {
        return mLatitude;
    }



    /**
     * Returns the item longitude
     */
    public Double getLongitude() {
        return mLongitude;
    }


    /**
     * Sets the item longitude
     *
     * @param longitude
     *            longitude to set
     */
    public final void setLongitude(Double longitude) {
        mLongitude = longitude;
    }

    /**
     * Returns the item corrente
     */
    public Integer getCorrente() {
        return mCorrente;
    }


    /**
     * Sets the item corrente
     *
     * @param corrente
     *            corrente to set
     */
    public final void setCorrente(Integer corrente) {
        mCorrente = corrente;
    }



    /**
     * Sets the item id
     *
     * @param id
     *            id to set
     */
    public final void setId(String id) {
        mId = id;
    }

    /**
     * Indicates if the item is marked as completed
     */
    public boolean isComplete() {
        return mComplete;
    }

    /**
     * Marks the item as completed or incompleted
     */
    public void setComplete(boolean complete) {
        mComplete = complete;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof DevicesLightPointsTemp && ((DevicesLightPointsTemp) o).mId == mId;
    }
}