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
import android.util.Log;
import android.view.View;
import android.view.Window;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

/**
 * Created by Adam on 2016-09-29.
 */

public class LaneGame extends Activity {

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(new GameView(this));
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("LaneGame Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
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

}

class GameLoop extends Thread {

    private float frameRate = 60;
    private float frameTime = 1000 / frameRate;

    private DeGame logicGame;
    private Resources gameResources;
    private Canvas gameCanvas;

    public GameLoop(Resources res, Canvas canvas) {
        logicGame = new DeGame(res, canvas);
    }

    @Override
    public void run()
    {
        while (true) {
            float startTime = System.currentTimeMillis();

            logicGame.Update();
            logicGame.Draw();

            float endTime = System.currentTimeMillis();
            long deltaTime = (long) (frameTime - (endTime - startTime));
            try {
                Thread.sleep(deltaTime);
            } catch (InterruptedException e) {
            }
        }
    }

}

class DeGame {
    private Resources resources;
    private Canvas canvas;

    private int x = 0;
    private Paint paint;

    public DeGame(Resources res, Canvas cas) {
        resources = res;
        canvas = cas;

        paint = new Paint();
        paint.setTextSize(50);
    }

    public void Draw() {
        canvas.drawColor(Color.WHITE);
        canvas.drawRect(new Rect(x, 0, x + 50, 50), paint);
    }

    public void Update() {
        x += 1;
        Log.d("DEBUG", "X: " + x);
    }
}