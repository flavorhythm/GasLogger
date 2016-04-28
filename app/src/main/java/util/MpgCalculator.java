package util;

import android.content.Context;

import com.zenoyuki.flavorhythm.gaslogger.ApplicationDatabase;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import data.DataAccessObject;
import model.FuelLog;
import model.MilesPerGal;

/**
 * Created by zyuki on 1/8/2016.
 */
public final class MpgCalculator {
    private static final String NULL_VALUE = "- -";

    private MpgCalculator() {}

    public static String calculate(Context context) {
        //Sets up DB variable and puts all entries into arraylist fuelLogArrayList
        DataAccessObject dataAO = ((ApplicationDatabase)context).dataAccess;
        List<FuelLog> fuelLogArrayList = dataAO.getAllEntries(null, null);

        if(fuelLogArrayList.size() <= 1) {
            return NULL_VALUE;
        }

        return mainCalculator(fuelLogArrayList);
    }

    public static List<MilesPerGal> findMpgList(Context context) {
        List<MilesPerGal> mpgList = new ArrayList<>();

        DataAccessObject dataAccess = ((ApplicationDatabase)context).dataAccess;
        List<FuelLog> logList = dataAccess.getAllEntries(null, null);

        if(logList.size() > 1) {
            int initIndex = logList.size() - 1;

            for(int i = initIndex; i > 0; i--) {
                FuelLog fuelLogNext = logList.get(i - 1);
                FuelLog fuelLogCurrent = logList.get(i);

                mpgList.add(calculateMpg(fuelLogNext, fuelLogCurrent));
            }

            return mpgList;
        } else {
            return new ArrayList<>();
        }
    }

    public static MilesPerGal calculateMpg(FuelLog next, FuelLog curr) {
        if(!curr.getPartialFill() && !next.getPartialFill()) {
            double gasUsed = next.getFuelTopupAmount();
            int distTravel = next.getCurrentOdomVal() - curr.getCurrentOdomVal();

            return new MilesPerGal(
                    curr.getRecordDate(),
                    Double.valueOf(distTravel / gasUsed).floatValue()
            );
        }

        return null;
    }

    private static String mainCalculator(List<FuelLog> fuelLogArrayList) {
        List<Double> mpgList = new ArrayList<>();
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

        mpgAvg = mpgAvg < 100.0 ? mpgAvg : 99.9;
        DecimalFormat df = new DecimalFormat("##.0"); //Formats MPG value
        return df.format(mpgAvg);
    }
}
