package net.ictcampus.paketdienst;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
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

        ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(getApplicationContext(), MenuActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });
    }

}