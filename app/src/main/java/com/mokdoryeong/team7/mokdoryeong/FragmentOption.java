package com.mokdoryeong.team7.mokdoryeong;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;



public class FragmentOption extends Fragment {

    Switch frag_option_switch_alarm;
    Switch frag_option_switch_back;
    SeekBar frag_option_seek_alarm;
    TextView frag_option_sec;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_option, container, false);

        frag_option_seek_alarm = (SeekBar) rootView.findViewById(R.id.frag_option_seek_alarm);
        frag_option_sec = (TextView) rootView.findViewById(R.id.frag_option_sec);
        frag_option_switch_back = (Switch) rootView.findViewById(R.id.frag_option_swit_back);
        frag_option_switch_alarm = (Switch) rootView.findViewById(R.id.frag_option_swit_alarm);

        frag_option_sec.setText(BackgroundService.alarmSeconds + "초");
        frag_option_seek_alarm.setProgress((int)(((float)(BackgroundService.alarmSeconds - 10)) / 3.0f * 10.0f));

        if (BackgroundService.isChecked)
            frag_option_switch_back.setChecked(true);
        else
            frag_option_switch_back.setChecked(false);

        frag_option_switch_back.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    Toast.makeText(getActivity(), "WidgetService 시작", Toast.LENGTH_SHORT).show();
                    BackgroundService.isChecked = true;
                } else {
                    Toast.makeText(getActivity(), "WidgetService 중지", Toast.LENGTH_SHORT).show();
                    BackgroundService.isChecked = false;
                }
            }
        });


        frag_option_switch_alarm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    frag_option_seek_alarm.setEnabled(true);
                    BackgroundService.isAlarmOn = true;
                }
                else {
                    frag_option_seek_alarm.setEnabled(false);
                    BackgroundService.isAlarmOn = false;
                }

            }
        });
        frag_option_seek_alarm.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                printSelected(i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                BackgroundService.alarmSeconds = ((int)((seekBar.getProgress() / 100.0f) * 30.0f)) + 10;
                Log.d("Alarmer", BackgroundService.alarmSeconds + " ");
                getActivity().startService(new Intent(getActivity(), BackgroundService.class));
                getActivity().stopService(new Intent(getActivity(), BackgroundService.class));
            }
        });

        frag_option_seek_alarm.setEnabled(false);
        return rootView;
    }

    public void printSelected(int value) {
        frag_option_sec.setText((((int)((value / 100.0f) * 30.0f)) + 10) + "초");
    }

    public void update() {
        if (BackgroundService.isChecked)
            frag_option_switch_back.setChecked(true);
        else
            frag_option_switch_back.setChecked(false);
    }
}
