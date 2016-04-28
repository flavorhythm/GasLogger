package data;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.MilesPerGal;
import util.Constant;
import model.FuelLog;

import static data.FillupTable.*;

/**
 * Created by ZYuki on 1/11/2016.
 */
public class DataAccessObject {
    private static final int DEL_ALL = -1;

    private List<FuelLog> logList;
    private List<MilesPerGal> mpgList;

    private SQLiteDatabase db;
    private DatabaseHelper dbHelper;
    private Context context;

    public DataAccessObject(Context context) {
        dbHelper = DatabaseHelper.getInstance(context);
        logList = new ArrayList<>();
        mpgList = new ArrayList<>();
        queryAll(null, null);

        this.context = context;
    }

    public void open() throws SQLException {db = dbHelper.getWritableDatabase();}

    public void close() {db.close();}

    public List<FuelLog> getLogList() {return logList;}

    public List<MilesPerGal> getMpgList() {return mpgList;}

    public float getMpgAvg() {
        int cumulative = 0;

        for(MilesPerGal mpg : mpgList) {
            cumulative += mpg.getMpg();
        }

        return cumulative / mpgList.size();
    }

    public void queryAll(String nullableSelection, String[] nullableSelectionArgs) {
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
                FuelLog entry = new FuelLog(); //Creates new fuelLog object

                entry.setItemID(cursor.getInt(cursor.getColumnIndex(KEY_ID))); //Sets fuelLog object's itemID variable to data within database
                entry.setCurrentOdomVal(cursor.getInt(cursor.getColumnIndex(ODOM_VAL))); //Sets fuelLog object's currentOdomVal variable to data within database
                entry.setFuelTopupAmount(cursor.getDouble(cursor.getColumnIndex(FUEL_AMOUNT))); //Sets fuelLog object's fuelTopupAmount variable to data within database
                entry.setPartialFill(cursor.getInt(cursor.getColumnIndex(PARTIAL_FILL)) == 1);
                entry.setRecordDate(cursor.getLong(cursor.getColumnIndex(RECORD_DATE)));

                logList.add(entry); //Adds the entire fuelLog object into the fuelLogList
            } while(cursor.moveToNext());
        }

        cursor.close();

        updateMpgs();
    }

    public MilesPerGal calculateMpg(FuelLog nextEntry, FuelLog currEntry) {
        if(!currEntry.getPartialFill() && !nextEntry.getPartialFill()) {
            double gasUsed = nextEntry.getFuelTopupAmount();
            int distTravel = nextEntry.getCurrentOdomVal() - currEntry.getCurrentOdomVal();

            return new MilesPerGal(
                    currEntry.getRecordDate(),
                    Double.valueOf(distTravel / gasUsed).floatValue()
            );
        }

        return null;
    }

    public long addLog(FuelLog entry) {
        final int first = 0;
        final int next = 1;
        logList.add(first, entry);
        mpgList.add(calculateMpg(logList.get(next), logList.get(first)));

        int partialFillInt = entry.getPartialFill() ? 1 : 0;

        ContentValues values = new ContentValues();
        values.put(ODOM_VAL, entry.getCurrentOdomVal());
        values.put(FUEL_AMOUNT, entry.getFuelTopupAmount());
        values.put(PARTIAL_FILL, partialFillInt);
        values.put(RECORD_DATE, entry.getRecordDate());

        long entryID = db.insert(TABLE_NAME, null, values);

        logList.get(first).setItemID(Long.valueOf(entryID).intValue());

        updatePreferences();

        return entryID;
    }

    public boolean updateLog(FuelLog entry) {
        int partialFillInt = entry.getPartialFill() ? 1 : 0;

        ContentValues values = new ContentValues();
        values.put(KEY_ID, entry.getItemID());
        values.put(ODOM_VAL, entry.getCurrentOdomVal());
        values.put(FUEL_AMOUNT, entry.getFuelTopupAmount());
        values.put(PARTIAL_FILL, partialFillInt);
        values.put(RECORD_DATE, entry.getRecordDate());

        int updateCount = db.update(
                TABLE_NAME,
                values,
                KEY_ID + " =?",
                new String[] {String.valueOf(entry.getItemID())}
        );

        updateLists(entry);
        updatePreferences();

        final int oneUpdate = 1;
        return updateCount == oneUpdate;
    }

    public void clearLogs() {
        logList.clear();
        mpgList.clear();
        deleteAllEntries();

        updatePreferences();
    }

    public FuelLog findEntryById(int entryId) {
        for(FuelLog aLog : logList) {
            if(aLog.getItemID() == entryId) {
                return aLog;
            }
        }

        return null;
    }

    private void updateLists(FuelLog entry) {
        for(int i = 0; i < logList.size(); i++) {
            FuelLog thisEntry = logList.get(i);

            if(thisEntry.getItemID() == entry.getItemID()) {
                thisEntry.setCurrentOdomVal(entry.getCurrentOdomVal());
                thisEntry.setFuelTopupAmount(entry.getFuelTopupAmount());
                thisEntry.setPartialFill(entry.getPartialFill());
            }
        }

        updateMpgs();
        updatePreferences();
    }

    private void updateMpgs() {
        mpgList.clear();

        if(logList.size() > 1) {
            int initIndex = logList.size() - 1;

            for(int i = initIndex; i > 0; i--) {
                FuelLog nextEntry = logList.get(i - 1);
                FuelLog currEntry = logList.get(i);

                mpgList.add(calculateMpg(nextEntry, currEntry));
            }
        }
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
//        String selection = ODOM_VAL + " = (SELECT MAX(" + ODOM_VAL + ") FROM " + TABLE_NAME + ")";
//
//        FuelLog entry = this.getAllEntries(selection, null).get(0);

        int maxOdom = 0;
        for(FuelLog entry : logList) {
            int prevOdom = entry.getCurrentOdomVal();

            if(maxOdom < prevOdom) {
                maxOdom = prevOdom;
            }
        }

        SharedPreferences.Editor editor = context.getSharedPreferences(Constant.PREF_NAME, Context.MODE_PRIVATE).edit();
        editor.putInt(Constant.MIN_MILEAGE_KEY, maxOdom);
        editor.apply();
    }

    /**********************************************************************************************/

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

    public int updateEntry(FuelLog entry) {
        int partialFillInt = entry.getPartialFill() ? 1 : 0;

        ContentValues values = new ContentValues();
        values.put(KEY_ID, entry.getItemID());
        values.put(ODOM_VAL, entry.getCurrentOdomVal());
        values.put(FUEL_AMOUNT, entry.getFuelTopupAmount());
        values.put(PARTIAL_FILL, partialFillInt);
        values.put(RECORD_DATE, entry.getRecordDate());

        int updateCount = db.update(
                TABLE_NAME,
                values,
                KEY_ID + " =?",
                new String[] {String.valueOf(entry.getItemID())}
        );

        updatePreferences();

        return updateCount;
    }

//    public FuelLog getEntry(String selection, String[] selectionArgs) {
//        List<FuelLog> fuelLogArrayList = getAllEntries(selection, selectionArgs);
//
//        if(fuelLogArrayList.size() == 1) {
//            return fuelLogArrayList.get(0);
//        } else {
//            return null;
//        }
//    }

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
}
