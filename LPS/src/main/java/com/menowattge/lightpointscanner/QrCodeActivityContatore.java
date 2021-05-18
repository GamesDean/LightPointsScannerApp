package com.menowattge.lightpointscanner;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static android.view.View.SYSTEM_UI_FLAG_FULLSCREEN;
import static android.view.View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
import static android.view.View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
import static android.view.View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
import static android.view.View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
import static android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE;

public class QrCodeActivityContatore extends AppCompatActivity implements ZXingScannerView.ResultHandler {


    private ZXingScannerView mScannerView;

    public double latitudine,longitudine;
    public String ldnContatore,citta,indirizzo,cognome,nome,numeroUtenza,numeroContratto,indirizzoUtenza, numeroCivico,
    numeroSerialeRadio;


    //PERMESSI CAMERA
    @SuppressLint("NewApi")
    public void CheckPermission() {

        if (this.checkSelfPermission(android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
            requestPermissions(new String[]{android.Manifest.permission.CAMERA}, 1);

    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_code_due);

        // total fullscreen
        getWindow().getDecorView().setSystemUiVisibility(SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
                SYSTEM_UI_FLAG_FULLSCREEN | SYSTEM_UI_FLAG_HIDE_NAVIGATION   |
                SYSTEM_UI_FLAG_LAYOUT_STABLE | SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        CheckPermission();


        getVariablesContatore();

        // Programmatically initialize the scanner view
        mScannerView = new ZXingScannerView(this);
        setContentView(mScannerView);
        mScannerView.startCamera();

    }

    public void getVariablesContatore(){
        citta = getIntent().getStringExtra("citta");
        indirizzo = getIntent().getStringExtra("indirizzo");
        latitudine = getIntent().getDoubleExtra("latitudine",0);
        longitudine = getIntent().getDoubleExtra("longitudine",0);

        ldnContatore = getIntent().getStringExtra("ldn");
        numeroSerialeRadio = getIntent().getStringExtra("numero_seriale_radio");

    }

    public void putVariables(Intent intent){

        intent.putExtra("citta",citta);
        intent.putExtra("indirizzo",indirizzo);
        intent.putExtra("latitudine",latitudine);
        intent.putExtra("longitudine",longitudine);

        // Prima etichetta
        intent.putExtra("ldn", ldnContatore);
        intent.putExtra("numero_seriale_radio",numeroSerialeRadio);

        // Seconda etichetta
        intent.putExtra("cognome",cognome);
        intent.putExtra("nome",nome);
        intent.putExtra("numero_utenza",numeroUtenza);
        intent.putExtra("numero_contratto",numeroContratto);
        intent.putExtra("indirizzo_utenza",indirizzoUtenza);
        intent.putExtra("numero_civico",numeroCivico);

    }

    @Override
    public void onResume() {
        super.onResume();

        //total fullscreen
        getWindow().getDecorView().setSystemUiVisibility(SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
                SYSTEM_UI_FLAG_FULLSCREEN | SYSTEM_UI_FLAG_HIDE_NAVIGATION   |
                SYSTEM_UI_FLAG_LAYOUT_STABLE | SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        CheckPermission();
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();          // Start camera on resume
    }


    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();           // Stop camera on pause
    }



    @Override
    public void handleResult(Result rawResult) {

        // Salvo la lettura in un array usando i ";" per separare le stringhe
        String etichetta[] = rawResult.getText().split(";");

        // cognome e nome sono sempre presenti
        cognome = etichetta[0];
        nome = etichetta[1];

        // questi altri dati possono anche non essere presenti
        try {
            numeroUtenza = etichetta[2];
        }catch (IndexOutOfBoundsException e){
            e.printStackTrace();
            numeroUtenza="";
        };
        try {
            numeroContratto = etichetta[3];
        }catch (IndexOutOfBoundsException e){
            e.printStackTrace();
            numeroContratto="";
        };
        try {
            indirizzoUtenza = etichetta[4];
        }catch (IndexOutOfBoundsException e){
            e.printStackTrace();
            indirizzoUtenza="";
        };
        try {
            numeroCivico = etichetta[5];
        }catch (IndexOutOfBoundsException e){
            e.printStackTrace();
            numeroCivico="";
        };

        Intent intent = new Intent(getApplicationContext(), InsertMatrContActivity.class);
        putVariables(intent);

        startActivity(intent);
        finish();


        // TEST FATTI OK

        Log.d("TEST_cognome", cognome);
        Log.d("TEST_nome", nome);
        Log.d("TEST_numeroUtenza", numeroUtenza);
        Log.d("TEST_numeroContratto", numeroContratto);
        Log.d("TEST_indirizzoUtente", indirizzoUtenza);
        Log.d("TEST_numeroCivico", numeroCivico);

        Log.d("TEST_LDN", ldnContatore);


        //ripropone all'utente lo scan
        mScannerView.resumeCameraPreview(this);


    }


}
