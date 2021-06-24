package com.menowattge.lightpointscanner;

/**
 *
 *  Propone a video un esempio di etichetta RLU da scansionare
 */


import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import static android.view.View.SYSTEM_UI_FLAG_FULLSCREEN;
import static android.view.View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
import static android.view.View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
import static android.view.View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
import static android.view.View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
import static android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE;

public class ShowEtichettaContatore extends AppCompatActivity {

   public String citta,indirizzo;
   public Double latitudine,longitudine;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_etichetta_contatore);

        getVariables();


        final Thread timeout = new Thread() {
            @Override
            public void run() {
                super.run();

                try {

                    sleep(5000);

                    Intent intent = new Intent(getApplicationContext(), QrCodeActivity.class);
                    putVariables(intent);
                    startActivity(intent);
                    finish();


                }catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        };

        timeout.start();



    }

    public void getVariables(){
        citta = getIntent().getStringExtra("citta");
        indirizzo = getIntent().getStringExtra("indirizzo");
        latitudine = getIntent().getDoubleExtra("latitudine",0);
        longitudine = getIntent().getDoubleExtra("longitudine",0);

        Log.d("TEST_appa", citta);
    }

    public void putVariables(Intent intent){

        intent.putExtra("citta", citta);
        intent.putExtra("indirizzo", indirizzo);
        intent.putExtra("latitudine", latitudine);
        intent.putExtra("longitudine", longitudine);
    }


    @Override
    protected void onResume() {
        super.onResume();

        getWindow().getDecorView().setSystemUiVisibility(SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
                SYSTEM_UI_FLAG_FULLSCREEN | SYSTEM_UI_FLAG_HIDE_NAVIGATION   |
                SYSTEM_UI_FLAG_LAYOUT_STABLE | SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

}



