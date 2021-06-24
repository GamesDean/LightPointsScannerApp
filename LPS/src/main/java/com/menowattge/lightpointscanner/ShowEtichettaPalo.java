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

import kotlin.collections.DoubleIterator;

import static android.view.View.SYSTEM_UI_FLAG_FULLSCREEN;
import static android.view.View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
import static android.view.View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
import static android.view.View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
import static android.view.View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
import static android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE;

public class ShowEtichettaPalo extends AppCompatActivity {


    public double latitudine,longitudine;
    public String indirizzoRadio,nomePuntoLuce,citta,indirizzo,serialeApparecchio,
            codiceApparecchio,tipo,idConfigurazione,modello,potenza,profilo,identificativo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_etichetta_palo);

        getVariables();



        final Thread timeout = new Thread() {
            @Override
            public void run() {
                super.run();

                try {

                    sleep(5000);

                    Intent intent = new Intent(getApplicationContext(), QrCodeActivityTre.class);
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


    public void putVariables(Intent intent){
        // GPS
        // TODO RIPRISTINARE, ORA COMMENTATO PER DEBUG

        intent.putExtra("citta",citta);
        intent.putExtra("indirizzo",indirizzo);
        intent.putExtra("latitudine",latitudine);
        intent.putExtra("longitudine",longitudine);

        // Prima etichetta
        intent.putExtra("indirizzo_radio", indirizzoRadio); // indirizzo radio D735...
        intent.putExtra("nome_punto_luce", nomePuntoLuce);

        // Seconda etichetta
        intent.putExtra("seriale_apparecchio",serialeApparecchio);
        intent.putExtra("codice_apparecchio",codiceApparecchio);
        intent.putExtra("tipo",tipo);
        intent.putExtra("id_configurazione",idConfigurazione);
        intent.putExtra("modello",modello);
        intent.putExtra("potenza",potenza);
        intent.putExtra("profilo",profilo);

        // Dato inserito manualmente
        intent.putExtra("identificativo_palo",identificativo);
    }



    public void getVariables(){

        citta = getIntent().getStringExtra("citta");
        indirizzo = getIntent().getStringExtra("indirizzo");
        latitudine = getIntent().getDoubleExtra("latitudine",0);
        longitudine = getIntent().getDoubleExtra("longitudine",0);

        // prima etichetta
        indirizzoRadio = getIntent().getStringExtra("indirizzo_radio");
        nomePuntoLuce = getIntent().getStringExtra("nome_punto_luce");

        // seconda etichetta
        serialeApparecchio = getIntent().getStringExtra("seriale_apparecchio");
        codiceApparecchio =  getIntent().getStringExtra("codice_apparecchio");
        tipo = getIntent().getStringExtra("tipo");
        idConfigurazione = getIntent().getStringExtra("id_configurazione");
        modello = getIntent().getStringExtra("modello");
        potenza = getIntent().getStringExtra("potenza");
        profilo = getIntent().getStringExtra("profilo");


    }

    @Override
    protected void onResume() {
        super.onResume();

        getWindow().getDecorView().setSystemUiVisibility(SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
                SYSTEM_UI_FLAG_FULLSCREEN | SYSTEM_UI_FLAG_HIDE_NAVIGATION   |
                SYSTEM_UI_FLAG_LAYOUT_STABLE | SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

}



