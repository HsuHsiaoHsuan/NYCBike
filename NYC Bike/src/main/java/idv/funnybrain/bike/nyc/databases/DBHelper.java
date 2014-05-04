package idv.funnybrain.bike.nyc.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by KuoPiHua on 2014/5/4.
 */
public class DBHelper extends SQLiteOpenHelper {
    // ---- static variable START ----
    private static final String TAG = "DBHelper";
    private static final boolean D = false;

    private static final String DB_NAME = "station.db";
    private static final int DB_VERSION = 1;
    public static final String DB_TABLE = "bike";
    public static final String DB_COL_ID = "_id";
    public static final String DB_COL_STATION_ID = "station_id";

    private static final String DB_CREATE = "CREATE TABLE " + DB_TABLE + " (" +
            DB_COL_ID + " INTEGER PRIMARY KEY," +
            DB_COL_STATION_ID + " TEXT NOT NULL UNIQUE)";
    // ---- static variable END ----

    // ---- local variable START ----
    private SQLiteDatabase db;
    private Context mContext = null;
    private SQLiteDatabase sqLiteDatabase = null;
    // ---- local variable END ----

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        if(D) { Log.d(TAG, "constructor"); }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        if(D) Log.d(TAG, "onCreate");
        this.db = db;
        db.execSQL(DB_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE);
        onCreate(db);
    }

    public long insertFavor(ContentValues values) {
        db = getWritableDatabase();
        long result = 0L;
        try {
            result = db.insert(DB_TABLE, null, values);
        } catch(SQLiteConstraintException sqlce) {
            return result;
        }finally {
        }
        return result;
    }

    public Cursor queryAll() {
        db = getReadableDatabase();
        Cursor cursor = db.query(DB_TABLE, new String[] {DB_COL_STATION_ID}, null, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            return cursor;
        }
        return null;
    }

    public boolean queryIsFavor(String id) {
        if(D) Log.d(TAG, "query id: " + id);
        db = getReadableDatabase();
        Cursor cursor = db.query(DB_TABLE, new String[] {DB_COL_STATION_ID}, DB_COL_STATION_ID + "=?", new String[]{id}, null, null, null);
        int index = cursor.getColumnIndexOrThrow(DB_COL_STATION_ID);
        if(cursor != null) {
            if(D) Log.d(TAG, "query(id) : cursor not null, count: " + cursor.getCount());
            if(cursor.moveToFirst()) {
                if(D) Log.d(TAG, "return true");
                return true;
            } else {
                if(D) Log.d(TAG, "return false");
                db.close();
                return false;
            }
        } else {
            if(D) Log.d(TAG, "query(id) : cursor null");
            return false;
        }
    }

    public int removeFavor(String id) {
        if(db == null) {
            db = getWritableDatabase();
        }
        int result = db.delete(DB_TABLE, DB_COL_STATION_ID + "=?", new String[]{id});
        //db.close();
        return result;
    }

    public void close() {
        if(db != null) {
            db.close();
        }
    }
}
