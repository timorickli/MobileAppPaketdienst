package net.ictcampus.paketdienst;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
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
}
