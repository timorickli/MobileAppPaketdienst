package net.ictcampus.paketdienst;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

public class ImpressumActivity extends AppCompatActivity {
    private TextView title, text1, text2, text3, text4, text5;
    private ImageButton imageButton;
    private View view;

    /**
     * OnCreate Method
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_impressum);

        view = this.getWindow().getDecorView();
        title = (TextView) findViewById(R.id.textView);
        text1 = (TextView) findViewById(R.id.kontakt);
        text2 = (TextView) findViewById(R.id.credits);
        text3 = (TextView) findViewById(R.id.email);
        text4 = (TextView) findViewById(R.id.textView1);
        text5 = (TextView) findViewById(R.id.textView2);
        imageButton = (ImageButton) findViewById(R.id.imageButton);

        //Set a clickable e-mail link in the TextView
        TextView email = (TextView) findViewById(R.id.email);
        email.setText(Html.fromHtml("<a href=\"mailto:timo.rickli@ict-campus.net\">Email: timo.rickli@ict-campus.net</a>"));
        email.setMovementMethod(LinkMovementMethod.getInstance());

        //ImageButton with onClick event for back to settings
        ImageButton ib = findViewById(R.id.imageButton);
        ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SettingActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });

        //Dark Mode check
        SharedPreferences settingFile = getSharedPreferences("settings", Context.MODE_PRIVATE);
        if (settingFile.getBoolean("DARK", false)) {
            darkMode();
        } else {
            whiteMode();
        }
    }

    /**
     * Method for the normal Mode Theme
     */
    private void whiteMode() {
        view.setBackgroundColor(Color.WHITE);
        text1.setTextColor(Color.BLACK);
        text2.setTextColor(Color.BLACK);
        text3.setTextColor(Color.BLACK);
        text4.setTextColor(Color.BLACK);
        text5.setTextColor(Color.BLACK);
        title.setTextColor(Color.BLACK);
        imageButton.setImageResource(R.drawable.settingbtn_black);
    }

    /**
     * Method for the Darkmode color Theme
     */
    private void darkMode() {
        view.setBackgroundColor(Color.BLACK);
        text1.setTextColor(Color.WHITE);
        text2.setTextColor(Color.WHITE);
        text3.setTextColor(Color.WHITE);
        text4.setTextColor(Color.WHITE);
        text5.setTextColor(Color.WHITE);
        title.setTextColor(Color.WHITE);
        imageButton.setImageResource(R.drawable.settingbtn_white);
    }
}
