package com.mokdoryeong.team7.mokdoryeong;

import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.util.Log;

import org.joda.time.DateTime;


public class Alarmer extends Thread{

    private DateTime lastTime = DateTime.now();

    private int limitation = BackgroundService.alarmSeconds;
    private int timer = limitation;
    private Handler backgroundHandler;

    private boolean isAlive = false;

    public Alarmer(Handler backgroundHandler) {
        this.backgroundHandler = backgroundHandler;
    }

    public void run(){
        Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
        isAlive = true;
        while(true){
            --timer;
            try{Thread.sleep(1000);}catch(Exception e){}
            Log.d("Alarmer", timer+"");

            if(isAlive == false)
                break;

            if(timer <= 0) {
                notifyAlarm();
                break;
            }
        }
    }

    public void setLimitationSeconds(int limitation){
        this.limitation = limitation;
    }

    public void update(float angle, DateTime currentTime){

        if(currentTime.minus(lastTime.getMillis()).getMillis() > 1000) {
            lastTime = currentTime;
            if (!(10.0f < angle && angle < 35.0f))
                if (timer < limitation)
                    timer += 2;
        }
    }

    private void notifyAlarm(){
        Message msg = new Message();
        msg.what = 0;
        backgroundHandler.sendMessage(msg);
    }

    public void abort(){
        isAlive = false;
    }
}
