package net.ictcampus.paketdienst;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

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
        SharedPreferences.Editor editor = settingFile.edit();
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
            editor.putBoolean("MUSIC", true);
            editor.commit();
        } else if (!settingMusic.isChecked()) {
            editor.putBoolean("MUSIC", false);
            editor.commit();
        }

        //SoundEffects
        Switch settingMusicEffects = (Switch) findViewById(R.id.settingMusicEffects);
        if (settingMusicEffects.isChecked()) {
            editor.putBoolean("MUSICEFFECTS", true);
            editor.commit();
        } else if (!settingMusicEffects.isChecked()) {
            editor.putBoolean("MUSICEFFECTS", false);
            editor.commit();
        }

        //DarkMode
        Switch settingDark = (Switch) findViewById(R.id.settingDark);
        Boolean dark= settingFile.getBoolean("DARK",false);
        settingDark.setChecked(dark);
        settingDark.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                editor.putBoolean("DARK", isChecked);
                editor.commit();
                if(isChecked){
                    darkMode();
                }
                else {
                    whiteMode();
                }
            }
        });
        //Dark Mode
        if(settingFile.getBoolean("DARK",false)){
            darkMode();
        }
        else {
            whiteMode();
        }
        //Clickable TextView
        TextView impressum = (TextView) findViewById(R.id.textViewImpressum);
        impressum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(getApplicationContext(), ImpressumActivity.class);
                startActivity(intent1);
            }
        });
    }

    private void whiteMode() {
        View view = this.getWindow().getDecorView();
        view.setBackgroundColor(Color.WHITE);
        TextView title= (TextView) findViewById(R.id.textView);
        TextView text1= (TextView) findViewById(R.id.textView10);
        TextView text2= (TextView) findViewById(R.id.textView12);
        TextView text3= (TextView) findViewById(R.id.textView11);
        TextView text4= (TextView) findViewById(R.id.textView8);
        TextView text5= (TextView) findViewById(R.id.textView9);
        TextView text6= (TextView) findViewById(R.id.textView4);
        TextView impressum = (TextView) findViewById(R.id.textViewImpressum);
        Spinner spinner = (Spinner) findViewById(R.id.settingSpinner);
        ImageButton imageButton= (ImageButton) findViewById(R.id.imageButton);
        text1.setTextColor(Color.BLACK);
        text2.setTextColor(Color.BLACK);
        text3.setTextColor(Color.BLACK);
        text4.setTextColor(Color.BLACK);
        text5.setTextColor(Color.BLACK);
        text6.setTextColor(Color.BLACK);
        title.setTextColor(Color.BLACK);
        impressum.setTextColor(Color.BLACK);
        spinner.setBackgroundColor(Color.WHITE);
        imageButton.setImageResource(R.drawable.settingbtn_black);
    }

    private void darkMode(){
        View view = this.getWindow().getDecorView();
        view.setBackgroundColor(Color.BLACK);
        TextView title= (TextView) findViewById(R.id.textView);
        TextView text1= (TextView) findViewById(R.id.textView10);
        TextView text2= (TextView) findViewById(R.id.textView12);
        TextView text3= (TextView) findViewById(R.id.textView11);
        TextView text4= (TextView) findViewById(R.id.textView8);
        TextView text5= (TextView) findViewById(R.id.textView9);
        TextView text6= (TextView) findViewById(R.id.textView4);
        TextView impressum = (TextView) findViewById(R.id.textViewImpressum);
        Spinner spinner = (Spinner) findViewById(R.id.settingSpinner);
        ImageButton imageButton= (ImageButton) findViewById(R.id.imageButton);
        text1.setTextColor(Color.WHITE);
        text2.setTextColor(Color.WHITE);
        text3.setTextColor(Color.WHITE);
        text4.setTextColor(Color.WHITE);
        text5.setTextColor(Color.WHITE);
        text6.setTextColor(Color.WHITE);
        title.setTextColor(Color.WHITE);
        impressum.setTextColor(Color.WHITE);
        spinner.setBackgroundColor(Color.WHITE);
        imageButton.setImageResource(R.drawable.settingbtn_white);
    }

}
