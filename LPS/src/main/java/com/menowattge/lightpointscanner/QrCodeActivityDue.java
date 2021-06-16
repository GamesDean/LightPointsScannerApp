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

    public double latitudine,longitudine;
    public String indirizzoRadio,nomePuntoLuce,citta,indirizzo,
            serialeApparecchio,codiceApparecchio,tipo,idConfigurazione,modello,potenza,profilo,power;


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


        // TODO PER DEBUG SALTO DALLO SPLASH A QUESTA CLASSE QUINDI PER ORA COMMENTO  : RIPRISTINARE
        // prelevo i dati da QrCodeActivity quindi coordinate e scansione etichetta RLU
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

        indirizzoRadio = getIntent().getStringExtra("indirizzo_radio");
        nomePuntoLuce = getIntent().getStringExtra("nome_punto_luce");
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

        Log.d("TEST_QrCodeActivity2", citta);
        Log.d("TEST_QrCodeActivity2", indirizzo);
        Log.d("TEST_QrCodeActivity2", String.valueOf(latitudine));
        Log.d("TEST_QrCodeActivity2", String.valueOf(longitudine));
        Log.d("TEST_QrCodeActivity2", indirizzoRadio);
        Log.d("TEST_QrCodeActivity2", nomePuntoLuce);

        // Seconda etichetta
        intent.putExtra("seriale_apparecchio",serialeApparecchio);
        intent.putExtra("codice_apparecchio",codiceApparecchio);
        intent.putExtra("tipo",tipo);
        intent.putExtra("id_configurazione",idConfigurazione);
        intent.putExtra("modello",modello);
        intent.putExtra("potenza",power);
        intent.putExtra("profilo",profilo);
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
        String [] lettereAlfabeto = {"A","B","C","D","E","F","G","H","I","L","M","N","O","P","Q","R","S","T","U","V"};

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

        String check = rawResult.getText().substring(0,3);
        Log.d("CHECK",check);

        if (!check.equals("S/N")){
            Toast.makeText(getApplicationContext(),"Qr code errato",Toast.LENGTH_LONG).show();

        }else{

            // Salvo la lettura in un array usando i ":" per separare le stringhe
            String etichetta[] = rawResult.getText().split(":");

            serialeApparecchio = etichetta[1].substring(1,13);  // 10G190800001
            codiceApparecchio = etichetta[2].substring(1,19);  // H13M39A4A03050VP01 18 caratteri

            //prelevo i primi sei caratteri
            String firstSixChars = etichetta[2].substring(1,7); //H70S81

            tipo = firstSixChars.substring(0,1); // M,G,H
            idConfigurazione = firstSixChars.substring(1,3); // id_configurazione ex corrente
            potenza = firstSixChars.substring(4,6); // 30W
            modello = firstSixChars.substring(3,4); // E,S...

            String lettera = etichetta[2].substring(15,16); // V
            profilo = Integer.toString(fromLetterToNumber(lettera)); // 20


            // NB la potenza è espressa in hex eseguo quindi la confersione
            int num = Integer.parseInt(potenza,16);
            power = String.valueOf(num);

            // TEST EFFETTUATI OK
            Log.d("TEST_SERIALE : ", serialeApparecchio);
            Log.d("TEST_CODICE : ", codiceApparecchio);
            // Log.d("TEST_QrCodeActivity2", firstFourChars);
            Log.d("TEST_ID : ", idConfigurazione);
            Log.d("TEST_TIPO : ", tipo);
            Log.d("TEST_POTENZA", power);
            Log.d("TEST_MODELLO : ", modello);
            //Log.d("TEST_QrCodeActivity2", lettera);
            Log.d("TEST_PROFILO : ", profilo);



            Intent intent = new Intent(getApplicationContext(), QrCodeActivityQuestion.class);
            // invio a QrCodeActivityQuestion i valori cumulativi delle letture effettuate dai 2 qrcode
            putVariables(intent);

            startActivity(intent);
            finish();

        }


        //ripropone all'utente lo scan
        mScannerView.resumeCameraPreview(this);


    }


}
