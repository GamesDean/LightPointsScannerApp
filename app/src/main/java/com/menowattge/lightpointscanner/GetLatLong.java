package com.menowattge.lightpointscanner;

/**
 *
 *  Classe per prelevare le coordinate, la città, l'indirizzo
 */

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;

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
     //integer for permissions results request
    private static final int ALL_PERMISSIONS_RESULT = 1011;
    private static final int PERMISSION_REQUEST_FINE_LOCATION = 1;


    private TextView textCoordinate;

//-----------

    public String city,address="";
    double latitude,longitude;
    private  int x = 0;



    /**
     * Ask for GPS permission, just once (first install only)
     */

    public void CheckPermission(final GoogleApiClient gapiClient) {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if (!prefs.contains("First")) {
            // if (this.checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            builder.setTitle("INFO Geolocalizzazione");
            builder.setMessage("In applicazione del Regolamento generale sulla protezione dei dati (GDPR) del 27 aprile 2016 " +
                    "si dichiara all’utilizzatore dell’app, denominata LightPointScanner, che nessun dato personale" +
                    " verrà archiviato e/o trasferito e/o sarà oggetto di proliferazione. Si dichiara che il dato geografico," +
                    " relativo alla sola posizione del palo di illuminazione, verrà archiviato e/o trasferito solo dopo " +
                    "specifica autorizzazione da parte dell'utilizzatore dell'app LightPointScanner . " +
                    "Si ricorda che l’uscita dall’app LightPointScanner  rende non più necessario " +
                    "l’uso del circuito GPS: per risparmiare energia si consiglia di disattivarlo");
            builder.setPositiveButton(R.string.ho_letto, null);



            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_FINE_LOCATION);

                    locationChecker(gapiClient, GetLatLong.this);

                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putBoolean("First",true);
                    editor.commit();
                    System.out.println("PRIMO AVVIO\n");

                }
            });

            builder.show();
        }else{
            System.out.println("SUCCESSIVI AVVII_\n");
            if (this.checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_FINE_LOCATION);
            locationChecker(gapiClient, GetLatLong.this);
        }

    }





    /**
     * Prompt user to enable GPS and Location Services
     *
     * @param mGoogleApiClient
     * @param activity
     */
    public static void locationChecker(GoogleApiClient mGoogleApiClient, final Activity activity) {
        final LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(30 * 1000);
        locationRequest.setFastestInterval(5 * 1000);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);
        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                final LocationSettingsStates state = result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can initialize location requests here.
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:

                        // Location settings are not satisfied. But could be fixed by showing the user a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),and check the result in onActivityResult().
                            status.startResolutionForResult(
                                    activity, 1000);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way to fix the settings so we won't show the dialog.
                        break;
                }
            }
        });
    }



    @Override
    protected void onCreate(Bundle state) {

        super.onCreate(state);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_lat_long);

/*
        // we add permissions we need to request location of the users
        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);

        permissionsToRequest = permissionsToRequest(permissions);



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (permissionsToRequest.size() > 0) {
                requestPermissions(permissionsToRequest.toArray(new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
            }
        }

*/


        // we build google api client
        googleApiClient = new GoogleApiClient.Builder(this).
                addApi(LocationServices.API).
                addConnectionCallbacks(this).
                addOnConnectionFailedListener(this).build();



        // riempio file di testo con coordinate,poi una volta riempito, lancio QrCodeActivity
        textCoordinate = (findViewById(R.id.textViewcoordinate));

        if (googleApiClient != null) {
            googleApiClient.connect();
        }

        CheckPermission(googleApiClient);

    }

    @Override
    protected  void onResume(){
        super.onResume();


        if (googleApiClient != null) {
            googleApiClient.connect();
        }

        CheckPermission(googleApiClient);


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
            x++;
            // passando il valore di k uguale a 0 faccio eseguire immediatamente l'operazione poichè il GPS essendo già attivo
            // è di conseguenza preciso e non devo attendere che azzecchi la posizione.
            getFromCoordinate(x,200);

        }


        startLocationUpdates();
    }




    // aggiorna la posizione

    /**
        Clone di OnConnected con qualche variante. Richiamata quando l'utente avvia l'app ed ha il GPS non
        ancora attivo

     **/
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
            // prelevo lat e long
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            textCoordinate.setText("Latitudine : " + latitude + "\nLongitudine : " + longitude);
            x++;
            // k = 200 poichè corrisponde a circa 30 secondi, necessari e sufficienti affinchè il GPS determini
            // ho messo 200 perchè così non da problemi (misteriosi) con l'update nel DB che fa successivamente
            // la posizione corretta
            getFromCoordinate(x,400);

        }

        startLocationUpdates();


    }

    /**
     *
     * @param x contatore
     * @param k iterazioni, 400 sono circa 30 secondi durante i quali avviene il refresh della posizione GPS corretta
     */


    public void getFromCoordinate(int x, int k){
        // mi serve per prendere l'indirizzo, la città ed altri dati
        Geocoder gcd = new Geocoder(getApplicationContext(), Locale.getDefault());
        List<Address> addresses = null;

        try {
            addresses = gcd.getFromLocation(latitude, longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (addresses.size() > 0) {


            address = addresses.get(0).getAddressLine(0);
            city = addresses.get(0).getLocality();

            //mostra a video sotto le coordinate anche la città.
            textCoordinate.append("\ncittà : " + city);
            textCoordinate.append("\nindirizzo : " + address);

        }

        if(textCoordinate.getText().toString().length()>1&& (x>=k)) {
            // intent che lancia l'activity e gli passa i valori rilevati.
            // li passo a QrCodeActivity come classe intermedia dato che il vero destinatario è ToDoActivity
            Intent intentQr = new Intent(getApplicationContext(), QrCodeActivity.class);
            intentQr.putExtra("citta", city);
            intentQr.putExtra("indirizzo", address);
            intentQr.putExtra("latitudine", latitude);
            intentQr.putExtra("longitudine", longitude);

            startActivity(intentQr);



        }

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
