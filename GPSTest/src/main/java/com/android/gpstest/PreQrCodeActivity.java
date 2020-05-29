package com.android.gpstest;

/**
 *
 *  Classe che ha il compito di controllare lo stato del GPS e nel caso proporre l'utente un dialog per accenderlo.
 *  Il controllo avviene in prima battuta cioè all'avvio dell'app, per un monitoraggio costante ho implementato un BR.
 *  Mantengo comunque questa classe anche perchè ha un Layout essendo un'Activity.
 */


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;


public class PreQrCodeActivity extends AppCompatActivity {

    private GpsLocationReceiver gps;
    private IntentFilter filter;

    private ProgressDialog pd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.pre_activity_qr_code);

        pd = new ProgressDialog(new ContextThemeWrapper(PreQrCodeActivity.this,R.style.ProgressDialogCustom));


    }


    /**
     *  Se il GPS non è attivo, un dialog invita l'utente ad accenderlo
     *  al termine di 3 secondi carica la mappa
     *  Se il GPS è attivo, viene avviata la classe MapsActivity per geolocalizzarsi
     */
    @Override
    protected void onResume(){
        super.onResume();

        Activity mContext = PreQrCodeActivity.this;//change this your activity name
        StartLocationAlert startLocationAlert = new StartLocationAlert(mContext);

        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE );
        final boolean statusOfGPS = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (statusOfGPS){
            final Thread timeout = new Thread() {
                @Override
                public void run() {
                    super.run();
                    try {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                pd.setMessage("Caricamento ...");
                                pd.show();
                                pd.setCanceledOnTouchOutside(false);

                            }
                        });

                        sleep(1000);

                    }catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    Intent intent = new Intent(getApplicationContext(), com.android.gpstest.GpsTestActivity.class);
                    startActivity(intent);
                    finish();
                }
            };

            timeout.start();

        }


        final IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(gps,filter);


    }




}
