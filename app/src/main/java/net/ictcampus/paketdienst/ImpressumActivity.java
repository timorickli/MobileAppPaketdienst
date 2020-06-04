package net.ictcampus.paketdienst;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

public class ImpressumActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_impressum);
        TextView email= (TextView) findViewById(R.id.email);
        email.setText(Html.fromHtml("<a href=\"mailto:timo.rickli@ict-campus.net\">Email: timo.rickli@ict-campus.net</a>"));
        email.setMovementMethod(LinkMovementMethod.getInstance());
    }
}
