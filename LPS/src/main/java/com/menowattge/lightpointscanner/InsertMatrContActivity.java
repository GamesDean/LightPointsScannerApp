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
 *  Permette all'utente di inserire la matricola del contatore. Passa poi questi dati a SendDataActivity
 */
public class InsertMatrContActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    public double longitudine,latitudine;
    public String citta,indirizzo,cognome,nome, numeroUtenza, numeroContratto,indirizzoUtenza,numeroCivico,ldnContatore,
            numeroSerialeRadio;

    public String matricolaCont = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_matr_cont);

        // total fullscreen
        getWindow().getDecorView().setSystemUiVisibility(SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
                SYSTEM_UI_FLAG_FULLSCREEN | SYSTEM_UI_FLAG_HIDE_NAVIGATION   |
                SYSTEM_UI_FLAG_LAYOUT_STABLE | SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        // TODO decommentare all interno ok
        getVariables();

        final EditText editText = findViewById(R.id.editTextCont);
        editText.setSelection(0);
        Button button_ok = findViewById(R.id.button_ok_cont);

        final Intent intent = new Intent(getApplicationContext(), SendDataContActivity.class);

        button_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                matricolaCont = editText.getText().toString();

                    putVariables(intent);
                    startActivity(intent);
                    finish();
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

        intent.putExtra("citta",citta);
        intent.putExtra("indirizzo",indirizzo);
        intent.putExtra("latitudine",latitudine);
        intent.putExtra("longitudine",longitudine);

        // Seconda etichetta
        intent.putExtra("cognome",cognome);
        intent.putExtra("nome",nome);
        intent.putExtra("numero_utenza",numeroUtenza);
        intent.putExtra("numero_contratto",numeroContratto);
        intent.putExtra("indirizzo_utenza",indirizzoUtenza);
        intent.putExtra("numero_civico",numeroCivico);

        intent.putExtra("ldn",ldnContatore);
        intent.putExtra("numero_seriale_radio",numeroSerialeRadio);


        // Dato inserito manualmente
        intent.putExtra("matricola_contatore",matricolaCont);
    }


    public void getVariables(){

        citta = getIntent().getStringExtra("citta");
        indirizzo = getIntent().getStringExtra("indirizzo");
        latitudine = getIntent().getDoubleExtra("latitudine",0);
        longitudine = getIntent().getDoubleExtra("longitudine",0);

        numeroSerialeRadio = getIntent().getStringExtra("numero_seriale_radio");
        ldnContatore = getIntent().getStringExtra("ldn");

        // seconda etichetta
        cognome = getIntent().getStringExtra("cognome");
        nome =  getIntent().getStringExtra("nome");
        numeroUtenza = getIntent().getStringExtra("numero_utenza");
        numeroContratto = getIntent().getStringExtra("numero_contratto");
        indirizzoUtenza = getIntent().getStringExtra("indirizzo_utenza");
        numeroCivico = getIntent().getStringExtra("numero_civico");

        Log.d("TEST_cognome", cognome);
        Log.d("TEST_nome", nome);
        Log.d("TEST_numeroUtenza", numeroUtenza);
        Log.d("TEST_numeroContratto", numeroContratto);
        Log.d("TEST_indirizzoUtente", indirizzoUtenza);
        Log.d("TEST_numeroCivico", numeroCivico);

        Log.d("TEST_ldn", ldnContatore);


    }


}
