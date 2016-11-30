package com.example.galba.sendmails;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    SharedPreferences preferences = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        updateText();
        SendMailsService.start(getApplicationContext());
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        updateText();
    }

    private void updateText() {
        TextView textView = (TextView) findViewById(R.id.textViewUpdateTime);
        textView.setText("Last mail time: " + preferences.getString("lastMailTime", "never"));
    }
}
