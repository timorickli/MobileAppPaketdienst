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

public class MainActivity extends AppCompatActivity {
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkPersmissionsLocationFine();
    }

    //Testen ob die Berechtigungen für die Location gesetzt wurden
    private void checkPersmissionsLocationFine(){
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            //wenn sie bereits gesetzt sind (vor API level 24)
            Intent intent= new Intent(getApplicationContext(), MapActivity.class);
            startActivity(intent);

        }
        else {
            //Keine Berechtigungen auf die Location
            if(shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)){
                Toast.makeText(this, "Location permission is needed to show the current position.",Toast.LENGTH_SHORT).show();
            }
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
        }
    }

    private void checkPermissionLocationCorase(){
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            //wenn sie bereits gesetzt sind (vor API level 24)
            Intent intent= new Intent(getApplicationContext(), MapActivity.class);
            startActivity(intent);

        }
        else {
            //Keine Berechtigungen auf die Location
            if(shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION)){
                Toast.makeText(this, "Location permission is needed to show the current position.",Toast.LENGTH_SHORT).show();
            }
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},2);
        }
    }

    private void checkPermissionCamera(){
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            //wenn sie bereits gesetzt sind (vor API level 24)
            Intent intent= new Intent(getApplicationContext(), MapActivity.class);
            startActivity(intent);

        }
        else {
            //Keine Berechtigungen auf die Location
            if(shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)){
                Toast.makeText(this, "Location permission is needed to show the AR vision.",Toast.LENGTH_SHORT).show();
            }
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA},3);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                // Schauen ob die Berechtigung angenommen wurde
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent= new Intent(getApplicationContext(), MapActivity.class);
                    checkPermissionLocationCorase();
                } else {
                    //Berechtigung Fehlgeschlagen
                    Toast.makeText(MainActivity.this, "Berechtigung fehlgeschlagen", Toast.LENGTH_SHORT).show();
                }
                break;
            case 2:
                // Schauen ob die Berechtigung angenommen wurde
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent= new Intent(getApplicationContext(), MapActivity.class);
                    checkPermissionCamera();
                } else {
                    //Berechtigung Fehlgeschlagen
                    Toast.makeText(MainActivity.this, "Berechtigung fehlgeschlagen", Toast.LENGTH_SHORT).show();
                }
                break;

            case 3:
                // Schauen ob die Berechtigung angenommen wurde
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent= new Intent(getApplicationContext(), MapActivity.class);
                    startActivity(intent);
                } else {
                    //Berechtigung Fehlgeschlagen
                    Toast.makeText(MainActivity.this, "Berechtigung fehlgeschlagen", Toast.LENGTH_SHORT).show();
                }
                break;


            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                break;
        }
    }
}
