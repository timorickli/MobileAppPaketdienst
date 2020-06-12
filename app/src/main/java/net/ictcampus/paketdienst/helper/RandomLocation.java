package net.ictcampus.paketdienst.helper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import net.ictcampus.paketdienst.MapActivity;

import java.util.Random;

/**
 * Class to get the location and create a random Location in range of around 100m
 */
public class RandomLocation {

    /**
     * Gets Location via GPS or Network
     *
     * @return current location
     */
    @SuppressLint("MissingPermission")
    public Location getLocation(LocationManager locationManager) {
        Location location = null;
        try {

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
     * Creates random coordinates within a certain range
     *
     * @return coordinates
     */
    public LatLng createLocation(LocationManager locationManager) {
        LatLng latLng;
        double randomLong;
        double randomLati;
        final double multi = 0.0001;
        Random random = new Random();
        int operatorLong = random.nextInt((2 - 1) + 1) + 1;
        int operatorLati = random.nextInt((2 - 1) + 1) + 1;
        MapActivity map = new MapActivity();

        //Switch for negative or positive value for one part of the coordinate
        switch (operatorLong) {
            case 1:
                //Random coordinate within range
                randomLong = getLocation(locationManager).getLongitude() + (random.nextInt((10 - 1) + 1) + 1) * multi;
                break;
            default:
                //Random coordinate within range
                randomLong = getLocation(locationManager).getLongitude() - (random.nextInt((10 - 1) + 1) + 1) * multi;
                break;
        }

        //Switch for negative or positive value for the other part of the coordinate
        switch (operatorLati) {
            case 1:
                //Random coordinate within range
                randomLati = getLocation(locationManager).getLatitude() + (random.nextInt((10 - 1) + 1) + 1) * multi;
                break;
            default:
                //Random coordinate within range
                randomLati = getLocation(locationManager).getLatitude() - (random.nextInt((10 - 1) + 1) + 1) * multi;
                break;
        }

        //returns random coordinates
        latLng = new LatLng(randomLati, randomLong);
        return latLng;
    }

}
