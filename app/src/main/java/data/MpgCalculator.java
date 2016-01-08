package data;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import java.text.DecimalFormat;
import java.util.ArrayList;

import model.FuelLog;

/**
 * Created by zyuki on 1/8/2016.
 */
public final class MpgCalculator {
    private MpgCalculator() {}

    public static String calculate(Context context) {
        //Sets up DB variable and puts all entries into arraylist fuelLogArrayList
        DatabaseHandler db = new DatabaseHandler(context);
        db.getReadableDatabase();
        ArrayList<FuelLog> fuelLogArrayList = db.getAllEntries();
        db.close();

        //There are only three possible outcomes of this switch. Negative numbers are irrelevant since user cannot input a negative value.
        switch(fuelLogArrayList.size()) {
            //If there no entries in the DB, the preferences is updated to the value ZERO.
            //This is done so that if the DB is cleared, the user can input any odometer value.
            case 0: clearPreferences();
            //If there are no entries (from the case above with no "break") or just one entry in the DB, returns ZERO.
            //This is done because there aren't enough entries to calculate an MPG value from.
            case 1: return "0";
            //Returns an actual MPG calculation if there is more than one entry.
            default: return mainCalculator(fuelLogArrayList);
        }
    }

    private static void clearPreferences() {
        SharedPreferences.Editor editor = new Activity().getPreferences(Activity.MODE_PRIVATE).edit();
        editor.putInt(Constants.MIN_MILEAGE_KEY, 0);

        editor.apply();
    }

    private static String mainCalculator(ArrayList<FuelLog> fuelLogArrayList) {
        ArrayList<Double> mpgList = new ArrayList<>();
        int initIndex = fuelLogArrayList.size() - 1;

        for(int i = initIndex; i > 0; i--) {
            FuelLog fuelLogNext = fuelLogArrayList.get(i - 1);
            FuelLog fuelLogCurrent = fuelLogArrayList.get(i);

            double gasUsed = fuelLogNext.getFuelTopupAmount();
            int distTravel = fuelLogNext.getCurrentOdomVal() - fuelLogCurrent.getCurrentOdomVal();

            mpgList.add(distTravel / gasUsed);
        }

        double mpgAvg = 0;
        for(double mpg : mpgList) {mpgAvg += mpg;} mpgAvg /= mpgList.size();

        mpgAvg = mpgAvg < 1000.0 ? mpgAvg : 999.9;
        DecimalFormat df = new DecimalFormat("###.0"); //Formats MPG value
        return df.format(mpgAvg);
    }
}