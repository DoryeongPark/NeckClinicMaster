package com.mokdoryeong.team7.mokdoryeong;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.os.Handler;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import android.graphics.Rect;
import android.widget.LinearLayout;

import org.opencv.core.Scalar;

import java.util.ArrayList;


public class DiagnosisCreator extends Activity implements CameraBridgeViewBase.CvCameraViewListener2{

    private ArrayList<Rect> roiCandidates;
    private Mat finalImageCandidate;

    private int faceX1;
    private int faceY1;
    private int faceX2;
    private int faceY2;

    private FrameLayout mainLayout;

    private Mat imgFrame;
    private JavaCameraView javaCameraView;

    private LinearLayout guideViewPlatform = null;
    private LinearLayout progressViewPlatform = null;

    private ImageView targetView = null;

    private Rect faceDetectionArea = null;

    private FaceDetectionRoutine faceDetectionRoutine = null;
    static{
        System.loadLibrary("MyOpencvLibs");
    }

    BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this){
        @Override
        public void onManagerConnected(int status) {
            switch(status){
                case BaseLoaderCallback.SUCCESS:
                    javaCameraView.enableView();
                    break;
                default:
                    super.onManagerConnected(status);
                    break;
            }
        }
    };

    private void loadTargetView(){
        if(targetView != null)
            mainLayout.removeView(targetView);

        DisplayMetrics dm = getApplicationContext().getResources().getDisplayMetrics();
        int targetViewWidth = (int)((float)dm.heightPixels * 0.8f);
        targetView = (ImageView)findViewById(R.id.target_view);
        targetView.setLayoutParams(new FrameLayout.LayoutParams(targetViewWidth, targetViewWidth));
        ViewGroup.MarginLayoutParams margin = (ViewGroup.MarginLayoutParams)targetView.getLayoutParams();
        margin.setMargins((int)((float)(dm.widthPixels - targetViewWidth) / 2f),
                          (int)((float)(dm.heightPixels - targetViewWidth) / 2f), 0, 0);
        targetView.setLayoutParams(margin);
    }

    private void loadGuideView(){
        if(guideViewPlatform != null)
            mainLayout.removeView(guideViewPlatform);

        DisplayMetrics dm = getApplicationContext().getResources().getDisplayMetrics();
        int guideViewWidth = (int)((float)dm.widthPixels * 0.16f);
        int guideViewHeight= (int)((float)dm.heightPixels);
        guideViewPlatform = (LinearLayout)findViewById(R.id.guide_view_platform);
        guideViewPlatform.setLayoutParams(new FrameLayout.LayoutParams(guideViewWidth, guideViewHeight));
    }

    private void loadProgressView(){
        if(progressViewPlatform != null)
            mainLayout.removeView(progressViewPlatform);

        DisplayMetrics dm = getApplicationContext().getResources().getDisplayMetrics();
        int progressViewWidth = (int)((float)dm.widthPixels * 0.16f);
        int progressViewHeight= (int)((float)dm.heightPixels);
        progressViewPlatform = (LinearLayout)findViewById(R.id.progress_view_platform);
        progressViewPlatform.setLayoutParams(new FrameLayout.LayoutParams(progressViewWidth, progressViewHeight));
        ViewGroup.MarginLayoutParams margin = (ViewGroup.MarginLayoutParams) progressViewPlatform.getLayoutParams();
        margin.setMargins((int)((float)(dm.widthPixels - progressViewWidth)), 0, 0, 0);
        progressViewPlatform.setLayoutParams(margin);

    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diagnosis);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        faceX1 = faceY1 = faceX2 = faceY2 = 0;

        mainLayout = (FrameLayout)findViewById(R.id.diagnosis_mainlayout);

        javaCameraView = (JavaCameraView)findViewById(R.id.java_camera_view);
        javaCameraView.setVisibility(View.VISIBLE);
        javaCameraView.setCvCameraViewListener(this);

        loadTargetView();
        loadGuideView();
        loadProgressView();

        roiCandidates = new ArrayList<Rect>();
    }

    @Override
    public void onWindowFocusChanged (boolean hasFocus) {
        int[] location = new int[2];
        targetView.getLocationInWindow(location);
        faceDetectionArea = new Rect(location[0], location[1],
                location[0] + targetView.getWidth(), location[1] + targetView.getHeight());
    }

    @Override
    protected void onPause(){
        super.onPause();

        if(faceDetectionRoutine != null) {
            faceDetectionRoutine.abort();
            faceDetectionRoutine = null;
        }

        if(javaCameraView != null)
            javaCameraView.disableView();

    }

    @Override
    protected void onDestroy(){
        super.onDestroy();

        if(faceDetectionRoutine != null) {
            faceDetectionRoutine.abort();
            faceDetectionRoutine = null;
        }

        if(javaCameraView != null)
            javaCameraView.disableView();

    }

    @Override
    protected void onResume(){
        super.onResume();

        if(OpenCVLoader.initDebug()) {
            Log.d("OpenCV", "OpenCV successfully loaded");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
        else {
            Log.d("OpenCV", "OpenCV not loaded");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_9, this, mLoaderCallback);
        }

        if(faceDetectionRoutine == null) {
            faceDetectionRoutine = new FaceDetectionRoutine(this);
            faceDetectionRoutine.start();
        }
    }


    @Override
    public void onCameraViewStarted(int width, int height) {
        imgFrame = new Mat(height, width, CvType.CV_8UC4);
    }

    @Override
    public void onCameraViewStopped() {
        imgFrame.release();
    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        imgFrame = inputFrame.rgba();

        if(faceDetectionArea.contains(faceX1, faceY1, faceX2, faceY2))
            Core.circle(imgFrame, new Point((faceX1 + faceX2) / 2, faceY2), 10, new Scalar(0, 255, 0), -1);

        return imgFrame;
    }

    public Mat getCopiedFrame() {
        if(imgFrame == null)
            return null;
        Mat copiedFrame = new Mat();
        imgFrame.copyTo(copiedFrame);
        return copiedFrame;
    }

    public void setPoints(Mat resultFrame, int x1, int y1, int x2, int y2){

        faceX1 = y1; faceY1 = imgFrame.rows() - x1;
        faceX2 = y2; faceY2 = imgFrame.rows() - x2;

        if(resultFrame == null)
            return;

        //First condition - Is detected ROI located at appropriate face area
        if(faceDetectionArea.contains(faceX1, faceY1, faceX2, faceY2)) {
            if (roiCandidates.isEmpty()) {
                roiCandidates.add(new Rect(x1, y1, x2, y2));
                finalImageCandidate = null;
                finalImageCandidate = resultFrame;
                return;
            }else{//Second condition - Is detected ROI almost matching with first one
                int faceCenterX = (x1 + x2) / 2;
                int faceCenterY = (y1 + y2) / 2;
                int standardCenterX = roiCandidates.get(0).centerX();
                int standardCenterY = roiCandidates.get(0).centerY();
                if(faceCenterX > standardCenterX - 20 && faceCenterX < standardCenterX + 20 &&
                        faceCenterY > standardCenterY - 20 && faceCenterY < standardCenterY + 20) {
                    roiCandidates.add(new Rect(x1, y1, x2, y2));
                    Log.d("OpenCV", roiCandidates.size() + "");
                    if(roiCandidates.size() == 3){
                        makeDiagnosis();
                    }
                }else{
                    roiCandidates.clear();
                }
            }
        }else{
            return;
        }
    }

    private void makeDiagnosis(){
        faceDetectionRoutine.abort();
        javaCameraView.disableView();

        int[] faceStartPoint = new int[2];
        int[] neckStartPoint = new int[2];

        int faceStartPointX = 0;
        int faceStartPointY = 0;
        int neckStartPointX = 0;
        int neckStartPointY = 0;

        int dividePoint = roiCandidates.size();

        DisplayMetrics dm = getApplicationContext().getResources().getDisplayMetrics();

        for(Rect roi : roiCandidates){
            faceStartPointX += roi.right;
            faceStartPointY += (roi.top + roi.bottom) / 2;
            neckStartPointX += roi.left + ((roi.right - roi.left ) / 2);
            neckStartPointY += roi.bottom;
        }

        faceStartPointX /= dividePoint;
        faceStartPointY /= dividePoint;
        neckStartPointX /= dividePoint;
        neckStartPointY /= dividePoint;

        faceStartPoint[0] = faceStartPointX; faceStartPoint[1] = faceStartPointY;
        neckStartPoint[0] = neckStartPointX; neckStartPoint[1] = neckStartPointY;

        Intent intent = new Intent(this, DiagnosisView.class);
        intent.putExtra("image", finalImageCandidate.nativeObj);
        intent.putExtra("faceStartPoint", faceStartPoint);
        intent.putExtra("neckStartPoint", neckStartPoint);

        startActivity(intent);
    }

}
