package com.menowattge.lightpointscanner;

/**
 *
 *  Propone a video una schermata di presentazione
 */

// TODO cambiare splashscreen introdurne uno idoneo

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.widget.Toast;

public class SplashScreenActivity extends AppCompatActivity  {

    public void quit(){

        finishAffinity();
        System.exit(0);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_screen);


        final Thread timeout = new Thread() {
            @Override
            public void run() {
                super.run();

                try {

                    runOnUiThread(new Runnable() {
                        public void run() {


                            ConnectivityManager mgr = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                            NetworkInfo netInfo = mgr.getActiveNetworkInfo();
                            boolean isConnected = netInfo != null &&
                                    netInfo.isConnectedOrConnecting();

                            if (isConnected ) {

                            }
                            else {
                                //No internet
                                Toast.makeText(getApplicationContext(),"Impossibile avviare l'app :\nE'necessario l'accesso ad internet".toUpperCase(),Toast.LENGTH_LONG).show();

                                finish();

                            }
                        }
                    });

                    sleep(4000);

                }catch (InterruptedException e) {
                    e.printStackTrace();
                }

                Intent intent = new Intent(getApplicationContext(), com.menowattge.lightpointscanner.PreQrCodeActivity.class);
                startActivity(intent);

                finish();
            }
        };

        timeout.start();



    }



    }



