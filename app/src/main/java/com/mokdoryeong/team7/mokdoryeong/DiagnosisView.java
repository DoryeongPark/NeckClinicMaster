package com.mokdoryeong.team7.mokdoryeong;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;

import java.util.Vector;

public class DiagnosisView extends Activity {

    private ImageView diagnosisView;
    private Mat image;
    private int[] faceStartPoint;
    private int[] neckStartPoint;

    private LinearLayout headerView;
    private LinearLayout footerView;

    private TextView textViewAngle;
    private TextView textViewPercent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.dialog_diagnosis);

        Intent intent = getIntent();
        image = new Mat(intent.getExtras().getLong("image"));
        faceStartPoint = intent.getExtras().getIntArray("faceStartPoint");
        neckStartPoint = intent.getExtras().getIntArray("neckStartPoint");

        Core.transpose(image, image);
        Core.flip(image, image, 1);

        Mat imageForAnalysis = new Mat();
        image.copyTo(imageForAnalysis);

        //Analysis Routine
        int[] detectedPointArray = OpencvRoutine.detectNeckPoints(imageForAnalysis.getNativeObjAddr(), faceStartPoint[0], faceStartPoint[1], neckStartPoint[0], neckStartPoint[1]);
        imageForAnalysis.release();
        Vector<Point> detectedPoints = new Vector<Point>();
        Vector<Integer> angles = new Vector<Integer>();

        for (int i = 0; i < detectedPointArray.length; ++i) {//If angle's bigger than 110 stop adding...
            if (i % 2 == 0) {
                if (i == 0) {
                    detectedPoints.add(new Point(detectedPointArray[i], detectedPointArray[i + 1]));
                } else {
                    int intervalAngle = calculateAngle(detectedPointArray[i - 2], detectedPointArray[i - 1],
                            detectedPointArray[i], detectedPointArray[i + 1]);
                    if (intervalAngle > 100 || intervalAngle < 30)
                        break;
                    detectedPoints.add(new Point(detectedPointArray[i], detectedPointArray[i + 1]));
                    angles.add(calculateAngle(faceStartPoint[0], faceStartPoint[1],
                            detectedPointArray[i], detectedPointArray[i + 1]));
                }
            }
        }

        Core.line(image, new Point(faceStartPoint[0], faceStartPoint[1]), detectedPoints.lastElement(), new Scalar(255, 0, 0), 5);
        Core.line(image, detectedPoints.lastElement(), new Point(detectedPoints.lastElement().x, faceStartPoint[1]), new Scalar(255, 0, 0), 5);
        Core.circle(image, new Point(faceStartPoint[0], faceStartPoint[1]), 5, new Scalar(0, 255, 0), -1);
        Core.circle(image, detectedPoints.lastElement(), 5, new Scalar(0, 255, 0), -1);

        int neckAngle = calculateAngle(faceStartPoint[0], faceStartPoint[1], (int) detectedPoints.lastElement().x, (int) detectedPoints.lastElement().y);

        Log.d("OpenCV", "Neck Angle - " + neckAngle);

        for (Point p : detectedPoints)
            Core.circle(image, p, 5, new Scalar(0, 0, 255), -1);

        float averageAngle = 0;
        for (int angle : angles)
            averageAngle += angle;
        averageAngle /= angles.size();

        float percent = 100.0f - ((averageAngle - 30) * 2); // (averageAngle - 30) / 50 * 100
        Log.d("OpenCV", "Percentage - " + percent);

        diagnosisView = (ImageView) findViewById(R.id.diagnosis_view);
        Bitmap resultImage = Bitmap.createBitmap(image.cols(), image.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(image, resultImage);
        diagnosisView.setImageBitmap(resultImage);

        initHeader();
        initFooter();

        textViewAngle = (TextView) findViewById(R.id.diagnosis_view_textview_angle);
        textViewPercent = (TextView) findViewById(R.id.diagnosis_view_textview_percent);

        textViewAngle.setText("목 각도 - " + String.valueOf(neckAngle) + "º");
        textViewPercent.setText("거북목 지수 - " + String.valueOf((int)percent) + "%");
        textViewAngle.setTypeface(MainActivity.old_L_type);
        textViewPercent.setTypeface(MainActivity.old_L_type);

    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        image.release();
    }

    @Override
    protected void onPause(){
        super.onPause();
        image.release();
        finish();
    }

    private void initHeader(){
        headerView = (LinearLayout)findViewById(R.id.diagnosis_view_header);
        DisplayMetrics dm = getApplicationContext().getResources().getDisplayMetrics();

        int width = (int)((float)dm.widthPixels);
        int height = (int)((float)dm.heightPixels * 0.16);

        headerView.setLayoutParams(new FrameLayout.LayoutParams(width, height));
        ViewGroup.MarginLayoutParams margin = (ViewGroup.MarginLayoutParams) headerView.getLayoutParams();
        margin.setMargins(0, 0, 0, 0);
        headerView.setLayoutParams(margin);
    }

    private void initFooter(){
        footerView = (LinearLayout)findViewById(R.id.diagnosis_view_footer);
        DisplayMetrics dm = getApplicationContext().getResources().getDisplayMetrics();

        int width = (int)((float)dm.widthPixels);
        int height = (int)((float)dm.heightPixels * 0.16);

        footerView.setLayoutParams(new FrameLayout.LayoutParams(width, height));
        ViewGroup.MarginLayoutParams margin = (ViewGroup.MarginLayoutParams) footerView.getLayoutParams();
        margin.setMargins(0, dm.heightPixels - height , 0, 0);
        footerView.setLayoutParams(margin);
    }

    private int calculateAngle(int upperX, int upperY, int lowerX, int lowerY){
        int dx, dy;
        int ax, ay;
        float t;

        dx = lowerX - upperX;
        ax = Math.abs(dx);
        dy = lowerY - upperY;
        ay = Math.abs(dy);
        t = (ax + ay == 0) ? 0 : (float)dy / (ax + ay);

        if (dx < 0)
            t = 2 - t;

        else if (dy < 0)
            t = 4 + t;

        t = t * 90.0f;
        return (int)t;
    }

}
