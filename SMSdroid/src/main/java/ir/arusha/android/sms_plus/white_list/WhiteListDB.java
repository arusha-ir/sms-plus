package ir.arusha.android.sms_plus.white_list;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import de.ub0r.android.logg0r.Log;

/**
 * Database holding white listed numbers.
 *
 * @author marek, flx
 */
public final class WhiteListDB {

    /**
     * Key in table.
     */
    public static final String KEY_NR = "nr";
    /**
     * Projection.
     */
    public static final String[] PROJECTION = new String[]{KEY_NR};
    /**
     * TAG for debug out.
     */
    private static final String TAG = "whitelist";
    /**
     * Name of {@link SQLiteDatabase}.
     */
    private static final String DATABASE_NAME = "whitelist";
    /**
     * Version of {@link SQLiteDatabase}.
     */
    private static final int DATABASE_VERSION = 1;
    /**
     * Table in {@link SQLiteDatabase}.
     */
    private static final String DATABASE_TABLE = "numbers";
    /**
     * SQL to create {@link SQLiteDatabase}.
     */
    private static final String DATABASE_CREATE
            = "CREATE TABLE IF NOT EXISTS " + DATABASE_TABLE + " (" + KEY_NR + " varchar(50) )";

    /**
     * {@link DatabaseHelper}.
     */
    private final DatabaseHelper dbHelper;

    /**
     * {@link SQLiteDatabase}.
     */
    private SQLiteDatabase db;

    /**
     * Default constructor.
     *
     * @param context {@link Context}
     */
    public WhiteListDB(final Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    /**
     * Open database.
     *
     * @return {@link WhiteListDB}
     */
    public WhiteListDB open() {
        db = dbHelper.getWritableDatabase();
        return this;
    }

    /**
     * Close database.
     */
    public void close() {
        dbHelper.close();
    }

    /**
     * Insert a number into the spam database.
     *
     * @param nr number
     * @return id in database
     */
    public long insertNr(final String nr) {
        if (nr == null) {
            return -1L;
        }
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_NR, nr);
        return db.insert(DATABASE_TABLE, null, initialValues);
    }

    /**
     * Check if number is white listed.
     *
     * @param nr number
     * @return true if number is white listed
     */
    public boolean isInDB(final String nr) {
        Log.d(TAG, "isInDB(", nr, ")");
        if (nr == null) {
            return false;
        }
        final Cursor cursor = db.query(DATABASE_TABLE, PROJECTION, KEY_NR + " = ?",
                new String[]{nr}, null, null, null);
        final boolean ret = cursor.moveToFirst();
        if (!cursor.isClosed()) {
            cursor.close();
        }
        return ret;
    }

    /**
     * Get all white listed numbers.
     *
     * @return white list
     */
    public int getEntrieCount() {
        final Cursor cursor = db.rawQuery("SELECT COUNT(" + KEY_NR + ") FROM " + DATABASE_TABLE, null);
        Log.d(TAG, cursor.toString());
        int ret = 0;
        if (cursor.moveToFirst()) {
            ret = cursor.getInt(0);
        }
        if (!cursor.isClosed()) {
            cursor.close();
        }
        return ret;
    }

    /**
     * Get all entries from white list.
     *
     * @return array of entries
     */
    public String[] getAllEntries() {
        final Cursor cursor = db.query(DATABASE_TABLE, PROJECTION, null, null, null, null,
                null);
        if (cursor == null) {
            return null;
        }
        final String[] ret = new String[cursor.getCount()];
        if (cursor.moveToFirst()) {
            int i = 0;
            do {
                ret[i] = cursor.getString(0);
                Log.d(TAG, "white list: ", ret[i]);
                ++i;
            } while (cursor.moveToNext());
        }
        if (!cursor.isClosed()) {
            cursor.close();
        }
        return ret;
    }

    /**
     * Remove number from white list.
     *
     * @param nr number
     */
    public void removeNr(final String nr) {
        if (nr == null) {
            return;
        }
        db.delete(DATABASE_TABLE, KEY_NR + " = ?", new String[]{nr});
    }

    /**
     * {@link DatabaseHelper} for opening the database.
     *
     * @author marek
     */
    private static class DatabaseHelper extends SQLiteOpenHelper {

        /**
         * Default constructor.
         *
         * @param context {@link Context}
         */
        DatabaseHelper(final Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(final SQLiteDatabase db) {
            db.execSQL(DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(final SQLiteDatabase db, final int oldVersion, final int newVersion) {
            Log.w(TAG, "Upgrading database from version ", oldVersion, " to ", newVersion
                    + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
            onCreate(db);
        }
    }
}
