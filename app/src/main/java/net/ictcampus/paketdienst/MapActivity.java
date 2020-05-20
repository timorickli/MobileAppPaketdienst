package net.ictcampus.paketdienst;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Switch;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Locale;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.jar.Manifest;

public class MapActivity extends Activity implements OnMapReadyCallback {

    private GoogleMap map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        //Create Map
        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.google_map);
        mapFragment.getMapAsync(this);
        //Location Manager
        //Button Click Event
        ImageButton ib = (ImageButton) findViewById(R.id.imageButton);
        ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(getApplicationContext(), MenuActivity.class);
                startActivity(intent);
            }
        });
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        map = googleMap;
        map.setMyLocationEnabled(true);
        map.setBuildingsEnabled(false);
        map.getUiSettings().setIndoorLevelPickerEnabled(false);
        map.getUiSettings().setMapToolbarEnabled(false);

        if(map != null){
            CameraPosition cameraPosition= new CameraPosition.Builder()
                    .target(new LatLng(getLocation().getLatitude(), getLocation().getLongitude())).zoom(17.0f).build();
            CameraUpdate cameraUpdate= CameraUpdateFactory
                    .newCameraPosition(cameraPosition);
            map.moveCamera(cameraUpdate);
            createPakets();
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
                }
                else if (net){
                    location= locationManager.getLastKnownLocation(locationManager.NETWORK_PROVIDER);
                    Log.d("Network Provider", "Network Provider");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return location;
    }
public void createPakets() {
    double randomLong;
    double randomLati;
    final double multi = 0.0001;
    Random random = new Random();

    for (int y = 1; y < 4; y++) {
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
        createIconPakets(y, randomLati, randomLong);

    }
}
public void createIconPakets(int nummber, double randomLati, double randomLong){
        final int height= 100;
        final int width= 100;
        switch (nummber){
            case 1:
                BitmapDrawable bitmapdraw = (BitmapDrawable)getResources().getDrawable(R.drawable.paket_einzeln) ;
                Bitmap b = bitmapdraw.getBitmap();
                Bitmap marker = Bitmap.createScaledBitmap(b, width, height, false);
                map.addMarker(new MarkerOptions()
                        .position(new LatLng(randomLati, randomLong))
                        .icon(BitmapDescriptorFactory.fromBitmap(marker))
                );
                break;
            case 2:
                BitmapDrawable bitmapdraw1 = (BitmapDrawable)getResources().getDrawable(R.drawable.paket_stapel) ;
                Bitmap b1 = bitmapdraw1.getBitmap();
                Bitmap marker1 = Bitmap.createScaledBitmap(b1, width, height, false);
                map.addMarker(new MarkerOptions()
                        .position(new LatLng(randomLati, randomLong))
                        .icon(BitmapDescriptorFactory.fromBitmap(marker1))

                );
                break;
            default:
                BitmapDrawable bitmapdraw2 = (BitmapDrawable)getResources().getDrawable(R.drawable.paket_laster) ;
                Bitmap b2 = bitmapdraw2.getBitmap();
                Bitmap marker2 = Bitmap.createScaledBitmap(b2, width, height, false);
                map.addMarker(new MarkerOptions()
                        .position(new LatLng(randomLati, randomLong))
                        .icon(BitmapDescriptorFactory.fromBitmap(marker2))
                );

        }
}


    public void destroyPakets(){

    }
    public void createMailBox(){

    }
}
