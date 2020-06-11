package net.ictcampus.paketdienst;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import net.ictcampus.paketdienst.helper.GameTimer;

/**
 * Class for the shop activity
 * to buy PowerUps or Upgrades
 */
public class ShopActivity extends AppCompatActivity implements View.OnClickListener {
    private SharedPreferences inventoryFile, timersFile;
    private SharedPreferences.Editor editorInventory, editorTimers;
    private TextView txtItem1, txtItem2, txtItem3, txtItem4;
    private Button btnItem1, btnItem2, btnItem3, btnItem4;
    private TextView tokensView;
    private ImageButton btnHome;
    private int tokens;
    private GameTimer dt1, dt2;

    /**
     * Initialises Activity
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);

        //New helper
        dt1 = new GameTimer(20);
        dt2 = new GameTimer(20);

        //Shared Prefrences
        inventoryFile = getSharedPreferences("inventory", Context.MODE_PRIVATE);
        timersFile = getSharedPreferences("timers", Context.MODE_PRIVATE);
        editorInventory = inventoryFile.edit();
        editorTimers = timersFile.edit();
        editorInventory.putInt("TOKENS", 200).apply();

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
        txtItem1.setText(R.string.shopMail);
        txtItem2.setText(R.string.shopZoll);
        txtItem3.setText(R.string.shopPakete);
        txtItem4.setText(R.string.shopZeit);

        //Setup current token Value
        tokens = inventoryFile.getInt("TOKENS", 0);
        tokensView = findViewById(R.id.tokens);
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
     * Method on Start Action
     * Gets Timer, calculates and displays time left
     */
    @Override
    protected void onStart() {
        super.onStart();

        //Checks, if time is over for Item
        if (!dt1.checkState(timersFile, btnItem1, "millisLeftItem1", "timerRunningItem1", "endTimeItem1")) {
            editorInventory.putInt("RANGE", 0).apply();
            btnItem1.setText("400");
            btnItem1.setClickable(true);
        } else {
            btnItem1.setClickable(false);
        }

        //Checks, if time is over for Item
        if (!dt2.checkState(timersFile, btnItem2, "millisLeftItem2", "timerRunningItem2", "endTimeItem2")) {
            editorInventory.putInt("MULTIPLIER", 1).apply();
            btnItem2.setText("125");
            btnItem2.setClickable(true);
        } else {
            btnItem2.setClickable(false);
        }
    }

    /**
     * Method on Stop action
     */
    @Override
    protected void onStop() {
        super.onStop();

        //Saves values of Both timers
        dt1.beforeChange(editorTimers, "millisLeftItem1", "timerRunningItem1", "endTimeItem1");
        dt2.beforeChange(editorTimers, "millisLeftItem2", "timerRunningItem2", "endTimeItem2");
    }

    /**
     * OnClick Event for the Buttons
     *
     * @param v view
     */
    @Override
    public void onClick(View v) {
        Intent intent;

        //Switch case to decide, which Button was clicked
        switch (v.getId()) {
            case R.id.button1:
                //Checks, if you have enough tokens
                if (inventoryFile.getInt("TOKENS", 0) >= Integer.parseInt(btnItem1.getText().toString()) && btnItem1.isClickable()) {

                    //Increases Range
                    editorInventory.putInt("RANGE", 1).apply();

                    //Commits the buy and gives information for the user
                    editorInventory.putInt("TOKENS", inventoryFile.getInt("TOKENS", 0) - Integer.parseInt(btnItem1.getText().toString())).apply();
                    btnItem1.setClickable(false);
                    dt1.startGameTimer(btnItem1);
                    Toast.makeText(ShopActivity.this, R.string.shopBuy, Toast.LENGTH_SHORT).show();

                    //Updates current token balance
                    tokens = inventoryFile.getInt("TOKENS", 0);
                    tokensView.setText(getString(R.string.inventoryTokens) + " " + String.valueOf(tokens));

                } else {
                    Toast.makeText(ShopActivity.this, R.string.shopKeineMünzen, Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.button2:
                //Checks, if you have enough tokens
                if (inventoryFile.getInt("TOKENS", 0) >= Integer.parseInt(btnItem2.getText().toString()) && btnItem2.isClickable()) {

                    //Adds Multiplier
                    editorInventory.putInt("MULTIPLIER", 2).apply();

                    //Commits the buy and gives information for the user
                    editorInventory.putInt("TOKENS", inventoryFile.getInt("TOKENS", 0) - Integer.parseInt(btnItem2.getText().toString())).apply();
                    btnItem2.setClickable(false);
                    dt2.startGameTimer(btnItem2);
                    Toast.makeText(ShopActivity.this, R.string.shopBuy, Toast.LENGTH_SHORT).show();

                    //Updates current token balance
                    tokens = inventoryFile.getInt("TOKENS", 0);
                    tokensView.setText(getString(R.string.inventoryTokens) + " " + String.valueOf(tokens));

                } else {
                    Toast.makeText(ShopActivity.this, R.string.shopKeineMünzen, Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.button3:
                //Checks, if you have enough tokens
                if (inventoryFile.getInt("TOKENS", 0) >= Integer.parseInt(btnItem3.getText().toString())) {

                    //Commits the buy and gives information for the user
                    editorInventory.putInt("TOKENS", inventoryFile.getInt("TOKENS", 0) - Integer.parseInt(btnItem3.getText().toString())).apply();
                    editorInventory.putInt("PACKAGES", inventoryFile.getInt("PACKAGES", 0) + 10).apply();

                    //Starts timer for delivery
                    GameTimer dt = new GameTimer(30);
                    dt.resetTimer(editorTimers, "TimeLeft", "TimerRunning", "EndTime");
                    dt.startGameTimer();
                    dt.beforeChange(editorTimers, "TimeLeft", "TimerRunning", "EndTime");
                    Toast.makeText(ShopActivity.this, R.string.shopBuy, Toast.LENGTH_SHORT).show();

                    //Updates current token balance
                    tokens = inventoryFile.getInt("TOKENS", 0);
                    tokensView.setText(getString(R.string.inventoryTokens) + " " + String.valueOf(tokens));

                } else {
                    Toast.makeText(ShopActivity.this, R.string.shopKeineMünzen, Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.button4:

                //Checks, if you have enough tokens and that there's a active timer
                if (inventoryFile.getInt("TOKENS", 0) >= Integer.parseInt(btnItem4.getText().toString()) && timersFile.getBoolean("timerRunning", false) == true) {
                    //Commits the buy and gives information for the user
                    increaseTime();
                    editorInventory.putInt("TOKENS", inventoryFile.getInt("TOKENS", 0) - Integer.parseInt(btnItem4.getText().toString())).apply();
                    Toast.makeText(ShopActivity.this, R.string.shopBuy, Toast.LENGTH_SHORT).show();

                    //Updates current token balance
                    tokens = inventoryFile.getInt("TOKENS", 0);
                    tokensView.setText(getString(R.string.inventoryTokens) + " " + String.valueOf(tokens));

                } else if (!timersFile.getBoolean("timerRunning", false)) {
                    //If theres no active timer
                    Toast.makeText(ShopActivity.this, R.string.noTimer, Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(ShopActivity.this, R.string.shopKeineMünzen, Toast.LENGTH_SHORT).show();
                }
                break;

            default:

                //Starts Intent without transition
                intent = new Intent(getApplicationContext(), MenuActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                break;
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
     * Increases time to deliver a package
     */
    private void increaseTime() {
        editorInventory.putLong("endTimeDelivery", timersFile.getLong("endTimeDelivery", 0) + 60 * 20 * 1000);
        editorInventory.apply();
    }
}