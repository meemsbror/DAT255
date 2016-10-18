package gruppn.kasslr.task;

import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import gruppn.kasslr.Kasslr;
import gruppn.kasslr.db.KasslrDatabase;
import gruppn.kasslr.model.Vocabulary;
import gruppn.kasslr.model.VocabularyItem;

public class RemoveVocabularyTask extends AsyncTask<Vocabulary, Void, List<VocabularyItem>> {
    private static final String DEBUG_TAG = "RemoveVocabularyTask";

    private Kasslr app;

    public RemoveVocabularyTask(Kasslr app) {
        this.app = app;
    }

    @Override
    protected List<VocabularyItem> doInBackground(Vocabulary... vocabularies) {
        List<VocabularyItem> itemsToRemove = new ArrayList<>();
        KasslrDatabase db = null;

        try {
            db = new KasslrDatabase(app);

            for (Vocabulary vocabulary : vocabularies) {
                if (vocabulary.getOwner().getId().equals(app.getUserId())) {
                    db.remove(vocabulary);

                }

                // Remove items that aren't mine
                for (VocabularyItem item : vocabulary.getItems()) {
                    if (item.isMine()) {
                        itemsToRemove.add(item);
                        db.remove(item);
                        app.getImageFile(item).delete();
                    }
                }
            }
        } catch (SQLiteException | IllegalArgumentException e) {
            Log.e(DEBUG_TAG, "Failed to remove vocabulary", e);
        } finally {
            if (db != null) {
                db.close();
            }
        }
        return itemsToRemove;
    }

    @Override
    protected void onPostExecute(List<VocabularyItem> itemsToRemove) {
        for (VocabularyItem item : itemsToRemove) {
            app.getShelf().removeItem(item);
        }
    }
}
