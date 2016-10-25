package gruppn.kasslr;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import gruppn.kasslr.model.ProfileInformation;
import gruppn.kasslr.model.Shelf;
import gruppn.kasslr.model.User;
import gruppn.kasslr.model.Vocabulary;
import gruppn.kasslr.model.VocabularyItem;
import gruppn.kasslr.task.LoadShelfTask;

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
    private List<Vocabulary> feedVocabularies;
    private boolean shouldUpdateFeed = true;

    @Override
    public void onCreate() {
        super.onCreate();

        shelf = new Shelf();
        feedVocabularies = new ArrayList<>();
    }

    public void loadShelf() {
        Log.d(DEBUG_TAG, "Loading shelf");
        new LoadShelfTask(this).execute(shelf);
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

        profileInformation.setUserId(newId);

        loadShelf();

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

    public void sendUsernameUpdate(Context context, String newName) {
        String url = Web.baseUrl+"?action=update_user";

        final Map<String, String> params = new HashMap();
        params.put("user", profileInformation.getUserId());
        params.put("name", newName);

        StringRequest strRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response)
                    {
                        System.out.println(response);
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
                            vocabulary.setUniversalId(0); // ghetto fix pls ignore

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
                    .setMaxRetries(3)
                    .startUpload(); //Starting the upload

            System.out.println("uploaded dat picture");

            triggerFeedUpdate();

        } catch (Exception exc) {
            System.out.println(exc.toString());
        }
    }

    private void uploadVocabularyItems(Vocabulary vocabulary) {
        for(VocabularyItem item : vocabulary.getItems()){
            String imagePath = getImageFile(item).getPath();
            String word = item.getName();
            uploadMultipart(imagePath, word, profileInformation.getUserId(), vocabulary.getUniversalId());
        }
    }

    public void increaseScore(ProfileInformation.CompletedAction completedAction) {
        profileInformation.incScore(completedAction);
    }


    public void loadFeedItems(Context context, final VocabularyFeedAdapter va){
        feedVocabularies.clear();

        String url = Web.baseUrl+"?action=feed";
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            va.addVocabularies(parseFeedJson(response.getJSONArray("feed")));
                            for (Vocabulary vocabulary : va.getVocabularies()) {
                                if (!feedVocabularies.contains(vocabulary)) {
                                    feedVocabularies.add(vocabulary);
                                    shouldUpdateFeed = false;
                                }
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                            va.addVocabularies(getShelf().getVocabularies());
                            for (Vocabulary vocabulary : getShelf().getVocabularies()) {
                                if (!feedVocabularies.contains(vocabulary)) {
                                    feedVocabularies.add(vocabulary);
                                    shouldUpdateFeed = false;
                                }
                            }
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        va.addVocabularies(getShelf().getVocabularies());
                        for (Vocabulary vocabulary : getShelf().getVocabularies()) {
                            if (!feedVocabularies.contains(vocabulary)) {
                                feedVocabularies.add(vocabulary);
                                shouldUpdateFeed = false;
                            }
                        }
                    }
                });

        Web.getInstance(context).addToRequestQueue(jsObjRequest);
    }


    private List<Vocabulary> parseFeedJson(JSONArray feed) throws JSONException {
        ArrayList<Vocabulary> response = new ArrayList<Vocabulary>();

        for (int i = 0 ; i < feed.length(); i++) {
            JSONObject obj = feed.getJSONObject(i);

            JSONObject ownerObj = obj.getJSONObject("owner");
            User owner = new User(ownerObj.getString("name"), ownerObj.getString("id"), ownerObj.getString("image"));

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
        return profileInformation.getUserId();
    }
    public String getUserName() {
        return profileInformation.getName();
    }
    public void setUserName(String newName){ profileInformation.setName(newName);}
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

    public List<Vocabulary> getFeedVocabularies(){
        return feedVocabularies;
    }

    public void triggerFeedUpdate() {
        shouldUpdateFeed = true;
    }

    public boolean shouldUpdateFeed() {
        return shouldUpdateFeed;
    }
}
