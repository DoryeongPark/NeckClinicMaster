package com.mokdoryeong.team7.mokdoryeong;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.renderscript.RenderScript;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import org.joda.time.DateTime;

public class BackgroundService extends Service {

    public static boolean isAlive = false;

    public static boolean isAlarmOn = false;
    public static int alarmSeconds = 10;

    private WindowManager wm;

    private PitchCalculator pc;
    private WidgetView widget;
    private CervicalDataCreator cdc;

    private Alarmer alarmer;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what){
                case 0:
                    if(isAlarmOn)
                        executeAlarm();
                    break;
                default:
                    break;
            }
        }
    };

    private BroadcastReceiver windowStateReceiver = new BroadcastReceiver(){
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
                pc.turnOn();
                alarmer = new Alarmer(handler);
                alarmer.setLimitationSeconds(alarmSeconds);
                alarmer.start();

            }
            else if(intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                pc.turnOff();
                alarmer.abort();
            }
        }
    };

    public BackgroundService() {}

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //Change service state
        isAlive = true;

        //For widget
        wm = (WindowManager)getSystemService(WINDOW_SERVICE);
        widget = new WidgetView(this, wm);

        //For pitch angle
        pc = new PitchCalculator((SensorManager)getSystemService(SENSOR_SERVICE), wm);
        pc.registerPitchAngleListener(new PitchCalculator.PitchAngleListener() {
            @Override
            public void onPitchAngleCalculated(float pitchAngle, boolean isStanding) {
                //Handling sensor data
                Log.d("Sensor", String.valueOf(pitchAngle + " " + isStanding));
                widget.update(pitchAngle);
                cdc.update(pitchAngle);
                alarmer.update(pitchAngle, DateTime.now());
            }
        });

        registerReceiver(windowStateReceiver, new IntentFilter(Intent.ACTION_SCREEN_ON));
        registerReceiver(windowStateReceiver, new IntentFilter(Intent.ACTION_SCREEN_OFF));

        //To send sensor data for CervicalDataCreator
        cdc = new CervicalDataCreator(getApplicationContext());

                //Execute Alarmer
        alarmer = new Alarmer(handler);
        alarmer.setLimitationSeconds(alarmSeconds);
        alarmer.start();


        pc.turnOn();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        //Change Service state
        isAlive = false;

        if(widget != null) {
            ((WindowManager) getSystemService(WINDOW_SERVICE)).removeView(widget);
            widget = null;
        }

        unregisterReceiver(windowStateReceiver);
        pc.turnOff();
        alarmer.abort();
    }

    public void executeAlarm(){
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new Notification.Builder(this)
                .setContentTitle("거북목 알림")
                .setContentText("목 자세에 주의가 필요합니다.")
                .setSmallIcon(R.drawable.intro_center)
                .setTicker("목 자세에 주의가 필요합니다")
                .setContentIntent(pendingIntent)
                .build();

        notification.flags = Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(1, notification);
        Toast.makeText(this, "목 자세에 주의가 필요합니다", Toast.LENGTH_SHORT).show();

        alarmer = new Alarmer(handler);
        alarmer.start();
    }

}
