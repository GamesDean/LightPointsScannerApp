package com.menowattge.lightpointscanner;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

public class SelectActivity extends AppCompatActivity {

    ImageButton buttonLampione;
    ImageButton buttonContatore;

    public String citta;
    public String indirizzo;
    public double latitudine;
    public double longitudine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        buttonLampione = findViewById(R.id.imageButtonLamp);
        buttonContatore= findViewById(R.id.imageButtonContatore);

        // prendo da GpsStatusFragment citta indirizzo e le coordinate
        getVariables();


        buttonLampione.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), QrCodeActivity.class);

                intent.putExtra("citta", citta);
                intent.putExtra("indirizzo", indirizzo);
                intent.putExtra("latitudine", latitudine);
                intent.putExtra("longitudine", longitudine);

                startActivity(intent);
            }
        });


        buttonContatore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // TODO QrCodeActivityWater (forse non necessario)
                Intent intent = new Intent(getApplicationContext(), QrCodeActivity.class);

               // inoltro i dati raccolti fin qui(coordinate citta indirizzo) alla prossima activity
               putVariables(intent);

               startActivity(intent);
            }
        });


    }

    /**
     * Prende da GPSTestActivity citta indirizzo e coordinate
     */

    public void getVariables(){
        citta = getIntent().getStringExtra("citta");
        indirizzo = getIntent().getStringExtra("indirizzo");
        latitudine = getIntent().getDoubleExtra("latitudine",0);
        longitudine = getIntent().getDoubleExtra("longitudine",0);
    }

    public void putVariables(Intent intent){

        intent.putExtra("citta", citta);
        intent.putExtra("indirizzo", indirizzo);
        intent.putExtra("latitudine", latitudine);
        intent.putExtra("longitudine", longitudine);
    }
}