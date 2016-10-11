package gruppn.kasslr.game;

import android.graphics.Bitmap;
import android.graphics.RectF;

public class TargetImage {
    private Bitmap bitmap;
    private int age = 0;
    private float frameRate;
    private int gameWidth;
    private int gameHeight;
    private float x;
    private float y;
    private float width;
    private float height;

    public TargetImage(Bitmap bitmap, float frameRate, int gameWidth, int gameHeight) {
        this.bitmap = bitmap;
        this.frameRate = frameRate;
        this.gameWidth = gameWidth;
        this.gameHeight = gameHeight;
    }

    public void tick() {
        age++;

        if (age <= frameRate * 0.2f) {
            // First 0.2 seconds
            float dW = (gameWidth / 2f) / (frameRate * 0.2f);
            width += dW;
            height = width * 4f / 3f;

            // Center of screen
            x = (gameWidth - width) / 2f;
            y = (gameHeight - height) / 2f;
        } else if (age > frameRate * 0.8f && age <= frameRate * 1f) {
            // Between 0.8 and 1 seconds
            float dW = (gameWidth / 3f - gameWidth / 2f) / (frameRate * 0.2f);
            width += dW;
            height = width * 4f / 3f;

            // Move towards top center of screen
            x = (gameWidth - width) / 2f;
            float dY = (30f - (gameHeight / 2f - gameWidth / 3f)) / (frameRate * 0.2f);
            y += dY;
        }
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public RectF getRect() {
        return new RectF(x, y, x + width, y + height);
    }
}
