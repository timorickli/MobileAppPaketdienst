package net.ictcampus.paketdienst;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

public class InventoryActivity extends AppCompatActivity {
    private int packages;
    private int tokens;
    private View view;
    private TextView title, text2, text3;
    private ImageButton imageButton;

    /**
     * Prepares everything when acitvity is created
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

        //Initialization of all variables
        view = this.getWindow().getDecorView();
        title = (TextView) findViewById(R.id.textView2);
        text2 = (TextView) findViewById(R.id.packages);
        text3 = (TextView) findViewById(R.id.tokens);
        imageButton = (ImageButton) findViewById(R.id.imageButton);

        //Get Values out of File and set them in TextView
        SharedPreferences inventoryFile = this.getSharedPreferences("inventory", Context.MODE_PRIVATE);
        TextView packagesView = findViewById(R.id.packages);
        TextView tokensView = findViewById(R.id.tokens);
        tokens = inventoryFile.getInt("TOKENS", 0);
        packages = inventoryFile.getInt("PACKAGES", 0);
        tokensView.setText(String.valueOf(tokens));
        packagesView.setText(String.valueOf(packages));

        //ImageButton with onClick event for back to menu
        ImageButton ib = findViewById(R.id.imageButton);
        ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MenuActivity.class);
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
        text2.setTextColor(Color.BLACK);
        text3.setTextColor(Color.BLACK);
        title.setTextColor(Color.BLACK);
        imageButton.setImageResource(R.drawable.settingbtn_black);
    }

    /**
     * Method for the dark Mode Theme
     */
    private void darkMode() {
        view.setBackgroundColor(Color.BLACK);
        text2.setTextColor(Color.WHITE);
        text3.setTextColor(Color.WHITE);
        title.setTextColor(Color.WHITE);
        imageButton.setImageResource(R.drawable.settingbtn_white);
    }
}