package com.menowattge.lightpointscanner;

/**
 *
 *  Classe che ha il compito di controllare lo stato del GPS e nel caso portare l'utente alla schermata di settings per accenderlo.
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
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import java.util.ArrayList;




public class PreQrCodeActivity extends AppCompatActivity {

    private GpsLocationReceiver gps;
    private IntentFilter filter;

    // lists for permissions
    private ArrayList<String> permissionsToRequest;
    private ArrayList<String> permissions = new ArrayList<>();
    private static final int ALL_PERMISSIONS_RESULT = 1011;
    private ProgressDialog pd;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.pre_activity_qr_code);

        pd = new ProgressDialog(PreQrCodeActivity.this);


    }


    /**
     *  Se il GPS non è attivo, una scritta avvisa che l'utente verrà portato alla schermata delle impostazioni
     *  che al termine di 4 secondi verranno proposte a video.
     *  Se il GPS è attivo, viene avviata la classe GetLatLong per inquadrare il Qrcode con la fotocamera.
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

                                pd.setMessage("Caricamento Mappa...");
                                pd.show();
                                pd.setCanceledOnTouchOutside(false);

                            }
                        });

                        sleep(3000);

                    }catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    Intent intent = new Intent(getApplicationContext(), com.menowattge.lightpointscanner.MapsActivity.class);
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
