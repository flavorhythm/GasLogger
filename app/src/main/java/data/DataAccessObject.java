package data;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import util.Constant;
import model.FuelLog;

import static data.FillupTable.*;

/**
 * Created by ZYuki on 1/11/2016.
 */
public class DataAccessObject {
    private static final int DEL_ALL = -1;

    private SQLiteDatabase db;
    private DatabaseHelper dbHelper;
    private Context context;

    public DataAccessObject(Context context) {
        dbHelper = DatabaseHelper.getInstance(context);

        this.context = context;
    }

    public void open() throws SQLException {
        db = dbHelper.getWritableDatabase();
    }

    public void close() {
        db.close();
    }

    public long addEntry(FuelLog entry) {
        int partialFillInt = entry.getPartialFill() ? 1 : 0;

        ContentValues values = new ContentValues();
        values.put(ODOM_VAL, entry.getCurrentOdomVal());
        values.put(FUEL_AMOUNT, entry.getFuelTopupAmount());
        values.put(PARTIAL_FILL, partialFillInt);
        values.put(RECORD_DATE, entry.getRecordDate());

        long entryID = db.insert(TABLE_NAME, null, values);

        updatePreferences();

        return entryID;
    }

    public FuelLog getEntry(String selection, String[] selectionArgs) {
        List<FuelLog> fuelLogArrayList = getAllEntries(selection, selectionArgs);

        if(fuelLogArrayList.size() == 1) {
            return fuelLogArrayList.get(0);
        } else {
            return null;
        }
    }

    public List<FuelLog> getAllEntries(String nullableSelection, String[] nullableSelectionArgs) {
        List<FuelLog> fuelLogArrayList = new ArrayList<>();
        Cursor cursor = db.query(
                TABLE_NAME,
                ALL_COLUMNS,
                nullableSelection,
                nullableSelectionArgs,
                null,
                null,
                RECORD_DATE + " DESC"
        );

        if(cursor.moveToFirst()) {
            do {
                FuelLog fuelLog = new FuelLog(); //Creates new fuelLog object

                fuelLog.setItemID(cursor.getInt(cursor.getColumnIndex(KEY_ID))); //Sets fuelLog object's itemID variable to data within database
                fuelLog.setCurrentOdomVal(cursor.getInt(cursor.getColumnIndex(ODOM_VAL))); //Sets fuelLog object's currentOdomVal variable to data within database
                fuelLog.setFuelTopupAmount(cursor.getDouble(cursor.getColumnIndex(FUEL_AMOUNT))); //Sets fuelLog object's fuelTopupAmount variable to data within database
                fuelLog.setPartialFill(cursor.getInt(cursor.getColumnIndex(PARTIAL_FILL)) == 1);
                fuelLog.setRecordDate(cursor.getLong(cursor.getColumnIndex(RECORD_DATE)));

                fuelLogArrayList.add(fuelLog); //Adds the entire fuelLog object into the fuelLogList
            } while(cursor.moveToNext());
        }

        cursor.close();
        return fuelLogArrayList;
    }

    public int deleteEntry(int id) {
        final String SELECT_ALL = "1";
        String selection = (id == DEL_ALL) ? SELECT_ALL : KEY_ID + " = " + id;

        int delCount = db.delete(
                TABLE_NAME,
                selection,
                null
        );

        updatePreferences();

        return delCount;
    }

    public int deleteAllEntries() {
        return deleteEntry(DEL_ALL);
    }

    private void updatePreferences() {
        String selection = ODOM_VAL + " = (SELECT MAX(" + ODOM_VAL + ") FROM " + TABLE_NAME + ")";

        FuelLog entry = this.getEntry(selection, null);

        SharedPreferences.Editor editor = context.getSharedPreferences(Constant.PREF_NAME, Context.MODE_PRIVATE).edit();
        if(entry != null) {
            editor.putInt(Constant.MIN_MILEAGE_KEY, entry.getCurrentOdomVal());
        } else {
            editor.putInt(Constant.MIN_MILEAGE_KEY, 0);
        }

        editor.apply();
    }
}
