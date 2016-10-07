package gruppn.kasslr;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Environment;

import java.io.File;

import gruppn.kasslr.model.Shelf;

public class Kasslr extends Application {
    private Shelf shelf;
    private ScoreHelper scoreHelper = new ScoreHelper();
    private Bitmap sharedBitmap;

    @Override
    public void onCreate() {
        super.onCreate();

        shelf = new Shelf();
        initUserData();
    }

    private void initUserData() {
        SharedPreferences sharedPref = getSharedPreferences(Kasslr.class.getName(), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        String tempUserId = sharedPref.getString(getString(R.string.key_user_id), "none");

        if(tempUserId.equals("none")){
            tempUserId = requestNewUser();
        }
    }

    private String requestNewUser() {
        SharedPreferences sharedPref = getSharedPreferences(Kasslr.class.getName(), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        String userID = "dassid";

        editor.putString(getString(R.string.key_user_id), userID);
        editor.commit();

        return userID;
    }

    public void increaseScore(ScoreHelper.CompletedAction completedAction){
        scoreHelper.incScore(completedAction);
    }
    public int getCurrentScore(){
        return Integer.parseInt(getString(R.string.score));
    }

    public Shelf getShelf() {
        return shelf;
    }

    public String getUserId() {
        return getString(R.string.key_user_id);
    }

    public File getPictureDirectory() {
        return new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Kasslr");
    }

    public Bitmap getSharedBitmap() {
        Bitmap temp = sharedBitmap;
        sharedBitmap = null;
        return temp;
    }

    public void setSharedBitmap(Bitmap bitmap) {
        sharedBitmap = bitmap;
    }
}
