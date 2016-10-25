package gruppn.kasslr.game;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Window;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

import gruppn.kasslr.Kasslr;
import gruppn.kasslr.R;
import gruppn.kasslr.model.Vocabulary;
import gruppn.kasslr.model.VocabularyItem;

/**
 * Created by Adam on 2016-09-29.
 */

public class LaneGame extends Activity {

    private GestureDetectorCompat mDetector;
    private GameView view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        view = new GameView(this, ((Kasslr)getApplication()).getActiveVocabulary());
        setContentView(view);
        mDetector = new GestureDetectorCompat(this, new GameGestureListener());
    }

    @Override
    public void onPause() {
        view.pause();
        finish();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Tell the gameView resume method to execute
        view.resume();
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setMessage(R.string.want_to_exit_game)
                .setCancelable(false)
                .setPositiveButton("Ja", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        LaneGame.this.finish();
                    }
                })
                .setNegativeButton("Nej", null)
                .show();
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
        view.sendTouchEvent(event);
        return super.onTouchEvent(event);
    }

}

class GameView extends SurfaceView implements Runnable {

    Thread gameThread = null;
    SurfaceHolder ourHolder;

    private int gameWidth;
    private int gameHeight;
    volatile boolean playing;
    Canvas canvas;
    Paint paint;

    static final String DEBUG_TAG = "GAMELOGIC";
    static final int NUM_LANES = 3;
    static final int BACKGROUND_COLOR = Color.parseColor("#000000");

    private float frameRate = 60;
    private float frameTime = 1000 / frameRate;

    private float playerY = 150;
    private float playerX = 150;
    private float playerDeltaY = 0;
    private float playerDeltaX = 2;
    private int playerTarget = 0;

    private Vocabulary vocabulary;
    private List<VocabularyItem> completedWords = new ArrayList<VocabularyItem>();
    private HashMap<VocabularyItem, Integer> failedAttempts = new HashMap<>();
    private TargetImage targetImage = null;

    private Random particleSpawner;
    private Set<Particle> particles = new HashSet<Particle>();
    private Set<Target> liveTargets = new HashSet<Target>();
    private int score = 0;
    private long gameFinished = 0;

    private Background background;
    private float backgroundPosition = 0;

    private int tickCount = 0;
    private float tickLength = 0;

    private Kasslr app;

    Bitmap swipeInstruction, homeButton, retryButton;
    private boolean tutorialSkipped = false;
    private boolean isBeingTouched = false;
    private int touchingTime = 0;
    private HashMap<VocabularyItem, Bitmap> itemImageMap;
    private FeedbackOverlay feedback;

    public GameView(LaneGame gameActivity, Vocabulary vocabulary) {
        super(gameActivity);
        ourHolder = getHolder();
        paint = new Paint();
        playing = true;
        app = (Kasslr) gameActivity.getApplication();
        this.vocabulary = vocabulary;
        particleSpawner = new Random(vocabulary.getUniversalId() * 29411L);

        swipeInstruction = BitmapFactory.decodeResource(getResources(), R.drawable.swipe);
        homeButton = BitmapFactory.decodeResource(getResources(), R.drawable.ic_home_white_36dp);
        retryButton = BitmapFactory.decodeResource(getResources(), R.drawable.ic_replay_white_36dp);
        loadImages(vocabulary);

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
            tickLength = (endTime - startTime)*1.0f / frameTime;
            //fps = 1000.0 / (endTime - startTime)*1.0;
            try {
                if(deltaTime < 0)
                    deltaTime = 0;
                Thread.sleep(deltaTime);
            } catch (InterruptedException e) {
            }
        }
    }

    private void loadImages(Vocabulary vocabulary){
        itemImageMap = new HashMap<>();
        for(VocabularyItem item : vocabulary.getItems()){
            itemImageMap.put(item, loadImage(item));
        }
    }

    private Bitmap loadImage(VocabularyItem item){

        InputStream is = null;
        try {
            if (item.getImageName().startsWith("http")) {
                is = new URL(item.getImageName()).openStream();
            } else {
                is = new FileInputStream(app.getImageFile(item));
            }

            Bitmap bitmap = BitmapFactory.decodeStream(is);
            if (bitmap.getWidth() > 720) {
                return Bitmap.createScaledBitmap(bitmap, 720, 960, false);
            } else {
                return bitmap;
            }
        } catch (IOException e) {
            Log.e(DEBUG_TAG, "Failed to load image", e);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    // Ignore
                }
            }
        }

        return null;
    }

    private Bitmap getItemImage(VocabularyItem item) {
        return itemImageMap.get(item);
    }

    private void updateGameDimensions(){
        gameWidth = canvas.getWidth();
        gameHeight = canvas.getHeight();
        playerY = gameHeight - 260;
        playerX = gameWidth / 2;

        background = new Background(gameWidth, vocabulary.getUniversalId()*8491499L);

        for(int i = 0; i < gameHeight; i+=2){
            spawnStars(i);
        }

    }

    public void draw() {
        canvas = ourHolder.lockCanvas();
        if(gameWidth == 0 || gameHeight == 0)
            updateGameDimensions();

        canvas.drawColor(BACKGROUND_COLOR);

        drawBackground();
        drawParticles();
        drawCurrentImage();
        drawTargets();

        paint.setColor(Color.WHITE);
        Path playerPath = new Path();
        playerPath.moveTo(50,-50);
        playerPath.lineTo(100,50);
        playerPath.lineTo(100,140);
        playerPath.lineTo(0,140);
        playerPath.lineTo(0,50);
        playerPath.lineTo(50,-50);

        playerPath.offset(playerX-50, playerY-50);
        canvas.drawPath(playerPath, paint);
        //canvas.drawRect(new Rect((int)(playerX-50), (int)playerY, (int)(playerX-50)+100, (int)playerY+100), paint);


        paint.setColor(Color.WHITE);

        /*
        paint.setTextSize(30);
        paint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText("FPS: " + fps, 20, 40, paint);
        canvas.drawText("trgt: " + playerTarget + " / tk: " + tickLength, 20, 80, paint);
        canvas.drawText("run: " + tickCount, 20, 120, paint);
        canvas.drawText("spd: " + getTargetSpeed(), 20, 160, paint);
        canvas.drawText("particles: " + particles.size(), 20, 200, paint);
        canvas.drawText(background.getStats(), 20, 240, paint);
        canvas.drawText("words: " + completedWords.size() + "/" + vocabulary.getItems().size(), 20, 280, paint);
        canvas.drawText("touched: " + isBeingTouched + " for " + touchingTime, 20, 320, paint);
*/
        drawOverlay();

        ourHolder.unlockCanvasAndPost(canvas);
    }

    private void drawOverlay() {

        drawProgressIndicator();

        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(80);
        canvas.drawText(score+"p", gameWidth-100, 100, paint);

        if(gameFinished > 0){
            drawFinishScreen();
            return;
        }

        if(tutorialIsOpen()) {
            Paint alphaPaint = new Paint();
            paint.setColor(Color.BLACK);
            paint.setAlpha(100);
            if(tickCount > 80) {
                int alpha = 255 - (int) (((tickCount - 80) / 20.0) * 255);
                alphaPaint.setAlpha(alpha);
            }
            canvas.drawRect(0, 0, gameWidth, gameHeight, paint);
            float scaleFactor = (float) gameWidth/swipeInstruction.getWidth();
            canvas.drawBitmap(swipeInstruction, null, new RectF(0, gameHeight / 2, gameWidth, gameHeight / 2 + swipeInstruction.getHeight()*scaleFactor), alphaPaint);
            paint.setAlpha(255);
        }

        drawFeedback();
    }

    private void drawProgressIndicator() {
        int i = 1;
        paint.setColor(Color.WHITE);
        for(VocabularyItem item : vocabulary.getItems()){
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(3);
            canvas.drawCircle(gameWidth/20, gameHeight - (i*gameHeight/18), gameWidth/40, paint);

            if(completedWords.contains(item)) {
                paint.setStyle(Paint.Style.FILL);
                canvas.drawCircle(gameWidth/20, gameHeight - (i*gameHeight/18), gameWidth/55, paint);
            }

            i++;
        }
        paint.setStyle(Paint.Style.FILL);
    }

    private void drawFeedback(){

        if(feedback == null)
            return;

        if(feedback.getSpawnTime() + feedback.getLifeTime() < tickCount){
            feedback = null;
            return;
        }

        float progress = 1.0f- ((tickCount-feedback.getSpawnTime())*1.0f / feedback.getLifeTime()*1.0f);

        Paint fPaint = new Paint();
        fPaint.setStyle(Paint.Style.STROKE);
        fPaint.setColor(feedback.getColor());
        fPaint.setAlpha((int)(120 - 80*progress));
        fPaint.setStrokeWidth((float)Math.sin(progress*Math.PI/2.0)*gameWidth/8);
        canvas.drawRect(0, 0, gameWidth, gameHeight, fPaint);
    }

    private void drawFinishScreen() {

        if(gameFinished == 0)
            return;

        paint.setColor(0x88000000);
        canvas.drawRect(gameWidth/10, 4*gameWidth/10, gameWidth - gameWidth/10, gameHeight - 4*gameWidth/10, paint);

        paint.setColor(Color.WHITE);
        paint.setTextAlign(Paint.Align.CENTER);

        paint.setTextSize(gameWidth/15);
        canvas.drawText("SPELET ÖVER", gameWidth/2, gameHeight/2 - 2*gameWidth/10, paint);

        paint.setTextSize(2*gameWidth/10);
        canvas.drawText(score+"", gameWidth/2, gameHeight/2, paint);

        paint.setTextSize(gameWidth/15);
        canvas.drawText("poäng", gameWidth/2, gameHeight/2 + gameWidth/10, paint);

        canvas.drawBitmap(homeButton,  null,  new RectF(gameWidth/2 - 2*gameWidth/10, gameHeight/2+2*gameWidth/10, gameWidth/2 - 1*gameWidth/10, gameHeight/2+3*gameWidth/10), null);
        canvas.drawBitmap(retryButton, null,  new RectF(gameWidth/2 + 1*gameWidth/10, gameHeight/2+2*gameWidth/10, gameWidth/2 + 2*gameWidth/10, gameHeight/2+3*gameWidth/10), null);

    }

    private void drawBackground(){
        int startI = (int)(-backgroundPosition/background.getYPosition(-1));
        ArrayList<ArrayList<BackgroundTile>> chunks = background.getChunks(startI);
        int i = 0;
        for(ArrayList<BackgroundTile> tiles : chunks) {
            for(BackgroundTile tile : tiles){
                paint.setColor(tile.getColor());
                Path poly = tile.getPoly();
                poly.offset(0, gameHeight+background.getYPosition(i)+backgroundPosition%background.getYPosition(-1));
                canvas.drawPath(poly, paint);
            }
            i++;
        }
    }

    private void drawParticles(){
        for(Particle particle : particles){
            float progress = ((particle.getLifeSpan()-particle.getAge())*1.0F / particle.getLifeSpan()*1.0F);
            paint.setColor(particle.getColor(progress));
            canvas.drawCircle(particle.getX(), particle.getY(), progress*particle.getSize(), paint);
        }
    }

    private void drawTargets(){

        paint.setTextSize(34);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setColor(Color.WHITE);

        for(Target target : liveTargets){
            //canvas.drawCircle(target.getX(), target.getY(), 20, paint);
            canvas.drawText(target.getVocabularyItem().getName().toUpperCase(), target.getX(), target.getY(), paint);
        }
    }

    private void drawCurrentImage() {
        if (targetImage == null)
            return;

        canvas.drawBitmap(targetImage.getBitmap(), null, targetImage.getRect(), null);
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

            if (playerX > laneToX(playerTarget)) {
                playerDeltaX = -30;
            }
            if (playerX < laneToX(playerTarget)) {
                playerDeltaX = 30;
            }

        }else{
            Log.d(DEBUG_TAG, "flick verticla w/ velocity " + velocityY);
          //  playerDeltaY += velocityY/2000;
        }
    }

    public void tick() {
        tickCount++;

        attemptToEndSelf();

        if(playerY+100 > gameHeight || playerY < 0)
            playerDeltaY = -playerDeltaY;

        if(playerDeltaX > 0 && playerX > laneToX(playerTarget) || playerDeltaX < 0 && playerX < laneToX(playerTarget)){
            playerDeltaX = 0;
            playerX = laneToX(playerTarget);
        }


        playerX += playerDeltaX*tickLength;

        playerY += playerDeltaY*tickLength;

        playerDeltaY *= 0.8;

        if( touchingTime == 0 && playerY < gameHeight-260)
            playerDeltaY = 4;

        if(playerY > gameHeight-260) {
            playerDeltaY = 0;
            playerY = gameHeight-260;
        }

        if(playerY < 2*gameHeight/3){
            playerY = 2*gameHeight/3;
        }

        if(gameWidth > 0) {

            spawnParticles();
            updateParticles();

            spawnTargets();
            updateTargets();

            updateCurrentImage();
        }

        backgroundPosition += Math.sqrt(getTargetSpeed())/4;

        updatetouchingTime();
    }

    private void updatetouchingTime() {
        if(isBeingTouched)
            touchingTime++;
        else
            touchingTime = 0;

        if(touchingTime == 10)
            playerDeltaY = -10;

    }

    private void attemptToEndSelf() {
        if(gameFinished == 0)
            return;

        if(gameFinished + 3*1000 < System.currentTimeMillis()){

        }
    }


    private void closeGame(){
        ((Activity)getContext()).finish();
    }

    private int laneToX(int lane){
        return gameWidth/2 + (gameWidth/4) * lane;
    }

    private void spawnParticles(){

        Random rand = particleSpawner;

        //spawn exhaust
        for (int i=0; i < rand.nextInt(9); i++){
            float x = playerX-40+rand.nextInt(80);
            float y = playerY+80;
            float deltaX = -playerDeltaX * (rand.nextFloat()*0.08F) + (rand.nextFloat()*2.0F - 1.0F);
            float deltaY = -playerDeltaY * (rand.nextFloat()*0.08F) + (rand.nextFloat()*2.0F - 0.2F);
            int lifespan = rand.nextInt(200);
            int temperature = rand.nextInt(255);
            if(isHyperspeedActive())
                temperature = 128 + rand.nextInt(127);
            Particle particle = new Particle(x, y, deltaX, deltaY, lifespan, temperature, 20, true);
            particles.add(particle);
        }

        // spawn stars
        spawnStars(0);

    }

    private void spawnStars(int y){
        Random rand = particleSpawner;
        double limit = 0.14;
        if(isHyperspeedActive())
            limit = 0.4;
        if(rand.nextFloat() < limit) {
            float x = rand.nextInt(gameWidth);
            int depth = rand.nextInt(3) + 1;
            float deltaY = depth*depth*getTargetSpeed()/30f;
            Particle particle = new Particle(x, y, 0, deltaY, 20000, 128+rand.nextInt(127), depth, false);
            particles.add(particle);
        }
    }

    private void updateParticles(){
        Iterator<Particle> iterator = particles.iterator();
        while (iterator.hasNext()) {
            Particle particle = iterator.next();
            particle.tick(gameHeight, isHyperspeedActive());

            if(particle.isDead())
                iterator.remove();
        }

    }

    private void spawnTargets(){
        if(tutorialIsOpen())
            return;
        if(liveTargets.size() > 0)
            return;

        Random rand = new Random();

        VocabularyItem theItemWeWant = getUnusedVocabularyItem();
        if(theItemWeWant == null) {
            targetImage = null;
            finishGame();
            return;
        }

        targetImage = new TargetImage(getItemImage(theItemWeWant), frameRate, gameWidth, gameHeight);
        
        int benignTargetNum = rand.nextInt(NUM_LANES);

        ArrayList<VocabularyItem> spawnedItems = new ArrayList<VocabularyItem>();

        for(int i=0; i < NUM_LANES; i++){
            int x = laneToX(i-1);
            int y = -rand.nextInt(gameHeight/4) - gameHeight/4;
            VocabularyItem item = getRandomVocabularyItem(theItemWeWant, spawnedItems);
            if(benignTargetNum == i)
                item = theItemWeWant;
            else
                spawnedItems.add(item);
            Target target = new Target(x, y, benignTargetNum == i, item);
            liveTargets.add(target);
        }
    }

    private boolean isHyperspeedActive(){
        return touchingTime > 10;
    }

    private boolean tutorialIsOpen() {
        return (!tutorialSkipped && tickCount < 100);
    }

    private VocabularyItem getUnusedVocabularyItem(){

        if(vocabulary.getItems().size() <= completedWords.size())
            return null;

        Random rand = new Random();

        while(true) {
            VocabularyItem item = vocabulary.getItems().get(rand.nextInt(vocabulary.getItems().size()));
            if(!completedWords.contains(item))
                return item;
        }
    }

    private VocabularyItem getRandomVocabularyItem(VocabularyItem notThisItemThough, ArrayList<VocabularyItem> notTheseEither){
        Random rand = new Random();
        int attempts = 0; //this thing is stupid but yeh mvp
        while(true) {
            VocabularyItem item = vocabulary.getItems().get(rand.nextInt(vocabulary.getItems().size()));
            if(item != notThisItemThough && !notTheseEither.contains(item) || attempts > 20)
                return item;
            attempts++;
        }
    }

    private void updateTargets(){
        Iterator<Target> iterator = liveTargets.iterator();
        while (iterator.hasNext()) {
            Target target = iterator.next();
            target.tick(getTargetSpeed());

            if(target.getY() > playerY-50 && target.getY() < playerY+150 && Math.abs(target.getX() - playerX) < 70 ){
                if(target.isBenign()) {
                    makePlayerFeelGoodForPickingTheCorrectTarget(target.getVocabularyItem());
                    completedWords.add(target.getVocabularyItem());
                    feedback = new FeedbackOverlay(tickCount, Color.GREEN, 4);
                }else {
                    updateFailedAttempts(target.getVocabularyItem());
                    Vibrator v = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
                    v.vibrate(300);
                    feedback = new FeedbackOverlay(tickCount, Color.RED, 16);
                }
                iterator.remove();
                continue;
            }

            if(target.getY() > gameHeight)
                iterator.remove();
        }

    }

    private void makePlayerFeelGoodForPickingTheCorrectTarget(VocabularyItem item) {
        if(failedAttempts.containsKey(item)){
            int amountOfFailedAttempts = failedAttempts.get(item);
            if(amountOfFailedAttempts > 3){
                addPlayerScore(1);
                return;
            }

            addPlayerScore(4-amountOfFailedAttempts);
            return;
        }

        addPlayerScore(5);
    }

    private void addPlayerScore(int i) {
        score += i;
    }

    private void updateFailedAttempts(VocabularyItem item) {
        if(!failedAttempts.containsKey(item)) {
            failedAttempts.put(item, 1);
            return;
        }
        int amountOfFailedAttempts = failedAttempts.get(item);

        failedAttempts.put(item, amountOfFailedAttempts+1);

    }

    private void updateCurrentImage() {
        if (targetImage == null)
            return;

        targetImage.tick();
    }

    private void finishGame() {
        if(gameFinished > 0)
            return;
        gameFinished = System.currentTimeMillis();
    }

    private float getTargetSpeed(){
        if(isHyperspeedActive())
            return 24f*tickLength;
        return 4f*tickLength;
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

    public void sendTouchEvent(MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_DOWN){
            if(tutorialIsOpen())
                tutorialSkipped = true;
            if(gameFinished > 0){
                registerButtonPress(event);
            }

            isBeingTouched = true;
        }else if(event.getAction() == MotionEvent.ACTION_UP){
            isBeingTouched = false;
        }
    }

    private void registerButtonPress(MotionEvent event) {

        //primitive check of height bounds for end screen buttons
        if(event.getY() < gameHeight/2 + gameWidth/10 || event.getY() > gameHeight/2 + 4*gameWidth/10)
            return;

        if(event.getX() < gameWidth/2){
            closeGame();
        }else{
            Activity myActivity = ((Activity)getContext());
            Intent intent = myActivity.getIntent();
            myActivity.finish();
            myActivity.startActivity(intent);
        }
    }
}