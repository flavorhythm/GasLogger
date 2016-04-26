package data;

import android.database.sqlite.SQLiteDatabase;

/**
 * Created by ZYuki on 1/11/2016.
 */
public class FillupTable {
    private FillupTable() {}

    public static final String TABLE_NAME = "fueling_tbl";

    public static final String KEY_ID = "_id";
    public static final String ODOM_VAL = "odometer";
    public static final String FUEL_AMOUNT = "fuel_amount";
    public static final String PARTIAL_FILL = "partial_fill";
    public static final String RECORD_DATE = "record_date";

    public static final String[] ALL_COLUMNS = new String[] {
            KEY_ID,
            ODOM_VAL,
            FUEL_AMOUNT,
            PARTIAL_FILL,
            RECORD_DATE
    };

    private static final String CREATE_FUEL_LOG_TABLE = "CREATE TABLE " + TABLE_NAME + "(" +
        KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
        ODOM_VAL + " INTEGER NOT NULL, " +
        FUEL_AMOUNT + " REAL NOT NULL, " +
        PARTIAL_FILL + " INTEGER NOT NULL, " +
        RECORD_DATE + " INTEGER NOT NULL);";

    public static void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_FUEL_LOG_TABLE);
    }

    public static void onUpgrade(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}
