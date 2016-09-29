package gruppn.kasslr;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.MotionEventCompat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;

/**
 * Created by Adam on 2016-09-29.
 */

public class LaneGame extends Activity {


    final String DEBUG_TAG = "GaME";

    private GestureDetectorCompat mDetector;
    private GameView view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        view = new GameView(this);
        setContentView(view);
        mDetector = new GestureDetectorCompat(this, new GameGestureListener());
    }

    @Override
    public void onDestroy() {
        view.stop();
        super.onDestroy();
    }

    @Override
    public void onPause() {
        view.stop();
        super.onDestroy();
    }


    class GameGestureListener extends GestureDetector.SimpleOnGestureListener {
        private static final String DEBUG_TAG = "Gestures";

        @Override
        public boolean onFling(MotionEvent event1, MotionEvent event2,
                               float velocityX, float velocityY) {
            Log.d(DEBUG_TAG, "vel x: " + velocityX);
            Log.d(DEBUG_TAG, "vel y: " + velocityY);
            view.sendUserInput(velocityX, velocityY);
            return true;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        this.mDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

}

class GameView extends View {

    private GameLoop mainLoop;
    Bitmap gameBitmap;
    Canvas gameCanvas;

    public GameView(Context context) {
        super(context);
        this.setDrawingCacheEnabled(true);

        gameCanvas = new Canvas();

        mainLoop = new GameLoop(getResources(), gameCanvas);
        mainLoop.start();
    }

    @SuppressLint("DrawAllocation") @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int w = MeasureSpec.getSize(widthMeasureSpec);
        int h = MeasureSpec.getSize(heightMeasureSpec);

        gameBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);

        gameCanvas.setBitmap(gameBitmap);

        setMeasuredDimension(w, h);
    }

    @Override
    public void onDraw(Canvas canvas) {
        canvas.drawBitmap(gameBitmap, 0, 0, new Paint());
        invalidate();
    }


    public void stop() {
        mainLoop.tellToStop();
    }

    public void sendUserInput(float velocityX, float velocityY){
        mainLoop.updateInput(velocityX, velocityY);
    }

}

class GameLoop extends Thread {

    volatile boolean timeToStop = false;
    private float frameRate = 60;
    private float frameTime = 1000 / frameRate;

    private DeGame logicGame;
    private Resources gameResources;
    private Canvas gameCanvas;

    public GameLoop(Resources res, Canvas canvas) {
        logicGame = new DeGame(res, canvas);
    }

    public void tellToStop(){
        timeToStop = true;
    }

    public void updateInput(float velocityX, float velocityY){
        logicGame.updateInput(velocityX, velocityY);
    }

    @Override
    public void run()
    {
        while (!timeToStop) {
            float startTime = System.currentTimeMillis();

            logicGame.tick();
            logicGame.draw();

            float endTime = System.currentTimeMillis();
            long deltaTime = (long) (frameTime - (endTime - startTime));
            try {
                if(deltaTime < 0)
                    deltaTime = 0;
                Thread.sleep(deltaTime);
            } catch (InterruptedException e) {
            }
        }
    }

}

class DeGame {
    private Resources resources;
    private Canvas canvas;

    private int y = 50;
    private int x = 50;
    private int deltaY = 1;
    private float deltaX = 0;
    private Paint paint;
    final String DEBUG_TAG = "GAMELOGIC";

    public DeGame(Resources res, Canvas cas) {
        resources = res;
        canvas = cas;

        paint = new Paint();
        paint.setTextSize(50);
    }

    public void draw() {
        canvas.drawColor(Color.parseColor("#2f81f0"));
        paint.setColor(Color.WHITE);
        canvas.drawRect(new Rect(x, y, x+100, y+100), paint);
    }

    public void updateInput(float velocityX, float velocityY){

        float absX = Math.abs(velocityX);
        float absY = Math.abs(velocityY);

        //sidewards movement
        if(absX > absY){
            Log.d(DEBUG_TAG, "flick sidewards w/ velocity " + velocityX);
            deltaX += velocityX/2000;

            // vertical movement
        }else{
            Log.d(DEBUG_TAG, "flick verticla w/ velocity " + velocityY);
        }
    }

    public void tick() {
        y += deltaY;
        x += deltaX;
        if(y > canvas.getHeight())
            deltaY = -1;
        if(y < 0)
            deltaY = 1;
        if(x > canvas.getWidth() || x < 0)
            deltaX = -deltaX;

        // Log.d("DEBUG", "Y: " + y);
    }
}