package net.ictcampus.paketdienst;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

public class InventoryActivity extends AppCompatActivity {
    private int tokens;
    private int packages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);
        ImageButton ib = findViewById(R.id.imageButton);

        SharedPreferences inventoryFile = this.getSharedPreferences("inventory", Context.MODE_PRIVATE);
        tokens = inventoryFile.getInt("TOKENS", 0);
        packages = inventoryFile.getInt("PACKAGES", 0);

        TextView packagesView = findViewById(R.id.packages);
        TextView tokensView = findViewById(R.id.tokens);
        tokensView.setText(String.valueOf(tokens));
        packagesView.setText(String.valueOf(packages));

        //Clicklistener homebutton
        ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MenuActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });

        //Dark Mode
        SharedPreferences settingFile = getSharedPreferences("settings", Context.MODE_PRIVATE);
        if (settingFile.getBoolean("DARK", false)) {
            darkMode();
        } else {
            whiteMode();
        }
    }


    /**
     * Style to white
     */
    private void whiteMode() {
        View view = this.getWindow().getDecorView();
        view.setBackgroundColor(Color.WHITE);
        TextView title = (TextView) findViewById(R.id.textView2);
        TextView text1 = (TextView) findViewById(R.id.textView3);
        TextView text2 = (TextView) findViewById(R.id.packages);
        TextView text3 = (TextView) findViewById(R.id.tokens);
        ImageButton imageButton = (ImageButton) findViewById(R.id.imageButton);
        text1.setTextColor(Color.BLACK);
        text2.setTextColor(Color.BLACK);
        text3.setTextColor(Color.BLACK);
        title.setTextColor(Color.BLACK);
        imageButton.setImageResource(R.drawable.settingbtn_black);
    }

    /**
     * Style to Darkmode
     */
    private void darkMode() {
        View view = this.getWindow().getDecorView();
        view.setBackgroundColor(Color.BLACK);
        TextView title = (TextView) findViewById(R.id.textView2);
        TextView text1 = (TextView) findViewById(R.id.textView3);
        TextView text2 = (TextView) findViewById(R.id.packages);
        TextView text3 = (TextView) findViewById(R.id.tokens);
        ImageButton imageButton = (ImageButton) findViewById(R.id.imageButton);
        text1.setTextColor(Color.WHITE);
        text2.setTextColor(Color.WHITE);
        text3.setTextColor(Color.WHITE);
        title.setTextColor(Color.WHITE);
        imageButton.setImageResource(R.drawable.settingbtn_white);
    }

}