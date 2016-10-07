package gruppn.kasslr.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

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
    private static final String DEBUG_TAG = "KasslrDatabase";
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "kasslr";

    private Context mContext;

    public KasslrDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create items table
        db.execSQL("CREATE TABLE items ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "name TEXT,"
                + "image TEXT"
                + ")");

        // Create vocabularies table
        db.execSQL("CREATE TABLE vocabularies ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "name TEXT,"
                + "owner TEXT"
                + ")");

        // Create vocabulary_content table
        db.execSQL("CREATE TABLE vocabulary_content ("
                + "vocabulary_id INTEGER,"
                + "item_id INTEGER,"
                + "PRIMARY KEY(vocabulary_id, item_id)"
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

    public void reset() {
        mContext.deleteDatabase(DATABASE_NAME);
    }

    public void dump() {
        SQLiteDatabase db = null;
        try {
            db = getWritableDatabase();

            Log.d(DEBUG_TAG, "Dumping items");
            Cursor c = db.rawQuery("SELECT id, name, image FROM items", null);
            while (c.moveToNext()) {
                Log.d(DEBUG_TAG, "id: " + c.getInt(0) + ", name: " + c.getString(1) + ", image: " + c.getString(2));
            }
            c.close();

            Log.d(DEBUG_TAG, "Dumping vocabularies");
            c = db.rawQuery("SELECT id, name, owner FROM vocabularies", null);
            while (c.moveToNext()) {
                Log.d(DEBUG_TAG, "id: " + c.getInt(0) + ", name: " + c.getString(1) + ", owner: " + c.getString(2));
            }
            c.close();

            Log.d(DEBUG_TAG, "Dumping vocabulary_content");
            c = db.rawQuery("SELECT vocabulary_id, item_id FROM vocabulary_content", null);
            while (c.moveToNext()) {
                Log.d(DEBUG_TAG, "vocabulary_id: " + c.getInt(0) + ", item_id: " + c.getInt(1));
            }
            c.close();
        } finally {
            if (db != null) {
                db.close();
            }
        }
    }

    public List<Vocabulary> getVocabularies() throws SQLiteException {
        List<Vocabulary> vocabularies = new ArrayList<>();

        SQLiteDatabase db = null;
        Cursor cVoc = null;
        try {
            db = getReadableDatabase();

            cVoc = db.rawQuery("SELECT id, name, owner FROM vocabularies", null);
            while (cVoc.moveToNext()) {
                Vocabulary vocabulary = new Vocabulary(cVoc.getString(2), cVoc.getString(1), cVoc.getInt(0));

                List<VocabularyItem> items = new ArrayList<>();
                Cursor cItems = null;
                try {
                    cItems = db.rawQuery("SELECT id, name, image FROM items WHERE id IN "
                            + "(SELECT item_id FROM vocabulary_content WHERE vocabulary_id = ?)",
                            new String[] { vocabulary.getId() + "" });

                    while (cItems.moveToNext()) {
                        items.add(new VocabularyItem(cItems.getString(1), cItems.getString(2), cItems.getInt(0)));
                    }
                    vocabulary.setItems(items);
                } finally {
                    if (cItems != null) {
                        cItems.close();
                    }
                }

                vocabularies.add(vocabulary);
            }
        } finally {
            if (db != null) {
                db.close();
            }
            if (cVoc != null) {
                cVoc.close();
            }
        }

        return vocabularies;
    }

    public List<VocabularyItem> getItems() throws SQLiteException {
        List<VocabularyItem> items = new ArrayList<>();

        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = getReadableDatabase();

            cursor = db.rawQuery("SELECT id, name, image FROM items", null);
            while (cursor.moveToNext()) {
                items.add(new VocabularyItem(cursor.getString(1), cursor.getString(2), cursor.getInt(0)));
            }
        } finally {
            if (db != null) {
                db.close();
            }
            if (cursor != null) {
                cursor.close();
            }
        }

        return items;
    }

    public void save(VocabularyItem item, SQLiteDatabase db) throws SQLiteException {
        if (item.getName().isEmpty()) {
            throw new IllegalArgumentException("An item must have a name");
        }
        if (item.getImageName().isEmpty()) {
            throw new IllegalArgumentException("An item must have an image");
        }

        Cursor cursor = null;
        try {
            if (item.getId() != 0) {
                // Item exists in database; update name
                db.execSQL("UPDATE items SET name = ? WHERE id = ?",
                        new Object[] { item.getName(), item.getId() });
            } else {
                // Item does not exist; insert new
                db.execSQL("INSERT INTO items (name, image) VALUES (?, ?)",
                        new Object[] { item.getName(), item.getImageName() });

                cursor = db.rawQuery("SELECT last_insert_rowid()", null);
                cursor.moveToNext();
                item.setId(cursor.getInt(0));
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public void save(VocabularyItem item) throws SQLiteException {
        SQLiteDatabase db = null;

        try {
            db = getWritableDatabase();
            save(item, db);
        } finally {
            if (db != null) {
                db.close();
            }
        }
    }

    public void save(Vocabulary vocabulary) throws SQLiteException {
        if (vocabulary.getOwner().isEmpty()) {
            throw new IllegalArgumentException("A vocabulary must have an owner");
        }
        if (vocabulary.getTitle().isEmpty()) {
            throw new IllegalArgumentException("A vocabulary must have a title");
        }
        if (vocabulary.getItems().isEmpty()) {
            throw new IllegalArgumentException("A vocabulary must have at least one item");
        }

        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = getWritableDatabase();

            if (vocabulary.getId() != 0) {
                // Vocabulary exists in database; update name/owner
                db.execSQL("UPDATE vocabularies SET name = ?, owner = ? WHERE id = ?",
                        new Object[] { vocabulary.getTitle(), vocabulary.getOwner(), vocabulary.getId() });

                // Delete previous connections
                db.execSQL("DELETE FROM vocabulary_content WHERE vocabulary_id = ?",
                        new Object[] { vocabulary.getId() });
            } else {
                // Vocabulary does not exist; insert new
                db.execSQL("INSERT INTO vocabularies (name, owner) VALUES (?, ?)",
                        new Object[] { vocabulary.getTitle(), vocabulary.getOwner() });

                cursor = db.rawQuery("SELECT last_insert_rowid()", null);
                cursor.moveToNext();
                vocabulary.setId(cursor.getInt(0));
            }

            for (VocabularyItem item : vocabulary.getItems()) {
                // Save connection between item and vocabulary
                db.execSQL("INSERT INTO vocabulary_content (vocabulary_id, item_id) VALUES (?, ?)",
                        new Object[] { vocabulary.getId(), item.getId() });
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
