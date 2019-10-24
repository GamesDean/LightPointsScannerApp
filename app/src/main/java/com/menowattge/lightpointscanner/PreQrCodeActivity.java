package com.menowattge.lightpointscanner;

/**
 *
 *  Classe che ha il compito di controllare lo stato del GPS e nel caso portare l'utente alla schermata di settings per accenderlo.
 *  Il controllo avviene in prima battuta cioè all'avvio dell'app, per un monitoraggio costante ho implementato un BR.
 *  Mantengo comunque questa classe anche perchè ha un Layout essendo un'Activity.
 */


import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;

public class PreQrCodeActivity extends AppCompatActivity {

    private GpsLocationReceiver gps;
    private IntentFilter filter;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pre_activity_qr_code);

        gps = new GpsLocationReceiver();
        filter = new IntentFilter(Context.LOCATION_SERVICE);
    }


    /**
     *  Se il GPS non è attivo, una scritta avvisa che l'utente verrà portato alla schermata delle impostazioni
     *  che al termine di 4 secondi verranno proposte a video.
     *  Se il GPS è attivo, viene avviata la classe GetLatLong per inquadrare il Qrcode con la fotocamera.
     */
    @Override
    protected void onResume(){
        super.onResume();

        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE );
        final boolean statusOfGPS = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (!statusOfGPS){

            final Thread timeout = new Thread() {
                @Override
                public void run() {
                    super.run();

                    try {
                        sleep(4000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);




                }
            };

            timeout.start();

        }
        else{
            Intent intent = new Intent(getApplicationContext(), com.menowattge.lightpointscanner.GetLatLong.class);
            startActivity(intent);
        }

        final IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_BATTERY_CHANGED);



        registerReceiver(gps,filter);

    }
}
