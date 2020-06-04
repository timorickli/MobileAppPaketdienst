package net.ictcampus.paketdienst;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

public class MenuActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        ImageButton ib = findViewById(R.id.imageButton);
        TextView spielanleitung = findViewById(R.id.spielanleitung);
        TextView shop = findViewById(R.id.shop);
        TextView inventory = findViewById(R.id.inventory);
        TextView settings = findViewById(R.id.settings);

        //Dark Mode
        SharedPreferences settingFile = getSharedPreferences("settings", Context.MODE_PRIVATE);
        if(settingFile.getBoolean("DARK",false)){
            darkMode();
        }
        else {
            whiteMode();
        }

        //ClickListeners for all subs
        ib.setOnClickListener(this);
        spielanleitung.setOnClickListener(this);
        inventory.setOnClickListener(this);
        shop.setOnClickListener(this);
        settings.setOnClickListener(this);
    }


    /**
     * ClickListener to decide which TextView got clicked
     *
     * @param v Clicked view
     */
    @Override
    public void onClick(View v) {
        Intent intent;

        //Switch case to decide, which TextView was clicked
        switch (v.getId()) {
            case R.id.spielanleitung:
                intent = new Intent(getApplicationContext(), InstructionActivity.class);
                break;

            case R.id.shop:
                intent = new Intent(getApplicationContext(), ShopActivity.class);
                break;

            case R.id.inventory:
                intent = new Intent(getApplicationContext(), InventoryActivity.class);
                break;

            case R.id.settings:
                intent = new Intent(getApplicationContext(), SettingActivity.class);
                break;
            default:
                intent = new Intent(getApplicationContext(), MapActivity.class);
                break;

        }
        startActivity(intent);
        overridePendingTransition(0, 0);

    }

    private void whiteMode() {
        View view = this.getWindow().getDecorView();
        view.setBackgroundColor(Color.WHITE);
        TextView title= (TextView) findViewById(R.id.textView);
        TextView text1= (TextView) findViewById(R.id.inventory);
        TextView text2= (TextView) findViewById(R.id.settings);
        TextView text3= (TextView) findViewById(R.id.shop);
        TextView text4= (TextView) findViewById(R.id.spielanleitung);
        ImageButton imageButton= (ImageButton) findViewById(R.id.imageButton);
        text1.setTextColor(Color.BLACK);
        text2.setTextColor(Color.BLACK);
        text3.setTextColor(Color.BLACK);
        text4.setTextColor(Color.BLACK);
        title.setTextColor(Color.BLACK);
        imageButton.setImageResource(R.drawable.settingbtn_black);
    }

    private void darkMode() {
        View view = this.getWindow().getDecorView();
        view.setBackgroundColor(Color.BLACK);
        TextView title= (TextView) findViewById(R.id.textView);
        TextView text1= (TextView) findViewById(R.id.inventory);
        TextView text2= (TextView) findViewById(R.id.settings);
        TextView text3= (TextView) findViewById(R.id.shop);
        TextView text4= (TextView) findViewById(R.id.spielanleitung);
        ImageButton imageButton= (ImageButton) findViewById(R.id.imageButton);
        text1.setTextColor(Color.WHITE);
        text2.setTextColor(Color.WHITE);
        text3.setTextColor(Color.WHITE);
        text4.setTextColor(Color.WHITE);
        title.setTextColor(Color.WHITE);
        imageButton.setImageResource(R.drawable.settingbtn_white);
    }
}
