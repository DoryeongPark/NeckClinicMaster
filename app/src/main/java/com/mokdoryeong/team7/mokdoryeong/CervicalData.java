package com.mokdoryeong.team7.mokdoryeong;

import org.joda.time.DateTime;

import java.io.Serializable;

public class CervicalData implements Serializable {

    private DateTime startTime;
    private DateTime finishTime;
    private float averageAngle;
    private float cervicalRiskIndex;

    public CervicalData(){
        startTime = null;
    }

    public CervicalData(DateTime startTime, DateTime finishTime, float averageAngle){
        this.startTime = startTime;
        this.finishTime = finishTime;
        this.averageAngle = averageAngle;
        this.cervicalRiskIndex = (int)(averageAngle/20)+1;
    }

    public DateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(DateTime startTime) {
        this.startTime = startTime;
    }

    public float getCervicalRiskIndex() {
        return cervicalRiskIndex;
    }

    public void setCervicalRiskIndex(float cervicalRiskIndex) {
        this.cervicalRiskIndex = cervicalRiskIndex;
    }

    public DateTime getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(DateTime finishTime) {
        this.finishTime = finishTime;
    }

    public float getAverageAngle() {
        return averageAngle;
    }

    public void setAverageAngle(float averageAngle) {
        this.averageAngle = averageAngle;
    }

    public boolean isValid(){
        if(finishTime.minus(startTime.getMillis()).getSecondOfMinute() < 20)
            return false;
        else
            return true;
    }

    public String getAllTimeString()
    {
        return
                startTime.getYear() + "년 " +
                        startTime.getMonthOfYear() + "월 " +
                        startTime.getDayOfMonth() + "일 " +
                        startTime.getHourOfDay() + "시 " +
                        startTime.getMinuteOfHour() + "분 " +
                        startTime.getSecondOfMinute() + "초";

    }

    public String getHourTimeString(){
        return
                startTime.getYear() + "년 " +
                        startTime.getMonthOfYear() + "월 " +
                        startTime.getDayOfMonth() + "일 " +
                        startTime.getHourOfDay() + "시 ";
    }

    public String getSpecificString()
    {
        int sec = startTime.getSecondOfDay() - finishTime.getSecondOfDay();
        int min = sec/60;
        sec = sec%60;
        int hour = min/60;
        min = min % 60;
        String str = "기간 : "+hour+"시-"+min+"분-"+sec+"초\n";
        str+="평균목각도 : "+(int)averageAngle+"\n";
        str+="목부담레벨 : "+cervicalRiskIndex+"\n";
        return str;
    }

    public void add(CervicalData c){

        if(startTime == null){
            startTime = c.startTime;
            finishTime = c.finishTime;
            averageAngle = c.averageAngle;
            cervicalRiskIndex = c.cervicalRiskIndex;
            return;
        }

        if(startTime.isAfter(c.getStartTime().getMillis())){
            startTime = c.getStartTime();
        }

        if(finishTime.isBefore(c.getFinishTime().getMillis())){
            finishTime = c.getFinishTime();
        }

        averageAngle = (averageAngle + c.getAverageAngle()) / 2.0f;
        cervicalRiskIndex = (cervicalRiskIndex + c.getCervicalRiskIndex()) / 2.0f;
    }

}
