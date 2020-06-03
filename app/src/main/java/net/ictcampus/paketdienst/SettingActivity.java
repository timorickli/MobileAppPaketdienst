package net.ictcampus.paketdienst;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Switch;

public class SettingActivity extends AppCompatActivity {
    private boolean settingDarkCheck;
    private boolean settingMusicCheck;
    private boolean settingMusicEffectsCheck;
    private SharedPreferences settingFile;
    private int selectPosition;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        settingFile = getSharedPreferences("settings", Context.MODE_PRIVATE);
        //Spinner selection
        Spinner spinner = (Spinner) findViewById(R.id.settingSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.settingMapBack, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        int selection= settingFile.getInt("MAPSTYLE",0);
        spinner.setSelection(selection);

        //ImageButton with ClickListener
        ImageButton ib = findViewById(R.id.imageButton);
        ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = settingFile.edit();
                editor.putInt("MAPSTYLE", spinner.getSelectedItemPosition());
                editor.commit();
                Intent intent = new Intent(getApplicationContext(), MenuActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });

        //Switch Button Listener
        Switch settingMusic = (Switch) findViewById(R.id.settingMusic);

        //Music
        if (settingMusic.isChecked()) {
            SharedPreferences.Editor editor = settingFile.edit();
            editor.putInt("MAPSTYLE", spinner.getSelectedItemPosition());
            editor.commit();
        } else if (!settingMusic.isChecked()) {
            settingMusicCheck = false;
        }

        //SoundEffects
        Switch settingMusicEffects = (Switch) findViewById(R.id.settingMusicEffects);
        if (settingMusicEffects.isChecked()) {
            settingMusicEffectsCheck = true;
        } else if (!settingMusicEffects.isChecked()) {
            settingMusicEffectsCheck = false;
        }

        //DarkMode
        Switch settingDark = (Switch) findViewById(R.id.settingDark);
        if (settingDark.isChecked()) {
            settingDarkCheck = true;
        } else if (!settingDark.isChecked()) {
            settingDarkCheck = false;
        }
    }
}
