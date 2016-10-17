package gruppn.kasslr.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import gruppn.kasslr.model.User;
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
    private static final int DATABASE_VERSION = 4;
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
                + "image TEXT,"
                + "mine INTEGER DEFAULT 1"
                + ")");

        // Create vocabularies table
        db.execSQL("CREATE TABLE vocabularies ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "name TEXT,"
                + "owner TEXT,"
                + "universal_id INTEGER DEFAULT 0"
                + ")");

        // Create vocabulary_content table
        db.execSQL("CREATE TABLE vocabulary_content ("
                + "vocabulary_id INTEGER,"
                + "item_id INTEGER,"
                + "PRIMARY KEY(vocabulary_id, item_id)"
                + ")");

        // Create users table
        db.execSQL("CREATE TABLE users ("
                + "id TEXT PRIMARY KEY,"
                + "name TEXT,"
                + "picture TEXT"
                + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(DEBUG_TAG, "Upgrading database from " + oldVersion + " to " + newVersion);

        // Upgrade or create additional tables if necessary
        switch (oldVersion) {
            case 1:
                // Upgrade from version 1 to 2
                db.execSQL("ALTER TABLE vocabularies ADD COLUMN universal_id INTEGER DEFAULT 0");
            case 2:
                // Upgrade from version 2 to 3
                db.execSQL("ALTER TABLE items ADD COLUMN mine INTEGER DEFAULT 1");
            case 3:
                // Upgrade from version 3 to 4
                db.execSQL("DROP TABLE items");
                db.execSQL("DROP TABLE vocabularies");
                db.execSQL("DROP TABLE vocabulary_content");
                onCreate(db);
            default:
                break;
        }
    }

    public void reset() {
        mContext.deleteDatabase(DATABASE_NAME);
    }

    public void dump() {
        SQLiteDatabase db = null;
        try {
            db = getWritableDatabase();

            Log.d(DEBUG_TAG, "Dumping items");
            Cursor c = db.rawQuery("SELECT id, name, image, mine FROM items", null);
            while (c.moveToNext()) {
                Log.d(DEBUG_TAG, "id: " + c.getInt(0) + ", name: " + c.getString(1) + ", image: "
                        + c.getString(2) + ", mine: " + c.getInt(3));
            }
            c.close();

            Log.d(DEBUG_TAG, "Dumping vocabularies");
            c = db.rawQuery("SELECT id, name, owner, universal_id FROM vocabularies", null);
            while (c.moveToNext()) {
                Log.d(DEBUG_TAG, "id: " + c.getInt(0) + ", name: " + c.getString(1) + ", owner: "
                        + c.getString(2) + ", universal_id: " + c.getInt(3));
            }
            c.close();

            Log.d(DEBUG_TAG, "Dumping vocabulary_content");
            c = db.rawQuery("SELECT vocabulary_id, item_id FROM vocabulary_content", null);
            while (c.moveToNext()) {
                Log.d(DEBUG_TAG, "vocabulary_id: " + c.getInt(0) + ", item_id: " + c.getInt(1));
            }
            c.close();

            Log.d(DEBUG_TAG, "Dumping users");
            c = db.rawQuery("SELECT id, name, picture FROM users", null);
            while (c.moveToNext()) {
                Log.d(DEBUG_TAG, "id: " + c.getString(0) + ", name: " + c.getString(1) + ", picture: " + c.getString(2));
            }
            c.close();
        } finally {
            if (db != null) {
                db.close();
            }
        }
    }

    public List<Vocabulary> getVocabularies(List<VocabularyItem> allItems) throws SQLiteException {
        List<Vocabulary> vocabularies = new ArrayList<>();

        SQLiteDatabase db = null;
        Cursor cVoc = null;
        try {
            db = getReadableDatabase();

            cVoc = db.rawQuery("SELECT id, name, owner, universal_id FROM vocabularies ORDER BY id DESC", null);
            while (cVoc.moveToNext()) {
                Vocabulary vocabulary = new Vocabulary(getUser(cVoc.getString(2), db), cVoc.getString(1), cVoc.getInt(0), cVoc.getInt(3));

                List<VocabularyItem> items = new ArrayList<>();
                Cursor cItems = null;
                try {
                    cItems = db.rawQuery("SELECT item_id FROM vocabulary_content WHERE vocabulary_id = ?",
                            new String[] { vocabulary.getId() + "" });

                    while (cItems.moveToNext()) {
                        int id = cItems.getInt(0);

                        for (VocabularyItem item : allItems) {
                            if (item.getId() == id) {
                                items.add(item);
                                break;
                            }
                        }
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

    public User getUser(String id, SQLiteDatabase db) {
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT id, name, picture FROM users WHERE id = ?", new String[] { id });
            if (cursor.moveToNext()) {
                return new User(cursor.getString(1), cursor.getString(0), cursor.getString(2));
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return new User("Anonym");
    }

    public List<VocabularyItem> getItems() throws SQLiteException {
        List<VocabularyItem> items = new ArrayList<>();

        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = getReadableDatabase();

            cursor = db.rawQuery("SELECT id, name, image, mine FROM items", null);
            while (cursor.moveToNext()) {
                items.add(new VocabularyItem(cursor.getString(1), cursor.getString(2), cursor.getInt(0), cursor.getInt(3) == 1));
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
                db.execSQL("INSERT INTO items (name, image, mine) VALUES (?, ?, ?)",
                        new Object[] { item.getName(), item.getImageName(), item.isMine() ? 1 : 0 });

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
        if (vocabulary.getOwner() == null) {
            throw new IllegalArgumentException("A vocabulary must have an owner");
        }
        if (vocabulary.getTitle().isEmpty()) {
            throw new IllegalArgumentException("A vocabulary must have a title");
        }
        if (vocabulary.getItems().size() < 3) {
            throw new IllegalArgumentException("A vocabulary must have at least three items");
        }

        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = getWritableDatabase();

            // Save user
            save(vocabulary.getOwner(), db);

            if (vocabulary.getId() != 0) {
                // Vocabulary exists in database; update values
                db.execSQL("UPDATE vocabularies SET name = ?, owner = ?, universal_id = ? WHERE id = ?",
                        new Object[] { vocabulary.getTitle(), vocabulary.getOwner().getId(), vocabulary.getUniversalId(),
                                vocabulary.getId() });

                // Delete previous connections
                db.execSQL("DELETE FROM vocabulary_content WHERE vocabulary_id = ?",
                        new Object[] { vocabulary.getId() });
            } else {
                // Vocabulary does not exist; insert new
                db.execSQL("INSERT INTO vocabularies (name, owner, universal_id) VALUES (?, ?, ?)",
                        new Object[] { vocabulary.getTitle(), vocabulary.getOwner().getId(), vocabulary.getUniversalId() });

                cursor = db.rawQuery("SELECT last_insert_rowid()", null);
                cursor.moveToNext();
                vocabulary.setId(cursor.getInt(0));
            }

            for (VocabularyItem item : vocabulary.getItems()) {
                if (item.getId() == 0) {
                    // Save item
                    save(item, db);
                }

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

    public void save(User user, SQLiteDatabase db) {
        db.execSQL("REPLACE INTO users (id, name, picture) VALUES (?, ?, ?)",
                new Object[] { user.getId(), user.getName(), user.getProfilePic() });
    }

    public void remove(VocabularyItem item) throws SQLiteException {
        SQLiteDatabase db = null;

        try {
            db = getWritableDatabase();

            // Delete item
            db.execSQL("DELETE FROM items WHERE id = ?", new Object[] { item.getId() });

            // Delete connections
            db.execSQL("DELETE FROM vocabulary_content WHERE item_id = ?", new Object[] { item.getId() });

            // Remove id from item
            item.setId(0);
        } finally {
            if (db != null) {
                db.close();
            }
        }
    }

    public void remove(Vocabulary vocabulary) throws SQLiteException {
        SQLiteDatabase db = null;

        try {
            db = getWritableDatabase();

            // Delete vocabulary
            db.execSQL("DELETE FROM vocabularies WHERE id = ?", new Object[] { vocabulary.getId() });

            // Delete connections
            db.execSQL("DELETE FROM vocabulary_content WHERE vocabulary_id = ?", new Object[] { vocabulary.getId() });

            // Remove id from vocabulary
            vocabulary.setId(0);
        } finally {
            if (db != null) {
                db.close();
            }
        }
    }
}
