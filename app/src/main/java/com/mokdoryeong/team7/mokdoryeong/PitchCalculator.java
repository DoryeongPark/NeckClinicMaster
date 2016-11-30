package com.mokdoryeong.team7.mokdoryeong;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.Surface;
import android.view.WindowManager;


public class PitchCalculator implements SensorEventListener {

    private WindowManager wm;
    private SensorManager sm;

    private Sensor accSensor;
    private Sensor magSensor;

    private float[] accData;
    private float[] magData;
    private float[] rotation = new float[9];
    private float[] result = new float[3];

    private PitchAngleListener pitchAngleListener;

    interface PitchAngleListener{
        public void onPitchAngleCalculated(float pitchAngle, boolean isStanding);
    }

    public PitchCalculator(SensorManager sm, WindowManager wm){
        this.sm = sm;
        this.wm = wm;
        accSensor = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magSensor = sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
            accData = event.values.clone();

        if(event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
            magData = event.values.clone();
        calculatePitch();
    }

    private void calculatePitch(){
        float angle;
        boolean isStanding;

        if (accData != null && magData != null) {
            SensorManager.getRotationMatrix(rotation, null, accData, magData);
            SensorManager.getOrientation(rotation, result);

            if(wm.getDefaultDisplay().getRotation() == Surface.ROTATION_0 ||
                    wm.getDefaultDisplay().getRotation() == Surface.ROTATION_180) {//Portrait mode
                if (Math.abs(result[2]) > 1.3f && Math.abs(result[1]) < 0.75f) {//Laying
                    isStanding = false;
                    angle = (1.0f - Math.abs(result[1]) / 1.5f) * 90.0f;
                } else {
                    isStanding = true;//Standing
                    angle = Math.abs(result[1]) / 1.5f * 90.0f;
                }
                pitchAngleListener.onPitchAngleCalculated(angle, isStanding);
            }else{//Landscape mode
                if(Math.abs(result[2]) > 1.5f) {//Laying
                    isStanding = false;
                    angle = (Math.abs(result[2]) / 3.1f * 180.0f) - 90.0f;
                }else {//Standing
                    isStanding = true;
                    angle = Math.abs(result[2]) / 3.1f * 180.0f;
                }
                pitchAngleListener.onPitchAngleCalculated(angle, isStanding);
            }
        }
    }

    public void turnOn(){
        sm.registerListener(this, accSensor, SensorManager.SENSOR_DELAY_NORMAL);
        sm.registerListener(this, magSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void turnOff(){
        sm.unregisterListener(this);
    }

    public void registerPitchAngleListener(PitchAngleListener pitchAngleListener){
        this.pitchAngleListener = pitchAngleListener;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

}
