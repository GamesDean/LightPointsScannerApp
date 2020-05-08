package com.menowattge.lightpointscanner;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

public class PowerActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    public double qrlongitudine,qrlatitudine;
    public String qrCodeData,name,qrCitta,qrIndirizzo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_power);

       /* AlertDialog.Builder alertadd = new AlertDialog.Builder(PowerActivity.this);
        LayoutInflater factory = LayoutInflater.from(PowerActivity.this);
        final View view = factory.inflate(R.layout.dialog_power, null);
        alertadd.setView(view);
        alertadd.setTitle("Seleziona valore CAMPO1");
        alertadd.setMessage("Guarda l'immagine come esempio");
        alertadd.setNeutralButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dlg, int sumthin) {

            }
        });


        alertadd.show();
        alertadd.setCancelable(false);*/


        final Spinner spinner = (Spinner) findViewById(R.id.spinner_power);
        Button button_ok = findViewById(R.id.button_ok);

// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.ampere, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(this);


        final Intent intent = new Intent(getApplicationContext(), ToDoActivity.class);

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

                final String valoreCorrente = spinner.getSelectedItem().toString();

                intent.putExtra("valore_corrente", valoreCorrente);
                startActivity(intent);
            }
        });


    }

    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


}
