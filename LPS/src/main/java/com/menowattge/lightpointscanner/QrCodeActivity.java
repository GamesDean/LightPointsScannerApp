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
    private String menowattCodeMad = "MAD0";

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

        Log.d("TEST_1", citta);
    }

    public void putVariables(Intent intent){

        intent.putExtra("indirizzo_radio", indirizzoRadio); // indirizzo radio D735...
        intent.putExtra("nome_punto_luce", nomePuntoLuce);

        intent.putExtra("citta",citta);
        intent.putExtra("indirizzo",indirizzo);
        intent.putExtra("latitudine",latitudine);
        intent.putExtra("longitudine",longitudine);
    }

    public void putVariablesContatore(Intent intent, String ldnContatore, String indirizzoContatore){

        intent.putExtra("ldn", ldnContatore);
        intent.putExtra("numero_seriale_radio",indirizzoContatore);

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
             nomePuntoLuce  = rawResult.getText().substring(17,21);  // es : 30A

            // se finisce con R vuol dire che il nome è compreso tra 0-9 es : 5D ed il 21 esimo char è la R di RLU che NON deve
            // esserci poiche verrebbe ad es : 5DR , dunque scalo di un char così da tralasciare la R ed ottenere un dato corretto
            if (nomePuntoLuce.endsWith("R")){
                nomePuntoLuce  = rawResult.getText().substring(17,20);
            }

            Intent intent = new Intent(getApplicationContext(), ShowEtichettApparecchio.class);
            // invio a QrCodeActivityDue il valore del qrcode letto ed i valori dei dati acquisiti in precedenza
            putVariables(intent);
            startActivity(intent);
            finish();


        }
        // CONTATORE ACQUA
        else if (d735_MAD.equals(menowattCodeMad)){

            // es: MAD0 078719700104
            // MAD0 -> 2434 poi vai al contrario 2434040170198707
            String indirizzoContatore = rawResult.getText().substring(0,16);
            String ldnContatore="2434";

            int k=indirizzoContatore.length(); // lunghezza indirizzo del contatore, 16
            // ciclo 6 volte perche devo prelevare sei coppie
            for(int i=6;i>0;i--){
                 ldnContatore += indirizzoContatore.substring(k-2,k); // 14,16 - 12,14, etc
                k-=2;// sottraggo due perche prelevo delle coppie
            }

            // inviare a classe per seconda etichetta
            // SendData  che fara un UPDATE dato che LDN e chiavi saranno gia inserite.
            Intent intent = new Intent(getApplicationContext(), QrCodeActivityContatore.class);
            // invio a QrCodeActivityDue il valore del qrcode letto ed i valori dei dati acquisiti in precedenza
            putVariablesContatore(intent,ldnContatore,indirizzoContatore);

            Toast.makeText(getApplicationContext(),"OK, SCANNERIZZA ETICHETTA UTENTE",Toast.LENGTH_LONG).show();

            startActivity(intent);
            finish();
            Log.d("LDN : ",ldnContatore);

        }
        else {
            Toast.makeText(getApplicationContext(),"Qr code errato",Toast.LENGTH_LONG).show();

        }
            //ripropone all'utente lo scan
            mScannerView.resumeCameraPreview(this);
        }
    }






