package net.ictcampus.paketdienst;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.Locale;

public class ShopActivity extends AppCompatActivity implements View.OnClickListener {
    TextView txtItem1, txtItem2, txtItem3, txtItem4;
    Button btnItem1, btnItem2, btnItem3, btnItem4;
    ImageButton btnHome;
    private static final long START_TIME_IN_MILLIS = 60*30*1000;
    private CountDownTimer mCountDownTimer;
    private boolean mTimerRunning;
    private long mTimeLeftInMillis;
    private long mEndTime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);

        txtItem1 = findViewById(R.id.itemText1);
        txtItem2 = findViewById(R.id.itemText2);
        txtItem3 = findViewById(R.id.itemText3);
        txtItem4 = findViewById(R.id.itemText4);

        btnItem1 = findViewById(R.id.button);
        btnItem2 = findViewById(R.id.button2);
        btnItem3 = findViewById(R.id.button3);
        btnItem4 = findViewById(R.id.button4);
        btnItem1.setText("100");
        btnItem2.setText("200");
        btnItem3.setText("250");
        btnItem4.setText("400");
        btnHome = findViewById(R.id.imageButton2);
        btnItem1.setOnClickListener(this);
        btnItem2.setOnClickListener(this);
        btnItem3.setOnClickListener(this);
        btnItem4.setOnClickListener(this);
        btnHome.setOnClickListener(this);

        txtItem1.setText("Briefmarke:\nErhöht Reichweite");
        txtItem2.setText("Zollgebühren:\nDoppeltes Einokommen");
        txtItem3.setText("Pakete:\n10 zusätzliche Pakete");
        txtItem4.setText("B-Post:\nErhöht Zeit vorübergehend");
    }

    @Override
    public void onClick(View v) {
        Intent intent;

        SharedPreferences inventoryFile = getSharedPreferences("inventory", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = inventoryFile.edit();
        editor.putInt("TOKENS", 5000)
                .apply();

        //Switch case to decide, which Button was clicked
        switch (v.getId()) {
            case R.id.button:
                break;

            case R.id.button2:
                if (inventoryFile.getInt("TOKENS", 0)>= Integer.parseInt(btnItem2.getText().toString())&&btnItem2.isClickable()){
                    editor.putInt("MULTIPLIER", 2)
                            .apply();
                    editor.putInt("TOKENS", inventoryFile.getInt("TOKENS", 0) - Integer.parseInt(btnItem2.getText().toString()))
                            .apply();
                    btnItem2.setClickable(false);
                    startTimer();
                }
                break;

            case R.id.button3:
                if (inventoryFile.getInt("TOKENS", 0) >= Integer.parseInt(btnItem2.getText().toString())) {
                    editor.putInt("PACKAGES", inventoryFile.getInt("PACKAGES", 0) + 10)
                            .apply();
                    editor.putInt("TOKENS", inventoryFile.getInt("TOKENS", 0) - Integer.parseInt(btnItem3.getText().toString()))
                            .apply();
                }

                break;

            case R.id.button4:
                break;

            default:
                intent = new Intent(getApplicationContext(), MenuActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                break;
        }
    }

    private void startTimer() {
        mTimerRunning = true;
        mEndTime = System.currentTimeMillis() + mTimeLeftInMillis;
        mCountDownTimer = new CountDownTimer(mTimeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftInMillis = millisUntilFinished;
                updateTime();
            }
            @Override
            public void onFinish() {
                mTimerRunning = false;
            }
        }.start();
    }

    protected void onStop() {
        super.onStop();
        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong("millisLeft", mTimeLeftInMillis);
        editor.putBoolean("timerRunning", mTimerRunning);
        editor.putLong("endTime", mEndTime);
        editor.apply();
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        mTimeLeftInMillis = prefs.getLong("millisLeft", START_TIME_IN_MILLIS);
        mTimerRunning = prefs.getBoolean("timerRunning", false);
        if (mTimerRunning) {
            updateTime();
            mEndTime = prefs.getLong("endTime", 0);
            mTimeLeftInMillis = mEndTime - System.currentTimeMillis();
            if (mTimeLeftInMillis < 0) {
                mTimeLeftInMillis = 0;
                mTimerRunning = false;
                updateTime();
            } else {
                startTimer();
            }
        }
    }


    private void updateTime() {
        int minutes = (int) (mTimeLeftInMillis / 1000) / 60;
        int seconds = (int) (mTimeLeftInMillis / 1000) % 60;
        String timeFormat = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        btnItem2.setText(timeFormat);
    }
}