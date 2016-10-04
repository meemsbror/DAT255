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
    private Player profileInformation = new Player();
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

        profileInformation.setUserId(tempUserId);
    }

    private String requestNewUser() {
        SharedPreferences sharedPref = getSharedPreferences(Kasslr.class.getName(), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        Player newUser = new Player("userID");

        editor.putString(getString(R.string.key_user_id), newUser.getUserId());
        editor.commit();

        return newUser.getUserId();
    }

    public void increaseScore(Player.CompletedAction completedAction){
        profileInformation.incScore(completedAction);
    }
    public int getCurrentScore(){
        return profileInformation.getScore();
    }

    public Shelf getShelf() {
        return shelf;
    }

    public String getUserId() {
        return profileInformation.getUserId();
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
