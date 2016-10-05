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

    public List<Vocabulary> getVocabularies() throws SQLiteException {
        List<Vocabulary> vocabularies = new ArrayList<>();

        SQLiteDatabase db = null;
        Cursor cVocabularies = null;
        try {
            db = getReadableDatabase();

            cVocabularies = db.rawQuery("SELECT * FROM vocabularies", null);
            while (cVocabularies.moveToNext()) {
                Vocabulary vocabulary = new Vocabulary(cVocabularies.getString(1), cVocabularies.getString(2), cVocabularies.getInt(0));

                List<VocabularyItem> items = new ArrayList<>();
                Cursor cItems = null;
                try {
                    cItems = db.rawQuery("SELECT id, name, image FROM items WHERE id IN "
                            + "(SELECT item_id FROM vocabulary_content WHERE vocabulary_id = ?)",
                            new String[] { vocabulary.getId() + "" });

                    while (cItems.moveToNext()) {
                        items.add(new VocabularyItem(cItems.getString(1), cItems.getString(2), cItems.getInt(0)));
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

    public void save(VocabularyItem item) throws SQLiteException {
        if (item.getName().isEmpty()) {
            throw new IllegalArgumentException("An item must have a name");
        }
        if (item.getImageUrl().isEmpty()) {
            throw new IllegalArgumentException("An item must have an image");
        }

        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            if (item.getId() != 0) {
                // Item exists in database; update name
                db.execSQL("UPDATE items SET name = ? WHERE id = ?",
                        new Object[] { item.getName(), item.getId() });
            } else {
                // Item does not exist; insert new
                db.execSQL("INSERT INTO items (name, image) VALUES (?, ?)",
                        new Object[] { item.getName(), item.getImageUrl() });

                cursor.close();
                cursor = db.rawQuery("SELECT last_insert_rowid()", null);
                cursor.moveToNext();
                item.setId(cursor.getInt(0));
            }
        } finally {
            if (db != null) {
                db.close();
            }
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
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
                // Save item
                save(item);

                // Save connection between item and vocabulary
                db.execSQL("INSERT IGNORE INTO vocabulary_content (vocabulary_id, item_id) VALUES (?, ?)",
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
