package com.mokdoryeong.team7.mokdoryeong;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ViewFlipper;

import org.joda.time.DateTime;
import org.joda.time.Period;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Vector;


public class GraphView extends ImageView {

    public static final int MODE_HOUR = 0;
    public static final int MODE_DAY = 1;
    public static final int MODE_WEEK= 2;

    private float width;
    private float height;

    private ArrayList<CervicalData> dataSet;

    private int currentMode;

    public GraphView(Context context) {
        super(context);
        initSettings();
    }

    public GraphView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initSettings();
    }

    public GraphView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initSettings();
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);

    }

    private void initSettings(){
        dataSet = new ArrayList<CervicalData>();
        this.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                                            ViewGroup.LayoutParams.MATCH_PARENT));
        this.setBackgroundColor(Color.WHITE);
    }

    private void cutTimeSpan(ArrayList<CervicalData> dataSet){
        this.dataSet.clear();
        DateTime graphStartTime;

        if(currentMode == 0)
            graphStartTime = DateTime.now().minusHours(6);
        else if(currentMode == 1)
            graphStartTime = DateTime.now().minusDays(6);
        else
            graphStartTime = DateTime.now().minusWeeks(6);

        for(CervicalData c : dataSet){
            if(c.getStartTime().isBefore(graphStartTime))
                break;
            this.dataSet.add(c);
        }
    }

    public void update(ArrayList<CervicalData> dataSet){
        width = getWidth();
        height = getHeight();
        cutTimeSpan(dataSet);
        invalidate();
    }

    public void setMode(int mode){
        this.currentMode = mode;
    }

    protected void onDraw(Canvas canvas){
        width = getWidth();
        height = getHeight();

        if(width == 0 || height == 0){
            return;
        }

        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(4);

        //For bottom axis bar
        float graphHeight = height * 0.85f;
        canvas.drawLine(0, graphHeight, width, graphHeight, paint);

        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(6);

        DateTime[] borderTimes = new DateTime[6];
        Float[] borderData = new Float[6];

        //Draw data as view
        if(currentMode == MODE_HOUR) {
            for (int i = 0; i < 6; ++i)
                borderTimes[i] = DateTime.now().minusHours(i + 1);
            this.setBackgroundColor(getResources().getColor(R.color.a_graph1));
        }
        else if(currentMode == MODE_DAY) {
            for (int i = 0; i < 6; ++i)
                borderTimes[i] = DateTime.now().minusHours((i + 1) * 4);
            this.setBackgroundColor(getResources().getColor(R.color.a_graph2));
        }
        else {
            for (int i = 0; i < 6; ++i)
                borderTimes[i] = DateTime.now().minusDays(i + 1);
            this.setBackgroundColor(getResources().getColor(R.color.a_graph3));
        }

        int borderCount = 0;
        Vector<Vector<Float>> bucket = new Vector<Vector<Float>>(6);
        for(int i = 0; i < 6; ++i)
            bucket.add(new Vector<Float>());

        for(int i = 0; i < dataSet.size(); ++i){
            CervicalData currentData = dataSet.get(i);
            if(currentData.getFinishTime().isAfter(borderTimes[borderCount]))
                bucket.get(borderCount).add(currentData.getAverageAngle());
            else {
                ++borderCount;
                --i;

                if(borderCount == 6)
                    break;
            }
        }

        for(int i = 0; i < 6; ++i){
            float sum = 0;
            for(float f : bucket.get(i))
                sum += f;
            if(bucket.get(i).size() != 0)
                borderData[i] = sum / bucket.get(i).size();
            else
                borderData[i] = 0.0f;
        }

        float startPoint = width * 11.0f / 12.0f;
        float interval = width / 6.0f;

        Point previousPoint = new Point(0, 0);
        Point currentPoint = new Point(0, 0);

        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(30);

        for(int i = 0; i < 6; ++i){
            if(borderData[i] != 0.0f) {
                currentPoint = new Point((int)startPoint, (int)(graphHeight * (1.0f - borderData[i] / 90.0f)));
                canvas.drawCircle(currentPoint.x, currentPoint.y, 10.0f, paint);
                canvas.drawText(String.valueOf(((int)((float)borderData[i]))), currentPoint.x, currentPoint.y - 25, paint);
            }

            if(previousPoint.x != 0 && previousPoint.y != 0)
                canvas.drawLine(currentPoint.x, currentPoint.y, previousPoint.x, previousPoint.y, paint);

            if(currentMode == MODE_HOUR)
                canvas.drawText(String.valueOf(i + 1) + "시간", startPoint, graphHeight + 35, paint);
            else if(currentMode == MODE_DAY)
                canvas.drawText(String.valueOf((i + 1) * 4) + "시간", startPoint, graphHeight + 35, paint);
            else
                canvas.drawText(String.valueOf(i + 1) + "일", startPoint, graphHeight + 35, paint);

            previousPoint = currentPoint;
            startPoint -= interval;
        }
    }
}
