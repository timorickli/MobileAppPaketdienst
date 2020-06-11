package net.ictcampus.paketdienst;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

/**
 * Class for the shop activity
 * to buy PowerUps or Upgrades
 */
public class ShopActivity extends AppCompatActivity implements View.OnClickListener {
    private SharedPreferences inventoryFile, timersFile;
    private SharedPreferences.Editor editorInventory, editorTimers;
    private CountDownTimer countDownTimerItem2, countDownTimerItem1;
    private static final long ITEM_DURATION = 60 * 30 * 1000;
    private TextView txtItem1, txtItem2, txtItem3, txtItem4;
    private Button btnItem1, btnItem2, btnItem3, btnItem4;
    private boolean timerRunningItem2, timerRunningItem1;
    private long timeLeftItem2, timeLeftItem1;
    private long endTimeItem2, endTimeItem1;
    private ImageButton btnHome;
    private int tokens;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);

        inventoryFile = getSharedPreferences("inventory", Context.MODE_PRIVATE);
        timersFile = getSharedPreferences("timers", Context.MODE_PRIVATE);
        editorInventory = inventoryFile.edit();
        editorTimers = timersFile.edit();

        //Get the Different TextViews and Buttons
        txtItem1 = (TextView) findViewById(R.id.itemText1);
        txtItem2 = (TextView) findViewById(R.id.itemText2);
        txtItem3 = (TextView) findViewById(R.id.itemText3);
        txtItem4 = (TextView) findViewById(R.id.itemText4);
        btnItem1 = (Button) findViewById(R.id.button1);
        btnItem2 = (Button) findViewById(R.id.button2);
        btnItem3 = (Button) findViewById(R.id.button3);
        btnItem4 = (Button) findViewById(R.id.button4);
        btnHome = findViewById(R.id.imageButton2);

        //Set the Text of the different Buttons
        btnItem1.setText("400");
        btnItem2.setText("125");
        btnItem3.setText("75");
        btnItem4.setText("30");

        //Set Click Listener on Buttons
        btnItem1.setOnClickListener(this);
        btnItem2.setOnClickListener(this);
        btnItem3.setOnClickListener(this);
        btnItem4.setOnClickListener(this);
        btnHome.setOnClickListener(this);

        //Set the Text on TextView
        txtItem2.setText(R.string.shopZoll);
        txtItem3.setText(R.string.shopPakete);
        txtItem4.setText(R.string.shopZeit);

        //Setup current token Value
        tokens = inventoryFile.getInt("TOKENS", 0);
        TextView tokensView = findViewById(R.id.tokens);
        tokensView.setText(getString(R.string.inventoryTokens) + ' ' + String.valueOf(tokens));

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
        View view = this.getWindow().getDecorView();
        view.setBackgroundColor(Color.WHITE);
        TextView title = (TextView) findViewById(R.id.textView4);
        TextView text1 = (TextView) findViewById(R.id.itemText1);
        TextView text2 = (TextView) findViewById(R.id.itemText3);
        TextView text3 = (TextView) findViewById(R.id.itemText4);
        TextView text4 = (TextView) findViewById(R.id.itemText2);
        Button btn1 = (Button) findViewById(R.id.button1);
        Button btn2 = (Button) findViewById(R.id.button2);
        Button btn3 = (Button) findViewById(R.id.button3);
        Button btn4 = (Button) findViewById(R.id.button4);
        ImageButton imageButton = (ImageButton) findViewById(R.id.imageButton2);
        text1.setTextColor(Color.BLACK);
        text2.setTextColor(Color.BLACK);
        text3.setTextColor(Color.BLACK);
        text4.setTextColor(Color.BLACK);
        title.setTextColor(Color.BLACK);
        btn1.setTextColor(Color.BLACK);
        btn1.setBackgroundColor(Color.GRAY);
        btn2.setTextColor(Color.BLACK);
        btn2.setBackgroundColor(Color.GRAY);
        btn3.setTextColor(Color.BLACK);
        btn3.setBackgroundColor(Color.GRAY);
        btn4.setTextColor(Color.BLACK);
        btn4.setBackgroundColor(Color.GRAY);
        imageButton.setImageResource(R.drawable.settingbtn_black);
    }

    /**
     * Method for the dark Mode Theme
     */
    private void darkMode() {
        View view = this.getWindow().getDecorView();
        view.setBackgroundColor(Color.BLACK);
        TextView title = (TextView) findViewById(R.id.textView4);
        TextView text1 = (TextView) findViewById(R.id.itemText1);
        TextView text2 = (TextView) findViewById(R.id.itemText3);
        TextView text3 = (TextView) findViewById(R.id.itemText4);
        TextView text4 = (TextView) findViewById(R.id.itemText2);
        Button btn1 = (Button) findViewById(R.id.button1);
        Button btn2 = (Button) findViewById(R.id.button2);
        Button btn3 = (Button) findViewById(R.id.button3);
        Button btn4 = (Button) findViewById(R.id.button4);
        ImageButton imageButton = (ImageButton) findViewById(R.id.imageButton2);
        text1.setTextColor(Color.WHITE);
        text2.setTextColor(Color.WHITE);
        text3.setTextColor(Color.WHITE);
        text4.setTextColor(Color.WHITE);
        title.setTextColor(Color.WHITE);
        btn1.setTextColor(Color.WHITE);
        btn1.setBackgroundColor(Color.DKGRAY);
        btn2.setTextColor(Color.WHITE);
        btn2.setBackgroundColor(Color.DKGRAY);
        btn3.setTextColor(Color.WHITE);
        btn3.setBackgroundColor(Color.DKGRAY);
        btn4.setTextColor(Color.WHITE);
        btn4.setBackgroundColor(Color.DKGRAY);
        imageButton.setImageResource(R.drawable.settingbtn_white);
    }

    /**
     * OnClick Event for the Buttons
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        Intent intent;

        //Switch case to decide, which Button was clicked
        switch (v.getId()) {
            case R.id.button1:
                if (inventoryFile.getInt("TOKENS", 0) >= Integer.parseInt(btnItem1.getText().toString()) && btnItem1.isClickable()) {
                    editorInventory.putInt("RANGE", 1)
                            .apply();
                    editorInventory.putInt("TOKENS", inventoryFile.getInt("TOKENS", 0) - Integer.parseInt(btnItem1.getText().toString()))
                            .apply();
                    btnItem1.setClickable(false);
                    startTimerItem1();
                    Toast.makeText(ShopActivity.this, R.string.shopBuy, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ShopActivity.this, R.string.shopKeineMünzen, Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.button2:
                if (inventoryFile.getInt("TOKENS", 0) >= Integer.parseInt(btnItem2.getText().toString()) && btnItem2.isClickable()) {
                    editorInventory.putInt("MULTIPLIER", 2)
                            .apply();
                    editorInventory.putInt("TOKENS", inventoryFile.getInt("TOKENS", 0) - Integer.parseInt(btnItem2.getText().toString()))
                            .apply();
                    btnItem2.setClickable(false);
                    startTimerItem2();
                    Toast.makeText(ShopActivity.this, R.string.shopBuy, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ShopActivity.this, R.string.shopKeineMünzen, Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.button3:
                if (inventoryFile.getInt("TOKENS", 0) >= Integer.parseInt(btnItem3.getText().toString())) {
                    editorInventory.putInt("TOKENS", inventoryFile.getInt("TOKENS", 0) - Integer.parseInt(btnItem3.getText().toString()))
                            .apply();
                    Toast.makeText(ShopActivity.this, R.string.shopBuy, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ShopActivity.this, R.string.shopKeineMünzen, Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.button4:
                if (inventoryFile.getInt("TOKENS", 0) >= Integer.parseInt(btnItem4.getText().toString())) {
                    editorInventory.putInt("PACKAGES", inventoryFile.getInt("PACKAGES", 0) + 10)
                            .apply();
                    editorInventory.putInt("TOKENS", inventoryFile.getInt("TOKENS", 0) - Integer.parseInt(btnItem4.getText().toString()))
                            .apply();
                    increaseTime();
                    Toast.makeText(ShopActivity.this, R.string.shopBuy, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ShopActivity.this, R.string.shopKeineMünzen, Toast.LENGTH_SHORT).show();
                }
                break;

            default:
                intent = new Intent(getApplicationContext(), MenuActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                break;
        }
    }

    /**
     * Method on Stop action
     */
    @Override
    protected void onStop() {
        super.onStop();
        editorInventory.putLong("millisLeftItem1", timeLeftItem1);
        editorInventory.putBoolean("timerRunningItem1", timerRunningItem1);
        editorInventory.putLong("endTimeItem1", endTimeItem1);

        editorInventory.putLong("millisLeftItem2", timeLeftItem2);
        editorInventory.putBoolean("timerRunningItem2", timerRunningItem2);
        editorInventory.putLong("endTimeItem2", endTimeItem2);

        editorInventory.apply();

        if (countDownTimerItem2 != null) {
            countDownTimerItem2.cancel();
        }
        if (countDownTimerItem1 != null) {
            countDownTimerItem1.cancel();
        }
    }

    /**
     * Method on Start Action
     * Gets Timer, calculates and displays time left
     */
    @Override
    protected void onStart() {
        super.onStart();

        timeLeftItem2 = timersFile.getLong("millisLeftItem2", ITEM_DURATION);
        timerRunningItem2 = timersFile.getBoolean("timerRunningItem2", false);

        timeLeftItem1 = timersFile.getLong("millisLeftItem1", ITEM_DURATION);
        timerRunningItem1 = timersFile.getBoolean("timerRunningItem1", false);

        if (timerRunningItem2) {
            endTimeItem2 = timersFile.getLong("endTimeItem2", 0);
            timeLeftItem2 = endTimeItem2 - System.currentTimeMillis();
            if (timeLeftItem2 < 0) {
                timeLeftItem2 = 0;
                timerRunningItem2 = false;
                editorInventory.putInt("MULTIPLIER", 1)
                        .apply();
                btnItem2.setText("2000");
            } else {
                startTimerItem2();
            }
        }

        if (timerRunningItem1) {
            endTimeItem1 = timersFile.getLong("endTimeItem1", 0);
            timeLeftItem1 = endTimeItem1 - System.currentTimeMillis();
            if (timeLeftItem1 < 0) {
                timeLeftItem1 = 0;
                timerRunningItem1 = false;
                editorInventory.putInt("RANGE", 0)
                        .apply();
                btnItem1.setText("1000");
            } else {
                startTimerItem1();
            }
        }
    }

    /**
     * Method to start the Timer
     */
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
                editorInventory.putInt("MULTIPLIER", 1).apply();
            }
        }.start();
    }

    /**
     * To start another timer
     */
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
                editorInventory.putInt("RANGE", 0).apply();
            }
        }.start();
    }

    /**
     * Updates the timer displayed on button
     *
     * @param button
     * @param time
     */
    private void updateTimeButton(Button button, Long time) {
        int minutes = (int) (time / 1000) / 60;
        int seconds = (int) (time / 1000) % 60;

        //String formatter
        String timeFormat = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        button.setText(timeFormat);
    }

    /**
     * Increases time to deliver a package
     */
    private void increaseTime() {
        editorInventory.putLong("endTimeDelivery", timersFile.getLong("endTimeDelivery", 0) + 60 * 20 * 1000);
        editorInventory.apply();
    }
}