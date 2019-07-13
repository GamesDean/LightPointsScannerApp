package com.example.zumoappname;

/**
 *
 *  Classe che ha il compito di controllare lo stato del GPS e nel caso portare l'utente alla schermata di settings per accenderlo
 */


import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;

public class PreQrCodeActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pre_activity_qr_code);

    }

    //TODO credo sarebbe meglio utilizzare un SERVICE che controlli sempre lo stato del GPS anche nelle altre activity

    @Override
    protected void onResume(){
        super.onResume();

        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE );
        boolean statusOfGPS = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (!statusOfGPS){
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        }
        else{
            Intent intent = new Intent(getApplicationContext(), com.example.zumoappname.GetLatLong.class);
            startActivity(intent);
        }
    }
}
