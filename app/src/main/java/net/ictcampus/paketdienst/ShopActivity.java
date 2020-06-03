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

public class ShopActivity extends AppCompatActivity implements View.OnClickListener {
    TextView txtItem1, txtItem2, txtItem3, txtItem4;
    Button btnItem1, btnItem2, btnItem3, btnItem4;
    ImageButton btnHome;
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
        btnItem1.setText("100");
        btnItem2.setText("200");
        btnItem3.setText("250");
        btnItem4.setText("400");
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

        //Switch case to decide, which Button was clicked
        switch (v.getId()) {
            case R.id.button:
                break;

            case R.id.button2:
                if (inventoryFile.getInt("TOKENS", 0)>= Integer.parseInt(btnItem2.getText().toString())){
                    editor.putInt("MULTIPLIER", 2)
                            .apply();
                    editor.putInt("TOKENS", inventoryFile.getInt("TOKENS", 0) - Integer.parseInt(btnItem2.getText().toString()))
                            .apply();
                    CountDownTimer countDownTimer = new CountDownTimer(60 * 30 * 1000, 1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {

                        }

                        @Override
                        public void onFinish() {
                            editor.putInt("MULTIPLIER", 1)
                                    .apply();
                        }
                    }.start();
                }

                break;

            case R.id.button3:
                if (inventoryFile.getInt("TOKENS", 0)>= Integer.parseInt(btnItem2.getText().toString())){
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
}

