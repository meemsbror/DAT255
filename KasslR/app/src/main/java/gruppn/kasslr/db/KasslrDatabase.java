package gruppn.kasslr.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

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
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // Create tables
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
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
}
