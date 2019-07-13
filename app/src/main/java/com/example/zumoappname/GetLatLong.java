package com.example.zumoappname;

/**
 *
 *  Classe per prelevare le coordinate, la città, l'indirizzo
 */

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class GetLatLong extends AppCompatActivity implements  GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {


    //----------

    private Location location;

    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private static final long UPDATE_INTERVAL = 5000, FASTEST_INTERVAL = 5000; // = 5 seconds
    // lists for permissions
    private ArrayList<String> permissionsToRequest;
    private ArrayList<String> permissions = new ArrayList<>();
    // integer for permissions results request
    private static final int ALL_PERMISSIONS_RESULT = 1011;

    private TextView textCoordinate;

//-----------

    public String city,address="";
    double latitude,longitude;




    @Override
    protected void onCreate(Bundle state) {

        super.onCreate(state);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_lat_long);


        // we add permissions we need to request location of the users
        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);

        permissionsToRequest = permissionsToRequest(permissions);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (permissionsToRequest.size() > 0) {
                requestPermissions(permissionsToRequest.toArray(
                        new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
            }
        }

        // we build google api client
        googleApiClient = new GoogleApiClient.Builder(this).
                addApi(LocationServices.API).
                addConnectionCallbacks(this).
                addOnConnectionFailedListener(this).build();



        // riempio file di testo con coordinate,poi una volta riempito, lancio QrCodeActivity
        textCoordinate = (findViewById(R.id.textViewcoordinate));



    }

    @Override
    protected  void onResume(){
        super.onResume();

        if (googleApiClient != null) {
            googleApiClient.connect();
        }


    }


    private ArrayList<String> permissionsToRequest(ArrayList<String> wantedPermissions) {
        ArrayList<String> result = new ArrayList<>();

        for (String perm : wantedPermissions) {
            if (!hasPermission(perm)) {
                result.add(perm);
            }
        }

        return result;
    }




    private boolean hasPermission(String permission) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
        }

        return true;
    }


    @Override
    protected void onStart() {
        super.onStart();

        if (googleApiClient != null) {
            googleApiClient.connect();
        }
    }


    @Override
    public void onPause() {

        super.onPause();


        // stop location updates
        if (googleApiClient != null && googleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
            googleApiClient.disconnect();
        }
    }





    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        // Permissions ok, we get last location
        location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);

        if (location != null) {

            // prelevo lat e long
            latitude = location.getLatitude();
            longitude = location.getLongitude();

            // le mostro a video per una frazione di secondo
            textCoordinate.setText("Latitudine : " + latitude + "\nLongitudine : " + longitude);

            // mi serve per prendere l'indirizzo, la città ed altri dati
            Geocoder gcd = new Geocoder(this, Locale.getDefault());

            List<Address> addresses = null;
            try {
                addresses = gcd.getFromLocation(latitude, longitude, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (addresses.size() > 0) {


                address = addresses.get(0).getAddressLine(0);
                city = addresses.get(0).getLocality();

                //mostra a video sotto le coordinate anche la città. Il tutto per una fraz di secondo, cambiare con pulsante
                // dato che spesso fa i calzetti cioè ci mette un po' per prendere la posizione corretta.
                textCoordinate.append("\ncittà : " + city);

            }
            else {
                // do your stuff
            }

        }

        // quando vengono correttamente rilevate le coordinate, parte l'activity che scansiona il qrcode
        // TODO : rivedere questa parte, meglio inserire un pulsante cosicchè il gps rilevi meglio la posizione
        if(textCoordinate.getText().toString().length()>1) {

            // intent che lancia l'activity e gli passa i valori rilevati.
            // li passo a QrCodeActivity come classe intermedia dato che il vero destinatario è ToDoActivity
            Intent intentQr = new Intent(getApplicationContext(), QrCodeActivity.class);
            intentQr.putExtra("citta",city);
            intentQr.putExtra("indirizzo",address);
            intentQr.putExtra("latitudine",latitude);
            intentQr.putExtra("longitudine",longitude);

            startActivity(intentQr);
        }

        startLocationUpdates();
    }


    // aggiorna la posizione
    private void startLocationUpdates() {
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(UPDATE_INTERVAL);
        locationRequest.setFastestInterval(FASTEST_INTERVAL);

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "You need to enable permissions to display location !", Toast.LENGTH_SHORT).show();
        }

        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
    }


    // aggiorna la posizione
   private  void refreshCoordinate(){

       if (ActivityCompat.checkSelfPermission(this,
               Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
               && ActivityCompat.checkSelfPermission(this,
               Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
           return;
       }

       // Permissions ok, we get last location
       location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);

       if (location != null) {
           System.out.println("Latitude : " + location.getLatitude() + "\nLongitude : " + location.getLongitude());
           textCoordinate.setText("Latitudine : " + location.getLatitude() + "\nLongitudine : " + location.getLongitude());
       }

       if(textCoordinate.getText().toString().length()>1) {

           Intent intentQr = new Intent(getApplicationContext(), QrCodeActivity.class);
           startActivity(intentQr);
       }

       startLocationUpdates();

   }



   // metodi dell'interfaccia  che utilizzano le funzioni di cui sopra per aggiornare  la posizione

    @Override
    public void onConnectionSuspended(int i) {

        refreshCoordinate();


    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        refreshCoordinate();

    }

    @Override
    public void onLocationChanged(Location location) {

        refreshCoordinate();

    }
}
