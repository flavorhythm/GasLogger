package data;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.github.mikephil.charting.data.Entry;

import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import model.MilesPerGal;
import util.Constant;
import model.FuelLog;

import static data.FillupTable.*;

/**
 * Created by ZYuki on 1/11/2016.
 */
public class DataAccessObject {
    private List<FuelLog> logList;
    private List<Entry> entryList;
    private List<String> labelList;

    private SQLiteDatabase db;
    private DatabaseHelper dbHelper;
    private Context context;

    public DataAccessObject(Context context) {
        dbHelper = DatabaseHelper.getInstance(context);
        logList = new ArrayList<>();

        entryList = new ArrayList<>();
        labelList = new ArrayList<>();

        this.context = context;
    }

    public void open() throws SQLException {
        db = dbHelper.getWritableDatabase();

        queryAll(null, null);
    }

    public void close() {db.close();}

    public List<FuelLog> getLogList() {return logList;}

    public List<Entry> getEntryList() {return entryList;}

    public List<String> getLabelList() {return labelList;}

    public int getLogSize() {return logList.size();}

    public void queryAll(String nullableSelection, String[] nullableSelectionArgs) {
        Cursor cursor = db.query(
                TABLE_NAME,
                ALL_COLUMNS,
                nullableSelection,
                nullableSelectionArgs,
                null,
                null,
                RECORD_DATE + Constant.SORT_ORDER
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

    public long addLog(FuelLog entry) {
        final int first = 0;
        logList.add(first, entry);

        if(logList.size() > 1) {
            updateMpgs();
        }

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

    public int deleteAllEntries() {
        int delCount = db.delete(
                TABLE_NAME,
                Constant.DEL_ALL,
                null
        );

        updatePreferences();

        return delCount;
    }

    public int clearLogs() {
        logList.clear();

        entryList.clear();
        labelList.clear();

        int delCount = deleteAllEntries();

        updatePreferences();

        return delCount;
    }

    public String formattedAvg() {
        double mpgAvg = getMpgAvg();

        if(mpgAvg != Constant.EMPTY_DOUBLE) {
            mpgAvg = mpgAvg < 100.0 ? mpgAvg : 99.9;
            DecimalFormat df = new DecimalFormat(Constant.FLOAT_FORMAT); //Formats MPG value

            return df.format(mpgAvg);
        } else {
            return Constant.NULL_MPG;
        }
    }

    public double getMpgAvg() {
        if(logList.size() > 1) {
            int cumulative = 0;

            for(Entry entry : entryList) {
                cumulative += entry.getVal();
            }

            return cumulative / entryList.size();
        } else {
            return Constant.EMPTY_DOUBLE;
        }
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

    public FuelLog findEntryById(int entryId) {
        for(FuelLog aLog : logList) {
            if(aLog.getItemID() == entryId) {
                return aLog;
            }
        }

        return null;
    }

    public Map<String, FuelLog> getVicinity(int entryId) {
        Map<String, FuelLog> vicinityMap = new HashMap<>();
        int position = 0;

        for(FuelLog aLog : logList) {
            if(aLog.getItemID() == entryId) {
                position = logList.indexOf(aLog);
            }
        }

        //TODO: doesn't work when list is empty
        if(!logList.isEmpty()) {
            if (position != (logList.size() - 1)) {
                vicinityMap.put(Constant.NEXT_ENTRY, logList.get(position - 1));
            }
            if (position != 0) {
                vicinityMap.put(Constant.PREV_ENTRY, logList.get(position + 1));
            }
        }

        return vicinityMap;
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
        entryList.clear();
        labelList.clear();

        if(logList.size() > 1) {
            int initIndex = logList.size() - 1;

            for(int i = initIndex; i > 0; i--) {
                FuelLog nextEntry = logList.get(i - 1);
                FuelLog currEntry = logList.get(i);

                MilesPerGal mpg = calculateMpg(nextEntry, currEntry);
                entryList.add(new Entry(mpg.getMpg(), initIndex - i));

                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(mpg.getRecordDate());
                labelList.add(new SimpleDateFormat("MM/dd/yyyy", Locale.US).format(cal.getTime()));
            }
        }
    }

    private void updatePreferences() {
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
}
