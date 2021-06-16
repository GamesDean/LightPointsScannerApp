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

public class QrCodeActivityTre extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    public double longitudine,latitudine;
    public String indirizzo,name,nomePuntoLuce,indirizzoRadio,serialeApparecchio,citta,
            codiceApparecchio,tipo,potenza,identificativo,idConfigurazione,modello,profilo;
    private ZXingScannerView mScannerView;


    //PERMESSI CAMERA
    @SuppressLint("NewApi")
    public void CheckPermission() {

        if (this.checkSelfPermission(android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
            requestPermissions(new String[]{android.Manifest.permission.CAMERA}, 1);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_code_tre);

        // total fullscreen
        getWindow().getDecorView().setSystemUiVisibility(SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
                SYSTEM_UI_FLAG_FULLSCREEN | SYSTEM_UI_FLAG_HIDE_NAVIGATION   |
                SYSTEM_UI_FLAG_LAYOUT_STABLE | SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        CheckPermission();

        // TODO decommentare
        getVariables();

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
    public void handleResult(Result result) {

        identificativo = result.getText();
        Log.d("identificativo",identificativo);

        if(identificativo.length()==8){
            Intent intent = new Intent(getApplicationContext(),SendDataActivity.class);
            putVariables(intent);
            startActivity(intent);
            //Toast.makeText(getApplicationContext(),"id : "+identificativo,Toast.LENGTH_LONG).show();
        }else{

            Toast.makeText(getApplicationContext(),"QrCode errato",Toast.LENGTH_LONG).show();

        }


        //ripropone all'utente lo scan
        mScannerView.resumeCameraPreview(this);

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

        //TODO DECOMMENTARE
        citta = getIntent().getStringExtra("citta");
        indirizzo = getIntent().getStringExtra("indirizzo");
        latitudine = getIntent().getDoubleExtra("latitudine",0);
        longitudine = getIntent().getDoubleExtra("longitudine",0);

        // prima etichetta
        indirizzoRadio = getIntent().getStringExtra("indirizzo_radio");
        nomePuntoLuce = getIntent().getStringExtra("nome_punto_luce");

        Log.d("TEST_QrCodeActivity3", citta);
        Log.d("TEST_QrCodeActivity3", indirizzo);
        Log.d("TEST_QrCodeActivity3", String.valueOf(latitudine));
        Log.d("TEST_QrCodeActivity3", String.valueOf(longitudine));
        Log.d("TEST_QrCodeActivity3", indirizzoRadio);
        Log.d("TEST_QrCodeActivity3", nomePuntoLuce);


        // seconda etichetta
        serialeApparecchio = getIntent().getStringExtra("seriale_apparecchio");
        codiceApparecchio =  getIntent().getStringExtra("codice_apparecchio");
        tipo = getIntent().getStringExtra("tipo");
        idConfigurazione = getIntent().getStringExtra("id_configurazione");
        modello = getIntent().getStringExtra("modello");
        potenza = getIntent().getStringExtra("potenza");
        profilo = getIntent().getStringExtra("profilo");


        Log.d("TEST_QrCodeActivity3", serialeApparecchio);
        Log.d("TEST_QrCodeActivity3", codiceApparecchio);
        Log.d("TEST_QrCodeActivity3", idConfigurazione);
        Log.d("TEST_QrCodeActivity3", tipo);
        Log.d("TEST_QrCodeActivity3", potenza);
        Log.d("TEST_QrCodeActivity3", modello);
        Log.d("TEST_QrCodeActivity3", profilo);

    }
}