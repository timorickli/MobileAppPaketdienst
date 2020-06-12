package net.ictcampus.paketdienst;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

/**
 * Class for the Settings Activity to set the darkmode or change the map style
 */
public class SettingActivity extends AppCompatActivity {
    private TextView title, text4, text5, text6, impressum;

    /*To later add music settings
    private TextView text1, text2, text3;
     */

    private SharedPreferences settingFile;
    private ImageButton imageButton;
    private Spinner spinner;
    private View view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        //Initialization of all variables
        view = this.getWindow().getDecorView();
        title = (TextView) findViewById(R.id.textView);

        /*To later add Music controls
        text1 = (TextView) findViewById(R.id.textView10);
        text2 = (TextView) findViewById(R.id.textView12);
        text3 = (TextView) findViewById(R.id.textView11);
         */

        text4 = (TextView) findViewById(R.id.textView8);
        text5 = (TextView) findViewById(R.id.textView9);
        text6 = (TextView) findViewById(R.id.textView4);
        impressum = (TextView) findViewById(R.id.textViewImpressum);
        spinner = (Spinner) findViewById(R.id.settingSpinner);
        imageButton = (ImageButton) findViewById(R.id.imageButton);

        //Get the Settings File
        settingFile = getSharedPreferences("settings", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settingFile.edit();

        //Get Spinner selection and fill it with data
        Spinner spinner = (Spinner) findViewById(R.id.settingSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.settingMapBack, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        //Sets the default selection on the data in the file
        int selection = settingFile.getInt("MAPSTYLE", 0);
        spinner.setSelection(selection);

        //ImageButton with onClick event for back to menu
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

        //DarkMode
        Switch settingDark = (Switch) findViewById(R.id.settingDark);
        Boolean dark = settingFile.getBoolean("DARK", false);
        settingDark.setChecked(dark);
        settingDark.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                editor.putBoolean("DARK", isChecked);
                editor.commit();
                if (isChecked) {
                    darkMode();
                } else {
                    whiteMode();
                }
            }
        });

        //Clickable TextView to get to the Impressum
        TextView impressum = (TextView) findViewById(R.id.textViewImpressum);
        impressum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(getApplicationContext(), ImpressumActivity.class);
                startActivity(intent1);
            }
        });

        //Dark Mode check
        if (settingFile.getBoolean("DARK", false)) {
            darkMode();
        } else {
            whiteMode();
        }

        /*To add later Music controls
        //Switch Button Listeners for music
        Switch settingMusic = (Switch) findViewById(R.id.settingMusic);
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

         */

    }

    /**
     * Method to load normal Mode Theme
     */
    private void whiteMode() {
        view.setBackgroundColor(Color.WHITE);

        /*to add later music settings
        text1.setTextColor(Color.BLACK);
        text2.setTextColor(Color.BLACK);
        text3.setTextColor(Color.BLACK);
         */

        text4.setTextColor(Color.BLACK);
        text5.setTextColor(Color.BLACK);
        text6.setTextColor(Color.BLACK);
        title.setTextColor(Color.BLACK);
        impressum.setTextColor(Color.BLACK);
        spinner.setBackgroundColor(Color.WHITE);
        imageButton.setImageResource(R.drawable.settingbtn_black);
    }

    /**
     * Method to load dark Mode Theme
     */
    private void darkMode() {
        view.setBackgroundColor(Color.BLACK);

        /*to add later music settings
        text1.setTextColor(Color.WHITE);
        text2.setTextColor(Color.WHITE);
        text3.setTextColor(Color.WHITE);
         */

        text4.setTextColor(Color.WHITE);
        text5.setTextColor(Color.WHITE);
        text6.setTextColor(Color.WHITE);
        title.setTextColor(Color.WHITE);
        impressum.setTextColor(Color.WHITE);
        spinner.setBackgroundColor(Color.WHITE);
        imageButton.setImageResource(R.drawable.settingbtn_white);
    }
}
