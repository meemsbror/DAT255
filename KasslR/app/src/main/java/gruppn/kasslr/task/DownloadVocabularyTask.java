package gruppn.kasslr.task;

import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import gruppn.kasslr.Kasslr;
import gruppn.kasslr.db.KasslrDatabase;
import gruppn.kasslr.model.Vocabulary;
import gruppn.kasslr.model.VocabularyItem;

public class DownloadVocabularyTask extends AsyncTask<Vocabulary, Void, Void> {
    private static final String DEBUG_TAG = "DownloadVocabularyTask";

    private Kasslr app;

    public DownloadVocabularyTask(Kasslr app) {
        this.app = app;
    }

    @Override
    protected Void doInBackground(Vocabulary... vocabularies) {
        for (Vocabulary fromVocabulary : vocabularies) {
            Vocabulary vocabulary = new Vocabulary(fromVocabulary.getOwner(), fromVocabulary.getTitle());

            // Download images
            List<VocabularyItem> items = new ArrayList<>();
            for (VocabularyItem fromItem : fromVocabulary.getItems()) {
                String url = fromItem.getImageName();
                String imageName = UUID.randomUUID().toString();

                VocabularyItem item = new VocabularyItem(fromItem.getName(), imageName, fromItem.getId(), fromItem.isMine());

                try {
                    downloadImageToFile(url, app.getImageFile(item));
                    items.add(item);
                } catch (IOException e) {
                    Log.e(DEBUG_TAG, "Failed to download image from " + url, e);
                }
            }
            vocabulary.setItems(items);

            // Add to shelf
            app.getShelf().addVocabulary(vocabulary);

            // Save to database
            KasslrDatabase db = null;
            try {
                db = new KasslrDatabase(app);
                db.save(vocabulary);

                Log.d(DEBUG_TAG, "Successfully saved vocabulary " + vocabulary.getTitle());
            } catch (SQLiteException | IllegalArgumentException e) {
                Log.e(DEBUG_TAG, "Failed to save vocabulary " + vocabulary.getTitle(), e);
            } finally {
                if (db != null) {
                    db.close();
                }
            }
        }
        return null;
    }

    private void downloadImageToFile(String url, File file) throws IOException {
        InputStream is = null;
        OutputStream os = null;
        try {
            is = new URL(url).openStream();
            Bitmap bitmap = BitmapFactory.decodeStream(is);

            os = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    // Ignore
                }
            }
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    // Ignore
                }
            }
        }
    }

    private File getImageFile(String imageName) {
        if (!app.getImageDirectory().exists()) {
            app.getImageDirectory().mkdir();
        }

        return new File(app.getImageDirectory(), imageName + ".jpg");
    }
}
