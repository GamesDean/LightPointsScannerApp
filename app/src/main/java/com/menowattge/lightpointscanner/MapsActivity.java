package com.menowattge.lightpointscanner;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends AppCompatActivity implements
        ReverseGeo.OnTaskComplete, OnMapReadyCallback {

    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 1;
    private Button button;
    private TextView textview;
    private boolean addressRequest;

    int secondi;

    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback mLocationCallback;

    private GoogleMap googleMap;

    public boolean firstTime;

    SupportMapFragment mapFragment;
    Marker currentLocationMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reverse_geo);
        //creo la mappa
        mapFragment = SupportMapFragment.newInstance();
        getSupportFragmentManager().beginTransaction().add(R.id.map, mapFragment).commit();
        //flag primo avvio - serve per animare la mappa una volta sola
        firstTime = true;
        //controllo che sia attivo
        checkGpsStatus();

        button = findViewById(R.id.button);
        textview = findViewById(R.id.textview);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!addressRequest && isGooglePlayServicesAvailable() ) {
                    getAddress(false);
                }
            }
        });


        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (addressRequest) { new ReverseGeo(MapsActivity.this, MapsActivity.this).execute(locationResult.getLastLocation());
                }
            }
        };

    }


    @Override
    protected void onPause() {
        super.onPause();
        if (mFusedLocationClient!=null) {
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
            Log.i("onPause", "done");
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mFusedLocationClient!=null) {
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
            Log.i("onStop", "done");
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        checkGpsStatus();
    }


    // -------------------------------------------------------------------------------------------- //

    /**
     *
     * Controllo scrupoloso per i services Google altrimenti non funziona la mappa
     * @return
     */
    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int status = googleApiAvailability.isGooglePlayServicesAvailable(this);
        if (ConnectionResult.SUCCESS == status)
            return true;
        else {
            if (googleApiAvailability.isUserResolvableError(status))
                Toast.makeText(this, "Sono necessari Google Play Services per usare l'app : installali dal Play Store", Toast.LENGTH_LONG).show();
        }
        return false;
    }

    /**
     * Controllo stato del GPS
     *
     */

    public void checkGpsStatus(){
        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE );
        final boolean statusOfGPS = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (!statusOfGPS){
            StartLocationAlert startLocationAlert = new StartLocationAlert(MapsActivity.this);
        }
    }


    /**
     * Al primo avvio in assoluto mostro le info del GDPR. Controllo invece sempre il GPS se attivo
     * Se non sono stati concessi, chiedo i permessi.
     * Infine prelevo le coordinate e quindi indirizzo
     * @param dialog
     */
    private void getAddress(boolean dialog) {

        checkGpsStatus();
        secondi=30000;
        if (dialog){
            secondi = 10000;
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            final android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);

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
                    ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},MY_PERMISSIONS_REQUEST_LOCATION);
                }
            });

            builder.show();
        } else {
            addressRequest = true;
        //Request location updates//
            mFusedLocationClient.requestLocationUpdates(getLocationRequest(secondi),mLocationCallback,null);

        }

    }


    private LocationRequest getLocationRequest(int secondi) {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(secondi);
        return locationRequest;
    }



    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {

        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (isGooglePlayServicesAvailable()) {
                        getAddress(false);
                    }

                } else {
                    Toast.makeText(this, "Necessari permessi GPS", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return null;
    }




  // --------------------------------------------------------------------------------------------//

    /**
     * Quando ha il valore di ritorno contenente lat-lon indirizzo generato dalla classe asincrona ReverseGeo,
     * zooma la mappa ed aggiorna il marker
     * @param result
     */
    @Override
    public void onTaskComplete(String result) {
        if (addressRequest) {

            Log.d("RESULT : ", result);
            String coordinate[] = result.split(";");
            String lat = coordinate[0];
            String lon = coordinate[1];
            String address = coordinate[2];
            String city = coordinate[3];
            Log.d("address", address + "--" + city);
            double latitude = Double.parseDouble(lat);
            double longitude = Double.parseDouble(lon);

            mapFragment.getMapAsync(
                    new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(GoogleMap mMap) {
                            //una volta sola all'avvio muovo la camera della mappa spostandomi sulle opportune coordinate
                            if(firstTime) {
                                mMap.clear(); //clear old markers
                                mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(41.29246, 12.5736108)));

                                CameraPosition googlePlex = CameraPosition.builder()
                                        .target(new LatLng(latitude, longitude))
                                        .zoom(17)
                                        .bearing(0)
                                        .tilt(45)
                                        .build();

                                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(googlePlex), 1500, null);

                                currentLocationMarker = mMap.addMarker(new MarkerOptions()
                                        .position(new LatLng(latitude, longitude))
                                        .title("Punto Luce")
                                        .snippet("Meridio verrà inserito in questa posizione"));

                                firstTime=false;
                                // aggiorno il marker senza ricaricare la mappa ed animarla ogni volta
                            }else {
                                LatLng latLng = new LatLng(latitude, longitude);
                                //todo verificare che DAVVERO aggiorni il marker sulla mappa : mi devo spostare
                                MarkerAnimation.animateMarkerToGB(currentLocationMarker, latLng, new LatLngInterpolator.Spherical());
                            }

                            // gestisco il click sul marker
                            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener(){

                                @Override
                                public boolean onMarkerClick(Marker marker) {
                                    android.app.AlertDialog alertDialog = new AlertDialog.Builder(MapsActivity.this)
                                            .setIcon(android.R.drawable.ic_dialog_alert)
                                            .setTitle("Inserimento Punto Luce")
                                            .setMessage("Inserire QUI?\n\n"+address+"\n"+"lat : "+latitude+" lon : "+longitude)
                                            .setCancelable(false)

                                            // al click su SI prendo i dati raccolti ed avvio l'activity per la scan del qrcode
                                            .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {

                                                    Toast.makeText(getApplicationContext(),"Ok, scansiona il QRCODE",Toast.LENGTH_LONG).show();

                                                    Intent intentQr = new Intent(getApplicationContext(), QrCodeActivity.class);
                                                    intentQr.putExtra("citta", city);
                                                    intentQr.putExtra("indirizzo", address);
                                                    intentQr.putExtra("latitudine", latitude);
                                                    intentQr.putExtra("longitudine", longitude);
                                                    startActivity(intentQr);
                                                    finish();}
                                            })

                                            // al click su NO ricalcolo la posizione
                                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    Toast.makeText(getApplicationContext(),"Nuova Posizione entro 10 sec...",Toast.LENGTH_LONG).show();
                                                    if(isGooglePlayServicesAvailable())
                                                    getAddress(true);
                                                }
                                            })
                                            .show();
                                    alertDialog.setCanceledOnTouchOutside(false);

                                    return false;
                                }
                            });

                        }
                    });


        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
    }
}