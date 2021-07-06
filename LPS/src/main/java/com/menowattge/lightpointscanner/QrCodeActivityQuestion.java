package com.menowattge.lightpointscanner;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.widget.Button;

import static android.view.View.SYSTEM_UI_FLAG_FULLSCREEN;
import static android.view.View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
import static android.view.View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
import static android.view.View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
import static android.view.View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
import static android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE;

public class QrCodeActivityQuestion extends AppCompatActivity {

    public double longitudine,latitudine;
    public String indirizzoRadio,nomePuntoLuce,citta,indirizzo,tipo,idConfigurazione,modello,profilo,potenza,
    serialeApparecchio,codiceApparecchio,identificativo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_code_question);
        // total fullscreen
        getWindow().getDecorView().setSystemUiVisibility(SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
                SYSTEM_UI_FLAG_FULLSCREEN | SYSTEM_UI_FLAG_HIDE_NAVIGATION   |
                SYSTEM_UI_FLAG_LAYOUT_STABLE | SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        // TODO decommentare OK
        getVariables();

        // NESSUN DATO DA INSERIRE
        Button fabNo = findViewById(R.id.fab_no);
        fabNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Ok, nessuna etichetta da scansionare", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();


                // passo le variabili alla classe che le invierà al server
                //TODO decommentare ok
                Intent intent = new Intent(getApplicationContext(), SendDataActivity.class);
                //passo a SendDataActivity l'identificativo vuoto
                identificativo="";
                intent.putExtra("identificativo_palo",identificativo);
                putVariables(intent);
                startActivity(intent);
            }
        });

        //INSERIMENTO MANUALE
        Button fabManual = findViewById(R.id.fab_manual);
        fabManual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Inserire manualmente il codice", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                // passo le variabili alla classe che consente l'inserimento manuale del codice del palo
                Intent intent = new Intent(getApplicationContext(), ManualValueActivity.class);
                putVariables(intent);
                startActivity(intent);
            }
        });

        // ETICHETTA PALO PRESENTE
        Button fabSi = findViewById(R.id.fab_si);
        fabSi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Scansionare l'etichetta", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                // passo le variabili alla classe che scansiona l'etichetta del palo
                Intent intent = new Intent(getApplicationContext(), ShowEtichettaPalo.class);
                // TODO decommentare all'interno
                putVariables(intent);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        //total fullscreen
        getWindow().getDecorView().setSystemUiVisibility(SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
                SYSTEM_UI_FLAG_FULLSCREEN | SYSTEM_UI_FLAG_HIDE_NAVIGATION   |
                SYSTEM_UI_FLAG_LAYOUT_STABLE | SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

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
    }

    public void getVariables(){

        //TODO DECOMMENTARE
        citta = getIntent().getStringExtra("citta");
        indirizzo = getIntent().getStringExtra("indirizzo");
        latitudine = getIntent().getDoubleExtra("latitudine",0);
        longitudine = getIntent().getDoubleExtra("longitudine",0);

        // prima etichetta
        indirizzoRadio = getIntent().getStringExtra("indirizzo_radio");
        nomePuntoLuce = getIntent().getStringExtra("nome_punto_luce");

        Log.d("TEST_QrCodeActivityQ", citta);
        Log.d("TEST_QrCodeActivityQ", indirizzo);
        Log.d("TEST_QrCodeActivityQ", String.valueOf(latitudine));
        Log.d("TEST_QrCodeActivityQ", String.valueOf(longitudine));
        Log.d("TEST_QrCodeActivityQ", indirizzoRadio);
        Log.d("TEST_QrCodeActivityQ", nomePuntoLuce);

        // seconda etichetta
        serialeApparecchio = getIntent().getStringExtra("seriale_apparecchio");
        codiceApparecchio =  getIntent().getStringExtra("codice_apparecchio");
        tipo = getIntent().getStringExtra("tipo");
        idConfigurazione = getIntent().getStringExtra("id_configurazione");
        modello = getIntent().getStringExtra("modello");
        potenza = getIntent().getStringExtra("potenza");
        profilo = getIntent().getStringExtra("profilo");

        Log.d("TEST_QrCodeActivityQ", serialeApparecchio);
        Log.d("TEST_QrCodeActivityQ", codiceApparecchio);
        Log.d("TEST_QrCodeActivityQ", idConfigurazione);
        Log.d("TEST_QrCodeActivityQ", tipo);
        Log.d("TEST_QrCodeActivityQ", potenza);
        Log.d("TEST_QrCodeActivityQ", modello);
        Log.d("TEST_QrCodeActivityQ", profilo);

    }
}