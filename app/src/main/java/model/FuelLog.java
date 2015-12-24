package model;

/**
 * Created by zyuki on 11/30/2015.
 */
public class FuelLog {

    //Values that are stored into DB
    private int itemID;                 //Unique ID for every entry. Saved into DB column "_id"
    private int currentOdomVal;       //
    private float fuelTopupAmount;      //
    private long recordDate;          //
    private boolean skipEntry;

    //Setters
    public void setItemID(int itemID) {this.itemID = itemID;}
    public void setCurrentOdomVal(int currentTachVal) {this.currentOdomVal = currentTachVal;}
    public void setFuelTopupAmount(float fuelTopup) {this.fuelTopupAmount = fuelTopup;}
    public void setRecordDate(long recordDate) {this.recordDate = recordDate;}
    public void setSkipEntry(boolean skipEntry) {this.skipEntry = skipEntry;}

    //Getters
    public int getItemID() {return itemID;}
    public int getCurrentOdomVal() {return currentOdomVal;}
    public float getFuelTopupAmount() {return fuelTopupAmount;}
    public long getRecordDate() {return recordDate;}
    public boolean getSkipEntry() {return skipEntry;}
}
