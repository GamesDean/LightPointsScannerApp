package com.menowattge.lightpointscanner;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import static android.view.View.SYSTEM_UI_FLAG_FULLSCREEN;
import static android.view.View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
import static android.view.View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
import static android.view.View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
import static android.view.View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
import static android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE;

public class InsertOrDeleteActivity extends AppCompatActivity {

    ImageButton buttonInsertDevice;
    ImageButton buttonDeleteDevice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_or_delete);

        // total fullscreen
        getWindow().getDecorView().setSystemUiVisibility(SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
                SYSTEM_UI_FLAG_FULLSCREEN | SYSTEM_UI_FLAG_HIDE_NAVIGATION   |
                SYSTEM_UI_FLAG_LAYOUT_STABLE | SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);


        // TODO immagini idonee
        buttonDeleteDevice = findViewById(R.id.imageButtonDeleteDevice);
        buttonInsertDevice = findViewById(R.id.imageButtonInsertDevice);

        buttonInsertDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // TODO goto PreQrCodeActivity
                // nel caso di inserimento di un device
                Intent intent = new Intent(getApplicationContext(),PreQrCodeActivity.class);
                startActivity(intent);
            }
        });

        buttonDeleteDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO DeleteDeviceActivity
                Toast.makeText(getApplicationContext(),"eliminare device",Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getApplicationContext(),QrCodeActivityDelete.class);
                startActivity(intent);

            }
        });
    }
}