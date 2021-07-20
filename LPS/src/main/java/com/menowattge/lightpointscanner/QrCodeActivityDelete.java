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

public class QrCodeActivityDelete  extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    private ZXingScannerView mScannerView;
    // solo gli ID che iniziano con D735 (in minuscolo perchè nel qr è così) sono accettati in quanto Menowatt
    private String menowattCode = "d735";
    // contatori acqua
    private String menowattCodeMad = "MAD0"; // TODO vedere se lower o uppercase

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_code_delete);

        // total fullscreen
        getWindow().getDecorView().setSystemUiVisibility(SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
                SYSTEM_UI_FLAG_FULLSCREEN | SYSTEM_UI_FLAG_HIDE_NAVIGATION   |
                SYSTEM_UI_FLAG_LAYOUT_STABLE | SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        CheckPermission();

        // Programmatically initialize the scanner view
        mScannerView = new ZXingScannerView(this);
        setContentView(mScannerView);
        mScannerView.startCamera();


    }



    //PERMESSI CAMERA
    @SuppressLint("NewApi")
    public void CheckPermission() {

        if (this.checkSelfPermission(android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
            requestPermissions(new String[]{android.Manifest.permission.CAMERA}, 1);

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

        //prelevo solo l'inizio dell'indirizzo radio ovvero d735
        String d735_MAD = rawResult.getText().substring(0,4);
        Log.d("d735", d735_MAD);

        // controllo che sia un nostro qrcode controllando  D735 per RLU, MAD0 per contatori acqua
        if (d735_MAD.equals(menowattCode)) {

            // prelevo il valore dal qrcode letto
            String qrCodeData = rawResult.getText().substring(0,16); // indirizzo radio D735...
            // TODO DOPO gestire BUG dei tre caratteri es : 5A prende anche un terzo ma non dovrebbe, 15A è ok
            String name       = rawResult.getText().substring(17,21);  // es : 30A
            Log.d("name", name);

            Intent intent = new Intent(getApplicationContext(), DeleteDeviceActivity.class);
            intent.putExtra("qrCode_delete", qrCodeData); // indirizzo radio D735...
            intent.putExtra("name_delete", name);


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

            String name = "CONTATORE ACQUA";
            Intent intent = new Intent(getApplicationContext(), DeleteDeviceActivity.class);
            intent.putExtra("qrCode_delete", ldnContatore); // indirizzo 2434
            intent.putExtra("name_delete", name);
            startActivity(intent);
            finish();


        }
        else {
            Toast.makeText(getApplicationContext(),"Qr code errato",Toast.LENGTH_LONG).show();

        }
        //ripropone all'utente lo scan
        mScannerView.resumeCameraPreview(this);
    }
}