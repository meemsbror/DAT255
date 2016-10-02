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
    final int MAP_CHUNK_WIDTH = 16;
    final int MAP_CHUNK_HEIGHT = 16*4;
    final int NUM_LANES = 3;

    private float frameRate = 60;
    private float frameTime = 1000 / frameRate;
    private double fps = 0;

    private float playerY = 150;
    private float playerX = 150;
    private float playerDeltaY = 0;
    private float playerDeltaX = 2;
    private int playerTarget = 0;
    private PlayerState playerState = PlayerState.IDLE;

    private Set<Particle> particles = new HashSet<Particle>();
    private Set<Target> liveTargets = new HashSet<Target>();
    private int score = 0;

    private Bitmap background;
    private float backgroundPosition = 0;

    private int tickCount = 0;

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
        background = Bitmap.createBitmap(MAP_CHUNK_WIDTH, MAP_CHUNK_HEIGHT, Bitmap.Config.ARGB_8888);
        background.setHasAlpha(false);
        for (int y = 0; y < MAP_CHUNK_HEIGHT; y++)
        {
            for (int x = 0; x < MAP_CHUNK_WIDTH; x++)
            {
                double value = noise.eval(x / 8.0, y / 8.0);
                int rgb = 0x2f81f0;
                if(value < 0.2)
                    rgb = 0x2279F0;
                if(value < -0.4)
                    rgb = 0x0C69E8;
                //int rgb = 0x010101 * (int)((value + 1.0) * 127.5);
                background.setPixel(x, y, rgb);
            }
        }

        playerY = gameHeight - 260;

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
        drawTargets();

        paint.setColor(Color.DKGRAY);
        canvas.drawRect(new Rect((int)(playerX-50), (int)playerY, (int)(playerX-50)+100, (int)playerY+100), paint);


        paint.setColor(Color.WHITE);
        paint.setTextSize(50);
        paint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText("FPS: " + fps, 20, 40, paint);
        canvas.drawText("trgt: " + playerTarget, 20, 80, paint);
        canvas.drawText("run: " + tickCount, 20, 120, paint);
        canvas.drawText("spd: " + getTargetSpeed(), 20, 160, paint);

        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(80);
        canvas.drawText(score+"", gameWidth-100, 100, paint);
        ourHolder.unlockCanvasAndPost(canvas);
    }

    private void drawParticles(){
        for(Particle particle : particles){
            float progress = ((particle.getLifeSpan()-particle.getAge())*1.0F / particle.getLifeSpan()*1.0F);
            paint.setColor(Color.argb((int)(progress*255.0), 255, 255, 255));
            canvas.drawCircle(particle.getX(), particle.getY(), progress*20.0F, paint);
        }
    }

    private void drawTargets(){
        for(Target target : liveTargets){
            paint.setColor(Color.WHITE);
            if(target.isBenign())
                paint.setColor(Color.GREEN);
            canvas.drawCircle(target.getX(), target.getY(), 20, paint);
        }
    }

    public void updateInput(float velocityX, float velocityY){

        float absX = Math.abs(velocityX);
        float absY = Math.abs(velocityY);

        //playerDeltaX += velocityX/2000;
        //playerDeltaY += velocityY/2000;
        //sidewards movement
        if(absX > absY){
            Log.d(DEBUG_TAG, "flick sidewards w/ velocity " + velocityX);
           // playerDeltaX += velocityX/2000;
            if(velocityX > 0 && playerTarget < 1)
                playerTarget += 1;
            if(velocityX < 0 && playerTarget > -1)
                playerTarget -= 1;
            // vertical movement
        }else{
            Log.d(DEBUG_TAG, "flick verticla w/ velocity " + velocityY);
          //  playerDeltaY += velocityY/2000;
        }
    }

    public void tick() {
        tickCount++;

        if(playerY+100 > gameHeight || playerY < 0)
            playerDeltaY = -playerDeltaY;
        if(playerX > laneToX(playerTarget)) {
            if(playerDeltaX > 0)
                playerDeltaX = -3;
            else
                playerDeltaX *= 1.1;
        }
        if(playerX < laneToX(playerTarget)){
            if(playerDeltaX < 0)
                playerDeltaX = 3;
            else
                playerDeltaX *= 1.1;

        }

        playerY += playerDeltaY;
        playerX += playerDeltaX;

        playerDeltaY *= 0.995;

        if(gameWidth > 0) {

            spawnParticles();
            updateParticles();

            spawnTargets();
            updateTargets();

        }

        backgroundPosition += 1;
    }

    private int laneToX(int lane){
        return gameWidth/2 + (gameWidth/4) * lane;
    }

    private void spawnParticles(){
        int target = 300 - particles.size();
        Random rand = new Random();
        for (int i=0; i<target; i++){
            float x = playerX;
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

    private void spawnTargets(){
        if(tickCount%200 != 0)
            return;

        Random rand = new Random();
        int benignTargetNum = rand.nextInt(NUM_LANES);
        for(int i=0; i < NUM_LANES; i++){
            int x = laneToX(i-1);
            int y = -rand.nextInt(gameHeight/2);
            Target target = new Target(x, y, benignTargetNum == i);
            liveTargets.add(target);
        }
    }

    private void updateTargets(){
        Iterator<Target> iterator = liveTargets.iterator();
        while (iterator.hasNext()) {
            Target target = iterator.next();
            target.tick(getTargetSpeed());

            if(target.getY() > playerY-5 && target.getDistance(playerX, target.getY()) < 10){
                if(target.isBenign())
                    score++;
                else
                    score -= 5;
                iterator.remove();
                continue;
            }

            if(target.getY() > gameHeight)
                iterator.remove();
        }

    }

    private int getTargetSpeed(){
        return (int)(4 + Math.sqrt(tickCount*0.4)* 0.3);
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