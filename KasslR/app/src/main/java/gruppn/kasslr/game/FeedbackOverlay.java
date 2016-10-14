package gruppn.kasslr.game;

/**
 * Created by Adam on 2016-10-13.
 */
public class FeedbackOverlay {

    private int spawnTime;
    private int color;
    private int lifeTime;

    public FeedbackOverlay(int spawnTime, int color, int lifeTime){
        this.spawnTime = spawnTime;
        this.color = color;
        this.lifeTime = lifeTime;

    }

    public int getSpawnTime() {
        return spawnTime;
    }

    public int getColor() {
        return color;
    }

    public int getLifeTime() {
        return lifeTime;
    }

}
