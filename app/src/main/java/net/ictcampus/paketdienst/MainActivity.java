package net.ictcampus.paketdienst;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

/**
 * Class for the Startup to check to permissions
 */
public class MainActivity extends AppCompatActivity {
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        intent = new Intent(getApplicationContext(), MapActivity.class);
        setContentView(R.layout.activity_main);
        checkPersmissionsLocationFine();
    }

    /**
     * Checks if permissions are given, and asks for them
     */
    private void checkPersmissionsLocationFine() {
        //If already granted
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            startActivity(intent);
        } else {
            //No permissions on location
            if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                Toast.makeText(this, "Location permission is needed to show the current position.", Toast.LENGTH_SHORT).show();
            }
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
    }

    private void checkPermissionLocationCorase() {
        //If corase already granted
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            startActivity(intent);
        } else {
            //No permissions on location
            if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION)) {
                Toast.makeText(this, "Location permission is needed to show the current position.", Toast.LENGTH_SHORT).show();
            }
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 2);
        }
    }

    private void checkPermissionCamera() {
        //If camerpermission already granted
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            startActivity(intent);
        } else {
            //No permissions on camera
            if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                Toast.makeText(this, "Location permission is needed to show the AR vision.", Toast.LENGTH_SHORT).show();
            }
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, 3);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                //Looks permission granted
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkPermissionLocationCorase();
                } else {
                    //Permissions not granted
                    Toast.makeText(MainActivity.this, "Berechtigung fehlgeschlagen", Toast.LENGTH_SHORT).show();
                }
                break;

            case 2:
                //Looks permission granted
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkPermissionCamera();
                } else {
                    //Permissions not granted
                    Toast.makeText(MainActivity.this, "Berechtigung fehlgeschlagen", Toast.LENGTH_SHORT).show();
                }
                break;

            case 3:
                //Looks permission granted
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startActivity(intent);
                } else {
                    //Permissions not granted
                    Toast.makeText(MainActivity.this, "Berechtigung fehlgeschlagen", Toast.LENGTH_SHORT).show();
                }
                break;

            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                break;
        }
    }
}
