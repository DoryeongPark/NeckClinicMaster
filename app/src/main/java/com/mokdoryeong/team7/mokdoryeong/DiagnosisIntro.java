package com.mokdoryeong.team7.mokdoryeong;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;


public class DiagnosisIntro extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.intro_diagnosis);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                Intent intent = new Intent(DiagnosisIntro.this, DiagnosisCreator.class);
                startActivity(intent);
                finish();
            }
        }, 3000);

    }

}
