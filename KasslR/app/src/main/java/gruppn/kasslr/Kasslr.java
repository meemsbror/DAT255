package gruppn.kasslr;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.net.Uri;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.UploadNotificationConfig;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
    private ProfileInformation profileInformation = new ProfileInformation();
    private Bitmap sharedBitmap;
    private Vocabulary activeVocabulary;

    @Override
    public void onCreate() {
        super.onCreate();

        shelf = new Shelf();
    }

    public void loadShelf() {
        Log.d(DEBUG_TAG, "Loading shelf");
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

    public void updateUserId(String newId) {

        SharedPreferences sharedPref = getSharedPreferences(Kasslr.class.getName(), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        if (newId.equals("offline")) {

            newId = sharedPref.getString(getString(R.string.key_user_id), "none");

        }

        editor.putString(getString(R.string.key_user_id), newId);
        editor.apply();

        System.out.println("logged in with user id " + newId);

    }

    private void requestNewUser(Context context) {

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
                        updateUserId("offline");
                    }
                });

        Web.getInstance(context).addToRequestQueue(jsObjRequest);

    }

    public void uploadVocabulary(Context context, final Vocabulary vocabulary) {
        String url = Web.baseUrl+"?action=new_vocabulary";

        final Map<String, String> params = new HashMap();
        params.put("user", profileInformation.getUserId());
        params.put("title", vocabulary.getTitle());

        StringRequest strRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response)
                    {
                        System.out.println(response);
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            System.out.println(jsonResponse.getInt("vocabularyId"));

                            vocabulary.setUniversalId(jsonResponse.getInt("vocabularyId"));
                            uploadVocabularyItems(vocabulary);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        System.out.println(error.toString());
                    }
                })
        {
            @Override
            protected Map<String, String> getParams()
            {
                return params;
            }
        };

        Web.getInstance(context).addToRequestQueue(strRequest);
    }

    public void uploadMultipart(String imagePath, String word, String user, int vocabularyId) {

        //Uploading code
        try {
            String uploadId = UUID.randomUUID().toString();

            //Creating a multi part request
            new MultipartUploadRequest(this, uploadId, Web.baseUrl+"?action=new_item")
                    .setUtf8Charset()
                    .addFileToUpload(imagePath, "image")
                    .addParameter("word", word)
                    .addParameter("user", user)
                    .addParameter("vocabularyId", String.valueOf(vocabularyId))
                    .setNotificationConfig(new UploadNotificationConfig())
                    .setMaxRetries(2)
                    .startUpload(); //Starting the upload

        } catch (Exception exc) {
            System.out.println(exc.toString());
        }
    }

    private void uploadVocabularyItems(Vocabulary vocabulary) {
        for(VocabularyItem item : vocabulary.getItems()){
            String imagePath = getImageFile(item).getPath();
            String word = item.getName();
            uploadMultipart(imagePath, word, getUserId(), vocabulary.getUniversalId());
        }
    }

    public void increaseScore(ProfileInformation.CompletedAction completedAction) {
        profileInformation.incScore(completedAction);
    }
    public void loadFeedItems(Context context, final VocabularyFeedAdapter va){

        String url = Web.baseUrl+"?action=feed";

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            va.addVocabularies(parseFeedJson(response.getJSONArray("feed")));
                        } catch (JSONException e) {
                            e.printStackTrace();
                            va.addVocabularies(getShelf().getVocabularies());
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        va.addVocabularies(getShelf().getVocabularies());
                    }
                });

        Web.getInstance(context).addToRequestQueue(jsObjRequest);



    }

    private List<Vocabulary> parseFeedJson(JSONArray feed) throws JSONException {
        ArrayList<Vocabulary> response = new ArrayList<Vocabulary>();

        for (int i = 0 ; i < feed.length(); i++) {
            JSONObject obj = feed.getJSONObject(i);
            String owner = obj.getString("owner");
            String title = obj.getString("title");
            int universalId = obj.getInt("id");
            Vocabulary voc = new Vocabulary(owner, title);
            voc.setUniversalId(universalId);

            ArrayList<VocabularyItem> items = new ArrayList<VocabularyItem>();
            JSONArray itemsArr = obj.getJSONArray("items");

            for(int j = 0; j < itemsArr.length(); j++){
                JSONArray itemValues = itemsArr.getJSONArray(j);
                VocabularyItem item = new VocabularyItem(itemValues.getString(0), itemValues.getString(1));
                item.setMine(false);
                items.add(item);
            }
            voc.setItems(items);
            response.add(voc);
        }
        return response;
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

    public Vocabulary getActiveVocabulary() {
        return activeVocabulary;
    }

    public void setActiveVocabulary(Vocabulary activeVocabulary) {
        this.activeVocabulary = activeVocabulary;
    }

    public File getImageDirectory() {
        return new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Kasslr");
    }

    private File getCachedImageDirectory() {
        return new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Cachelr");
    }

    public File[] getImageFiles() {
        File[] images = getImageDirectory().listFiles(IMAGE_FILTER);
        return images == null ? new File[] {} : images;
    }

    public File getImageFile(VocabularyItem item) {
        if(item.isMine())
            return new File(getImageDirectory(), item.getImageName() + ".jpg");

        return new File(getCachedImageDirectory(), item.getImageName() + ".jpg");
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
            result.items = new ArrayList<>();

            KasslrDatabase db = null;
            try {
                db = new KasslrDatabase(getApplicationContext());
                result.items.addAll(db.getItems());
                result.vocabularies = db.getVocabularies(result.items);
            } catch (SQLiteException | NullPointerException e) {
                Log.e(DEBUG_TAG, "Failed to load shelf", e);
            } finally {
                if (db != null) {
                    db.close();
                }
            }

            addItemsWithoutNames(result.items);

            return result;
        }

        private void addItemsWithoutNames(List<VocabularyItem> items) {
            List<VocabularyItem> itemsWithoutName = new ArrayList<>();

            for (File file : getImageFiles()) {
                String imageName = file.getName();
                imageName = imageName.substring(0, imageName.indexOf(".jpg"));

                if (!imageNameExists(items, imageName)) {
                    itemsWithoutName.add(new VocabularyItem("", imageName));
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
                result.shelf.getItems().clear();
                result.shelf.addItems(result.items);
            }
            if (result.vocabularies != null) {
                Log.d(DEBUG_TAG, "Loaded " + result.vocabularies.size() + " vocabularies into shelf");
                result.shelf.getVocabularies().clear();
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
