package com.mokdoryeong.team7.mokdoryeong;

import android.os.Process;

import org.joda.time.DateTime;


public class DataHeartBeat extends Thread {

    private CervicalDataCreator cdc;
    private int timer = 2;
    private DateTime startTime;

    public DataHeartBeat(CervicalDataCreator cdc){
        this.cdc = cdc;
        startTime = DateTime.now();
    }
    @Override
    public void run(){
        Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
        for(;;){
            try{Thread.sleep(1000); --timer;}catch(Exception e){}
            if(timer == 0){
                cdc.onHeartBeatFinished(startTime, DateTime.now());
                break;
            }
        }
    }
    public void continueHeartBeat(){
        timer = 2;
    }
}
