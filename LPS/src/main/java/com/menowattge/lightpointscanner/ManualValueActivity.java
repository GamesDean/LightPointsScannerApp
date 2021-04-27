package com.menowattge.lightpointscanner;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import static android.view.View.SYSTEM_UI_FLAG_FULLSCREEN;
import static android.view.View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
import static android.view.View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
import static android.view.View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
import static android.view.View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
import static android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE;

/**
 *  Permette all'utente di selezionare la potenza del Meridio. Passa poi questi dati a SendDataActivity
 */
public class ManualValueActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    public double longitudine,latitudine;
    public String indirizzoRadio,nomePuntoLuce,citta,indirizzo,potenza,identificativo,
            serialeApparecchio, codiceApparecchio, tipo, idConfigurazione, modello,profilo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_value);

        // total fullscreen
        getWindow().getDecorView().setSystemUiVisibility(SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
                SYSTEM_UI_FLAG_FULLSCREEN | SYSTEM_UI_FLAG_HIDE_NAVIGATION   |
                SYSTEM_UI_FLAG_LAYOUT_STABLE | SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        // TODO decommentare all interno ok
        getVariables();

       // final Spinner spinner = (Spinner) findViewById(R.id.spinner_power);
        final EditText editText = findViewById(R.id.editText);
        editText.setSelection(0);
        Button button_ok = findViewById(R.id.button_ok);


        final Intent intent = new Intent(getApplicationContext(), SendDataActivity.class);


        button_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                identificativo = editText.getText().toString();
                // TODO controlli lunghezza
                if(identificativo==""|| identificativo.isEmpty()){
                    Toast.makeText(ManualValueActivity.this,"Inserire ID palo",Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(ManualValueActivity.this,"id : "+identificativo,Toast.LENGTH_LONG).show();
                    // TODO decommentare ok
                    putVariables(intent);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    @Override
    protected void onResume(){
        super.onResume();

        //total fullscreen
        getWindow().getDecorView().setSystemUiVisibility(SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
                SYSTEM_UI_FLAG_FULLSCREEN | SYSTEM_UI_FLAG_HIDE_NAVIGATION   |
                SYSTEM_UI_FLAG_LAYOUT_STABLE | SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

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

        Log.d("TEST_QrCodeActivityM", citta);
        Log.d("TEST_QrCodeActivityM", indirizzo);
        Log.d("TEST_QrCodeActivityM", String.valueOf(latitudine));
        Log.d("TEST_QrCodeActivityM", String.valueOf(longitudine));
        Log.d("TEST_QrCodeActivityM", indirizzoRadio);
        Log.d("TEST_QrCodeActivityM", nomePuntoLuce);

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

        //TODO DECOMMENTARE
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

        Log.d("TEST_QrCodeActivityM", serialeApparecchio);
        Log.d("TEST_QrCodeActivityM", codiceApparecchio);
        Log.d("TEST_QrCodeActivityM", idConfigurazione);
        Log.d("TEST_QrCodeActivityM", tipo);
        Log.d("TEST_QrCodeActivityM", potenza);
        Log.d("TEST_QrCodeActivityM", modello);
        Log.d("TEST_QrCodeActivityM", profilo);

    }


}
