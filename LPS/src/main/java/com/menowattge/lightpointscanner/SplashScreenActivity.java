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
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SplashScreenActivity extends AppCompatActivity {

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

        ConnectivityManager mgr = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = mgr.getActiveNetworkInfo();
        boolean isConnected = netInfo != null && netInfo.isConnectedOrConnecting();

        final Thread timeout = new Thread() {
            @Override
            public void run() {
                super.run();

                try {

                    runOnUiThread(new Runnable() {
                        public void run() {
                            if (!isConnected ) {
                                Toast.makeText(getApplicationContext(),"Impossibile avviare l'app :\nE'necessario l'accesso ad internet".toUpperCase(),Toast.LENGTH_LONG).show();
                                finish();
                            }
                        }
                    });

                    sleep(4000);

                    if(isConnected){
                        //Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        // TODO DEBUG
                       // Intent intent = new Intent(getApplicationContext(), QrCodeActivityDue.class);
                        Intent intent = new Intent(getApplicationContext(), PreQrCodeActivity.class);
                        startActivity(intent);
                        finish();
                    }

                }catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        };

        timeout.start();



    }

    }



