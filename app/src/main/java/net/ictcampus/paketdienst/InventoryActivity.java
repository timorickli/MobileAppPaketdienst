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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);
        ImageButton ib = findViewById(R.id.imageButton);
        SharedPreferences tokensFile = this.getSharedPreferences("inventory", Context.MODE_PRIVATE);
        tokens = tokensFile.getInt("TOKENS", 0);
        TextView tokens = findViewById(R.id.tokens);
        tokens.setText(String.valueOf(tokens));
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