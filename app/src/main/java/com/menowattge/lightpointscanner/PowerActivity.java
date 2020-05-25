package com.menowattge.lightpointscanner;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;

import static android.view.View.SYSTEM_UI_FLAG_FULLSCREEN;
import static android.view.View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
import static android.view.View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
import static android.view.View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
import static android.view.View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
import static android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE;

/**
 *  Permette all'utente di selezionare la potenza del Meridio. Passa poi questi dati a SendDataActivity
 */
public class PowerActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    public double qrlongitudine,qrlatitudine;
    public String qrCodeData,name,qrCitta,qrIndirizzo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_power);

        // total fullscreen
        getWindow().getDecorView().setSystemUiVisibility(SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
                SYSTEM_UI_FLAG_FULLSCREEN | SYSTEM_UI_FLAG_HIDE_NAVIGATION   |
                SYSTEM_UI_FLAG_LAYOUT_STABLE | SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

       // final Spinner spinner = (Spinner) findViewById(R.id.spinner_power);
        final EditText editText = findViewById(R.id.editText);
        editText.setSelection(0);
        Button button_ok = findViewById(R.id.button_ok);

        // Create an ArrayAdapter using the string array and a default spinner layout
      //  ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.potenza, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
      //  adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
      //  spinner.setAdapter(adapter);

      //  spinner.setOnItemSelectedListener(this);


        final Intent intent = new Intent(getApplicationContext(), SendDataActivity.class);

        //prelevo i dati della scansione del qrcode
        qrCodeData = getIntent().getStringExtra("qrCode_");
        name = getIntent().getStringExtra("name");
        qrIndirizzo = getIntent().getStringExtra("qrIndirizzo_");
        qrlatitudine = getIntent().getDoubleExtra("qrLatitudine_",0);
        qrlongitudine = getIntent().getDoubleExtra("qrLongitudine_",0);
        qrCitta = getIntent().getStringExtra("qrCitta_");


        intent.putExtra("qrCode", qrCodeData);
        intent.putExtra("name_", name);
        intent.putExtra("qrCitta",qrCitta);
        intent.putExtra("qrIndirizzo",qrIndirizzo);
        intent.putExtra("qrLatitudine",qrlatitudine);
        intent.putExtra("qrLongitudine",qrlongitudine);


        button_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //final String valoreCorrente = spinner.getSelectedItem().toString();
                final String valoreCorrente = editText.getText().toString();

                intent.putExtra("valore_corrente", valoreCorrente);
                startActivity(intent);
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


}
