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

/**
 * Class for the manual of the game
 */
public class InstructionActivity extends AppCompatActivity {
    private ImageButton imageButton;
    private TextView text1, title;
    private View view;

    /**
     * OnCreate Method
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instruction);

        //Initialization of all variables
        view = this.getWindow().getDecorView();
        title = (TextView) findViewById(R.id.textView4);
        text1 = (TextView) findViewById(R.id.textManual);
        imageButton = (ImageButton) findViewById(R.id.imageButton2);

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
        text1.setText(getString(R.string.spielAnleitung));
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
        text1.setTextColor(Color.BLACK);
        title.setTextColor(Color.BLACK);
        imageButton.setImageResource(R.drawable.settingbtn_black);
    }

    /**
     * Method for the dark Mode Theme
     */
    private void darkMode() {
        view.setBackgroundColor(Color.BLACK);
        text1.setTextColor(Color.WHITE);
        title.setTextColor(Color.WHITE);
        imageButton.setImageResource(R.drawable.settingbtn_white);
    }
}
