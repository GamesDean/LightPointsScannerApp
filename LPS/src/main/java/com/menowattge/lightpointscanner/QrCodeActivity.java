package com.menowattge.lightpointscanner;

/**
 *  Classe per effettuare la scansione del qrcode
 */

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static android.view.View.SYSTEM_UI_FLAG_FULLSCREEN;
import static android.view.View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
import static android.view.View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
import static android.view.View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
import static android.view.View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
import static android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE;


public class QrCodeActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {


    private ZXingScannerView mScannerView;
    // prelevate dalla precedente activity
    public String citta;
    public String indirizzo;
    public double latitudine;
    public double longitudine;

    // da inviare alla prossima activity
    public String indirizzoRadio;
    public String nomePuntoLuce;

    // solo gli ID che iniziano con D735 (in minuscolo perchè nel qr è così) sono accettati in quanto Menowatt
    private String menowattCode = "d735";
    // contatori acqua
    private String menowattCodeMad = "MAD0"; // TODO vedere se lower o uppercase


    //PERMESSI CAMERA
    @SuppressLint("NewApi")
    public void CheckPermission() {

        if (this.checkSelfPermission(android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
            requestPermissions(new String[]{android.Manifest.permission.CAMERA}, 1);

    }


    @Override
    protected void onCreate(Bundle state) {

        super.onCreate(state);

        // total fullscreen
        getWindow().getDecorView().setSystemUiVisibility(SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
                SYSTEM_UI_FLAG_FULLSCREEN | SYSTEM_UI_FLAG_HIDE_NAVIGATION   |
                SYSTEM_UI_FLAG_LAYOUT_STABLE | SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        CheckPermission();
        // prendo dall'activity SelectActivity dei dati per poi passarli alla prossima activity
        getVariables();

        // Programmatically initialize the scanner view
        mScannerView = new ZXingScannerView(this);
        setContentView(mScannerView);
        mScannerView.startCamera();

    }

    public void getVariables(){
        citta = getIntent().getStringExtra("citta");
        indirizzo = getIntent().getStringExtra("indirizzo");
        latitudine = getIntent().getDoubleExtra("latitudine",0);
        longitudine = getIntent().getDoubleExtra("longitudine",0);
    }

    public void putVariables(Intent intent){

        intent.putExtra("indirizzo_radio", indirizzoRadio); // indirizzo radio D735...
        intent.putExtra("nome_punto_luce", nomePuntoLuce);

        intent.putExtra("citta",citta);
        intent.putExtra("indirizzo",indirizzo);
        intent.putExtra("latitudine",latitudine);
        intent.putExtra("longitudine",longitudine);
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


    /**
     * Preleva i dati dalla camera e quindi dal qrcode scansionato,se tutto ok avvia PowerActvity
     * @param rawResult risultato della scansione
     */
    @Override
    public void handleResult(Result rawResult) {
        // Qui è possibile gestire il risultato
        Log.v("risultato", rawResult.getText().substring(0,16));
        Log.v("risultato_qrcodeformat", rawResult.getBarcodeFormat().toString()); // Prints the scan format (qrcode, pdf417 etc.)
        //prelevo solo l'inizio dell'indirizzo radio ovvero d735
        String d735_MAD = rawResult.getText().substring(0,4);
        Log.i("d735", d735_MAD);

        // controllo che sia un nostro qrcode controllando  D735 per RLU, MAD0 per contatori acqua
        if (d735_MAD.equals(menowattCode)) {

             indirizzoRadio = rawResult.getText().substring(0,16); // indirizzo radio D735...
            // TODO DOPO gestire BUG dei tre caratteri es : 5A prende anche un terzo ma non dovrebbe, 15A è ok
             nomePuntoLuce  = rawResult.getText().substring(17,21);  // es : 30A

            Intent intent = new Intent(getApplicationContext(), QrCodeActivityDue.class);
            // invio a QrCodeActivityDue il valore del qrcode letto ed i valori dei dati acquisiti in precedenza
            putVariables(intent);

            Toast.makeText(getApplicationContext(),"OK, SCANNERIZZA LA SECONDA ETICHETTA",Toast.LENGTH_LONG).show();

            startActivity(intent);
            finish();


        }
        else if (d735_MAD.equals(menowattCodeMad)){

            Log.d("MAD","acqua");
            // TODO creare LDN a partire dal codice scansionato
            // MAD0 07 87 19 70 01 04
            //MAD0 à 2434 poi vai al contrario 04 01 70 19 87 07
            // String qrCodeData = rawResult.getText().substring(0,16); fare prove per vedere cosa prendere
            // e come invertire le coppie di numeri

            // ottenuto LDN lo incapsulo con citta,lat,lon ed indirizzo e lo invio a SendDataActivity
            // che fara un UPDATE dato che LDN e chiavi saranno gia inserite.


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






