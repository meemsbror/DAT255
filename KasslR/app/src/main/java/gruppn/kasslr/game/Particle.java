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
    private int temperature;

    public Particle(float x, float y, float deltaX, float deltaY, int lifeSpan, int temperature){
        this.x = x;
        this.y = y;
        this.deltaX = deltaX;
        this.deltaY = deltaY;
        this.lifeSpan = lifeSpan;
        this.temperature = temperature;
        this.age = 0;
    }

    public void tick(){
        age++;
        x += deltaX;
        y += deltaY;

        y += 8;
    }

    public boolean isDead(){
        return (age > lifeSpan);
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public int getLifeSpan() {
        return lifeSpan;
    }

    public int getAge() {
        return age;
    }

    public int getTemperature() {
        return temperature;
    }
}
