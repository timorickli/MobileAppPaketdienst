package net.ictcampus.paketdienst;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.ToggleButton;

public class SettingActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private boolean settingDarkCheck;
    private boolean settingMusicCheck;
    private boolean settingMusicEffectsCheck;
    private String spinnerSelection;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        //Spinner selection
        Spinner spinner= (Spinner) findViewById(R.id.settingSpinner);
        ArrayAdapter<CharSequence> adapter= ArrayAdapter.createFromResource(this,R.array.settingMapBack, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        //ImageButton with ClickListener
        ImageButton ib = findViewById(R.id.imageButton);
        ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(getApplicationContext(), MenuActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });
        //Switch Button Listener
        //Music
        Switch settingMusic = (Switch) findViewById(R.id.settingMusic);
        if(settingMusic.isChecked()){
            settingMusicCheck=true;
        }
        else if(!settingMusic.isChecked()){
            settingMusicCheck=false;
        }
        //SoundEffects
        Switch settingMusicEffects= (Switch) findViewById(R.id.settingMusicEffects);
        if(settingMusicEffects.isChecked()){
            settingMusicEffectsCheck=true;
        }
        else if(!settingMusicEffects.isChecked()){
            settingMusicEffectsCheck=false;
        }
        //DarkMode
        Switch settingDark =(Switch) findViewById(R.id.settingDark);
        if(settingDark.isChecked()){
            settingDarkCheck=true;
        }
        else if(!settingDark.isChecked()){
            settingDarkCheck=false;
        }


    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        spinnerSelection= parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        spinnerSelection= parent.getItemAtPosition(0).toString();
    }
}
