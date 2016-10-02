package gruppn.kasslr.game;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
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
        finish();
        super.onPause();
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

    private float playerY = 150;
    private float playerX = 150;
    private float playerDeltaY = 3;
    private float playerDeltaX = 2;
    private PlayerState playerState = PlayerState.IDLE;

    private Set<Particle> particles = new HashSet<Particle>();

    private Bitmap background;
    private float backgroundPosition = 0;

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

        OpenSimplexNoise noise = new OpenSimplexNoise();
        background = Bitmap.createBitmap(gameWidth, gameHeight*2, Bitmap.Config.ARGB_8888);
        background.setHasAlpha(false);
        for (int y = 0; y < gameHeight*2; y++)
        {
            for (int x = 0; x < gameWidth; x++)
            {
                double value = noise.eval(x / 180.0, y / 180.0);
                int rgb = 0x2f81f0;
                if(value < 0.2)
                    rgb = 0x2279F0;
                if(value < -0.4)
                    rgb = 0x0C69E8;
                //int rgb = 0x010101 * (int)((value + 1.0) * 127.5);
                background.setPixel(x, y, rgb);
            }
        }

        backgroundPosition = -gameHeight;
    }

    public void draw() {
        canvas = ourHolder.lockCanvas();
        if(gameWidth == 0 || gameHeight == 0)
            updateGameDimensions();

        canvas.drawColor(Color.parseColor("#2f81f0"));

        if(background != null) {
            canvas.drawBitmap(background, 0, backgroundPosition, null);
        }
        drawParticles();

        paint.setColor(Color.DKGRAY);
        canvas.drawRect(new Rect((int)playerX, (int)playerY, (int)playerX+100, (int)playerY+100), paint);

        paint.setColor(Color.WHITE);
        paint.setTextSize(50);
        canvas.drawText("FPS: " + fps, 20, 40, paint);
        ourHolder.unlockCanvasAndPost(canvas);
    }

    public void drawParticles(){
        for(Particle particle : particles){
            float progress = ((particle.getLifeSpan()-particle.getAge())*1.0F / particle.getLifeSpan()*1.0F);
            paint.setColor(Color.argb((int)(progress*255.0), 255, 255, 255));
            canvas.drawCircle(particle.getX(), particle.getY(), progress*20.0F, paint);
        }
    }

    public void updateInput(float velocityX, float velocityY){

        float absX = Math.abs(velocityX);
        float absY = Math.abs(velocityY);

        playerDeltaX += velocityX/2000;
        playerDeltaY += velocityY/2000;
        //sidewards movement
        if(absX > absY){
            Log.d(DEBUG_TAG, "flick sidewards w/ velocity " + velocityX);
           // playerDeltaX += velocityX/2000;

            // vertical movement
        }else{
            Log.d(DEBUG_TAG, "flick verticla w/ velocity " + velocityY);
          //  playerDeltaY += velocityY/2000;
        }
    }

    public void tick() {

        if(playerY+100 > gameHeight || playerY < 0)
            playerDeltaY = -playerDeltaY;
        if(playerX+100 > gameWidth || playerX < 0)
            playerDeltaX = -playerDeltaX;

        playerY += playerDeltaY;
        playerX += playerDeltaX;

        playerDeltaX *= 0.995;
        playerDeltaY *= 0.995;

        spawnParticles();
        updateParticles();

        backgroundPosition += 1;
    }

    private void spawnParticles(){

        if(gameWidth < 1)
            return;

        int target = 300 - particles.size();
        Random rand = new Random();
        for (int i=0; i<target; i++){
            float x = playerX+50;
            float y = playerY+70;
            float deltaX = -playerDeltaX * (rand.nextFloat()*0.08F) + (rand.nextFloat()*5.0F - 2.5F);
            float deltaY = -playerDeltaY * (rand.nextFloat()*0.08F) + (rand.nextFloat()*5.0F - 2.5F);
            int lifespan = rand.nextInt(300);
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

enum PlayerState{
    IDLE, MOVING, ATTACKING;
}