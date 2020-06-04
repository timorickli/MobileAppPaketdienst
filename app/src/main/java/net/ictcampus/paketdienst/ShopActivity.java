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
    private static final long ITEM_DURATION = 60*30*1000;
    private CountDownTimer countDownTimerItem2, countDownTimerItem1;
    private boolean timerRunningItem2, timerRunningItem1;
    private long timeLeftItem2, timeLeftItem1;
    private long endTimeItem2, endTimeItem1;


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
        btnItem1.setText("1000");
        btnItem2.setText("2000");
        btnItem3.setText("2500");
        btnItem4.setText("4000");
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
                if (inventoryFile.getInt("TOKENS", 0)>= Integer.parseInt(btnItem1.getText().toString())&&btnItem1.isClickable()){
                    editor.putInt("RANGE", 1)
                            .apply();
                    editor.putInt("TOKENS", inventoryFile.getInt("TOKENS", 0) - Integer.parseInt(btnItem2.getText().toString()))
                            .apply();
                    btnItem1.setClickable(false);
                    startTimerItem1();
                }
                break;

            case R.id.button2:
                if (inventoryFile.getInt("TOKENS", 0)>= Integer.parseInt(btnItem2.getText().toString())&&btnItem2.isClickable()){
                    editor.putInt("MULTIPLIER", 2)
                            .apply();
                    editor.putInt("TOKENS", inventoryFile.getInt("TOKENS", 0) - Integer.parseInt(btnItem2.getText().toString()))
                            .apply();
                    btnItem2.setClickable(false);
                    startTimerItem2();
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

    private void startTimerItem1() {
        timerRunningItem1 = true;
        endTimeItem1 = System.currentTimeMillis() + timeLeftItem1;
        countDownTimerItem1 = new CountDownTimer(timeLeftItem1, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftItem1 = millisUntilFinished;
                updateTimeButton(btnItem1, timeLeftItem1);
            }
            @Override
            public void onFinish() {
                timerRunningItem1 = false;
            }
        }.start();
    }

    private void startTimerItem2() {
        timerRunningItem2 = true;
        endTimeItem2 = System.currentTimeMillis() + timeLeftItem2;
        countDownTimerItem2 = new CountDownTimer(timeLeftItem2, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftItem2 = millisUntilFinished;
                updateTimeButton(btnItem2, timeLeftItem2);
            }
            @Override
            public void onFinish() {
                timerRunningItem2 = false;
            }
        }.start();
    }

    protected void onStop() {
        super.onStop();
        SharedPreferences prefs = getSharedPreferences("Timers", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong("millisLeftItem1", timeLeftItem1);
        editor.putBoolean("timerRunningItem1", timerRunningItem1);
        editor.putLong("endTimeItem1", endTimeItem1);
        editor.putLong("millisLeftItem2", timeLeftItem2);
        editor.putBoolean("timerRunningItem2", timerRunningItem2);
        editor.putLong("endTimeItem2", endTimeItem2);
        editor.apply();
        if (countDownTimerItem2 != null) {
            countDownTimerItem2.cancel();
        }
        if (countDownTimerItem1 != null) {
            countDownTimerItem1.cancel();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences prefs = getSharedPreferences("Timers", MODE_PRIVATE);
        SharedPreferences inventoryFile = getSharedPreferences("inventory", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = inventoryFile.edit();
        timeLeftItem2 = prefs.getLong("millisLeftItem2", ITEM_DURATION);
        timerRunningItem2 = prefs.getBoolean("timerRunningItem2", false);
        timeLeftItem1 = prefs.getLong("millisLeftItem1", ITEM_DURATION);
        timerRunningItem1 = prefs.getBoolean("timerRunningItem1", false);
        if (timerRunningItem2) {
            updateTimeButton(btnItem2, timeLeftItem2);
            endTimeItem2 = prefs.getLong("endTimeItem2", 0);
            timeLeftItem2 = endTimeItem2 - System.currentTimeMillis();
            if (timeLeftItem2 < 0) {
                timeLeftItem2 = 0;
                timerRunningItem2 = false;
                editor.putInt("MULTIPLIER", 1)
                        .apply();

            } else {
                startTimerItem2();
            }
        }
        if (timerRunningItem1) {
            updateTimeButton(btnItem1, timeLeftItem1);
            endTimeItem1 = prefs.getLong("endTimeItem2", 0);
            timeLeftItem1 = endTimeItem1 - System.currentTimeMillis();
            if (timeLeftItem1 < 0) {
                timeLeftItem1 = 0;
                timerRunningItem1 = false;
                editor.putInt("RANGE", 0)
                        .apply();

            } else {
                startTimerItem1();
            }
        }
    }

    private void updateTimeButton(Button button, Long time) {
        int minutes = (int) (time / 1000) / 60;
        int seconds = (int) (time / 1000) % 60;
        String timeFormat = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        button.setText(timeFormat);
    }
}