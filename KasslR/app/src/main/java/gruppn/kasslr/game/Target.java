package gruppn.kasslr.game;

import gruppn.kasslr.model.VocabularyItem;

/**
 * Created by Adam on 2016-10-03.
 */

public class Target {
    private float x;
    private float y;
    private boolean benign;
    private VocabularyItem vocabularyItem;

    public Target(float x, float y, boolean benign, VocabularyItem vocabularyItem){
        this.x = x;
        this.y = y;
        this.benign = benign;
        this.vocabularyItem = vocabularyItem;
    }

    public void tick(float velocity){
        y += velocity;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public double getDistance(float x2, float y2){
        return Math.sqrt(Math.pow((x2 - x), 2) + Math.pow((y2 - y), 2));
    }

    public boolean isBenign() {
        return benign;
    }

    public VocabularyItem getVocabularyItem(){ return vocabularyItem; }
}
