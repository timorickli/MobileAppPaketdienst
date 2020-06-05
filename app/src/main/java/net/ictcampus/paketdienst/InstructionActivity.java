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

public class InstructionActivity extends AppCompatActivity {

    /**
     * OnCreate Method
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instruction);
        //ImageButton with onClick event for back to menu
        ImageButton ib = findViewById(R.id.imageButton2);
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
        if(settingFile.getBoolean("DARK",false)){
            darkMode();
        }
        else {
            whiteMode();
        }
    }

    /**
     * Method for the normal Mode Theme
     */
    private void whiteMode() {
        View view = this.getWindow().getDecorView();
        view.setBackgroundColor(Color.WHITE);
        TextView title= (TextView) findViewById(R.id.textView4);
        TextView text1= (TextView) findViewById(R.id.textManual);
        ImageButton imageButton= (ImageButton) findViewById(R.id.imageButton2);
        text1.setTextColor(Color.BLACK);
        title.setTextColor(Color.BLACK);
        imageButton.setImageResource(R.drawable.settingbtn_black);
    }

    /**
     * Method for the dark Mode Theme
     */
    private void darkMode() {
        View view = this.getWindow().getDecorView();
        view.setBackgroundColor(Color.BLACK);
        TextView title= (TextView) findViewById(R.id.textView4);
        TextView text1= (TextView) findViewById(R.id.textManual);
        ImageButton imageButton= (ImageButton) findViewById(R.id.imageButton2);
        text1.setTextColor(Color.WHITE);
        title.setTextColor(Color.WHITE);
        imageButton.setImageResource(R.drawable.settingbtn_white);
    }
}
