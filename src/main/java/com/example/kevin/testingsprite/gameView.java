package com.example.kevin.testingsprite;

import android.view.SurfaceView;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

public class gameView extends SurfaceView implements Runnable{
    Thread gameThread = null;
    SurfaceHolder ourHolder;
    volatile boolean playing;
    Canvas canvas;
    Paint paint;
    long fps;
    private long timeThisFrame;

    Bitmap bg;
    Bitmap bitmapChar;
    boolean CharisMoving = false;
    boolean CharisJumping = false;

    float CharwalkSpeedPerSecond = 300;
    float CharjumpSpeedPerSecond = 500;
    float CharXPosition = 10;
    float CharYPosition = 410;
    private int Charframewidth = 144;
    private int Charframeheigh = 144;
    private int Charframecount = 6;
    private int CharcurrentFrame = 0;
    private long CharlastFrameChangeTime = 0;
    private int CharframeLengthInMilliseconds = 100;

    private Rect frameToDraw = new Rect(
            0,
            0,
            Charframewidth,
            Charframeheigh);

    RectF whereToDraw = new RectF(
            CharXPosition,
            CharYPosition,
            CharXPosition + Charframewidth,
            CharYPosition + Charframeheigh);

    public gameView(Context context) {
        super(context);
        ourHolder = getHolder();
        paint = new Paint();
        bg = BitmapFactory.decodeResource(this.getResources(), R.drawable.background);
        bitmapChar = BitmapFactory.decodeResource(this.getResources(), R.drawable.test);
        bitmapChar = Bitmap.createScaledBitmap(bitmapChar,
                Charframewidth * Charframecount,
                Charframeheigh * 4,
                false);
    }

    @Override
    public void run() {
        while (playing) {
            long startFrameTime = System.currentTimeMillis();
            update();
            draw();

            timeThisFrame = System.currentTimeMillis() - startFrameTime;
            if (timeThisFrame >= 1) {
                fps = 1000 / timeThisFrame;
            }
        }

    }
    public void update() {
        if(CharisMoving){
            CharXPosition = CharXPosition + (CharwalkSpeedPerSecond / fps);
            if(CharXPosition > this.getWidth()){
                CharXPosition = 0;
            }
        }
    }

    public void getCurrentrunFrame(){

        long time  = System.currentTimeMillis();
        if(CharisMoving) {
            if ( time > CharlastFrameChangeTime + CharframeLengthInMilliseconds) {
                CharlastFrameChangeTime = time;
                CharcurrentFrame--;
                if (CharcurrentFrame < 0) {

                    CharcurrentFrame = Charframecount-1;
                }
            }
        }
        frameToDraw.top =Charframeheigh * 2;
        frameToDraw.bottom =Charframeheigh * 3;
        frameToDraw.left = CharcurrentFrame * Charframewidth;
        frameToDraw.right = frameToDraw.left + Charframewidth;
    }
    public void getCurrentstandFrame(){

        long time  = System.currentTimeMillis();
        if(!CharisMoving) {
            if ( time > CharlastFrameChangeTime + CharframeLengthInMilliseconds) {
                CharlastFrameChangeTime = time;
                CharcurrentFrame--;
                if (CharcurrentFrame <= 0) {

                    CharcurrentFrame = Charframecount-1;
                }
            }
        }
        frameToDraw.top =0;
        frameToDraw.bottom =Charframeheigh;
        frameToDraw.left = CharcurrentFrame * Charframewidth;
        frameToDraw.right = frameToDraw.left + Charframewidth;
    }
    public void getCurrentjumpFrame(){

        long time  = System.currentTimeMillis();
        if(!CharisMoving) {
            if ( time > CharlastFrameChangeTime + CharframeLengthInMilliseconds) {
                CharlastFrameChangeTime = time;
                CharcurrentFrame--;
                if (CharcurrentFrame <= 0) {

                    CharcurrentFrame = Charframecount-1;
                }
            }
        }
        frameToDraw.top = Charframewidth;
        frameToDraw.bottom = Charframeheigh * 2;
        frameToDraw.left = CharcurrentFrame * Charframewidth;
        frameToDraw.right = frameToDraw.left + Charframewidth;
    }

    public void draw() {
        if (ourHolder.getSurface().isValid()) {
            canvas = ourHolder.lockCanvas();
            canvas.drawColor(Color.argb(255, 0, 0, 0));
            canvas.drawBitmap(bg, 0, 0, paint);
            paint.setColor(Color.argb(255, 249, 129, 0));
            paint.setTextSize(45);
            canvas.drawText("FPS:" + fps, 20, 40, paint);
            whereToDraw.set((int) CharXPosition,
                    (int) CharYPosition,
                    (int) CharXPosition + Charframewidth * 3,
                    (int) CharYPosition + Charframeheigh * 3);

            if (CharisMoving){
                getCurrentrunFrame();
            }
            else{
                getCurrentstandFrame();
            }

            canvas.drawBitmap(bitmapChar,
                    frameToDraw,
                    whereToDraw, paint);
            ourHolder.unlockCanvasAndPost(canvas);
        }

    }
    public void pause() {
        playing = false;
        try {
            gameThread.join();
        } catch (InterruptedException e) {
            Log.e("Error:", "joining thread");
        }

    }
    public void resume() {
        playing = true;
        gameThread = new Thread(this);
        gameThread.start();
    }
    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {

        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                CharisMoving = true;
                break;

            case MotionEvent.ACTION_UP:
                CharisMoving = false;
                break;
        }
        return true;
    }
}



