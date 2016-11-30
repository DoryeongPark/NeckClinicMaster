package com.mokdoryeong.team7.mokdoryeong;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class IntroActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        Typeface doh_type = Typeface.createFromAsset(getAssets(), "fonts/doh.ttf");
        Typeface old_L_type = Typeface.createFromAsset(getAssets(), "fonts/a_old_L.ttf");

        TextView intro_title_kor = (TextView) findViewById(R.id.intro_title_kor);
        intro_title_kor.setTypeface((doh_type));
        TextView intro_title_eng = (TextView) findViewById(R.id.intro_title_eng);
        intro_title_eng.setTypeface((doh_type));
        TextView intro_bot_text = (TextView) findViewById(R.id.intro_bot_text);
        intro_bot_text.setTypeface((old_L_type));

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                Intent intent = new Intent(IntroActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, 2000);
    }
}
