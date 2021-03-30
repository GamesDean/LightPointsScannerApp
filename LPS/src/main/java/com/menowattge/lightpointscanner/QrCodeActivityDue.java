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

public class QrCodeActivityDue extends AppCompatActivity implements ZXingScannerView.ResultHandler {


    private ZXingScannerView mScannerView;

    public double qrlongitudine,qrlatitudine;
    public String qrCodeData,name,qrCitta,qrIndirizzo;



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

        /*
        // TODO PER DEBUG SALTO DALLO SPLASH A QUESTA CLASSE QUINDI COMMENTO
        //prelevo i dati della scansione del primo QRCODE dell' RLU
        qrCodeData = getIntent().getStringExtra("qrCode_"); // indirizzo radio D735...
        name = getIntent().getStringExtra("name");

        qrIndirizzo = getIntent().getStringExtra("qrIndirizzo_");
        qrlatitudine = getIntent().getDoubleExtra("qrLatitudine_",0);
        qrlongitudine = getIntent().getDoubleExtra("qrLongitudine_",0);
        qrCitta = getIntent().getStringExtra("qrCitta_");


         */
        // Programmatically initialize the scanner view
        mScannerView = new ZXingScannerView(this);
        setContentView(mScannerView);
        mScannerView.startCamera();




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


        // PER TEST DOPO LO SPLASH SALTO DIRETTAMENTE QUI

        // TODO PRELEVARE I PRIMI 4 CARATTERI :
        //      TODO * PRIMO CARATTERE -> MODELLO : M oppure G oppure H [ MERIDIO GIANO HIPERION ]
        //      TODO * SECONDO E TERZO CARATTERE : Corrente Massima  DA DIVIDERE PER 100
        //      TODO * QUARTO CARATTERE -> DIMENSIONE : E,S,J,M,L,X,U,F [ ENTRY_LEVEL, SMALL, JUNIOR, MEDIUM ,LARGE, EXTRA-LARGE,UP,FULL]


        // Qui è possibile gestire il risultato

        // Salvo la lettura in un array usando i ":" per separare le stringhe
        String etichetta[] = rawResult.getText().split(":");

        //prelevo solo i primi 4 caratteri, gli altri non mi servono
        String firstFourChars = etichetta[2].substring(1,5); //H70S
        String potenza = etichetta[3]; // 30W
        String firstChar = firstFourChars.substring(0,1); // M,G,H
        String secondAndThirdChar = firstFourChars.substring(1,3); // corrente da dividere per 100
        String fourthChar = firstFourChars.substring(3,4); // E,S...

        Log.d("firstFourChars", firstFourChars);

        //Controllo che inizi per M ,G , H : meglio di niente
        if (firstChar.equals("M") || firstChar.equals("G") || firstChar.equals("H")) {

           // Intent intent = new Intent(getApplicationContext(), SendDataActivity.class); // TODO QrCodeActivityQuestion

            Intent intent = new Intent(getApplicationContext(), QrCodeActivityQuestion.class);
            // invio a QrCodeActivityQuestion il valore del qrcode letto ora ed i valori dei dati acquisiti dal primo QrCodeActivity
            // che a sua volta aveva le coordinate.
/*
            // coordinate
            intent.putExtra("qrCitta_",qrCitta);
            intent.putExtra("qrIndirizzo_",qrIndirizzo);
            intent.putExtra("qrLatitudine_",qrlatitudine);
            intent.putExtra("qrLongitudine_",qrlongitudine);
            // etichetta RLU
            intent.putExtra("qrCode_", qrCodeData); // indirizzo radio D735...
            intent.putExtra("name", name); // 15A
    */
            // etichetta Meridio
            intent.putExtra("modello",firstChar);
            intent.putExtra("corrente",secondAndThirdChar);
            intent.putExtra("dimensione",fourthChar);
            intent.putExtra("potenza",potenza);


            startActivity(intent);
            finish();


        }

        else {
            //         Toast.makeText(getApplicationContext(),"Qr code errato",Toast.LENGTH_LONG).show();
            Toast.makeText(getApplicationContext(),"testo : "+rawResult.getText(),Toast.LENGTH_LONG).show();
            Toast.makeText(getApplicationContext(),"formato : "+rawResult.getBarcodeFormat(),Toast.LENGTH_LONG).show();

        }
        //ripropone all'utente lo scan
        mScannerView.resumeCameraPreview(this);


    }


    }
