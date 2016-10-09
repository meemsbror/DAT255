package gruppn.kasslr;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;

import java.io.File;

import gruppn.kasslr.model.Shelf;

public class Kasslr extends Application {
    private Shelf shelf;
    private ProfileInformation profileInformation = new ProfileInformation();
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

    public void increaseScore(ProfileInformation.CompletedAction completedAction){
        profileInformation.incScore(completedAction);
    }

    public Shelf getShelf() {
        return shelf;
    }

    public String getUserId() {
        return getString(R.string.key_user_id);
    }
    public String getUserName() {
        return profileInformation.getName();
    }
    public void setUserName(String newName){
        profileInformation.setName(newName);
    }
    public int getScore(){
        return profileInformation.getScore();
    }
    public Uri getProfilePicture(){
        return profileInformation.getPicture();
    }
    public void setProfilePicture(Uri pic){
        profileInformation.setPicture(pic);
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
