package gruppn.kasslr;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

import gruppn.kasslr.db.KasslrDatabase;
import gruppn.kasslr.model.Shelf;
import gruppn.kasslr.model.Vocabulary;
import gruppn.kasslr.model.VocabularyItem;

public class Kasslr extends Application {
    private static final String DEBUG_TAG = "Kasslr";

    private static final FileFilter IMAGE_FILTER = new FileFilter() {
        @Override
        public boolean accept(File file) {
            return file.getName().toLowerCase().endsWith(".jpg");
        }
    };

    private Shelf shelf;
    private Player profileInformation = new Player();
    private Bitmap sharedBitmap;

    @Override
    public void onCreate() {
        super.onCreate();

        shelf = new Shelf();
        loadShelf();
    }

    private void loadShelf() {
        new LoadShelfTask().execute(shelf);
    }

    public void initUserData(Context context) {

        requestNewUser(context);

        //hehe
        /*

        SharedPreferences sharedPref = getSharedPreferences(Kasslr.class.getName(), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        String tempUserId = sharedPref.getString(getString(R.string.key_user_id), "none");

        if(tempUserId.equals("none") || tempUserId.equals("dassid")){
            requestNewUser(context);
        }else{
            updateUserId(tempUserId);
        }
        */

    }

    public void updateUserId(String newId){
        profileInformation.setUserId(newId);
    }

    private void requestNewUser(Context context) {
        SharedPreferences sharedPref = getSharedPreferences(Kasslr.class.getName(), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        String android_id = Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        System.out.println("Adnroid id: " + android_id);
        String url = Web.baseUrl+"?action=login&device="+android_id;

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            System.out.println(response.toString());
                            updateUserId(response.getString("user"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub

                    }
                });

        Web.getInstance(context).addToRequestQueue(jsObjRequest);

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

    public File getImageDirectory() {
        return new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Kasslr");
    }

    public File[] getImageFiles() {
        File[] images = getImageDirectory().listFiles(IMAGE_FILTER);
        return images == null ? new File[] {} : images;
    }

    public File getImageFile(VocabularyItem item) {
        return new File(getImageDirectory(), item.getImageName() + ".jpg");
    }

    public Bitmap getSharedBitmap() {
        Bitmap temp = sharedBitmap;
        sharedBitmap = null;
        return temp;
    }

    public void setSharedBitmap(Bitmap bitmap) {
        sharedBitmap = bitmap;
    }

    private class LoadShelfTask extends AsyncTask<Shelf, Void, ShelfResult> {
        @Override
        protected ShelfResult doInBackground(Shelf... shelfs) {
            if (shelfs.length == 0) {
                throw new IllegalArgumentException();
            }

            ShelfResult result = new ShelfResult();
            result.shelf = shelfs[0];

            KasslrDatabase db = null;
            try {
                db = new KasslrDatabase(getApplicationContext());
                result.items = db.getItems();
                result.vocabularies = db.getVocabularies();
            } catch (SQLiteException e) {
                Log.e(DEBUG_TAG, "Failed to load shelf", e);
            } finally {
                if (db != null) {
                    db.close();
                }
            }

            if (result.items == null) {
                result.items = new ArrayList<>();
            }

            addItemsWithoutNames(result.items);

            return result;
        }

        private void addItemsWithoutNames(List<VocabularyItem> items) {
            List<VocabularyItem> itemsWithoutName = new ArrayList<>();

            for (File file : getImageFiles()) {
                String name = file.getName();
                name = name.substring(0, name.indexOf(".jpg"));

                if (!imageNameExists(items, name)) {
                    itemsWithoutName.add(new VocabularyItem("", name));
                }
            }

            items.addAll(itemsWithoutName);
        }

        private boolean imageNameExists(List<VocabularyItem> items, String name) {
            boolean exists = false;
            for (VocabularyItem item : items) {
                if (name.equals(item.getImageName())) {
                    exists = true;
                    break;
                }
            }
            return exists;
        }

        @Override
        protected void onPostExecute(ShelfResult result) {
            if (result.items != null) {
                Log.d(DEBUG_TAG, "Loaded " + result.items.size() + " items into shelf");
                result.shelf.addItems(result.items);
            }
            if (result.vocabularies != null) {
                Log.d(DEBUG_TAG, "Loaded " + result.vocabularies.size() + " vocabularies into shelf");
                result.shelf.addVocabularies(result.vocabularies);
            }
        }
    }

    private class ShelfResult {
        private Shelf shelf;
        private List<VocabularyItem> items;
        private List<Vocabulary> vocabularies;
    }
}
