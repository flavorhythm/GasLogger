package util;

import android.content.Context;
import android.content.SharedPreferences;

import com.zenoyuki.flavorhythm.gaslogger.ApplicationDatabase;

import java.text.DecimalFormat;
import java.util.ArrayList;

import data.DataAccessObject;
import model.FuelLog;

/**
 * Created by zyuki on 1/8/2016.
 */
public final class MpgCalculator {
    private static final String NULL_VALUE = "- -";

    private MpgCalculator() {}

    public static String calculate(Context context) {
        //Sets up DB variable and puts all entries into arraylist fuelLogArrayList
        DataAccessObject dataAO = ((ApplicationDatabase)context).mDataAO;
        ArrayList<FuelLog> fuelLogArrayList = dataAO.getAllEntries(null, null);

        if(fuelLogArrayList.size() <= 1) {
            return NULL_VALUE;
        }

        return mainCalculator(fuelLogArrayList);
    }

    private static String mainCalculator(ArrayList<FuelLog> fuelLogArrayList) {
        ArrayList<Double> mpgList = new ArrayList<>();
        int initIndex = fuelLogArrayList.size() - 1;

        for(int i = initIndex; i > 0; i--) {
            FuelLog fuelLogNext = fuelLogArrayList.get(i - 1);
            FuelLog fuelLogCurrent = fuelLogArrayList.get(i);

            if(!fuelLogCurrent.getPartialFill() && !fuelLogNext.getPartialFill()) {
                double gasUsed = fuelLogNext.getFuelTopupAmount();
                int distTravel = fuelLogNext.getCurrentOdomVal() - fuelLogCurrent.getCurrentOdomVal();

                mpgList.add(distTravel / gasUsed);
            }
        }

        double mpgAvg = 0;
        for(double mpg : mpgList) {mpgAvg += mpg;} mpgAvg /= mpgList.size();

        mpgAvg = mpgAvg < 1000.0 ? mpgAvg : 999.9;
        DecimalFormat df = new DecimalFormat("###.0"); //Formats MPG value
        return df.format(mpgAvg);
    }
}
