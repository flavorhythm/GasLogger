package data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

import model.FuelLog;

/**
 * Created by zyuki on 11/30/2015.
 * Database handler that creates/upgrades database, adds/deletes entries from database.
 */
public class DatabaseHandler extends SQLiteOpenHelper {

    //Creates a private and unassignable ArrayList. Entries can still be "added" using the ArrayList.add method
    private final ArrayList<FuelLog> fuelLogList = new ArrayList<>();

    //Custom constructor which takes in @PARAM context, the only parameter that may differ. Other parameters are static and are set in the super call.
    public DatabaseHandler(Context context) {
        super(context, Constants.DATABASE_NAME, null, Constants.DATABASE_VERSION);
    }

    //onCreate override. Creates SQL database for the first time using the column names below
    @Override
    public void onCreate(SQLiteDatabase db) {
        //Creates a SQL command string
        String CREATE_FUEL_LOG_TABLE = "CREATE TABLE " + Constants.TABLE_NAME + "(" +
                Constants.KEY_ID + " INTEGER PRIMARY KEY, " +
                Constants.ODOM_VAL + " INTEGER, " +
                Constants.FUEL_AMOUNT + " REAL, " +
                Constants.RECORD_DATE + " INTEGER);";

        //Passes SQL command
        db.execSQL(CREATE_FUEL_LOG_TABLE);
    }

    //onUpgrade override. Replaces old version database with new version database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Passes SQL command to remove old database
        db.execSQL("DROP TABLE IF EXISTS " + Constants.TABLE_NAME);
        //Creates new database
        onCreate(db);
    }

    //Custom method. Retrieves all entries within the database
    public ArrayList<FuelLog> getAllEntries() {
        //Clears any possible residual data.
        fuelLogList.clear();

        //Creates db object to read database
        SQLiteDatabase db = this.getReadableDatabase();

        //Cursor object to cycle through database entries
        //query params are as follows: table name, string of column names, ..., sort by
        Cursor cursor = db.query(
                Constants.TABLE_NAME,
                new String[] {
                        Constants.KEY_ID,
                        Constants.ODOM_VAL,
                        Constants.FUEL_AMOUNT,
                        Constants.RECORD_DATE
                },
                null,
                null,
                null,
                null,
                Constants.RECORD_DATE + " DESC"
        );

        //If and Do statement that cycles through all entries in the database.
        if(cursor.moveToFirst()) {
            do {
                FuelLog fuelLog = new FuelLog(); //Creates new fuelLog object

                fuelLog.setItemID(cursor.getInt(cursor.getColumnIndex(Constants.KEY_ID))); //Sets fuelLog object's itemID variable to data within database
                fuelLog.setCurrentOdomVal(cursor.getInt(cursor.getColumnIndex(Constants.ODOM_VAL))); //Sets fuelLog object's currentOdomVal variable to data within database
                fuelLog.setFuelTopupAmount(cursor.getFloat(cursor.getColumnIndex(Constants.FUEL_AMOUNT))); //Sets fuelLog object's fuelTopupAmount variable to data within database
                fuelLog.setRecordDate(cursor.getLong(cursor.getColumnIndex(Constants.RECORD_DATE)));

                fuelLogList.add(fuelLog); //Adds the entire fuelLog object into the fuelLogList
            } while(cursor.moveToNext());
        }

        //Closing statements
        cursor.close();
        db.close();

        return fuelLogList; //Returns all that data within the database through the fuelLogList Arraylist.
    }

    public void addEntry(FuelLog entry) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Constants.ODOM_VAL, entry.getCurrentOdomVal());
        values.put(Constants.FUEL_AMOUNT, entry.getFuelTopupAmount());
        values.put(Constants.RECORD_DATE, System.currentTimeMillis());

        db.insert(Constants.TABLE_NAME, null, values);
        db.close();
    }

    public void deleteEntry(int id) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(
                Constants.TABLE_NAME,
                Constants.KEY_ID + " = ?",
                new String[]{String.valueOf(id)}
        );

        db.close();
    }

    public void editEntry(FuelLog entry) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Constants.ODOM_VAL, entry.getCurrentOdomVal());
        values.put(Constants.FUEL_AMOUNT, entry.getFuelTopupAmount());
        values.put(Constants.RECORD_DATE, System.currentTimeMillis());

        db.update(
                Constants.TABLE_NAME,
                values,
                Constants.KEY_ID + " = ?",
                new String[]{String.valueOf(entry.getItemID())}
        );

        db.close();
    }
}
