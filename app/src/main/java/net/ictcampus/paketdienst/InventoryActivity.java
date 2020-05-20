package net.ictcampus.paketdienst;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;

public class InventoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);
        ImageButton ib = findViewById(R.id.imageButton);
        int token = getIntent().getIntExtra("tokens", 0);
        TextView tokens = findViewById(R.id.tokens);
        tokens.setText(String.valueOf(token));
        ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(getApplicationContext(), MenuActivity.class);
                intent.putExtra("token", token);
                startActivity(intent);
                overridePendingTransition(0, 0);

            }
        });
    }

}
