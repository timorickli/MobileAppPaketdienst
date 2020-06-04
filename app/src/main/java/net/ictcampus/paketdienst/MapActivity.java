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
import android.widget.Button;
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

public class MapActivity extends Activity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, Serializable {
    private SharedPreferences inventoryFile, settingFile;
    private Bundle extra = new Bundle();
    private GoogleMap map;
    private InterstitialAd mInterstitialAd;
    private static ArrayList<MarkerOptions> markerOptions = new ArrayList<MarkerOptions>();
    private static ArrayList<MarkerOptions> markerOptionsMailBox = new ArrayList<MarkerOptions>();
    private static ArrayList<Marker> markers = new ArrayList<Marker>();
    private static ArrayList<Marker> markersMailBox = new ArrayList<Marker>();
    private final int height = 100;
    private final int width = 100;
    boolean timerRunning;
    long endTime, timeLeft;
    CountDownTimer countDownTimer;
    private static final long DELIVERY_TIME = 60 * 30 * 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        //Create Map
        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.google_map);
        mapFragment.getMapAsync(this);

        //Catch SharedPreferences
        settingFile = getSharedPreferences("settings", Context.MODE_PRIVATE);
        inventoryFile = getSharedPreferences("inventory", Context.MODE_PRIVATE);

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
                startActivity(intent);
            }
        });
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

        //Prepare the AD
        prepareAD();

        //Dark Mode
        SharedPreferences settingFile = getSharedPreferences("settings", Context.MODE_PRIVATE);
        if (settingFile.getBoolean("DARK", false)) {
            darkMode();
        } else {
            whiteMode();
        }
    }

    private void whiteMode() {
        ImageButton imageButton = (ImageButton) findViewById(R.id.imageButton);
        imageButton.setImageResource(R.drawable.settingbtn_black);
    }

    private void darkMode() {
        ImageButton imageButton = (ImageButton) findViewById(R.id.imageButton);
        imageButton.setImageResource(R.drawable.settingbtn_white);
    }

    public void prepareAD() {
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.adid));
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setMyLocationEnabled(true);
        map.setBuildingsEnabled(false);
        map.getUiSettings().setIndoorLevelPickerEnabled(false);
        map.getUiSettings().setMapToolbarEnabled(false);
        map.setOnMarkerClickListener(this);
        if (map != null) {
            map.moveCamera(CameraUpdateFactory
                    .newCameraPosition(new CameraPosition.Builder()
                            .target(new LatLng(getLocation().getLatitude(), getLocation().getLongitude())).zoom(17.0f).build()));
        }

        if (inventoryFile.getInt("PACKAGES", 0) > 0) {
            if (markerOptionsMailBox.size() == 0) {
                createMailBox();
            }
            if (markerOptionsMailBox.size() > 0) {
                addMarkersMailBox();
            }
        } else if (inventoryFile.getInt("PACKAGES", 0) == 0) {
            if (markerOptions.size() == 0) {
                createIconPakets();
            }
            if (markerOptions.size() > 0) {
                addMarkers();
            }
        }
        loadMapStyle();

    }

    /**
     * Changes selectable map-styles
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

    @SuppressLint("MissingPermission")
    public Location getLocation() {
        Location location = null;
        try {
            LocationManager locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);

            //schauen, welcher service verfügbar ist GPS/Netzwerk
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
        switch (operatorLong) {
            case 1:
                randomLong = getLocation().getLongitude() + (random.nextInt((10 - 1) + 1) + 1) * multi;
                break;
            default:
                randomLong = getLocation().getLongitude() - (random.nextInt((10 - 1) + 1) + 1) * multi;
                break;
        }
        switch (operatorLati) {
            case 1:
                randomLati = getLocation().getLatitude() + (random.nextInt((10 - 1) + 1) + 1) * multi;
                break;
            default:
                randomLati = getLocation().getLatitude() - (random.nextInt((10 - 1) + 1) + 1) * multi;
                break;

        }
        latLng = new LatLng(randomLati, randomLong);
        return latLng;
    }

    /**
     * Creates three different packages
     */
    public void createIconPakets() {
        for (int x = 0; x < 3; x++) {
            switch (x) {
                case 0:
                    BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.paket_einzeln);
                    Bitmap b = bitmapdraw.getBitmap();
                    Bitmap marker = Bitmap.createScaledBitmap(b, width, height, false);
                    markerOptions.add(new MarkerOptions()
                            .position(createLocation())
                            .icon(BitmapDescriptorFactory.fromBitmap(marker)
                            ));
                    markers.add(map.addMarker(markerOptions.get(0)));
                    break;
                case 1:
                    BitmapDrawable bitmapdraw1 = (BitmapDrawable) getResources().getDrawable(R.drawable.paket_stapel);
                    Bitmap b1 = bitmapdraw1.getBitmap();
                    Bitmap marker1 = Bitmap.createScaledBitmap(b1, width, height, false);
                    markerOptions.add(new MarkerOptions()
                            .position(createLocation())
                            .icon(BitmapDescriptorFactory.fromBitmap(marker1))
                    );
                    markers.add(map.addMarker(markerOptions.get(1)));
                    break;
                case 2:
                    BitmapDrawable bitmapdraw2 = (BitmapDrawable) getResources().getDrawable(R.drawable.paket_laster);
                    Bitmap b2 = bitmapdraw2.getBitmap();
                    Bitmap marker2 = Bitmap.createScaledBitmap(b2, width, height, false);
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
     * Converts markeroptions to marker
     */
    private void addMarkers() {
        markers.clear();
        for (MarkerOptions markerOption : markerOptions) {
            markers.add(map.addMarker(markerOption));
        }
        setTag();
    }

    /**
     * Converts markeroptions to marker
     */
    private void addMarkersMailBox() {
        for (int y = 0; y < markerOptionsMailBox.size(); y++) {
            markersMailBox.add(map.addMarker(markerOptionsMailBox.get(y)));
        }
    }

    /**
     * Sets tag for different package types
     */
    private void setTag() {
        int tag = 0;
        for (Marker marker : markers
        ) {
            tag = tag + 3;
            marker.setTag(tag);
        }
    }

    /**
     * Creation of Mailbox-Markers
     */
    public void createMailBox() {
        BitmapDrawable bitmapdraw2 = (BitmapDrawable) getResources().getDrawable(R.drawable.mailbox);
        Bitmap b2 = bitmapdraw2.getBitmap();
        Bitmap marker = Bitmap.createScaledBitmap(b2, width, height, false);
        for (int i = 0; i < inventoryFile.getInt("PACKAGES", 0); i++) {
            markerOptionsMailBox.add(new MarkerOptions()
                    .position(createLocation())
                    .icon(BitmapDescriptorFactory.fromBitmap(marker))
            );
            markersMailBox.add(map.addMarker(markerOptionsMailBox.get(i)));
        }
        Log.d("MailBox", "Creating");
    }

    private void startArMarker(Marker marker) {
        LatLng pos, posMarker;
        int markerOptSize;
        MarkerOptions markerOpt;
        Log.d("Marker", "Marker ist in der nähe");
        Intent intent = new Intent(getApplicationContext(), ArActivity.class);

        if (markers.contains(marker)) {
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
                default:
                    intent.putExtra("id", 1);
                    break;
            }
            startDeliveryTimer();
            markerOptions.clear();

            SharedPreferences timers = getSharedPreferences("Timers", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = timers.edit();
            editor.putLong("millisLeftDelivery", timeLeft);
            editor.putBoolean("timerRunningDelivery", timerRunning);
            editor.putLong("endTimeDelivery", endTime);
            editor.apply();

            intent.putExtra("location", markerOptions);
            startActivity(intent);
        } else if (markersMailBox.contains(marker)) {
            markerOptSize = markerOptionsMailBox.size();
            ArrayList<MarkerOptions> mailBoxSend = new ArrayList<MarkerOptions>();
            for (int i = 0; i < markerOptSize; i++) {
                markerOpt = markerOptionsMailBox.get(i);
                pos = markerOpt.getPosition();
                posMarker = marker.getPosition();
                if (!posMarker.equals(pos)) {
                    mailBoxSend.add(markerOpt);
                }
            }
            SharedPreferences timers = getSharedPreferences("Timers", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = timers.edit();
            if (markersMailBox.isEmpty()) {
                editor.remove("millisLeftDelivery");
                editor.remove("timerRunningDelivery");
                editor.remove("endTimeDelivery");
                editor.apply();
            } else {
                editor.putLong("millisLeftDelivery", timeLeft);
                editor.putBoolean("timerRunningDelivery", timerRunning);
                editor.putLong("endTimeDelivery", endTime);
                editor.apply();
            }
            intent.putExtra("id", 1);
            intent.putExtra("locationMailBox", mailBoxSend);
            startActivity(intent);
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        double range;
        if (inventoryFile.getInt("RANGE", 0) == 1) {
            range = 0.0007;
        } else {
            range = 0.0004;
        }
        Location locationPerson = getLocation();
        LatLng locatinMarker = marker.getPosition();
        Log.d("Marker", "Marker geklickt");
        if (locationPerson.getLatitude() - locatinMarker.latitude <= range && locationPerson.getLongitude() - locatinMarker.longitude <= range) {
            if (locationPerson.getLatitude() - locatinMarker.latitude >= -range && locationPerson.getLongitude() - locatinMarker.longitude >= -range) {
                startArMarker(marker);
            } else {
                Log.d("Marker", "Marker ist nicht in der nähe 1");
                showDialog(marker);
            }
        } else {
            Log.d("Marker", "Marker ist nicht in der nähe");
            showDialog(marker);
        }
        return true;
    }

    private void showDialog(Marker marker) {
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
                startArMarker(marker);
            }
        });
        AlertDialog.Builder showWarning = new AlertDialog.Builder(MapActivity.this);
        showWarning.setTitle(R.string.alert_dialogTitle);
        showWarning.setMessage(R.string.alert_dialogMessage);

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
        timerRunning = true;
        endTime = System.currentTimeMillis() + timeLeft;

        //Countdown Timer initialization
        countDownTimer = new CountDownTimer(timeLeft, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeft = millisUntilFinished;
                updateTime();
            }

            @Override
            public void onFinish() {
                timerRunning = false;

                //Overwrites Timers File
                SharedPreferences timers = getSharedPreferences("Timers", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = timers.edit();
                editor.putLong("millisLeftDelivery", timeLeft);
                editor.putBoolean("timerRunningDelivery", timerRunning);
                editor.putLong("endTimeDelivery", endTime);
                editor.apply();

                //Activity reload
                finish();
                startActivity(getIntent());
            }
        }.start();
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        SharedPreferences timers = getSharedPreferences("Timers", Context.MODE_PRIVATE);
        SharedPreferences inventoryFile = getSharedPreferences("inventory", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = inventoryFile.edit();
        timeLeft = timers.getLong("millisLeftDelivery", DELIVERY_TIME);
        timerRunning = timers.getBoolean("timerRunningDelivery", false);

        //Checks timer state
        if (timerRunning && inventoryFile.getInt("PACKAGES", 0) != 0) {
            endTime = timers.getLong("endTimeDelivery", 0);
            timeLeft = endTime - System.currentTimeMillis();
            if (timeLeft < 0) {
                timeLeft = 0;
                timerRunning = false;
                editor.putInt("PACKAGES", 0)
                        .apply();
            } else {
                updateTime();
                startDeliveryTimer();
            }
        }
    }

    /**
     * Updates the displayed time on TextView
     */
    private void updateTime() {
        TextView textView = findViewById(R.id.timeRemaining);

        int minutes = (int) (timeLeft / 1000) / 60;
        int seconds = (int) (timeLeft / 1000) % 60;

        //String formatter
        String timeFormat = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        textView.setText("Time remaining: " + timeFormat);
    }
}

