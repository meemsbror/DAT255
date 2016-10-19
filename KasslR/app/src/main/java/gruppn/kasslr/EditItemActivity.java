package gruppn.kasslr;

import android.content.Context;
import android.content.Intent;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import gruppn.kasslr.db.KasslrDatabase;
import gruppn.kasslr.model.Vocabulary;
import gruppn.kasslr.model.VocabularyItem;
import gruppn.kasslr.task.RemoveItemTask;

public class EditItemActivity extends AppCompatActivity {
    public static final String EXTRA_IMAGE_URI = "image";
    public static final String EXTRA_ITEM_INDEX = "item_index";
    public static final String EXTRA_EXIT_TRANSITION = "exit_transition";
    public static final String RESULT_ITEM_INDEX = "item_index";
    public static final String RESULT_ACTION = "action";
    public static final int ACTION_EDIT = 1;
    public static final int ACTION_REMOVE = 2;

    private static final String DEBUG_TAG = "EditItemActivity";

    private Kasslr app;
    private VocabularyItem item;
    private boolean exitTransition;

    private EditText txtWord;

    private InputMethodManager imm;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);

        app = (Kasslr) getApplication();
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        txtWord = (EditText) findViewById(R.id.txtWord);
        ImageView imgWord = (ImageView) findViewById(R.id.imgWord);

        Uri image = (Uri) getIntent().getExtras().get(EXTRA_IMAGE_URI);
        int itemIndex = getIntent().getExtras().getInt(EXTRA_ITEM_INDEX, -1);
        exitTransition = getIntent().getExtras().getBoolean(EXTRA_EXIT_TRANSITION, true);

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
                    saveItem();

                    // Hide keyboard
                    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);

                    close();
                    return true;
                }
                return false;
            }
        });

        // Set item name
        txtWord.append(item.getName());

        // Set item image
        Bitmap bitmap = app.getSharedBitmap();
        if (bitmap == null) {
            bitmap = BitmapFactory.decodeFile(app.getImageFile(item).getAbsolutePath());
        }
        imgWord.setImageBitmap(Bitmap.createScaledBitmap(bitmap, getImageWidth(), getImageHeight(), false));
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

    public void removeItem(View view) {
        if (item.getId() != 0 && existsInVocabulary(item)) {
            // Item already exists in a vocabulary; can't remove
            Toast.makeText(this, getString(R.string.error_remove_item_in_vocabulary), Toast.LENGTH_LONG).show();
            return;
        }

        // Hide keyboard
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);

        item.setRemoved();

        new RemoveItemTask(app).execute(item);

        int index = app.getShelf().getItems().indexOf(item);

        Intent result = new Intent();
        result.putExtra(RESULT_ITEM_INDEX, index);
        result.putExtra(RESULT_ACTION, ACTION_REMOVE);
        setResult(RESULT_OK, result);
        close();
    }

    public void saveItem() {
        String name = txtWord.getText().toString().trim();
        if (name.isEmpty() && item.getId() != 0 && existsInVocabulary(item)) {
            // Item already exists in a vocabulary; can't remove name
            Toast.makeText(this, getString(R.string.error_empty_item_name), Toast.LENGTH_LONG).show();
            return;
        }

        item.setName(name);
        new SaveItemTask().execute(item);

        Intent result = new Intent();
        result.putExtra(RESULT_ITEM_INDEX, app.getShelf().getItems().indexOf(item));
        result.putExtra(RESULT_ACTION, ACTION_EDIT);
        setResult(RESULT_OK, result);
    }

    private boolean existsInVocabulary(VocabularyItem theItem) {
        for (Vocabulary vocabulary : app.getShelf().getVocabularies()) {
            if (vocabulary.getItems().contains(theItem)) {
                return true;
            }
        }
        return false;
    }

    private int getImageWidth() {
        return getResources().getDisplayMetrics().widthPixels * 3 / 4;
    }

    private int getImageHeight() {
        return getResources().getDisplayMetrics().widthPixels;
    }

    private class SaveItemTask extends AsyncTask<VocabularyItem, Void, Void> {
        @Override
        protected Void doInBackground(VocabularyItem... items) {
            KasslrDatabase db = null;
            try {
                db = new KasslrDatabase(getApplicationContext());

                for (VocabularyItem item : items) {
                    if (item.getName().isEmpty()) {
                        db.remove(item);
                    } else {
                        db.save(item);
                    }
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
