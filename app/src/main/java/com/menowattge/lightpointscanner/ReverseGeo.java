package com.menowattge.lightpointscanner;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Classe asincrona che ritorna latitudine,longitudine citta ed indirizzo
 */

class ReverseGeo extends AsyncTask<Location, Void, String> {

    private Context mContext;
    private OnTaskComplete mListener;

    ReverseGeo(Context applicationContext, OnTaskComplete listener) {
        mListener = listener;
        mContext = applicationContext;
    }

    @Override
    protected void onPostExecute(String address) {
        mListener.onTaskComplete(address);
        super.onPostExecute(address);
    }

//Implement AsyncTask’s doInBackground() method,
//where we’ll convert the Location object into an address//
    @Override
    protected String doInBackground(Location... params) {
        Geocoder mGeocoder = new Geocoder(mContext,Locale.getDefault());
        Location location = params[0];
        List<Address> addresses = null;
        double latitude,longitude;
        String printAddress = "";
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        String lat = String.valueOf(latitude);
        String lon = String.valueOf(longitude);

        try {
            addresses = mGeocoder.getFromLocation(latitude,longitude, 1);

        } catch (IOException ioException) {
            printAddress = mContext.getString(R.string.no_address);
        }
        if (addresses.size() == 0) {
            if (printAddress.isEmpty()) {
                printAddress = mContext.getString(R.string.no_address);
            }
        } else {
            String indi = addresses.get(0).getAddressLine(0);
            String city = addresses.get(0).getLocality();
            printAddress = lat+";"+lon+";"+indi+";"+city;
        }
        return printAddress;
    }

//Create the OnTaskComplete interface, which takes a String as an argument//

    interface OnTaskComplete { View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);
        void onTaskComplete(String result);
    }
}
