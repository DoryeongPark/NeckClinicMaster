package com.mokdoryeong.team7.mokdoryeong;

import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

public class VideoActivity extends AppCompatActivity {

    private VideoView video_videoview;
    Uri VIDEO_URI;

    Typeface doh_type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        doh_type = Typeface.createFromAsset(getAssets(), "fonts/doh.ttf");

        TextView video_top_text = (TextView) findViewById(R.id.video_top_text);
        video_top_text.setTypeface(doh_type);

        Button video_start = (Button) findViewById(R.id.video_start);
        Button video_volume = (Button) findViewById(R.id.video_volume);
        video_start.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                video_videoview.seekTo(0);
                video_videoview.start();
            }
        });
        video_volume.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                AudioManager mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
                int maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, maxVolume, AudioManager.FLAG_SHOW_UI);
            }
        });

        MediaController mc = new MediaController(this);
        VIDEO_URI = Uri.parse("android.resource://"+getPackageName()+"/raw/stretch_video");
        video_videoview = (VideoView) findViewById(R.id.video_videoview);
        video_videoview.setMediaController(mc);
        video_videoview.setVideoURI(VIDEO_URI);
        video_videoview.requestFocus();

        video_videoview.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            public void onPrepared(MediaPlayer player) {
                Toast.makeText(getApplicationContext(), "동영상이 준비되었습니다.\n'재생' 버튼을 누르세요.", Toast.LENGTH_LONG).show();
            }
        });
    }

    protected void onResume() {
        Toast.makeText(getApplicationContext(), "동영상 준비중입니다.\n잠시 기다려주세요.", Toast.LENGTH_LONG).show();
        super.onResume();
    }
}
