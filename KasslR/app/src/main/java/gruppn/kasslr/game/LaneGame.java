package gruppn.kasslr.game;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Window;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

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
        view.pause();
        super.onDestroy();
    }

    @Override
    public void onPause() {
        view.pause();
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Tell the gameView resume method to execute
        view.resume();
    }


    class GameGestureListener extends GestureDetector.SimpleOnGestureListener {
        private static final String DEBUG_TAG = "Gestures";

        @Override
        public boolean onFling(MotionEvent event1, MotionEvent event2,
                               float velocityX, float velocityY) {
            Log.d(DEBUG_TAG, "vel x: " + velocityX);
            Log.d(DEBUG_TAG, "vel y: " + velocityY);
            view.updateInput(velocityX, velocityY);
            return true;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        this.mDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

}

class GameView extends SurfaceView implements Runnable {

    private int gameWidth;
    private int gameHeight;
    Thread gameThread = null;
    SurfaceHolder ourHolder;
    volatile boolean playing;
    //Bitmap gameBitmap;
    Canvas canvas;
    Paint paint;
    final String DEBUG_TAG = "GAMELOGIC";
    private float frameRate = 60;
    private float frameTime = 1000 / frameRate;
    private double fps = 0;

    private int boxY = 150;
    private int boxX = 150;
    private float boxDeltaY = 3;
    private float boxDeltaX = 2;

    private Set<Particle> particles = new HashSet<Particle>();

    public GameView(Context context) {
        super(context);
        ourHolder = getHolder();
        paint = new Paint();
        playing = true;
    }

    @Override
    public void run()
    {
        while (playing) {
            long startTime = System.currentTimeMillis();

            tick();

            if(ourHolder.getSurface().isValid()) {
                draw();
            }

            long endTime = System.currentTimeMillis();
            long deltaTime = (long) (frameTime - (endTime - startTime));
            fps = 1000.0 / (endTime - startTime)*1.0;
            try {
                if(deltaTime < 0)
                    deltaTime = 0;
                Thread.sleep(deltaTime);
            } catch (InterruptedException e) {
            }
        }
    }

    private void updateGameDimensions(){
        gameWidth = canvas.getWidth();
        gameHeight = canvas.getHeight();
    }

    public void draw() {
        canvas = ourHolder.lockCanvas();
        if(gameWidth == 0 || gameHeight == 0)
            updateGameDimensions();

        canvas.drawColor(Color.parseColor("#2f81f0"));

        drawParticles();

        paint.setColor(Color.DKGRAY);
        canvas.drawRect(new Rect(boxX, boxY, boxX+100, boxY+100), paint);

        paint.setColor(Color.WHITE);
        paint.setTextSize(50);
        canvas.drawText("FPS: " + fps, 20, 40, paint);
        ourHolder.unlockCanvasAndPost(canvas);
    }

    public void drawParticles(){
        for(Particle particle : particles){
            float progress = ((particle.getLifeSpan()-particle.getAge())*1.0F / particle.getLifeSpan()*1.0F);
            paint.setColor(Color.argb((int)(progress*255.0), 255, 255, 255));
            canvas.drawCircle(particle.getX(), particle.getY(), progress*40.0F, paint);
        }
    }

    public void updateInput(float velocityX, float velocityY){

        float absX = Math.abs(velocityX);
        float absY = Math.abs(velocityY);

        //sidewards movement
        if(absX > absY){
            Log.d(DEBUG_TAG, "flick sidewards w/ velocity " + velocityX);
            boxDeltaX += velocityX/2000;

            // vertical movement
        }else{
            Log.d(DEBUG_TAG, "flick verticla w/ velocity " + velocityY);
            boxDeltaY += velocityY/2000;
        }
    }

    public void tick() {

        if(boxY+100 > gameHeight || boxY < 0)
            boxDeltaY = -boxDeltaY;
        if(boxX+100 > gameWidth || boxX < 0)
            boxDeltaX = -boxDeltaX;

        boxY += boxDeltaY;
        boxX += boxDeltaX;

        spawnParticles();
        updateParticles();

    }

    private void spawnParticles(){

        if(gameWidth < 1)
            return;

        int target = 240 - particles.size();
        Random rand = new Random();
        for (int i=0; i<target; i++){
            float x = boxX+50;
            float y = boxY+50;
            float deltaX = -boxDeltaX * (rand.nextFloat()*0.4F) + (rand.nextFloat() - 2.0F);
            float deltaY = -boxDeltaY * (rand.nextFloat()*0.4F) + (rand.nextFloat() - 2.0F);
            int lifespan = rand.nextInt(1000);
            Particle particle = new Particle(x, y, deltaX, deltaY, lifespan);
            particles.add(particle);
        }
    }

    private void updateParticles(){
        Iterator<Particle> iterator = particles.iterator();
        while (iterator.hasNext()) {
            Particle particle = iterator.next();
            particle.tick();

            if(particle.isDead())
                iterator.remove();
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

}