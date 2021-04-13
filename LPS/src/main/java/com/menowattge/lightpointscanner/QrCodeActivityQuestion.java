package com.menowattge.lightpointscanner;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;

import static android.view.View.SYSTEM_UI_FLAG_FULLSCREEN;
import static android.view.View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
import static android.view.View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
import static android.view.View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
import static android.view.View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
import static android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE;

public class QrCodeActivityQuestion extends AppCompatActivity {


    public double qrlongitudine,qrlatitudine;
    public String qrCodeData,name,qrCitta,qrIndirizzo,tipo,idConfigurazione,modello,profilo,potenza;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_code_question);
        // total fullscreen
        getWindow().getDecorView().setSystemUiVisibility(SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
                SYSTEM_UI_FLAG_FULLSCREEN | SYSTEM_UI_FLAG_HIDE_NAVIGATION   |
                SYSTEM_UI_FLAG_LAYOUT_STABLE | SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        // TODO decommentare OK
        //getVariables();

        ExtendedFloatingActionButton fabNo = findViewById(R.id.fab_no);
        fabNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Ok, nessuna etichetta da scansionare", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                // passo le variabili alla classe che le invierà al server
                //TODO decommentare ok
                //Intent intent = new Intent(getApplicationContext(), SendDataActivity.class);
                //passVariables(intent);
                //startActivity(intent);
            }
        });

        ExtendedFloatingActionButton fabManual = findViewById(R.id.fab_manual);
        fabManual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Inserire manualmente il codice", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                // passo le variabili alla classe che consente l'inserimento manuale del codice del palo
                // TODO cambiare classe con InsertCodePalo.class poi DECOMMENTARE ok
                Intent intent = new Intent(getApplicationContext(), ManualValueActivity.class);
               // passVariables(intent);
                startActivity(intent);
            }
        });

        ExtendedFloatingActionButton fabSi = findViewById(R.id.fab_si);
        fabSi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Scansionare l'etichetta", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                // passo le variabili alla classe che scansiona l'etichetta del palo
                Intent intent = new Intent(getApplicationContext(), QrCodeActivityTre.class);
                // TODO decommentare ok
                //passVariables(intent);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        //total fullscreen
        getWindow().getDecorView().setSystemUiVisibility(SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
                SYSTEM_UI_FLAG_FULLSCREEN | SYSTEM_UI_FLAG_HIDE_NAVIGATION   |
                SYSTEM_UI_FLAG_LAYOUT_STABLE | SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

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
        intent.putExtra("tipo",tipo);
        intent.putExtra("id_configurazione",idConfigurazione);
        intent.putExtra("modello",modello);
        intent.putExtra("potenza",potenza);
        intent.putExtra("profilo",profilo);
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
        tipo = getIntent().getStringExtra("tipo");
        idConfigurazione = getIntent().getStringExtra("idConfigurazione");
        modello = getIntent().getStringExtra("modello");
        profilo = getIntent().getStringExtra("profilo");
        potenza = getIntent().getStringExtra("potenza");

    }
}