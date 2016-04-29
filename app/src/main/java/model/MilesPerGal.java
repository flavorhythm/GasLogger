package model;

/**
 * Created by zyuki on 4/26/2016.
 */
public class MilesPerGal {
    /***********************************************************************************************
     * GLOBAL VARIABLES
     **********************************************************************************************/
    private long recordDate;
    private float mpg;

    public MilesPerGal(long recordDate, float mpg) {
        this.recordDate = recordDate;
        this.mpg = mpg;
    }

    /***********************************************************************************************
     * GETTER METHODS
     **********************************************************************************************/
    public long getRecordDate() {return recordDate;}
    public float getMpg() {return mpg;}
}
