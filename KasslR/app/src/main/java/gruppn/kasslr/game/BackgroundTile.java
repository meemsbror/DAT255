package gruppn.kasslr.game;

import android.graphics.Path;

/**
 * Created by Adam on 2016-10-03.
 */

public class BackgroundTile {
    private Path poly;
    private int color;

    public BackgroundTile(Path poly, int color){
        this.poly = poly;
        this.color = color;
    }

    public Path getPoly(){
        return new Path(poly);
    }

    public int getColor(){
        return color;
    }
}
