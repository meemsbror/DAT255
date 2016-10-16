package gruppn.kasslr.task;

import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import gruppn.kasslr.Kasslr;
import gruppn.kasslr.db.KasslrDatabase;
import gruppn.kasslr.model.Shelf;
import gruppn.kasslr.model.Vocabulary;
import gruppn.kasslr.model.VocabularyItem;

public class LoadShelfTask extends AsyncTask<Shelf, Void, LoadShelfTask.Result> {
    private static final String DEBUG_TAG = "LoadShelfTask";

    private Kasslr app;

    public LoadShelfTask(Kasslr app) {
        this.app = app;
    }

    @Override
    protected Result doInBackground(Shelf... shelfs) {
        if (shelfs.length == 0) {
            throw new IllegalArgumentException();
        }

        Result result = new Result();
        result.shelf = shelfs[0];
        result.items = new ArrayList<>();

        KasslrDatabase db = null;
        try {
            db = new KasslrDatabase(app);
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

        for (File file : app.getImageFiles()) {
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
    protected void onPostExecute(Result result) {
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

    class Result {
        private Shelf shelf;
        private List<VocabularyItem> items;
        private List<Vocabulary> vocabularies;
    }
}