package gruppn.kasslr;

import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.TextView;

import gruppn.kasslr.db.KasslrDatabase;
import gruppn.kasslr.model.VocabularyItem;

public class EditItemActivity extends AppCompatActivity {
    public static final String EXTRA_IMAGE_URI = "image";
    public static final String EXTRA_ITEM_INDEX = "item_index";
    public static final String EXTRA_EXIT_TRANSITION = "exit_transition";

    private static final String DEBUG_TAG = "EditItemActivity";

    private Kasslr app;
    private VocabularyItem item;
    private boolean exitTransition;

    private TextView txtWord;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);

        app = (Kasslr) getApplication();

        txtWord = (TextView) findViewById(R.id.txtWord);
        ImageView imgWord = (ImageView) findViewById(R.id.imgWord);

        Uri image = (Uri) getIntent().getExtras().get(EXTRA_IMAGE_URI);
        int itemIndex = getIntent().getExtras().getInt(EXTRA_ITEM_INDEX, -1);

        if (image != null) {
            // Create item from image
            String name = image.getLastPathSegment();
            name = name.substring(0, name.indexOf(".jpg"));
            item = new VocabularyItem("", name);
            app.getShelf().getItems().add(item);
        } else if (itemIndex != -1) {
            // Get item from shelf
            item = app.getShelf().getItems().get(itemIndex);
        } else {
            Log.e(DEBUG_TAG, "Extras must either contain item index or an image uri");
            finish();
        }

        txtWord.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int action, KeyEvent keyEvent) {
                if (action == EditorInfo.IME_ACTION_DONE) {
                    saveItem(textView);
                    return true;
                }
                return false;
            }
        });

        // Set item name
        txtWord.setText(item.getName());

        // Set item image
        Bitmap bitmap = app.getSharedBitmap();
        if (bitmap == null) {
            bitmap = BitmapFactory.decodeFile(app.getImageFile(item).getAbsolutePath());
        }
        imgWord.setImageBitmap(Bitmap.createScaledBitmap(bitmap, getImageWidth(), getImageHeight(), false));

        exitTransition = getIntent().getExtras().getBoolean(EXTRA_EXIT_TRANSITION, true);
    }

    @Override
    public void onBackPressed() {
        close();
    }

    private void close() {
        if (!exitTransition) {
            finish();
        } else {
            supportFinishAfterTransition();
        }
    }

    public void saveItem(View view) {
        item.setName(txtWord.getText().toString().trim());
        if (!item.getName().isEmpty()) {
            new SaveItemsTask().execute(item);
        }

        setResult(app.getShelf().getItems().indexOf(item));
        close();
    }

    private int getImageWidth() {
        return getResources().getDisplayMetrics().widthPixels * 3 / 4;
}

    private int getImageHeight() {
        return getResources().getDisplayMetrics().widthPixels;
    }

    private class SaveItemsTask extends AsyncTask<VocabularyItem, Void, Void> {
        @Override
        protected Void doInBackground(VocabularyItem... items) {
            KasslrDatabase db = null;
            try {
                db = new KasslrDatabase(getApplicationContext());

                for (VocabularyItem item : items) {
                    db.save(item);
                }
                Log.d(DEBUG_TAG, "Successfully saved " + items.length + " item(s)");
            } catch (SQLiteException e) {
                Log.e(DEBUG_TAG, "Failed to save item(s)", e);
            } finally {
                if (db != null) {
                    db.close();
                }
            }

            return null;
        }
    }
}
