package gruppn.kasslr.game;

import android.graphics.Color;

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
    private int size;
    private boolean hasGravity;

    public Particle(float x, float y, float deltaX, float deltaY, int lifeSpan, int temperature, int size, boolean hasGravity){
        this.x = x;
        this.y = y;
        this.deltaX = deltaX;
        this.deltaY = deltaY;
        this.lifeSpan = lifeSpan;
        this.temperature = temperature;
        this.age = 0;
        this.size = size;
        this.hasGravity = hasGravity;
    }

    public void tick(int yBoundary){
        age++;
        x += deltaX;
        y += deltaY;

        if(hasGravity)
            y += 8;

        if(y > yBoundary)
            age = lifeSpan + 1;
    }

    public int getColor(double progress){
        return Color.argb((int)(progress*255.0), 255-getTemperature()/2, 255-getTemperature()/2, getTemperature());
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

    public int getSize(){
        return size;
    }
}
