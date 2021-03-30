package com.menowattge.lightpointscanner;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import static android.view.View.SYSTEM_UI_FLAG_FULLSCREEN;
import static android.view.View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
import static android.view.View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
import static android.view.View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
import static android.view.View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
import static android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
import static com.menowattge.lightpointscanner.SendDataActivity.valoreCorrente;

/**
 *  Permette all'utente di selezionare la potenza del Meridio. Passa poi questi dati a SendDataActivity
 */
public class ManualValueActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    public double qrlongitudine,qrlatitudine;
    public String qrCodeData,name,qrCitta,qrIndirizzo,firstChar,secondAndThirdChar,fourthChar,potenza,identificativo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_value);

        // total fullscreen
        getWindow().getDecorView().setSystemUiVisibility(SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
                SYSTEM_UI_FLAG_FULLSCREEN | SYSTEM_UI_FLAG_HIDE_NAVIGATION   |
                SYSTEM_UI_FLAG_LAYOUT_STABLE | SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        // TODO decommentare ok
        //getVariables();

       // final Spinner spinner = (Spinner) findViewById(R.id.spinner_power);
        final EditText editText = findViewById(R.id.editText);
        editText.setSelection(0);
        Button button_ok = findViewById(R.id.button_ok);


        final Intent intent = new Intent(getApplicationContext(), SendDataActivity.class);


        button_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                identificativo = editText.getText().toString();

                if(identificativo==""|| identificativo.isEmpty()){
                    Toast.makeText(ManualValueActivity.this,"Inserire ID palo",Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(ManualValueActivity.this,"id : "+identificativo,Toast.LENGTH_LONG).show();
                    // TODO decommentare ok
                    //passVariables(intent);
                    //startActivity(intent);
                    //finish();
                }


            }
        });


    }

    @Override
    protected void onResume(){
        super.onResume();

        //total fullscreen
        getWindow().getDecorView().setSystemUiVisibility(SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
                SYSTEM_UI_FLAG_FULLSCREEN | SYSTEM_UI_FLAG_HIDE_NAVIGATION   |
                SYSTEM_UI_FLAG_LAYOUT_STABLE | SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

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
