package gruppn.kasslr.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import gruppn.kasslr.model.Vocabulary;
import gruppn.kasslr.model.VocabularyItem;

/*
    How to read/write data to/from database;
    https://developer.android.com/guide/topics/data/data-storage.html#db

    How to implement onCreate and onUpgrade;
    http://stackoverflow.com/questions/21881992/when-is-sqliteopenhelper-oncreate-onupgrade-run
*/
public class KasslrDatabase extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "kasslr";

    public KasslrDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create vocabulary_items table
        db.execSQL("CREATE TABLE vocabulary_items ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "name TEXT,"
                + "image TEXT,"
                + "vocabulary_id INTEGER"
                + ")");

        // Create vocabularies table
        db.execSQL("CREATE TABLE vocabularies ("
                + "vocabulary_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "name TEXT,"
                + "owner TEXT"
                + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Upgrade or create additional tables if necessary
        /*
        switch (oldVersion) {
            case 1:
                // Upgrade from version 1 to 2 (no break statement until last case)
            case 2:
                // Upgrade from version 2 to 3

                break;
            default:
                break;
        }
        */
    }

    public List<Vocabulary> getVocabularies() throws SQLiteException {
        List<Vocabulary> vocabularies = new ArrayList<>();

        SQLiteDatabase db = null;
        Cursor cVocabularies = null;
        try {
            db = getReadableDatabase();

            cVocabularies = db.rawQuery("SELECT * FROM vocabularies", null);
            while (cVocabularies.moveToNext()) {
                Vocabulary vocabulary = new Vocabulary(cVocabularies.getString(1), cVocabularies.getString(2));

                List<VocabularyItem> items = new ArrayList<>();
                Cursor cItems = null;
                try {
                    cItems = db.rawQuery("SELECT name, image FROM vocabulary_items WHERE vocabulary_id = " + cVocabularies.getInt(0), null);
                    while (cItems.moveToNext()) {
                        items.add(new VocabularyItem(cItems.getString(0), cItems.getString(1)));
                    }
                } finally {
                    if (cItems != null) {
                        cItems.close();
                    }
                }
            }
        } finally {
            if (db != null) {
                db.close();
            }
            if (cVocabularies != null) {
                cVocabularies.close();
            }
        }

        return vocabularies;
    }

    public void saveVocabulary(Vocabulary vocabulary) throws SQLiteException {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = getWritableDatabase();
            db.execSQL("INSERT INTO vocabularies (name, owner) VALUES (?, ?)",
                    new Object[] {vocabulary.getTitle(), vocabulary.getOwner()});

            cursor = db.rawQuery("SELECT last_insert_rowid()", null);
            cursor.moveToNext();
            long id = cursor.getLong(0);

            for (VocabularyItem item : vocabulary.getItems()) {
                db.execSQL("INSERT INTO vocabulary_items (name, image, vocabulary_id) VALUES (?, ?, ?)",
                        new Object[] {item.getName(), item.getImageUrl(), id});
            }
        } finally {
            if (db != null) {
                db.close();
            }
            if (cursor != null) {
                cursor.close();
            }
        }
    }
}
