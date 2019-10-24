package com.menowattge.lightpointscanner;

/**
 * Represents an item in a ToDolist
 */
public class ToDoItem {

    /**
     * Item text
     */
    @com.google.gson.annotations.SerializedName("text")
    private String mText;

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
     * Item text
     */
    @com.google.gson.annotations.SerializedName("latitude")
    private Double mLatitude;

    /**
     * Item text
     */
    @com.google.gson.annotations.SerializedName("longitude")
    private Double mLongitude;

    /**
     * Indicates if the item is completed
     */
    @com.google.gson.annotations.SerializedName("complete")
    private boolean mComplete;

    /**
     * ToDoItem constructor
     */
    public ToDoItem() {

    }

    @Override
    public String toString() {
        return getText();
    }

    /**
     * Initializes a new ToDoItem
     *
     * @param text
     *            The item text
     * @param id
     *            The item id
     */
    public ToDoItem(String text, String id) {
        this.setText(text);
        this.setId(id);
    }

    /**
     * Returns the item text
     */
    public String getText() {
        return mText;
    }

    /**
     * Sets the item text
     *
     * @param text
     *            text to set
     */
    public final void setText(String text) {
        mText = text;
    }

    /**
     * Returns the item id
     */
    public String getId() {
        return mId;
    }





    /**
     * Returns the item text
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
     * Sets the item name
     *
     * @param via
     *            name to set
     */
    public final void setVia(String via) {
        mVia = via;
    }


    /**
     * Returns the item text
     */
    public String getVia() {
        return mVia;
    }

    /**
     * Sets the item name
     *
     * @param city
     *            name to set
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
     *            name to set
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
     * Sets the item name
     *
     * @param longitude
     *            name to set
     */
    public final void setLongitude(Double longitude) {
        mLongitude = longitude;
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
        return o instanceof ToDoItem && ((ToDoItem) o).mId == mId;
    }
}