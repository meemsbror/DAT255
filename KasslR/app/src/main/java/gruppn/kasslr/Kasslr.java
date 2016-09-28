package gruppn.kasslr;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import gruppn.kasslr.model.Shelf;

public class Kasslr extends Application {
    private Shelf shelf;
    private String userId;

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

        userId = tempUserId;
    }

    private String requestNewUser() {
        SharedPreferences sharedPref = getSharedPreferences(Kasslr.class.getName(), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        String newUserId = "dassid";

        editor.putString(getString(R.string.key_user_id), newUserId);
        editor.commit();

        return newUserId;
    }

    public Shelf getShelf() {
        return shelf;
    }

    public String getUserId() {
        return userId;
    }
}
