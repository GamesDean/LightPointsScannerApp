package com.menowattge.lightpointscanner;

/**
 *
 *  Classe per effettuare la scansione del qrcode
 */

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

//import es.dmoral.toasty.Toasty;


public class QrCodeActivity extends AppCompatActivity  implements ZXingScannerView.ResultHandler {


    private ZXingScannerView mScannerView;

    //dati provenienti dall'activity GetLatLong e destinati a PowerActivity
    public String qrCitta;
    public String qrIndirizzo;
    public double qrlatitudine;
    public double qrlongitudine;

    // private String menowattCode = "User : Operator\n" + "Pass :  Ledgear";

    // TODO trovare un discriminante per far scannerizzare soltanto i nostri qrcode. fare prova con pdf147
    private String menowattCode = "d735";



    //PERMESSI CAMERA
    public void CheckPermission() {

        if (this.checkSelfPermission(android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
            requestPermissions(new String[]{android.Manifest.permission.CAMERA}, 1);

    }


    @Override
    protected void onCreate(Bundle state) {

        super.onCreate(state);

        CheckPermission();
        // prendo dall'activity GetLatLong dei dati per poi passarli a SendDataActivity
        qrCitta = getIntent().getStringExtra("citta");
        qrIndirizzo = getIntent().getStringExtra("indirizzo");
        qrlatitudine = getIntent().getDoubleExtra("latitudine",0);
        qrlongitudine = getIntent().getDoubleExtra("longitudine",0);

        // Programmatically initialize the scanner view
        mScannerView = new ZXingScannerView(this);
        setContentView(mScannerView);
        mScannerView.startCamera();

    }


    @Override
    public void onResume() {
        super.onResume();

        CheckPermission();
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();          // Start camera on resume
    }


    @Override
    public void onPause() {

        super.onPause();
        mScannerView.stopCamera();           // Stop camera on pause
    }



    //Gestisco camera e qrcode

    @Override
    public void handleResult(Result rawResult) {
        // Qui è possibile gestire il risultato
        Log.v("risultato", rawResult.getText().substring(0,16));
        //Toast.makeText(getApplicationContext(), rawResult.getText().substring(0,15), Toast.LENGTH_SHORT).show();

        Log.v("risultato_qrcodeformat", rawResult.getBarcodeFormat().toString()); // Prints the scan format (qrcode, pdf417 etc.)
        //prelevo solo l'inizio dell'indirizzo radio ovvero d735
        String d735 = rawResult.getText().substring(0,4);
        Log.i("d735", d735);

        // controllo che sia un nostro qrcode controllando  D735
        if (d735.equals(menowattCode)) {

            //Toast.makeText(getApplicationContext(), "scansione ok", Toast.LENGTH_SHORT).show();

           // Intent intent = new Intent(getApplicationContext(), SendDataActivity.class);
            Intent intent = new Intent(getApplicationContext(), PowerActivity.class);
            // prelevo il valore dal qrcode letto
            String qrCodeData = rawResult.getText().substring(0,16); // indirizzo radio D735...
            String name       = rawResult.getText().substring(17,21);  // es : 30A

            // invio a PowerActivity il valore del qrcode letto ed i valori dei dati acquisiti in GetLatLong
            intent.putExtra("qrCode_", qrCodeData);
            intent.putExtra("name", name);
            intent.putExtra("qrCitta_",qrCitta);
            intent.putExtra("qrIndirizzo_",qrIndirizzo);
            intent.putExtra("qrLatitudine_",qrlatitudine);
            intent.putExtra("qrLongitudine_",qrlongitudine);

            startActivity(intent);



        } else {
            Toast.makeText(getApplicationContext(),"qr code errato",Toast.LENGTH_SHORT).show();        }

            // If you would like to resume scanning, call this method below:
            mScannerView.resumeCameraPreview(this);
        }
    }






