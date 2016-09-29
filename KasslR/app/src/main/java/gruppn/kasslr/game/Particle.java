package gruppn.kasslr.game;

/**
 * Created by Adam on 2016-09-29.
 */

public class Particle {

    private float x;
    private float y;
    private float deltaX;
    private float deltaY;
    private int lifeSpan;
    private int age;

    public Particle(float x, float y, float deltaX, float deltaY, int lifeSpan){
        this.x = x;
        this.y = y;
        this.deltaX = deltaX;
        this.deltaY = deltaY;
        this.lifeSpan = lifeSpan;
        this.age = 0;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getDeltaX() {
        return deltaX;
    }

    public void setDeltaX(float deltaX) {
        this.deltaX = deltaX;
    }

    public float getDeltaY() {
        return deltaY;
    }

    public void setDeltaY(float deltaY) {
        this.deltaY = deltaY;
    }

    public int getLifeSpan() {
        return lifeSpan;
    }

    public void setLifeSpan(int lifeSpan) {
        this.lifeSpan = lifeSpan;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void tick(){
        age++;
        x += deltaX;
        y += deltaY;
    }

    public boolean isDead(){
        return (age > lifeSpan);
    }
}
