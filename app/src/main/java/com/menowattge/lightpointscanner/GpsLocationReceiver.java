package com.menowattge.lightpointscanner;

/**
 *
 * Classe BR che controlla constantemente lo stato del GPS : se acceso avvia GetLatLong per scannerizzare il qrcode
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;

public class GpsLocationReceiver extends BroadcastReceiver  {


    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.location.PROVIDERS_CHANGED")) {
            // react on GPS provider change action


            LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE );
            boolean statusOfGPS = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            if(statusOfGPS) {
                //Intent intenT = new Intent(context, com.menowattge.lightpointscanner.GetLatLong.class);
                Intent intenT = new Intent(context, com.menowattge.lightpointscanner.MapsActivity.class);
                context.startActivity(intenT);

            }






        }
    }


}

