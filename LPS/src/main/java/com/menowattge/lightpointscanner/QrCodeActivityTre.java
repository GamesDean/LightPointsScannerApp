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

    public double qrlongitudine,qrlatitudine;
    public String qrCodeData,name,qrCitta,qrIndirizzo,firstChar,secondAndThirdChar,fourthChar,potenza,identificativo;
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
        //getVariables();

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
            //TODO ok DEBUG DECOMMENTARE
            //Intent intent = new Intent(getApplicationContext(),SendDataActivity.class);
            //passVariables(intent);
            //startActivity(intent);
            Toast.makeText(getApplicationContext(),"id : "+identificativo,Toast.LENGTH_LONG).show();
        }


        //ripropone all'utente lo scan
        mScannerView.resumeCameraPreview(this);

    }

    public void passVariables(Intent intent){

        intent.putExtra("qrCitta_",qrCitta);
        intent.putExtra("qrIndirizzo_",qrIndirizzo);
        intent.putExtra("qrLatitudine_",qrlatitudine);
        intent.putExtra("qrLongitudine_",qrlongitudine);
        // etichetta RLU
        intent.putExtra("qrCode_", qrCodeData); // indirizzo radio D735...
        intent.putExtra("name", name); // 15A

        // etichetta Meridio
        intent.putExtra("modello",firstChar);
        intent.putExtra("corrente",secondAndThirdChar);
        intent.putExtra("dimensione",fourthChar);
        intent.putExtra("potenza",potenza);

        //etichetta Palo
        intent.putExtra("id_palo",identificativo);
    }


    public void getVariables(){

        // RLU scan
        qrCodeData = getIntent().getStringExtra("qrCode_"); // indirizzo radio D735...
        name = getIntent().getStringExtra("name");

        // coordinate
        qrIndirizzo = getIntent().getStringExtra("qrIndirizzo_");
        qrlatitudine = getIntent().getDoubleExtra("qrLatitudine_",0);
        qrlongitudine = getIntent().getDoubleExtra("qrLongitudine_",0);
        qrCitta = getIntent().getStringExtra("qrCitta_");

        // from etichetta device
        firstChar = getIntent().getStringExtra("modello");
        secondAndThirdChar = getIntent().getStringExtra("corrente");
        fourthChar = getIntent().getStringExtra("dimensione");
        potenza = getIntent().getStringExtra("potenza");

    }
}