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

    /**
     * OnCreate Method
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_impressum);
        //Set a clickable e-mail link in the TextView
        TextView email= (TextView) findViewById(R.id.email);
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
        if(settingFile.getBoolean("DARK",false)){
            darkMode();
        }
        else {
            whiteMode();
        }
    }

    /**
     * Method for the normal Mode Theme
     */
    private void whiteMode() {
        View view = this.getWindow().getDecorView();
        view.setBackgroundColor(Color.WHITE);
        TextView title= (TextView) findViewById(R.id.textView);
        TextView text1= (TextView) findViewById(R.id.kontakt);
        TextView text2= (TextView) findViewById(R.id.credits);
        TextView text3= (TextView) findViewById(R.id.email);
        TextView text4= (TextView) findViewById(R.id.textView1);
        TextView text5= (TextView) findViewById(R.id.textView2);
        ImageButton imageButton= (ImageButton) findViewById(R.id.imageButton);
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
        View view = this.getWindow().getDecorView();
        view.setBackgroundColor(Color.BLACK);
        TextView title= (TextView) findViewById(R.id.textView);
        TextView text1= (TextView) findViewById(R.id.kontakt);
        TextView text2= (TextView) findViewById(R.id.credits);
        TextView text3= (TextView) findViewById(R.id.email);
        TextView text4= (TextView) findViewById(R.id.textView1);
        TextView text5= (TextView) findViewById(R.id.textView2);
        ImageButton imageButton= (ImageButton) findViewById(R.id.imageButton);
        text1.setTextColor(Color.WHITE);
        text2.setTextColor(Color.WHITE);
        text3.setTextColor(Color.WHITE);
        text4.setTextColor(Color.WHITE);
        text5.setTextColor(Color.WHITE);
        title.setTextColor(Color.WHITE);
        imageButton.setImageResource(R.drawable.settingbtn_white);
    }
}
