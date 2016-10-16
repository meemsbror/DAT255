package gruppn.kasslr.task;

import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.util.Log;

import gruppn.kasslr.Kasslr;
import gruppn.kasslr.db.KasslrDatabase;
import gruppn.kasslr.model.VocabularyItem;

public class RemoveItemTask extends AsyncTask<VocabularyItem, Void, Void> {
    private static final String DEBUG_TAG = "RemoveItemTask";

    private Kasslr app;

    public RemoveItemTask(Kasslr app) {
        this.app = app;
    }

    @Override
    protected Void doInBackground(VocabularyItem... items) {
        // Remove database entries
        KasslrDatabase db = null;
        try {
            db = new KasslrDatabase(app);

            for (VocabularyItem item : items) {
                if (item.getId() != 0) {
                    // Remove from database
                    db.remove(item);
                }

                // Remove image file
                app.getImageFile(item).delete();
            }
            Log.d(DEBUG_TAG, "Successfully removed " + items.length + " item(s)");
        } catch (SQLiteException e) {
            Log.e(DEBUG_TAG, "Failed to remove item(s)", e);
        } finally {
            if (db != null) {
                db.close();
            }
        }

        return null;
    }
}
