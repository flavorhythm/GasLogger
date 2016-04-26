package model;

/**
 * Created by zyuki on 11/30/2015.
 */
public class FuelLog {
    //TODO: Add cost variable and make necessary changes to DB objects
    //Values that are stored into DB
    private int itemID;                 //Unique ID for every entry. Saved into DB column "_id"
    private int currentOdomVal;         //
    private double fuelTopupAmount;     //
    private long recordDate;            //
    private boolean partialFill;

    //Setters
    public void setItemID(int itemID) {this.itemID = itemID;}
    public void setCurrentOdomVal(int currentTachVal) {this.currentOdomVal = currentTachVal;}
    public void setFuelTopupAmount(double fuelTopup) {this.fuelTopupAmount = fuelTopup;}
    public void setRecordDate(long recordDate) {this.recordDate = recordDate;}
    public void setPartialFill(boolean partialFill) {this.partialFill = partialFill;}

    //Getters
    public int getItemID() {return itemID;}
    public int getCurrentOdomVal() {return currentOdomVal;}
    public double getFuelTopupAmount() {return fuelTopupAmount;}
    public long getRecordDate() {return recordDate;}
    public boolean getPartialFill() {return partialFill;}
}
