/*
 * Copyright (C) 2008-2018 The Android Open Source Project,
 * Sean J. Barbeau (sjbarbeau@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.menowattge.lightpointscanner;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.GnssMeasurementsEvent;
import android.location.GnssStatus;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.menowattge.lightpointscanner.model.ConstellationType;
import com.menowattge.lightpointscanner.model.DilutionOfPrecision;
import com.menowattge.lightpointscanner.model.GnssType;
import com.menowattge.lightpointscanner.model.SatelliteMetadata;
import com.menowattge.lightpointscanner.model.SatelliteStatus;
import com.menowattge.lightpointscanner.util.CarrierFreqUtils;
import com.menowattge.lightpointscanner.util.DateTimeUtils;
import com.menowattge.lightpointscanner.util.MathUtils;
import com.menowattge.lightpointscanner.util.NmeaUtils;
import com.menowattge.lightpointscanner.util.PreferenceUtils;
import com.menowattge.lightpointscanner.util.SatelliteUtils;
import com.menowattge.lightpointscanner.util.SortUtil;
import com.menowattge.lightpointscanner.util.UIUtils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import static android.util.TypedValue.COMPLEX_UNIT_DIP;
import static android.util.TypedValue.COMPLEX_UNIT_PX;

public class GpsStatusFragment extends Fragment implements GpsTestListener {

    public final static String TAG = "GpsStatusFragment";

    private static final String EMPTY_LAT_LONG = "             ";

    @SuppressLint("SimpleDateFormat") // See #117
    SimpleDateFormat mDateFormat = new SimpleDateFormat(
            DateFormat.is24HourFormat(Application.get().getApplicationContext())
                    ? "HH:mm:ss" : "hh:mm:ss a");

    private Resources mRes;

    private Button GotoScan;

    private TextView mLatitudeView, mLongitudeView, mFixTimeView, mTTFFView, mAltitudeView,
            mAltitudeMslView, mHorVertAccuracyLabelView, mHorVertAccuracyView,
            mSpeedView, mSpeedAccuracyView, mBearingView, mBearingAccuracyView, mNumSats,
            mPdopLabelView, mPdopView, mHvdopLabelView, mHvdopView, mGnssNotAvailableView,
            mSbasNotAvailableView, mFixTimeErrorView,mCity;

    private CardView mLocationCard,mCityCard;

    private Location mLocation;

    private TableRow mSpeedBearingAccuracyRow;

    private RecyclerView mGnssStatusList;
    private RecyclerView mSbasStatusList;

    private SatelliteStatusAdapter mGnssAdapter;
    private SatelliteStatusAdapter mSbasAdapter;

    private List<SatelliteStatus> mGnssStatus = new ArrayList<>();

    private List<SatelliteStatus> mSbasStatus = new ArrayList<>();

    private int mSvCount;

    private String mSnrCn0Title;

    private long mFixTime;

    private boolean mNavigating;

    private Drawable mFlagUsa, mFlagRussia, mFlagJapan, mFlagChina, mFlagIndia, mFlagEU, mFlagICAO;

    private boolean mUseLegacyGnssApi = false;

    private String mTtff = "";

    private static final String METERS = Application.get().getResources().getStringArray(R.array.preferred_distance_units_values)[0];
    private static final String METERS_PER_SECOND = Application.get().getResources().getStringArray(R.array.preferred_speed_units_values)[0];
    private static final String KILOMETERS_PER_HOUR = Application.get().getResources().getStringArray(R.array.preferred_speed_units_values)[1];

    String mPrefDistanceUnits;
    String mPrefSpeedUnits;

    DeviceInfoViewModel mViewModel;

    private ProgressDialog pd;

    private final Observer<SatelliteMetadata> mSatelliteMetadataObserver = new Observer<SatelliteMetadata>() {
        @Override
        public void onChanged(@Nullable final SatelliteMetadata satelliteMetadata) {
            if (satelliteMetadata != null) {
                mNumSats.setText(mRes.getString(R.string.gps_num_sats_value,
                        satelliteMetadata.getNumSatsUsed(),
                        satelliteMetadata.getNumSatsInView(),
                        satelliteMetadata.getNumSatsTotal()));
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        mRes = getResources();
        setupUnitPreferences();

        View v = inflater.inflate(R.layout.gps_status, container,false);

        mLatitudeView = v.findViewById(R.id.latitude);
        mLongitudeView = v.findViewById(R.id.longitude);
        mFixTimeView = v.findViewById(R.id.fix_time);
        mFixTimeErrorView = v.findViewById(R.id.fix_time_error);
        mFixTimeErrorView.setOnClickListener(view -> showTimeErrorDialog(mFixTime));
        mTTFFView = v.findViewById(R.id.ttff);
        mAltitudeView = v.findViewById(R.id.altitude);
        mAltitudeMslView = v.findViewById(R.id.altitude_msl);
        mHorVertAccuracyLabelView = v.findViewById(R.id.hor_vert_accuracy_label);
        mHorVertAccuracyView = v.findViewById(R.id.hor_vert_accuracy);
        mSpeedView = v.findViewById(R.id.speed);
        mSpeedAccuracyView = v.findViewById(R.id.speed_acc);
        mBearingView = v.findViewById(R.id.bearing);
        mBearingAccuracyView = v.findViewById(R.id.bearing_acc);
        mNumSats = v.findViewById(R.id.num_sats);
        mPdopLabelView = v.findViewById(R.id.pdop_label);
        mPdopView = v.findViewById(R.id.pdop);
        mHvdopLabelView = v.findViewById(R.id.hvdop_label);
        mHvdopView = v.findViewById(R.id.hvdop);
        mCity = v.findViewById(R.id.address);

        GotoScan = v.findViewById(R.id.button_goto_scan);

        mSpeedBearingAccuracyRow = v.findViewById(R.id.speed_bearing_acc_row);

        mGnssNotAvailableView = v.findViewById(R.id.gnss_not_available);
        mSbasNotAvailableView = v.findViewById(R.id.sbas_not_available);

        mLatitudeView.setText(EMPTY_LAT_LONG);
        mLongitudeView.setText(EMPTY_LAT_LONG);

        mFlagUsa = getResources().getDrawable(R.drawable.ic_flag_usa);
        mFlagRussia = getResources().getDrawable(R.drawable.ic_flag_russia);
        mFlagJapan = getResources().getDrawable(R.drawable.ic_flag_japan);
        mFlagChina = getResources().getDrawable(R.drawable.ic_flag_china);
        mFlagIndia = getResources().getDrawable(R.drawable.ic_flag_india);
        mFlagEU = getResources().getDrawable(R.drawable.ic_flag_european_union);
        mFlagICAO = getResources().getDrawable(R.drawable.ic_flag_icao);

        mLocationCard = v.findViewById(R.id.status_location_card);
        mCityCard = v.findViewById(R.id.city_card);

        mLocationCard.setCardBackgroundColor(Color.parseColor("#bc6d6e"));

        pd = new ProgressDialog(new ContextThemeWrapper(GpsTestActivity.getInstance(),R.style.ProgressDialogCustom));
        pd.setMessage("\nGeolocalizzazione in corso : ->DURATA MEDIA 3 MINUTI\n\nAl termine si potrà controllare la propria posizione sulla mappa.\n\n->Cliccare sul pulsante OK verde in basso per scansionare l'etichetta...");
        pd.show();
        pd.setCanceledOnTouchOutside(false);

        mLocationCard.setOnLongClickListener(view -> {

            final Location location = mLocation;

            if (location != null) {
                boolean includeAltitude = Application.getPrefs().getBoolean(Application.get().getString(R.string.pref_key_share_include_altitude), false);
                String coordinateFormat = Application.getPrefs().getString(Application.get().getString(R.string.pref_key_coordinate_format), Application.get().getString(R.string.preferences_coordinate_format_dd_key));
                String formattedLocation = UIUtils.formatLocationForDisplay(location, null, includeAltitude,
                        null, null, null, coordinateFormat);
                if (!TextUtils.isEmpty(formattedLocation)) {


                }
            }
            return false;
        });

        GotoScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // ---------------------------------------------
                final Location location = mLocation;

                Geocoder mGeocoder = new Geocoder(GpsTestActivity.getInstance(), Locale.getDefault());
                double latitude_ = location.getLatitude();
                double longitude_ = location.getLongitude();

                //test debug comuni frazioni
              //  double latitude_ = 44.992932;
              //  double longitude_ = 9.0754435;
                //test debug comuni frazioni

                List<Address> addresses = null;
                try {
                    addresses = mGeocoder.getFromLocation(latitude_,longitude_, 2);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Log.d("DEBUG",addresses.toString());

                String address = addresses.get(1).getAddressLine(0);
                String city = addresses.get(1).getLocality();
                String city2 = addresses.get(0).getSubLocality(); //TODO SE NOT NULL city=city2

                //Intent intentQr = new Intent(getActivity(), QrCodeActivity.class);
                Intent intentSelect = new Intent(getActivity(), SelectActivity.class);
                intentSelect.putExtra("citta", city);
                intentSelect.putExtra("indirizzo", address);
                intentSelect.putExtra("latitudine", latitude_);
                intentSelect.putExtra("longitudine", longitude_);

                startActivity(intentSelect);
                getActivity().finish();
                getActivity().getSupportFragmentManager().popBackStack();

                // --------------------------------------------------------

            }
        });

        // GNSS
        LinearLayoutManager llmGnss = new LinearLayoutManager(getContext());
        llmGnss.setAutoMeasureEnabled(true);
        llmGnss.setOrientation(RecyclerView.VERTICAL);

        mGnssStatusList = v.findViewById(R.id.gnss_status_list);
        mGnssAdapter = new SatelliteStatusAdapter(ConstellationType.GNSS);
        mGnssStatusList.setAdapter(mGnssAdapter);
        mGnssStatusList.setFocusable(false);
        mGnssStatusList.setFocusableInTouchMode(false);
        mGnssStatusList.setLayoutManager(llmGnss);
        mGnssStatusList.setNestedScrollingEnabled(false);

       // mGnssStatusList.setBackgroundColor(Color.parseColor("#bc6d6e"));

        // SBAS
        LinearLayoutManager llmSbas = new LinearLayoutManager(getContext());
        llmSbas.setAutoMeasureEnabled(true);
        llmSbas.setOrientation(RecyclerView.VERTICAL);

        mSbasStatusList = v.findViewById(R.id.sbas_status_list);
        mSbasAdapter = new SatelliteStatusAdapter(ConstellationType.SBAS);
        mSbasStatusList.setAdapter(mSbasAdapter);
        mSbasStatusList.setFocusable(false);
        mSbasStatusList.setFocusableInTouchMode(false);
        mSbasStatusList.setLayoutManager(llmSbas);
        mSbasStatusList.setNestedScrollingEnabled(false);

       // mSbasStatusList.setBackgroundColor(Color.parseColor("#bc6d6e"));

        GpsTestActivity.getInstance().addListener(this);

        mViewModel = ViewModelProviders.of(getActivity()).get(DeviceInfoViewModel.class);
        mViewModel.getSatelliteMetadata().observe(getActivity(), mSatelliteMetadataObserver);

        return v;
    }

    private void setStarted(boolean navigating) {
        if (navigating != mNavigating) {
            if (!navigating) {
                mViewModel.reset();
                mLatitudeView.setText(EMPTY_LAT_LONG);
                mLongitudeView.setText(EMPTY_LAT_LONG);
                mFixTime = 0;
                updateFixTime();
                mTTFFView.setText("");
                mAltitudeView.setText("");
                mAltitudeMslView.setText("");
                mHorVertAccuracyView.setText("");
                mSpeedView.setText("");
                mSpeedAccuracyView.setText("");
                mBearingView.setText("");
                mBearingAccuracyView.setText("");
                mNumSats.setText("");
                mPdopView.setText("");
                mHvdopView.setText("");

                mSvCount = 0;
                mGnssStatus.clear();
                mSbasStatus.clear();
                mGnssAdapter.notifyDataSetChanged();
                mSbasAdapter.notifyDataSetChanged();
            }
            mNavigating = navigating;
        }
    }

    private void updateFixTime() {
        if (mFixTime == 0 || (GpsTestActivity.getInstance() != null && !GpsTestActivity.getInstance().mStarted)) {
            mFixTimeView.setText("");
            mFixTimeErrorView.setText("");
            mFixTimeErrorView.setVisibility(View.GONE);
        } else {
            if (DateTimeUtils.Companion.isTimeValid(mFixTime)) {
                mFixTimeErrorView.setVisibility(View.GONE);
                mFixTimeView.setVisibility(View.VISIBLE);
                mFixTimeView.setText(mDateFormat.format(mFixTime));
            } else {
                // Error in fix time
                mFixTimeErrorView.setVisibility(View.VISIBLE);
                mFixTimeView.setVisibility(View.GONE);
                mFixTimeErrorView.setText(mDateFormat.format(mFixTime));
            }
        }
    }

    /**
     * Update views for horizontal and vertical location accuracies based on the provided location
     * @param location
     */
    private void updateLocationAccuracies(Location location) {
        if (SatelliteUtils.isVerticalAccuracySupported(location)) {
            mHorVertAccuracyLabelView.setText(R.string.gps_hor_and_vert_accuracy_label);
            if (mPrefDistanceUnits.equalsIgnoreCase(METERS)) {
                mHorVertAccuracyView.setText(mRes.getString(R.string.gps_hor_and_vert_accuracy_value_meters,
                        location.getAccuracy(),
                        location.getVerticalAccuracyMeters()));
            } else {
                // Feet
                mHorVertAccuracyView.setText(mRes.getString(R.string.gps_hor_and_vert_accuracy_value_feet,
                        UIUtils.toFeet(location.getAccuracy()),
                        UIUtils.toFeet(location.getVerticalAccuracyMeters())));
            }
        } else {
            if (location.hasAccuracy()) {
                if (mPrefDistanceUnits.equalsIgnoreCase(METERS)) {
                    mHorVertAccuracyView.setText(mRes.getString(R.string.gps_accuracy_value_meters, location.getAccuracy()));
                } else {
                    // Feet
                    mHorVertAccuracyView.setText(mRes.getString(R.string.gps_accuracy_value_feet, UIUtils.toFeet(location.getAccuracy())));
                }
            } else {
                mHorVertAccuracyView.setText("");
            }
        }
    }

    /**
     * Update views for speed and bearing location accuracies based on the provided location
     * @param location
     */
    private void updateSpeedAndBearingAccuracies(Location location) {
        if (SatelliteUtils.isSpeedAndBearingAccuracySupported()) {
            mSpeedBearingAccuracyRow.setVisibility(View.VISIBLE);
            if (location.hasSpeedAccuracy()) {
                if (mPrefSpeedUnits.equalsIgnoreCase(METERS_PER_SECOND)) {
                    mSpeedAccuracyView.setText(mRes.getString(R.string.gps_speed_acc_value_meters_sec, location.getSpeedAccuracyMetersPerSecond()));
                } else if (mPrefSpeedUnits.equalsIgnoreCase(KILOMETERS_PER_HOUR)) {
                    mSpeedAccuracyView.setText(mRes.getString(R.string.gps_speed_acc_value_km_hour, UIUtils.toKilometersPerHour(location.getSpeedAccuracyMetersPerSecond())));
                } else {
                    // Miles per hour
                    mSpeedAccuracyView.setText(mRes.getString(R.string.gps_speed_acc_value_miles_hour, UIUtils.toMilesPerHour(location.getSpeedAccuracyMetersPerSecond())));
                }
            } else {
                mSpeedAccuracyView.setText("");
            }
            if (location.hasBearingAccuracy()) {
                mBearingAccuracyView.setText(mRes.getString(R.string.gps_bearing_acc_value, location.getBearingAccuracyDegrees()));
            } else {
                mBearingAccuracyView.setText("");
            }
        } else {
            mSpeedBearingAccuracyRow.setVisibility(View.GONE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        GpsTestActivity gta = GpsTestActivity.getInstance();
        setStarted(gta.mStarted);

        setupUnitPreferences();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.status_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int id = item.getItemId();
        if (id == R.id.sort_sats) {
            showSortByDialog();
        }
        return false;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    public void onGpsStarted() {
        setStarted(true);
    }

    public void onGpsStopped() {
        setStarted(false);
    }

    @SuppressLint("NewApi")
    public void gpsStart() {
    }

    public void gpsStop() {
    }

    public void onLocationChanged(Location location) {
        if (!UIUtils.isFragmentAttached(this)) {
            // Fragment isn't visible, so return to avoid IllegalStateException (see #85)
            return;
        }

        // coloro di verde il primo che va cliccato, bianco gli altri
        pd.dismiss();
        mLocationCard.setCardBackgroundColor(Color.parseColor("#edf4f0"));
        mCityCard.setCardBackgroundColor(Color.parseColor("#edf4f0"));

        // Cache location for copy to clipboard operation
        mLocation = location;

        Geocoder mGeocoder = new Geocoder(GpsTestActivity.getInstance(), Locale.getDefault());
        double latitude_ = location.getLatitude();
        double longitude_ = location.getLongitude();

        // DEBUG
        //LISCIANO
        //42.83034840619562, 13.601854824094545
        //PIATTONI
        //42.87358067411861, 13.70593183468471
        //CONCA ULIVI
        //42.93959549451618, 13.842534494862702
        // TORRAZZA
        //  double latitude_ = 44.992932;
        //  double longitude_ = 9.0754435;
        //RIPA
        //42.9549479,13.7793035

         //double latitude_ =  42.87358067411861;

         //double longitude_ = 13.70593183468471;
        // DEBUG
        List<Address> addresses = null;
        try {
            addresses = mGeocoder.getFromLocation(latitude_,longitude_, 2); // 2 lps light, 1 normal
        } catch (IOException e) {
            e.printStackTrace();
        }

        String address = addresses.get(1).getAddressLine(0); // get(1) light, get(0) normal

        Log.d("DEBUG",addresses.toString());
/*
        String city = addresses.get(0).getAdminArea(); //regione
        String city2 = addresses.get(0).getSubAdminArea(); // Provincia 
        String city3 = addresses.get(0).getSubLocality(); // null
        String city4 = addresses.get(0).getFeatureName(); //civico
        String city5 = addresses.get(0).getLocality(); // Città esatta
        String city6 = addresses.get(0).getPremises(); // null
        String city7 = addresses.get(0).getThoroughfare(); //via breve

        String city8 = addresses.get(0).getSubThoroughfare(); // 49
        String city9 = addresses.get(0).getAddressLine(0); // indirizzo

        Log.d(city,"citta normale");
        Log.d(city2,"citta subadmin");
        Log.d(city3,"citta sublocality");
        Log.d(city4,"citta feat");
        Log.d(city5,"citta locality");
        Log.d(city6,"citta prem");
        Log.d(city7,"citta  flare");
        Log.d(city8,"citta  sub");
        Log.d(city9,"citta  addr");
*/


        //Toast.makeText(getActivity(), city+"\n"+address, Toast.LENGTH_LONG).show();

        // Make sure TTFF is shown, if the TTFF is acquired before the mTTFFView is initialized
        mTTFFView.setText(mTtff);

        String coordinateFormat = Application.getPrefs().getString(getString(R.string.pref_key_coordinate_format), getString(R.string.preferences_coordinate_format_dd_key));
        switch (coordinateFormat) {
            // Constants below must match string values in do_not_translate.xml
            case "dd":
                // Decimal degrees
                mLatitudeView.setText(mRes.getString(R.string.gps_latitude_value, location.getLatitude()));
                mLongitudeView.setText(mRes.getString(R.string.gps_longitude_value, location.getLongitude()));
                break;
            case "dms":
                // Degrees minutes seconds
                mLatitudeView.setText(UIUtils.getDMSFromLocation(Application.get(), location.getLatitude(), UIUtils.COORDINATE_LATITUDE));
                mLongitudeView.setText(UIUtils.getDMSFromLocation(Application.get(), location.getLongitude(), UIUtils.COORDINATE_LONGITUDE));
                break;
            case "ddm":
                // Degrees decimal minutes
                mLatitudeView.setText(UIUtils.getDDMFromLocation(Application.get(), location.getLatitude(), UIUtils.COORDINATE_LATITUDE));
                mLongitudeView.setText(UIUtils.getDDMFromLocation(Application.get(), location.getLongitude(), UIUtils.COORDINATE_LONGITUDE));
                break;
            default:
                // Decimal degrees
                mLatitudeView.setText(mRes.getString(R.string.gps_latitude_value, location.getLatitude()));
                mLongitudeView.setText(mRes.getString(R.string.gps_longitude_value, location.getLongitude()));
                break;
        }

        mFixTime = location.getTime();

        String coordinate[] = address.split(",");
        String a = coordinate[0];
        String b = coordinate[1];
        String c = coordinate[2];
        mCity.setText(a+b+"\n"+c);


        if (location.hasAltitude()) {
            if (mPrefDistanceUnits.equalsIgnoreCase(METERS)) {
                mAltitudeView.setText(mRes.getString(R.string.gps_altitude_value_meters, location.getAltitude()));
            } else {
                // Feet
                mAltitudeView.setText(mRes.getString(R.string.gps_altitude_value_feet, UIUtils.toFeet(location.getAltitude())));
            }
        } else {
            mAltitudeView.setText("");
        }
        if (location.hasSpeed()) {
            if (mPrefSpeedUnits.equalsIgnoreCase(METERS_PER_SECOND)) {
                mSpeedView.setText(mRes.getString(R.string.gps_speed_value_meters_sec, location.getSpeed()));
            } else if (mPrefSpeedUnits.equalsIgnoreCase(KILOMETERS_PER_HOUR)) {
                mSpeedView.setText(mRes.getString(R.string.gps_speed_value_kilometers_hour, UIUtils.toKilometersPerHour(location.getSpeed())));
            } else {
                // Miles per hour
                mSpeedView.setText(mRes.getString(R.string.gps_speed_value_miles_hour, UIUtils.toMilesPerHour(location.getSpeed())));
            }
        } else {
            mSpeedView.setText("");
        }
        if (location.hasBearing()) {
            mBearingView.setText(mRes.getString(R.string.gps_bearing_value, location.getBearing()));
        } else {
            mBearingView.setText("");
        }
        updateLocationAccuracies(location);
        updateSpeedAndBearingAccuracies(location);
        updateFixTime();
    }

    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    public void onProviderEnabled(String provider) {
    }

    public void onProviderDisabled(String provider) {
    }

    @Deprecated
    public void onGpsStatusChanged(int event, GpsStatus status) {
        switch (event) {
            case GpsStatus.GPS_EVENT_STARTED:
                setStarted(true);
                break;

            case GpsStatus.GPS_EVENT_STOPPED:
                setStarted(false);
                break;

            case GpsStatus.GPS_EVENT_FIRST_FIX:
                mTtff = UIUtils.getTtffString(status.getTimeToFirstFix());
                if (mTTFFView != null) {
                    mTTFFView.setText(mTtff);
                }
                break;

            case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
                updateLegacyStatus(status);
                break;
        }
    }

    @Override
    public void onGnssFirstFix(int ttffMillis) {
        mTtff = UIUtils.getTtffString(ttffMillis);
        if (mTTFFView != null) {
            mTTFFView.setText(mTtff);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onSatelliteStatusChanged(GnssStatus status) {
        updateGnssStatus(status);
    }

    @Override
    public void onGnssStarted() {
        setStarted(true);
    }

    @Override
    public void onGnssStopped() {
        setStarted(false);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onGnssMeasurementsReceived(GnssMeasurementsEvent event) {
        // No-op
    }

    @Override
    public void onOrientationChanged(double orientation, double tilt) {
    }

    @Override
    public void onNmeaMessage(String message, long timestamp) {
        if (!isAdded()) {
            // Do nothing if the Fragment isn't added
            return;
        }
        if (message.startsWith("$GPGGA") || message.startsWith("$GNGNS") || message.startsWith("$GNGGA")) {
            Double altitudeMsl = NmeaUtils.getAltitudeMeanSeaLevel(message);
            if (altitudeMsl != null && mNavigating) {
                if (mPrefDistanceUnits.equalsIgnoreCase(METERS)) {
                    mAltitudeMslView.setText(mRes.getString(R.string.gps_altitude_msl_value_meters, altitudeMsl));
                } else {
                    mAltitudeMslView.setText(mRes.getString(R.string.gps_altitude_msl_value_feet, UIUtils.toFeet(altitudeMsl)));
                }
            }
        }
        if (message.startsWith("$GNGSA") || message.startsWith("$GPGSA")) {
            DilutionOfPrecision dop = NmeaUtils.getDop(message);
            if (dop != null && mNavigating) {
                showDopViews();
                mPdopView.setText(mRes.getString(R.string.pdop_value, dop.getPositionDop()));
                mHvdopView.setText(
                        mRes.getString(R.string.hvdop_value, dop.getHorizontalDop(),
                                dop.getVerticalDop()));
            }
        }
    }

    private void showDopViews() {
        mPdopLabelView.setVisibility(View.VISIBLE);
        mPdopView.setVisibility(View.VISIBLE);
        mHvdopLabelView.setVisibility(View.VISIBLE);
        mHvdopView.setVisibility(View.VISIBLE);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void updateGnssStatus(GnssStatus status) {
        mUseLegacyGnssApi = false;
        setStarted(true);
        updateFixTime();

        if (!UIUtils.isFragmentAttached(this)) {
            // Fragment isn't visible, so return to avoid IllegalStateException (see #85)
            return;
        }

        mSnrCn0Title = mRes.getString(R.string.gps_cn0_column_label);

        final int length = status.getSatelliteCount();
        mSvCount = 0;
        mGnssStatus.clear();
        mSbasStatus.clear();
        mViewModel.reset();
        while (mSvCount < length) {
            SatelliteStatus satStatus = new SatelliteStatus(status.getSvid(mSvCount), SatelliteUtils.getGnssConstellationType(status.getConstellationType(mSvCount)),
                    status.getCn0DbHz(mSvCount),
                    status.hasAlmanacData(mSvCount),
                    status.hasEphemerisData(mSvCount),
                    status.usedInFix(mSvCount),
                    status.getElevationDegrees(mSvCount),
                    status.getAzimuthDegrees(mSvCount));
            if (SatelliteUtils.isGnssCarrierFrequenciesSupported()) {
                if (status.hasCarrierFrequencyHz(mSvCount)) {
                    satStatus.setHasCarrierFrequency(true);
                    satStatus.setCarrierFrequencyHz(status.getCarrierFrequencyHz(mSvCount));
                }
            }

            if (satStatus.getGnssType() == GnssType.SBAS) {
                satStatus.setSbasType(SatelliteUtils.getSbasConstellationType(satStatus.getSvid()));
                mSbasStatus.add(satStatus);
            } else {
                mGnssStatus.add(satStatus);
            }
            mSvCount++;
        }
        mViewModel.setStatuses(mGnssStatus, mSbasStatus);

        refreshViews();
    }

    @Deprecated
    private void updateLegacyStatus(GpsStatus status) {
        mUseLegacyGnssApi = true;
        setStarted(true);
        updateFixTime();

        if (!UIUtils.isFragmentAttached(this)) {
            // Fragment isn't visible, so return to avoid IllegalStateException (see #85)
            return;
        }

        mSnrCn0Title = mRes.getString(R.string.gps_snr_column_label);

        Iterator<GpsSatellite> satellites = status.getSatellites().iterator();

        mSvCount = 0;
        mGnssStatus.clear();
        mSbasStatus.clear();
        mViewModel.reset();
        while (satellites.hasNext()) {
            GpsSatellite satellite = satellites.next();

            SatelliteStatus satStatus = new SatelliteStatus(satellite.getPrn(), SatelliteUtils.getGnssType(satellite.getPrn()),
                    satellite.getSnr(),
                    satellite.hasAlmanac(),
                    satellite.hasEphemeris(),
                    satellite.usedInFix(),
                    satellite.getElevation(),
                    satellite.getAzimuth());

            if (satStatus.getGnssType() == GnssType.SBAS) {
                satStatus.setSbasType(SatelliteUtils.getSbasConstellationTypeLegacy(satStatus.getSvid()));
                mSbasStatus.add(satStatus);
            } else {
                mGnssStatus.add(satStatus);
            }
            mSvCount++;
        }

        mViewModel.setStatuses(mGnssStatus, mSbasStatus);

        refreshViews();
    }

    private void refreshViews() {
        sortLists();

        updateListVisibility();
        mGnssAdapter.notifyDataSetChanged();
        mSbasAdapter.notifyDataSetChanged();
    }

    private void sortLists() {
        final int sortBy = PreferenceUtils.getSatSortOrderFromPreferences();
        // Below switch statement order must match arrays.xml sort_sats order
        switch (sortBy) {
            case 0:
                // Sort by Constellation
                mGnssStatus = SortUtil.Companion.sortByGnssThenId(mGnssStatus);
                mSbasStatus = SortUtil.Companion.sortBySbasThenId(mSbasStatus);
                break;
            case 1:
                // Sort by Carrier Frequency
                mGnssStatus = SortUtil.Companion.sortByCarrierFrequencyThenId(mGnssStatus);
                mSbasStatus = SortUtil.Companion.sortByCarrierFrequencyThenId(mSbasStatus);
                break;
            case 2:
                // Sort by Signal Strength
                mGnssStatus = SortUtil.Companion.sortByCn0(mGnssStatus);
                mSbasStatus = SortUtil.Companion.sortByCn0(mSbasStatus);
                break;
            case 3:
                // Sort by Used in Fix
                mGnssStatus = SortUtil.Companion.sortByUsedThenId(mGnssStatus);
                mSbasStatus = SortUtil.Companion.sortByUsedThenId(mSbasStatus);
                break;
            case 4:
                // Sort by Constellation, Carrier Frequency
                mGnssStatus = SortUtil.Companion.sortByGnssThenCarrierFrequencyThenId(mGnssStatus);
                mSbasStatus = SortUtil.Companion.sortBySbasThenCarrierFrequencyThenId(mSbasStatus);
                break;
            case 5:
                // Sort by Constellation, Signal Strength
                mGnssStatus = SortUtil.Companion.sortByGnssThenCn0ThenId(mGnssStatus);
                mSbasStatus = SortUtil.Companion.sortBySbasThenCn0ThenId(mSbasStatus);
                break;
            case 6:
                // Sort by Constellation, Used in Fix
                mGnssStatus = SortUtil.Companion.sortByGnssThenUsedThenId(mGnssStatus);
                mSbasStatus = SortUtil.Companion.sortBySbasThenUsedThenId(mSbasStatus);
                break;
        }
    }

    private void setupUnitPreferences() {
        SharedPreferences settings = Application.getPrefs();
        Application app = Application.get();

        mPrefDistanceUnits = settings
                .getString(app.getString(R.string.pref_key_preferred_distance_units_v2), METERS);
        mPrefSpeedUnits = settings
                .getString(app.getString(R.string.pref_key_preferred_speed_units_v2), METERS_PER_SECOND);
    }

    /**
     * Sets the visibility of the lists
     */
    private void updateListVisibility() {
        if (!mGnssStatus.isEmpty()) {
            mGnssNotAvailableView.setVisibility(View.GONE);
            mGnssStatusList.setVisibility(View.VISIBLE);
        } else {
            mGnssNotAvailableView.setVisibility(View.VISIBLE);
            mGnssStatusList.setVisibility(View.GONE);
        }
        if (!mSbasStatus.isEmpty()) {
            mSbasNotAvailableView.setVisibility(View.GONE);
            mSbasStatusList.setVisibility(View.VISIBLE);
        } else {
            mSbasNotAvailableView.setVisibility(View.VISIBLE);
            mSbasStatusList.setVisibility(View.GONE);
        }
    }

    private void showSortByDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.menu_option_sort_by);

        final int currentSatOrder = PreferenceUtils.getSatSortOrderFromPreferences();

        builder.setSingleChoiceItems(R.array.sort_sats, currentSatOrder,
                (dialog, index) -> {
                    setSortByClause(index);
                    dialog.dismiss();
                });
        AlertDialog dialog = builder.create();
        dialog.setOwnerActivity(getActivity());
        dialog.show();
    }

    @SuppressLint("StringFormatInvalid")
    private void showTimeErrorDialog(long time) {
        java.text.DateFormat format = SimpleDateFormat.getDateTimeInstance(java.text.DateFormat.LONG, java.text.DateFormat.LONG);

        TextView textView = (TextView) getLayoutInflater().inflate(R.layout.error_text_dialog, null);
        textView.setText(getString(R.string.error_time_message, format.format(time), DateTimeUtils.Companion.getNUM_DAYS_TIME_VALID()));

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.error_time_title);
        builder.setView(textView);
        Drawable drawable = getResources().getDrawable(android.R.drawable.ic_dialog_alert);
        DrawableCompat.setTint(drawable, getResources().getColor(R.color.colorPrimary));
        builder.setIcon(drawable);
        builder.setNeutralButton(R.string.main_help_close,
                (dialog, which) -> dialog.dismiss()
        );
        AlertDialog dialog = builder.create();
        dialog.setOwnerActivity(getActivity());
        dialog.show();
    }

    /**
     * Saves the "sort by" order to preferences
     *
     * @param index the index of R.array.sort_sats that should be set
     */
    private void setSortByClause(int index) {
        final String[] sortOptions = getResources().getStringArray(R.array.sort_sats);
        PreferenceUtils.saveString(getResources()
                        .getString(R.string.pref_key_default_sat_sort),
                        sortOptions[index]);
    }

    private class SatelliteStatusAdapter extends RecyclerView.Adapter<SatelliteStatusAdapter.ViewHolder> {

        ConstellationType mConstellationType;

        public SatelliteStatusAdapter(ConstellationType constellationType) {
            mConstellationType = constellationType;
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            private final TextView svId;
            private final TextView gnssFlagHeader;
            private final ImageView gnssFlag;
            private final LinearLayout gnssFlagLayout;
            private final TextView carrierFrequency;
            private final TextView signal;
            private final TextView elevation;
            private final TextView azimuth;
            private final TextView statusFlags;

            ViewHolder(View v) {
                super(v);
                svId = v.findViewById(R.id.sv_id);
                gnssFlagHeader = v.findViewById(R.id.gnss_flag_header);
                gnssFlag = v.findViewById(R.id.gnss_flag);
                gnssFlagLayout = v.findViewById(R.id.gnss_flag_layout);
                carrierFrequency = v.findViewById(R.id.carrier_frequency);
                signal = v.findViewById(R.id.signal);
                elevation = v.findViewById(R.id.elevation);
                azimuth = v.findViewById(R.id.azimuth);
                statusFlags = v.findViewById(R.id.status_flags);
            }

            public TextView getSvId() {
                return svId;
            }

            public TextView getFlagHeader() {
                return gnssFlagHeader;
            }

            public ImageView getFlag() {
                return gnssFlag;
            }

            public LinearLayout getFlagLayout() {
                return gnssFlagLayout;
            }

            public TextView getCarrierFrequency() {
                return carrierFrequency;
            }

            public TextView getSignal() {
                return signal;
            }

            public TextView getElevation() {
                return elevation;
            }

            public TextView getAzimuth() {
                return azimuth;
            }

            public TextView getStatusFlags() {
                return statusFlags;
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            View v = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.status_row_item, viewGroup, false);
            return new ViewHolder(v);
        }

        @Override
        public int getItemCount() {
            // Add 1 for header row
            if (mConstellationType == ConstellationType.GNSS) {
                return mGnssStatus.size() + 1;
            } else {
                return mSbasStatus.size() + 1;
            }
        }

        public void onBindViewHolder(ViewHolder v, final int position) {
            if (position == 0) {
                // Show the header field for the GNSS flag and hide the ImageView
                v.getFlagHeader().setVisibility(View.VISIBLE);
                v.getFlag().setVisibility(View.GONE);
                v.getFlagLayout().setVisibility(View.GONE);

                // Populate the header fields
                v.getSvId().setText(mRes.getString(R.string.gps_prn_column_label));
                v.getSvId().setTypeface(v.getSvId().getTypeface(), Typeface.BOLD);
                if (mConstellationType == ConstellationType.GNSS) {
                    v.getFlagHeader().setText(mRes.getString(R.string.gnss_flag_image_label));
                } else {
                    v.getFlagHeader().setText(mRes.getString(R.string.sbas_flag_image_label));
                }
                if (SatelliteUtils.isGnssCarrierFrequenciesSupported()) {
                    v.getCarrierFrequency().setVisibility(View.VISIBLE);
                    v.getCarrierFrequency().setText(mRes.getString(R.string.gps_carrier_column_label));
                    v.getCarrierFrequency().setTypeface(v.getCarrierFrequency().getTypeface(), Typeface.BOLD);
                } else {
                    v.getCarrierFrequency().setVisibility(View.GONE);
                }
                v.getSignal().setText(mSnrCn0Title);
                v.getSignal().setTypeface(v.getSignal().getTypeface(), Typeface.BOLD);
                v.getElevation().setText(mRes.getString(R.string.gps_elevation_column_label));
                v.getElevation().setTypeface(v.getElevation().getTypeface(), Typeface.BOLD);
                v.getAzimuth().setText(mRes.getString(R.string.gps_azimuth_column_label));
                v.getAzimuth().setTypeface(v.getAzimuth().getTypeface(), Typeface.BOLD);
                v.getStatusFlags().setText(mRes.getString(R.string.gps_flags_column_label));
                v.getStatusFlags().setTypeface(v.getStatusFlags().getTypeface(), Typeface.BOLD);
            } else {
                // There is a header at 0, so the first data row will be at position - 1, etc.
                int dataRow = position - 1;

                List<SatelliteStatus> sats;
                if (mConstellationType == ConstellationType.GNSS) {
                    sats = mGnssStatus;
                } else {
                    sats = mSbasStatus;
                }

                // Show the row field for the GNSS flag mImage and hide the header
                v.getFlagHeader().setVisibility(View.GONE);
                v.getFlag().setVisibility(View.VISIBLE);
                v.getFlagLayout().setVisibility(View.VISIBLE);

                // Populate status data for this row
                v.getSvId().setText(Integer.toString(sats.get(dataRow).getSvid()));
                v.getFlag().setScaleType(ImageView.ScaleType.FIT_START);

                GnssType type = sats.get(dataRow).getGnssType();
                switch (type) {
                    case NAVSTAR:
                        v.getFlag().setVisibility(View.VISIBLE);
                        v.getFlag().setImageDrawable(mFlagUsa);
                        break;
                    case GLONASS:
                        v.getFlag().setVisibility(View.VISIBLE);
                        v.getFlag().setImageDrawable(mFlagRussia);
                        break;
                    case QZSS:
                        v.getFlag().setVisibility(View.VISIBLE);
                        v.getFlag().setImageDrawable(mFlagJapan);
                        break;
                    case BEIDOU:
                        v.getFlag().setVisibility(View.VISIBLE);
                        v.getFlag().setImageDrawable(mFlagChina);
                        break;
                    case GALILEO:
                        v.getFlag().setVisibility(View.VISIBLE);
                        v.getFlag().setImageDrawable(mFlagEU);
                        break;
                    case IRNSS:
                        v.getFlag().setVisibility(View.VISIBLE);
                        v.getFlag().setImageDrawable(mFlagIndia);
                        break;
                    case SBAS:
                        setSbasFlag(sats.get(dataRow), v.getFlag());
                        break;
                    case UNKNOWN:
                        v.getFlag().setVisibility(View.INVISIBLE);
                        break;
                }
                if (SatelliteUtils.isGnssCarrierFrequenciesSupported()) {
                    if (sats.get(dataRow).getHasCarrierFrequency()) {
                        String carrierLabel = CarrierFreqUtils.getCarrierFrequencyLabel(sats.get(dataRow));
                        if (!carrierLabel.equals(CarrierFreqUtils.CF_UNKNOWN)) {
                            // Make sure it's the normal text size (in case it's previously been
                            // resized to show raw number).  Use another TextView for default text size.
                            v.getCarrierFrequency().setTextSize(COMPLEX_UNIT_PX, v.getSvId().getTextSize());
                            // Show label such as "L1"
                            v.getCarrierFrequency().setText(carrierLabel);
                        } else {
                            // Shrink the size so we can show raw number
                            v.getCarrierFrequency().setTextSize(COMPLEX_UNIT_DIP, 10);
                            // Show raw number for carrier frequency - Convert Hz to MHz
                            float carrierMhz = MathUtils.toMhz(sats.get(dataRow).getCarrierFrequencyHz());
                            v.getCarrierFrequency().setText(String.format("%.3f", carrierMhz));
                        }
                    } else {
                        v.getCarrierFrequency().setText("");
                    }
                } else {
                    v.getCarrierFrequency().setVisibility(View.GONE);
                }
                if (sats.get(dataRow).getCn0DbHz() != SatelliteStatus.NO_DATA) {
                    v.getSignal().setText(String.format("%.1f", sats.get(dataRow).getCn0DbHz()));
                } else {
                    v.getSignal().setText("");
                }

                if (sats.get(dataRow).getElevationDegrees() != SatelliteStatus.NO_DATA) {
                    v.getElevation().setText(mRes.getString(R.string.gps_elevation_column_value,
                            sats.get(dataRow).getElevationDegrees()).replace(".0", "").replace(",0", ""));
                } else {
                    v.getElevation().setText("");
                }

                if (sats.get(dataRow).getAzimuthDegrees() != SatelliteStatus.NO_DATA) {
                    v.getAzimuth().setText(mRes.getString(R.string.gps_azimuth_column_value,
                            sats.get(dataRow).getAzimuthDegrees()).replace(".0", "").replace(",0", ""));
                } else {
                    v.getAzimuth().setText("");
                }

                char[] flags = new char[3];
                flags[0] = !sats.get(dataRow).getHasAlmanac() ? ' ' : 'A';
                flags[1] = !sats.get(dataRow).getHasEphemeris() ? ' ' : 'E';
                flags[2] = !sats.get(dataRow).getUsedInFix() ? ' ' : 'U';
                v.getStatusFlags().setText(new String(flags));
            }
        }

        private void setSbasFlag(SatelliteStatus status, ImageView flag) {
            switch(status.getSbasType()) {
                case WAAS:
                    flag.setVisibility(View.VISIBLE);
                    flag.setImageDrawable(mFlagUsa);
                    break;
                case EGNOS:
                    flag.setVisibility(View.VISIBLE);
                    flag.setImageDrawable(mFlagEU);
                    break;
                case GAGAN:
                    flag.setVisibility(View.VISIBLE);
                    flag.setImageDrawable(mFlagIndia);
                    break;
                case MSAS:
                    flag.setVisibility(View.VISIBLE);
                    flag.setImageDrawable(mFlagJapan);
                    break;
                case SDCM:
                    flag.setVisibility(View.VISIBLE);
                    flag.setImageDrawable(mFlagRussia);
                    break;
                case SNAS:
                    flag.setVisibility(View.VISIBLE);
                    flag.setImageDrawable(mFlagChina);
                    break;
                case SACCSA:
                    flag.setVisibility(View.VISIBLE);
                    flag.setImageDrawable(mFlagICAO);
                    break;
                case UNKNOWN:
                default:
                    flag.setVisibility(View.INVISIBLE);
            }
        }
    }
}
