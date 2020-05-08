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
//Create a member variable of the FusedLocationProviderClient type//

    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback mLocationCallback;

    String indirizzoCompleto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reverse_geo);

        checkGpsStatus();

        button = findViewById(R.id.button);
        textview = findViewById(R.id.textview);

//Initialize mFusedLocationClient//

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

//Create the onClickListener//

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//Call getAddress, in response to onClick events//
                if (!addressRequest) {
                    getAddress(false);
                   // mFusedLocationClient.removeLocationUpdates(mLocationCallback);
                }
            }
        });

//Create a LocationCallback object//
//Override the onLocationResult() method,
//which is where this app receives its location updates//
//Execute ReverseGeo in response to addressRequest//
// Obtain the device's last known location from the FusedLocationProviderClient//

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (addressRequest) { new ReverseGeo(MapsActivity.this, MapsActivity.this).execute(locationResult.getLastLocation());
                }
            }
        };

      /*  if (!addressRequest) {
            getAddress(false);
        }*/




    }


    public void checkGpsStatus(){
        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE );
        final boolean statusOfGPS = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (!statusOfGPS){
            StartLocationAlert startLocationAlert = new StartLocationAlert(MapsActivity.this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkGpsStatus();
    }

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
//If the geocoder retrieves an address, then display this address in the TextView//
           // textview.setText(getString(R.string.address_text));

        }

    }

//Specify the requirements for your application's location requests//
    private LocationRequest getLocationRequest(int secondi) {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(secondi);
        return locationRequest;
    }

    @Override
    protected void onPause() {
        super.onPause();
        //locationManager.removeUpdates(this);
       // Log.i(TAG, "onPause, done");
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
        Log.i( "onPause","done");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {

        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getAddress(false);
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

    @Override
    public void onTaskComplete(String result) {
        if (addressRequest) {
            String indirizzo_completo = getString(R.string.address_text, result);
            //textview.setText(indirizzo_completo);
            Log.d("RESULT : ", result);

            String coordinate[] = result.split(";");

            String lat = coordinate[0];
            String lon = coordinate[1];
            String address = coordinate[2];
            String city = coordinate[3];

            Log.d("address",address+"--"+city);
            double latitude = Double.parseDouble(lat);
            double longitude = Double.parseDouble(lon);

            SupportMapFragment mapFragment = SupportMapFragment.newInstance();
            getSupportFragmentManager().beginTransaction().add(R.id.map, mapFragment).commit();
            mapFragment.getMapAsync(
                    new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(GoogleMap mMap) {

                            mMap.clear(); //clear old markers
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(41.29246 ,12.5736108)));

                            CameraPosition googlePlex = CameraPosition.builder()
                                    .target(new LatLng(latitude,longitude))
                                    .zoom(17)
                                    .bearing(0)
                                    .tilt(45)
                                    .build();

                            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(googlePlex), 1500, null);


                            mMap.addMarker(new MarkerOptions()
                                    .position(new LatLng(latitude,longitude))
                                    .title("Punto Luce")
                                    .snippet("Meridio verrà inserito in questa posizione"));

                            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener(){

                                @Override
                                public boolean onMarkerClick(Marker marker) {
                                    android.app.AlertDialog alertDialog = new AlertDialog.Builder(MapsActivity.this)
                                            .setIcon(android.R.drawable.ic_dialog_alert)
                                            .setTitle("Inserimento Punto Luce")
                                            .setMessage("Inserire QUI?\n\n"+address)
                                            .setCancelable(false)
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
                                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    Toast.makeText(getApplicationContext(),"Nuova Posizione entro 10 sec...",Toast.LENGTH_LONG).show();
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

    }
}