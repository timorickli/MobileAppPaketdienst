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

import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.CameraUpdate;
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
import java.util.Random;

public class MapActivity extends Activity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener , Serializable {
    private SharedPreferences inventoryFile;
    private Bundle extra= new Bundle();
    private GoogleMap map;
    private static ArrayList<MarkerOptions> markerOptions= new ArrayList<MarkerOptions>();
    private static ArrayList<MarkerOptions> markerOptionsMailBox= new ArrayList<MarkerOptions>();
    private static ArrayList<Marker> markers= new ArrayList<Marker>();
    private static ArrayList<Marker> markersMailBox= new ArrayList<Marker>();
    final int height= 100;
    final int width= 100;
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
        inventoryFile = getSharedPreferences("inventory", Context.MODE_PRIVATE);
        ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MapActivity.this, MenuActivity.class);
                if(markerOptions!=null) {
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
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setMyLocationEnabled(true);
        map.setBuildingsEnabled(false);
        map.getUiSettings().setIndoorLevelPickerEnabled(false);
        map.getUiSettings().setMapToolbarEnabled(false);
        map.setOnMarkerClickListener(this);
        if(map != null){
            map.moveCamera(CameraUpdateFactory
                    .newCameraPosition(new CameraPosition.Builder()
                            .target(new LatLng(getLocation().getLatitude(),getLocation().getLongitude())).zoom(17.0f).build()));
        }

        if(inventoryFile.getInt("PACKAGES", 0)>0){
            if(markerOptionsMailBox.size()==0){
                createMailBox();
            }
            if(markerOptionsMailBox.size() >0){
                if (getIntent().getParcelableArrayListExtra("locationMailBox") != null){
                    markerOptionsMailBox = getIntent().getParcelableArrayListExtra("locationMailBox");
                }
                addMarkersMailBox();
            }
        }
        else if(inventoryFile.getInt("PACKAGES", 0)==0) {
            if (markerOptions.size() == 0) {
                createIconPakets();
            }
            if (markerOptions.size() > 0) {
                addMarkers();
            }
        }
        try{
            boolean success= map.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.retro_style));
        }
        catch (Resources.NotFoundException e){
            Log.e("Map", "Map style not found");
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
        latLng= new LatLng(randomLati, randomLong);
        return latLng;
    }
    public void createIconPakets(){
        for(int x= 0; x < 3;x++) {
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
    private void addMarkers(){
        markers.clear();
        for (MarkerOptions markerOption:markerOptions) {
            markers.add(map.addMarker(markerOption));
        }
        setTag();
    }

    private void addMarkersMailBox(){
        for (int y= 0; y< markerOptionsMailBox.size();y++){
            markersMailBox.add(map.addMarker(markerOptionsMailBox.get(y)));
        }
    }

    private void setTag(){
        int tag= 0;
        for (Marker marker:markers
             ) {
            tag=tag+3;
            marker.setTag(tag);
        }
    }

    public void createMailBox(){
        BitmapDrawable bitmapdraw2 = (BitmapDrawable) getResources().getDrawable(R.drawable.mailbox);
        Bitmap b2 = bitmapdraw2.getBitmap();
        Bitmap marker = Bitmap.createScaledBitmap(b2, width, height, false);
        for(int i=0; i< inventoryFile.getInt("PACKAGES", 0); i++){
            markerOptionsMailBox.add(new MarkerOptions()
                    .position(createLocation())
                    .icon(BitmapDescriptorFactory.fromBitmap(marker))
            );
            markersMailBox.add(map.addMarker(markerOptionsMailBox.get(i)));
        }
        Log.d("MailBox", "Creating");
    }
    private void startArMarker(Marker marker) {
        LatLng pos,posMarker;
        int markerOptSize;
        MarkerOptions markerOpt;
        Log.d("Marker", "Marker ist in der nähe");
        Intent intent = new Intent(getApplicationContext(), ArActivity.class);
        if (markers.contains(marker)) {
            switch (Integer.parseInt(marker.getTag().toString())) {
                case 3:
                    intent.putExtra("id", 3);
                    break;
                case 6:
                    intent.putExtra("id", 2);
                    break;
                case 9:
                    intent.putExtra("id", 4);
                    break;
                default:
                    intent.putExtra("id", 1);
                    break;
            }
            intent.putExtra("location", markerOptions);
            startActivity(intent);
        } else if (markersMailBox.contains(marker)) {
            markerOptSize= markerOptionsMailBox.size();
            ArrayList<MarkerOptions> mailBoxSend= new ArrayList<MarkerOptions>();
            for(int i = 0;i<markerOptSize;i++){
                markerOpt= markerOptionsMailBox.get(i);
                pos= markerOpt.getPosition();
                posMarker= marker.getPosition();
                if(!posMarker.equals(pos)){
                    mailBoxSend.add(markerOpt);
                }
            }
            intent.putExtra("id", 1);
            intent.putExtra("locationMailBox", mailBoxSend);
            startActivity(intent);
        }
    }


    @Override
    public boolean onMarkerClick(Marker marker) {
        int tag;
        Location locationPerson= getLocation();
        LatLng locatinMarker= marker.getPosition();
        Log.d("Marker", "Marker geklickt");
        if(locationPerson.getLatitude()-locatinMarker.latitude <= 0.0004 && locationPerson.getLongitude()-locatinMarker.longitude<= 0.0004) {
            if (locationPerson.getLatitude() - locatinMarker.latitude >= -0.0004 && locationPerson.getLongitude() - locatinMarker.longitude >= -0.0004) {
                startArMarker(marker);


            }
            else {
                Log.d("Marker", "Marker ist nicht in der nähe 1");
                showDialog(marker);
            }

        }
        else {
            Log.d("Marker", "Marker ist nicht in der nähe");
            showDialog(marker);
        }
        return true;
    }
    private void showDialog(Marker marker) {
        AlertDialog.Builder showWarning= new AlertDialog.Builder(MapActivity.this);
        showWarning.setTitle(R.string.alert_dialogTitle);
        showWarning.setMessage(R.string.alert_dialogMessage);

        showWarning.setPositiveButton(R.string.alert_dialogUeberspringen, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startArMarker(marker);
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


}

