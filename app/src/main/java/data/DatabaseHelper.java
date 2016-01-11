package data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by ZYuki on 1/11/2016.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static DatabaseHelper thisInstance;

    private static final String DATABASE_NAME = "fuel_db";
    private static final int DATABASE_VERSION = 2;

    public static synchronized DatabaseHelper getInstance(Context context) {
        if(thisInstance == null) {
            thisInstance = new DatabaseHelper(context.getApplicationContext());
        }

        return thisInstance;
    }

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        FillupTable.onCreate(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        FillupTable.onUpgrade(db);
    }
}
