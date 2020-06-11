package net.ictcampus.paketdienst;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;

import static com.google.ar.sceneform.rendering.HeadlessEngineWrapper.TAG;

/**
 * Class to show the Map and to process the logical Parts of the Game
 */
public class MapActivity extends Activity implements OnMapReadyCallback, Serializable, GoogleMap.OnMarkerClickListener {
    private static ArrayList<MarkerOptions> markerOptionsMailBox = new ArrayList<MarkerOptions>();
    private static ArrayList<MarkerOptions> markerOptions = new ArrayList<MarkerOptions>();
    private static ArrayList<Marker> markersMailBox = new ArrayList<Marker>();
    private static ArrayList<Marker> markers = new ArrayList<Marker>();
    private SharedPreferences inventoryFile, settingFile, timersFile;
    private static final long DELIVERY_TIME = 60 * 20 * 1000;
    private InterstitialAd mInterstitialAd;
    private CountDownTimer countDownTimer;
    private ImageButton imageButton;
    private long endTime, timeLeft;
    private final int height = 100;
    private final int width = 100;
    private boolean timerRunning;
    SharedPreferences.Editor editorInventory, editorTimers;
    private GoogleMap map;

    /**
     * Prepares everything, when activity is created
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        imageButton = (ImageButton) findViewById(R.id.imageButton);

        //Create Map
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.google_map);
        mapFragment.getMapAsync(this);

        //Catch SharedPreferences
        settingFile = getSharedPreferences("settings", Context.MODE_PRIVATE);
        inventoryFile = getSharedPreferences("inventory", Context.MODE_PRIVATE);
        timersFile = getSharedPreferences("timers", Context.MODE_PRIVATE);
        editorInventory = inventoryFile.edit();
        editorTimers = timersFile.edit();

        //Button Click Event
        ImageButton ib = (ImageButton) findViewById(R.id.imageButton);
        ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MapActivity.this, MenuActivity.class);
                if (markerOptions != null) {
                    intent.putExtra("location", markerOptions);
                }
                if (markerOptionsMailBox != null) {
                    intent.putExtra("locationMailBox", markerOptionsMailBox);
                }
                beforeChange();
                startActivity(intent);
                overridePendingTransition(R.anim.slide_up,R.anim.slide_non);
            }
        });

        //Information from intent
        if (getIntent().getParcelableArrayListExtra("location") != null) {
            markerOptions = getIntent().getParcelableArrayListExtra("location");
        }
        if (getIntent().getParcelableArrayListExtra("locationMailBox") != null) {
            markerOptionsMailBox = getIntent().getParcelableArrayListExtra("locationMailBox");
        }
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        //Prepares the AD
        prepareAD();

        //Dark Mode
        if (settingFile.getBoolean("DARK", false)) {
            darkMode();
        } else {
            whiteMode();
        }
    }
    /**
     * After onCreate, timer gets initialized/prepared
     */
    @Override
    protected void onStart() {
        super.onStart();

        //Gets previous timer values
        timeLeft = timersFile.getLong("millisLeft", DELIVERY_TIME);
        timerRunning = timersFile.getBoolean("timerRunning", false);

        //Checks timer state and starts it if needed
        if (timerRunning) {
            updateTime();
            endTime = timersFile.getLong("endTime", 0);
            timeLeft = endTime - System.currentTimeMillis();
            if (timeLeft < 0) {
                timeLeft = 0;
                timerRunning = false;
            } else {
                updateTime();
                startDeliveryTimer();
            }
        }
    }
    /**
     * When maps ready, prepares marker and style...
     *
     * @param googleMap current map
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setMyLocationEnabled(true);
        map.setBuildingsEnabled(false);
        map.getUiSettings().setIndoorLevelPickerEnabled(false);
        map.getUiSettings().setMapToolbarEnabled(false);


        //Sets camera on Person and zooms in
        if (map != null) {
            map.moveCamera(CameraUpdateFactory
                    .newCameraPosition(new CameraPosition.Builder()
                            .target(new LatLng(getLocation().getLatitude(), getLocation().getLongitude())).zoom(17.0f).build()));
        }

        //Creates markers with logos depending on inventory
        if (inventoryFile.getInt("PACKAGES", 0) > 0) {
            if (markerOptionsMailBox.size() == 0) {
                createMailBox();
            }
            addMarkersMailBox();
        } else if (inventoryFile.getInt("PACKAGES", 0) == 0) {
            if (markerOptions.size() == 0) {
                createIconPackages();
            }
            addMarkers();
        }
        loadMapStyle();
        map.setOnMarkerClickListener(this);
    }
    /**
     * Gets Location via GPS or Network
     *
     * @return current location
     */
    @SuppressLint("MissingPermission")
    public Location getLocation() {
        Location location = null;
        try {
            LocationManager locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);

            //looks, which service is currently available
            boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            boolean net = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            if (!gps && !net) {

            } else {
                if (gps) {
                    location = locationManager.getLastKnownLocation(locationManager.GPS_PROVIDER);
                    Log.d("GPS Provider", "GPS Provider");
                } else if (net) {
                    location = locationManager.getLastKnownLocation(locationManager.NETWORK_PROVIDER);
                    Log.d("Network Provider", "Network Provider");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return location;
    }
    /**
     * Clickevent handler for marker on map
     *
     * @param marker clicked marker
     * @return true/false
     */
    @Override
    public boolean onMarkerClick(Marker marker) {
        double range;
        if(markers.contains(marker)){
            Log.d("Marker", "Marker contains");
        }
        //Changes range, if you have item active
        if (inventoryFile.getInt("RANGE", 0) == 1) {
            range = 0.0007;
        } else {
            range = 0.0004;
        }

        //Gets location of person and marker
        Location locationPerson = getLocation();
        LatLng locationMarker = marker.getPosition();
        Log.d("Marker", "Marker geklickt");

        //Compares locations with range
        if (locationPerson.getLatitude() - locationMarker.latitude <= range && locationPerson.getLongitude() - locationMarker.longitude <= range) {
            if (locationPerson.getLatitude() - locationMarker.latitude >= -range && locationPerson.getLongitude() - locationMarker.longitude >= -range) {
                startArMarker(marker);
            } else {
                Log.d("Marker", "Marker ist nicht in der nähe 1");
                showDialog(marker);
            }
        } else {
            Log.d("Marker", "Marker ist nicht in der nähe");
            showDialog(marker);
        }
        return false;
    }
    /**
     * Changes map-styles
     */
    private void loadMapStyle() {
        switch (settingFile.getInt("MAPSTYLE", 0)) {
            case 1:
                try {
                    boolean success = map.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.dark_style));
                } catch (Resources.NotFoundException e) {
                    Log.e("Map", "Map style DARK not found");
                }
                break;

            case 2:
                try {
                    boolean success = map.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.retro_style));
                } catch (Resources.NotFoundException e) {
                    Log.e("Map", "Map style RETRO not found");
                }
                break;

            case 3:
                try {
                    boolean success = map.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.fancy_style));
                } catch (Resources.NotFoundException e) {
                    Log.e("Map", "Map style FANCY not found");
                }
                break;

            default:
                try {
                    boolean success = map.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.normal_style));
                } catch (Resources.NotFoundException e) {
                    Log.e("Map", "Map style DEFAULT not found");
                }
                break;
        }
    }

    /**
     * Creates random coordinates within a certain range
     *
     * @return coordinates
     */
    public LatLng createLocation() {
        LatLng latLng;
        double randomLong;
        double randomLati;
        final double multi = 0.0001;
        Random random = new Random();
        int operatorLong = random.nextInt((2 - 1) + 1) + 1;
        int operatorLati = random.nextInt((2 - 1) + 1) + 1;

        //Switch for negative or positive value for one part of the coordinate
        switch (operatorLong) {
            case 1:
                //Random coordinate within range
                randomLong = getLocation().getLongitude() + (random.nextInt((10 - 1) + 1) + 1) * multi;
                break;
            default:
                //Random coordinate within range
                randomLong = getLocation().getLongitude() - (random.nextInt((10 - 1) + 1) + 1) * multi;
                break;
        }

        //Switch for negative or positive value for the other part of the coordinate
        switch (operatorLati) {
            case 1:
                //Random coordinate within range
                randomLati = getLocation().getLatitude() + (random.nextInt((10 - 1) + 1) + 1) * multi;
                break;
            default:
                //Random coordinate within range
                randomLati = getLocation().getLatitude() - (random.nextInt((10 - 1) + 1) + 1) * multi;
                break;
        }

        //returns random coordinates
        latLng = new LatLng(randomLati, randomLong);
        return latLng;
    }

    /**
     * Creates three different packages
     */
    public void createIconPackages() {
        for (int x = 0; x < 3; x++) {
            switch (x) {
                case 0:
                    //Logo for marker
                    BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.paket_einzeln);
                    Bitmap b = bitmapdraw.getBitmap();
                    Bitmap marker = Bitmap.createScaledBitmap(b, width, height, false);

                    //Creates marker
                    markerOptions.add(new MarkerOptions()
                            .position(createLocation())
                            .icon(BitmapDescriptorFactory.fromBitmap(marker)
                            ));
                    markers.add(map.addMarker(markerOptions.get(0)));
                    break;

                case 1:
                    //Logo for marker
                    BitmapDrawable bitmapdraw1 = (BitmapDrawable) getResources().getDrawable(R.drawable.paket_stapel);
                    Bitmap b1 = bitmapdraw1.getBitmap();
                    Bitmap marker1 = Bitmap.createScaledBitmap(b1, width, height, false);

                    //Creates marker
                    markerOptions.add(new MarkerOptions()
                            .position(createLocation())
                            .icon(BitmapDescriptorFactory.fromBitmap(marker1))
                    );
                    markers.add(map.addMarker(markerOptions.get(1)));
                    break;

                case 2:
                    //Logo for marker
                    BitmapDrawable bitmapdraw2 = (BitmapDrawable) getResources().getDrawable(R.drawable.paket_laster);
                    Bitmap b2 = bitmapdraw2.getBitmap();
                    Bitmap marker2 = Bitmap.createScaledBitmap(b2, width, height, false);

                    //Creates marker
                    markerOptions.add(new MarkerOptions()
                            .position(createLocation())
                            .icon(BitmapDescriptorFactory.fromBitmap(marker2))
                    );
                    markers.add(map.addMarker(markerOptions.get(2)));
                    break;
            }
        }
        setTag();
    }

    /**
     * Creates out of markeroptions different markers
     */
    private void addMarkers() {
        markers.clear();
        for (MarkerOptions markerOption : markerOptions) {
            markers.add(map.addMarker(markerOption));
        }
        setTag();
    }

    /**
     * Creates out of markeroptions different markers
     */
    private void addMarkersMailBox() {
        for (int y = 0; y < markerOptionsMailBox.size(); y++) {
            markersMailBox.add(map.addMarker(markerOptionsMailBox.get(y)));
        }
        setTagMailBox();
    }

    /**
     * Sets tag for different package types
     */
    private void setTag() {
        int tag = 0;
        for (Marker marker : markers) {
            tag = tag + 3;
            marker.setTag(tag);
        }
    }
    /**
     * Sets tag for different package types
     */
    private void setTagMailBox() {
        int tag = 1;
        for (Marker marker : markersMailBox) {
            marker.setTag(tag);
        }
    }



    /**
     * Creation of Mailbox-Markers
     */
    public void createMailBox() {

        //Logo for marker
        BitmapDrawable bitmapdraw2 = (BitmapDrawable) getResources().getDrawable(R.drawable.mailbox);
        Bitmap b2 = bitmapdraw2.getBitmap();
        Bitmap marker = Bitmap.createScaledBitmap(b2, width, height, false);

        //For each package in inventory, display one on map
        for (int i = 0; i < inventoryFile.getInt("PACKAGES", 0); i++) {
            markerOptionsMailBox.add(new MarkerOptions()
                    .position(createLocation())
                    .icon(BitmapDescriptorFactory.fromBitmap(marker))
            );
            markersMailBox.add(map.addMarker(markerOptionsMailBox.get(i)));
        }
        Log.d("MailBox", "Creating");
    }

    /**
     *Test Method for JUnit test as a exchage of createMailBox()
     */
    public void createMailBoxTest() {
        //For each package in inventory, display one on map
        for (int i = 0; i < 3; i++) {
            markerOptionsMailBox.add(new MarkerOptions()
                    .position(new LatLng(12.2, 123.2))
            );
        }
    }
    /**
     * Starts activity depending on marker clicked
     *
     * @param marker clicked marker
     */
    private void startArMarker(Marker marker) {
        LatLng pos, posMarker;
        int markerOptSize;
        MarkerOptions markerOpt;
        Log.d("Marker", "Marker ist in der nähe");
        Intent intent = new Intent(getApplicationContext(), ArActivity.class);
        //Depending on markers tag, tag is given to the next Activity
        if (!marker.getTag().equals(1)) {
            switch (Integer.parseInt(marker.getTag().toString())) {
                case 3:
                    intent.putExtra("id", 2);
                    break;
                case 6:
                    intent.putExtra("id", 3);
                    break;
                case 9:
                    intent.putExtra("id", 4);
                    break;
            }

            markers.clear();
            beforeChange();
            intent.putExtra("location", markerOptions);
            startActivity(intent);
        }

        //Checks if its a mailbox
        if(marker.getTag().equals(1)) {
            markerOptSize = markerOptionsMailBox.size();
            ArrayList<MarkerOptions> mailBoxSend = new ArrayList<MarkerOptions>();

            //Iterates through all markers in Mailbox, to decide which one to remove/send
            for (int i = 0; i < markerOptSize; i++) {
                markerOpt = markerOptionsMailBox.get(i);
                pos = markerOpt.getPosition();
                posMarker = marker.getPosition();
                if (!posMarker.equals(pos)) {
                    mailBoxSend.add(markerOpt);
                }
            }

            beforeChange();
            markerOptionsMailBox.clear();
            intent.putExtra("id", 1);
            intent.putExtra("locationMailBox", mailBoxSend);
            startActivity(intent);
        }
    }


    /**
     * Show Dialog if not within range
     *
     * @param marker clicked marker
     */
    private void showDialog(Marker marker) {

        //Detects if ad is closed
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
                startArMarker(marker);
            }
        });
        //Dialog loading
        AlertDialog.Builder showWarning = new AlertDialog.Builder(MapActivity.this);
        showWarning.setTitle(R.string.alert_dialogTitle);
        showWarning.setMessage(R.string.alert_dialogMessage);

        //Clicklistener to skip
        showWarning.setPositiveButton(R.string.alert_dialogUeberspringen, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();

                } else {
                    Log.d(TAG, "The interstitial wasn't loaded yet.");
                    startArMarker(marker);
                }

            }
        });

        //Clicklistener to cancel
        showWarning.setNegativeButton(R.string.alert_dialogAbbrechen, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog alert11 = showWarning.create();
        alert11.show();
    }

    /**
     * Creates and starts a timespan to deliver the package
     */
    private void startDeliveryTimer() {

        //Evaluates end time, so timer can run in background
        endTime = System.currentTimeMillis() + timeLeft;

        //Starts new timer
        countDownTimer = new CountDownTimer(timeLeft, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeft = millisUntilFinished;
                updateTime();
            }

            @Override
            public void onFinish() {
                timerRunning = false;
                editorInventory.putInt("PACKAGES", 0);
            }
        }.start();
        timerRunning = true;
        updateTime();
    }

    /**
     * Saves timer values before new Activity
     */
    private void beforeChange() {

        //Edits values, that timer basically runs in background
        editorTimers.putLong("millisLeft", timeLeft);
        editorTimers.putBoolean("timerRunning", timerRunning);
        editorTimers.putLong("endTime", endTime);
        editorTimers.apply();

        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    /**
     * Updates the displayed time on TextView
     */
    @SuppressLint("SetTextI18n")
    private void updateTime() {
        TextView textView = findViewById(R.id.timeRemaining);

        int minutes = (int) (timeLeft / 1000) / 60;
        int seconds = (int) (timeLeft / 1000) % 60;

        //String formatter
        String timeFormat = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        textView.setText("Time remaining: " + timeFormat);
    }

    /**
     * Button, depending on style
     */
    private void whiteMode() {
        imageButton.setImageResource(R.drawable.settingbtn_black);
    }

    /**
     * Button, depending on style
     */
    private void darkMode() {
        imageButton.setImageResource(R.drawable.settingbtn_white);
    }

    /**
     * Button, depending on style
     */
    public void prepareAD() {
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.adid));
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
    }
    public static ArrayList<MarkerOptions> getMarkerOptionsMailBox() {
        return markerOptionsMailBox;
    }
    public static ArrayList<Marker> getMarkers() {
        return markers;
    }
}

