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
        // TODO PER DEBUG SALTO DALLO SPLASH A QUESTA CLASSE QUINDI COMMENTO  : RIPRISTINARE
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

    /**
     * Associa ad una lettera passata come argomento un numero corrispondente alla posizione di quest'ultima nell'alfabeto
     * @param letter una lettera dell'alfabeto
     * @return number
     */
    public int fromLetterToNumber(String letter){

        int number=1;
        String [] lettereAlfabeto = {"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};

        for (int i=0;i<lettereAlfabeto.length;i++){
            if (letter.equals(lettereAlfabeto[i])){
                number+=i;
                Log.d("KKK",String.valueOf(number));
            }
        }
        return number;
    }

    @Override
    public void handleResult(Result rawResult) {

        // PER TEST DOPO LO SPLASH SALTO DIRETTAMENTE QUI

        // TODO PRELEVARE I PRIMI 4 CARATTERI :
        //      TODO * PRIMO CARATTERE -> TIPO : M oppure G oppure H [ MERIDIO GIANO HIPERION ]
        //      TODO * SECONDO E TERZO CARATTERE : "id_configurazione" [ex corrente]
        //      TODO * QUARTO CARATTERE -> MODELLO : E,S,J,M,L,X,U,F [ ENTRY_LEVEL, SMALL, JUNIOR, MEDIUM ,LARGE, EXTRA-LARGE,UP,FULL]

        // TODO PRELEVARE ANCHE IL *SERIALE (es : 10G190800001) ED IL *CODICE(es: H70SAHXS2090C040P01) PER INTERO
        // TODO che si chiameranno "seriale_apparecchio" e "codice_apparecchio" rispettivamente [al posto di POZZETTO e PORTELLA]

        /*

            H13M39A4103050V00

            • H: tipo apparecchio (in questo caso Hiperion)
            • 13: id configurazione (in questo caso 13);
            • M: Modello apparecchio (in questo caso Medium)
            • 39: Potenza (in questo caso 39W) – puoi prenderla da qui o direttamente dal campo relativo nel qr code
            • V: profilo in ordine alfabetico quindi A=1, B=2, C=3……….V=20
         */

        // Salvo la lettura in un array usando i ":" per separare le stringhe
        String etichetta[] = rawResult.getText().split(":");

        String serialeApparecchio = etichetta[1].substring(1,13);  // 10G190800001
        String codiceApparecchio = etichetta[2].substring(1,19);  // H13M39A4A03050VP01 18 caratteri

        //prelevo i primi quattro caratteri
        String firstFourChars = etichetta[2].substring(1,5); //H70S

        String tipo = firstFourChars.substring(0,1); // M,G,H
        String idConfigurazione = firstFourChars.substring(1,3); // id_configurazione ex corrente
        String potenza = etichetta[3].trim(); // 30W
        String modello = firstFourChars.substring(3,4); // E,S...

        String lettera = etichetta[2].substring(15,16); // V
        String profilo = Integer.toString(fromLetterToNumber(lettera));

        // TEST EFFETTUATI OK
        Log.d("YYY", serialeApparecchio);
        Log.d("YYY", codiceApparecchio);
        Log.d("YYY", firstFourChars);
        Log.d("YYY", idConfigurazione);
        Log.d("YYY", tipo);
        Log.d("YYY", potenza);
        Log.d("YYY", modello);
        Log.d("YYY", lettera);
        Log.d("YYY", profilo);

        //Controllo che inizi per M ,G , H : meglio di niente
        if (tipo.equals("M") || tipo.equals("G") || tipo.equals("H")) {

            Intent intent = new Intent(getApplicationContext(), QrCodeActivityQuestion.class);
            // invio a QrCodeActivityQuestion il valore del qrcode letto ora ed i valori dei dati acquisiti dal primo QrCodeActivity
            // che a sua volta aveva le coordinate.
/*
            TODO RIPRISTINARE
            // coordinate
            intent.putExtra("qrCitta_",qrCitta);
            intent.putExtra("qrIndirizzo_",qrIndirizzo);
            intent.putExtra("qrLatitudine_",qrlatitudine);
            intent.putExtra("qrLongitudine_",qrlongitudine);
            // etichetta RLU
            intent.putExtra("qrCode_", qrCodeData); // indirizzo radio D735...
            intent.putExtra("name", name); // 15A
    */
            // Seconda etichetta
            intent.putExtra("seriale_apparecchio",serialeApparecchio);
            intent.putExtra("codice_apparecchio",codiceApparecchio);

            intent.putExtra("tipo",tipo);
            intent.putExtra("id_configurazione",idConfigurazione);
            intent.putExtra("modello",modello);
            intent.putExtra("potenza",potenza);
            intent.putExtra("profilo",profilo);


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
