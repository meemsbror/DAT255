package gruppn.kasslr.game;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.SparseArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Adam on 2016-10-03.
 */

public class Background {
    final int MAP_CHUNK_WIDTH = 16;
    final double MAP_SCALE = 6.0;
    final int MAP_LEVELS = 3;

    private int gameWidth = 0;
    private int gameHeight = 0;
    private int polySize = 0;

    private int[] colors;

    OpenSimplexNoise noise;
    private final HashMap<Coordinate, Integer> noiseArray = new HashMap<Coordinate, Integer>();
    private final SparseArray<ArrayList<BackgroundTile>> mapChunks = new SparseArray<ArrayList<BackgroundTile>>();

    public Background(int width, int height){
        this.gameWidth = width;
        this.gameHeight = height;
        polySize = gameWidth / MAP_CHUNK_WIDTH;
        noise = new OpenSimplexNoise();

        colors = new int[]{Color.parseColor("#150024"), Color.parseColor("#2F004F")};
    }

    public ArrayList<BackgroundTile> getMapChunk(int y){

        ArrayList<BackgroundTile> chunk = mapChunks.get(y);
        if(chunk != null)
            return chunk;

        if(y%30 == 0)
            clearOldChunks(y);

        return generateChunk(y);
    }

    public String getStats(){
        return "chnks: " + mapChunks.size() + "\nnoise: " + noiseArray.size();
    }

    private void clearOldChunks(int y) {
        if(mapChunks.size() > 100) {
            int target = mapChunks.size() - 100;
            mapChunks.removeAtRange(0, target);
        }
    }

    public int getYPosition(int y){
        return polySize*y;
    }

    private ArrayList<BackgroundTile> generateChunk(int y) {
        ArrayList<BackgroundTile> tiles = new ArrayList<BackgroundTile>();

        for(int l = 1; l < MAP_LEVELS; l++){
            for (int x = 0; x < MAP_CHUNK_WIDTH; x++)
            {
                int val = getPointSurroundings(l, x, y);
                if(val > 0) {
                    Path path = getPathFromPointValue(val);
                    path.offset(x * polySize, 0);
                    tiles.add(new BackgroundTile(path, colors[l-1]));
                }
            }
        }
        mapChunks.put(y, tiles);
        return tiles;
    }


    private int getPointSurroundings(int depth, int x, int y){
        int desc = 1;
        int pointDepth = 0;
        if(!noiseArray.containsKey(new Coordinate(x,y)))
            pointDepth = updateNoiseArray(x, y);
        else
            pointDepth = noiseArray.get(new Coordinate(x,y));
        if(pointDepth < depth)
            return 0;
        if(pointDepth > depth)
            return 1;

        if(safeGetPointValue(pointDepth, x-1, y))
            desc *= 2; //right
        if(safeGetPointValue(pointDepth, x+1, y))
            desc *= 3; //left
        if(safeGetPointValue(pointDepth, x, y-1))
            desc *= 5; //top
        if(safeGetPointValue(pointDepth, x, y+1))
            desc *= 7; //bottom

        return desc;
    }

    private int updateNoiseArray(int x, int y) {
        double value = noise.eval(x / MAP_SCALE, y / MAP_SCALE);

        int depth = 0;
        if(value < -0.5)
            depth = 2;
        else if(value < 0)
            depth = 1;

        noiseArray.put(new Coordinate(x,y), depth);
        return depth;
    }

    private boolean safeGetPointValue(int depth, int x, int y){
        if(!noiseArray.containsKey(new Coordinate(x,y))) {
            return updateNoiseArray(x, y) >= depth;
        }
        return noiseArray.get(new Coordinate(x,y)) >= depth;
    }

    private Path getPathFromPointValue(int val){

        Path square = new Path();
        square.moveTo(0,0);
        square.lineTo(polySize, 0);
        square.lineTo(polySize, polySize);
        square.lineTo(0, polySize);
        square.lineTo(0, 0);

        Path bigOlTriangle = new Path();
        bigOlTriangle.moveTo(0,0);
        bigOlTriangle.lineTo(polySize, 0);
        bigOlTriangle.lineTo(0, polySize);

        if(val == 10 || val == 15 || val == 21 || val == 14){
            Matrix mMatrix = new Matrix();
            RectF bounds = new RectF();
            bigOlTriangle.computeBounds(bounds, true);
            int rotations = 1;
            if(val == 21)
                rotations = 2;
            else if(val == 14)
                rotations = 3;
            else if(val == 10)
                rotations = 0;
            mMatrix.postRotate(rotations*90.0F, bounds.centerX(), bounds.centerY());
            bigOlTriangle.transform(mMatrix);
            return bigOlTriangle;
        }

        return square;
    }

}
