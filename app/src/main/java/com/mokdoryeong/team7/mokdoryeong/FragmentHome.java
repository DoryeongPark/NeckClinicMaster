package com.mokdoryeong.team7.mokdoryeong;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


public class FragmentHome extends Fragment {

    private boolean isOpeningActivity = false;

    Button frag_home_btn_back;
    Drawable frag_home_back;
    Drawable frag_home_back_none;

    LinearLayout frag_home_level_background;
    ImageView frag_home_img;

    TextView frag_home_lv;
    TextView frag_home_sim;
    TextView frag_home_spec;

    ArrayList<Drawable> lv_img = new ArrayList<Drawable>();
    Drawable lv1;
    Drawable lv2;
    Drawable lv3;
    Drawable lv4;
    Drawable lv5;

    String[] lv_str = {
            "비옴",  "흐림",  "구름 조금", "대체로 맑음", "맑음!",
    };
    String[] lv_str_specific = {
            "평균 목각도가 20 도 이하입니다.\n목에 부담이 큽니다!",
            "평균 목각도가 20~40 도 사이입니다.\n목에 부담이 큽니다.",
            "평균 목각도가 40~60 도 사이입니다.\n목에 조금 부담이 갑니다.",
            "평균 목각도가 60~80 도 사이입니다.\n목에 부담이 거의 없습니다.",

            "평균 목각도가 80도 이상입니다.\n목에 부담이 없습니다."

    };

    static int angle;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_home, container, false);

        frag_home_level_background = (LinearLayout) rootView.findViewById(R.id.frag_home_level_background);
        frag_home_lv = (TextView) rootView.findViewById(R.id.frag_home_lv);
        frag_home_sim = (TextView) rootView.findViewById(R.id.frag_home_sim);
        frag_home_spec = (TextView) rootView.findViewById(R.id.frag_home_spec);

        frag_home_lv.setTypeface(((MainActivity)getActivity()).old_L_type);
        frag_home_sim.setTypeface(((MainActivity)getActivity()).old_L_type);
        frag_home_spec.setTypeface(((MainActivity)getActivity()).jua_type);

        frag_home_back = ContextCompat.getDrawable(getActivity(), R.drawable.frag_home_back);
        frag_home_back_none  = ContextCompat.getDrawable(getActivity(), R.drawable.frag_home_back_cancel);
        frag_home_btn_back = (Button) rootView.findViewById(R.id.frag_home_btn_back);
        frag_home_btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(BackgroundService.isChecked)
                {
                    Toast.makeText(getActivity(),"WidgetService 중지", Toast.LENGTH_SHORT).show();
                    BackgroundService.isChecked = false;
                    frag_home_btn_back.setCompoundDrawablesWithIntrinsicBounds(null, frag_home_back, null, null);
                    frag_home_btn_back.setText("\n백그라운드 실행");
                }
                else {
                    Toast.makeText(getActivity(),"WidgetService 시작", Toast.LENGTH_SHORT).show();
                    BackgroundService.isChecked = true;
                    frag_home_btn_back.setCompoundDrawablesWithIntrinsicBounds(null, frag_home_back_none, null, null);
                    frag_home_btn_back.setText("\n백그라운드 중지");
                }
            }
        });

        Button frag_home_btn_cam = (Button) rootView.findViewById(R.id.frag_home_btn_cam);
        frag_home_btn_cam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isOpeningActivity = true;
                Intent intent = new Intent(getActivity(), DiagnosisIntro.class);
                startActivity(intent);
                isOpeningActivity = false;
            }
        });

        Button frag_home_btn_stretch = (Button) rootView.findViewById(R.id.frag_home_btn_stretch);
        frag_home_btn_stretch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isOpeningActivity = true;
                Intent intent = new Intent(getActivity(), VideoActivity.class);
                startActivity(intent);
                isOpeningActivity = false;
            }
        });

        frag_home_img = (ImageView) rootView.findViewById(R.id.frag_home_img);
        lv1 = ContextCompat.getDrawable(getActivity(), R.drawable.frag_home_lv1);
        lv2 = ContextCompat.getDrawable(getActivity(), R.drawable.frag_home_lv2);
        lv3 = ContextCompat.getDrawable(getActivity(), R.drawable.frag_home_lv3);
        lv4 = ContextCompat.getDrawable(getActivity(), R.drawable.frag_home_lv4);
        lv5 = ContextCompat.getDrawable(getActivity(), R.drawable.frag_home_lv5);

        lv_img.add(lv5);
        lv_img.add(lv4);
        lv_img.add(lv3);
        lv_img.add(lv2);
        lv_img.add(lv1);

        setAverageAngle(angle);

        return rootView;
    }

    public void update()
    {
        if(BackgroundService.isChecked)
        {
            frag_home_btn_back.setCompoundDrawablesWithIntrinsicBounds(null, frag_home_back_none, null, null);
            frag_home_btn_back.setText("\n백그라운드 중지");
        }
        else {
            frag_home_btn_back.setCompoundDrawablesWithIntrinsicBounds(null, frag_home_back, null, null);
            frag_home_btn_back.setText("\n백그라운드 실행");
        }

        setAverageAngle(angle);

    }

    public void setAverageAngle(int averageAngle){
        int point = averageAngle / 20;

        switch(point){
            case 3:
                frag_home_level_background.setBackgroundColor(Color.argb(0xcc,0x00,0x96,0xFA));
                break;
            case 2:
                frag_home_level_background.setBackgroundColor(Color.argb(0xcc,0x1E,0x8E,0x30));
                break;
            case 1:
                frag_home_level_background.setBackgroundColor(Color.argb(0xcc,0xFF,0xD4,0x52));
                break;
            case 0:
                frag_home_level_background.setBackgroundColor(Color.argb(0xcc,0xFF,0x7F,0x43));
                break;
            default:
                frag_home_level_background.setBackgroundColor(Color.argb(0xcc,0xE1,0x51,0x51));
                break;
        }
        frag_home_lv.setText("Level : " + (point + 1));
        frag_home_img.setImageDrawable(lv_img.get(point));
        frag_home_sim.setText(lv_str[point]);
        frag_home_spec.setText(lv_str_specific[point]);
    }

    public boolean isOpeningActivity(){
        return isOpeningActivity;
    }

}
