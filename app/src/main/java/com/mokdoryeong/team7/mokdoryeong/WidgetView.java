package com.mokdoryeong.team7.mokdoryeong;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

public class WidgetView extends ImageView {

    private float angle = 0.0f;

    private boolean isMoving = false;

    private WindowManager wm;
    private WindowManager.LayoutParams wParams;

    public WidgetView(Context context, WindowManager wm) {
        super(context);
        this.wm = wm;
        initiateSettings();
    }

    public WidgetView(Context context, WindowManager wm, AttributeSet attrs) {
        super(context, attrs);
        this.wm = wm;
        initiateSettings();
    }

    public WidgetView(Context context, WindowManager wm, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.wm = wm;
        initiateSettings();
    }

    protected void onDraw(Canvas canvas){
        Paint paint = new Paint();
        Bitmap bitmap;

        if(angle < 15.0f)
            bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.angle0);
        else if(angle < 30.0f)
            bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.angle15);
        else if(angle < 45.0f)
            bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.angle30);
        else if(angle < 60.0f)
            bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.angle45);
        else if(angle < 75.0f)
            bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.angle60);
        else if(angle < 90.0f)
            bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.angle75);
        else
            bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.angle90);

        Rect imgDest = new Rect(0, 0, 180, 220);
        canvas.drawBitmap(bitmap, null, imgDest, null);
    }

    private void initiateSettings(){
        wParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        wParams.width = 180;
        wParams.height = 220;
        wParams.x = 0;
        wParams.y = 0;
        wm.addView(this, wParams);

        this.setOnTouchListener(new OnTouchListener(){
            float startX = 0.0f;
            float startY = 0.0f;
            int viewX = 0;
            int viewY = 0;
            @Override
            public boolean onTouch(View v, MotionEvent e) {
                switch(e.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        isMoving = true;
                        startX = e.getRawX();
                        startY = e.getRawY();
                        viewX = wParams.x;
                        viewY = wParams.y;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        int x = (int)(e.getRawX() - startX);
                        int y = (int)(e.getRawY() - startY);
                        wParams.x = viewX + x;
                        wParams.y = viewY + y;
                        wm.updateViewLayout(WidgetView.this, wParams);
                        break;
                    case MotionEvent.ACTION_UP:
                        isMoving = false;
                        break;
                }
                return true;
            }
        });
    }

    public void update(float angle){
        if(isMoving == false) {
            this.angle = angle;
            invalidate();
        }
    }

    public void setWidgetSize(int w, int h){
        wParams.width = w;
        wParams.height = h;
        wm.updateViewLayout(this, wParams);
    }

    public void setWidgetPos(int x, int y){
        wParams.x = x;
        wParams.y = y;
        wm.updateViewLayout(this, wParams);
    }

}
