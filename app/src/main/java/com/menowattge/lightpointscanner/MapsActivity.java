package com.menowattge.lightpointscanner;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
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

        //Obtain the SupportMapFragment//
/*

        SupportMapFragment mapFragment = SupportMapFragment.newInstance();
        getSupportFragmentManager().beginTransaction().add(R.id.map, mapFragment).commit();
        mapFragment.getMapAsync(
                new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

                mMap.clear(); //clear old markers

                CameraPosition googlePlex = CameraPosition.builder()
                        .target(new LatLng(37.4219999,-122.0862462))
                        .zoom(10)
                        .bearing(0)
                        .tilt(45)
                        .build();

                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(googlePlex), 10000, null);

                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(37.4219999, -122.0862462))
                        .title("Spider Man"));

                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(37.4629101,-122.2449094))
                        .title("Iron Man")
                        .snippet("His Talent : Plenty of money"));

                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(37.3092293,-122.1136845))
                        .title("Captain America"));
            }
        });
*/


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

//Implement getAddress//

    private void getAddress(boolean dialog) {
            secondi=30000;
        if (dialog){
            secondi = 10000;
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},MY_PERMISSIONS_REQUEST_LOCATION);
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

                            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(googlePlex), 3000, null);


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
                                                    startActivity(intentQr);                                   }
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