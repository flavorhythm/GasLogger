package com.zenoyuki.flavorhythm.gaslogger;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import java.util.ArrayList;

import adapter.ListAdapter;
import data.DataAccessObject;
import model.FuelLog;
import fragment.DialogRouter;
import util.Constant;

public class HistoryActivity extends AppCompatActivity {

    /***********************************************************************************************
     * GLOBAL VARIABLES
     **********************************************************************************************/
    /**PRIVATE VARIABLES**/
    // Main listview that displays all available entries from the DB
    private ListView entryList;
    // Counter to distinguish between "no entries available" and "at least one entry available"
    private int listCount = 0;
    // Instantiates the DataAccess Object globally so all the methods have access to it
    private DataAccessObject dataAO;

    /***********************************************************************************************
     * OVERRIDE METHODS
     **********************************************************************************************/
    /**Finds views, sets up dataAO and refreshes the entryList once**/
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Ties the layout to this activity
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        // Points to the DataAccessObject in the ApplicationDatabase
        dataAO = ((ApplicationDatabase)getApplication()).dataAccess;

        // Finds the ListView
        entryList = (ListView)findViewById(R.id.LV_history);
        // Sets the empty view for this list
        entryList.setEmptyView(findViewById(R.id.empty_txt_emptyListText));

        // Uses method refreshData to determine how many entries are currently saved in the DB
        // and to refresh the list
//        listCount = refreshData();
    }

    /**Creates the menu from layout >> menu_history.xml**/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_history, menu);
        return true;
    }

    /**Routes the selected item menu to its correct corresponding action**/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // A switch to route the selected items to the correct action
        switch (id) {
            case R.id.MI_del_all:

                // Determines if the list is empty or not
                int allOrNone = listCount > 0 ? Constant.ALL_ID : Constant.NO_ENTRIES;

                // When the "delete" item is clicked, a dialog fragment opens
                // Param1: this activity
                // Param2: integer value dependent on the number of entries in the DB
//                DialogRouter.showDeleteDialog(HistoryActivity.this, allOrNone);
                break;
        }

        // Returns the Super call of this method
        return super.onOptionsItemSelected(item);
    }

    /**Updates the entryList every time focus changes to this activity**/
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {

        // The Super call of this method
        super.onWindowFocusChanged(hasFocus);

        // Whenever this activity has focus...
        if (hasFocus) {
            // Use method refreshData to determine how many entries are currently saved in the DB
            // and to refresh the list
//            listCount = refreshData();
        }
    }

//    /***********************************************************************************************
//     * PRIVATE METHODS
//     **********************************************************************************************/
//    /**Updates the items displayed on the entryList**/
//    private int refreshData() {
//
//        // Instantiates a new ArrayList that is tied to the ListAdapter
//        ArrayList<FuelLog> fuelLogArrayList = new ArrayList<>();
//
//        // Instantiates a new ListAdapter
//        ListAdapter listAdapter = new ListAdapter(HistoryActivity.this, R.layout.row_item_list, fuelLogArrayList);
//        // Sets the adapter to the list
//        entryList.setAdapter(listAdapter);
//
//        // Pulls all entries from the DB into an ArrayList
//        // Param1: selection. Null for all entries
//        // Param2: selection arguments. Null for all entries
//        ArrayList<FuelLog> logsFromDB = dataAO.getAllEntries(null, null);
//
//        // For ever entry in the DB...
//        for (int i = 0; i < logsFromDB.size(); i++) {
//
//            // Instantiates a new temp FuelLog object to pass data from the DB arraylist to the listAdapter arraylist
//            FuelLog entry = new FuelLog();
//
//            // Copies odom value from DB to the list
//            entry.setCurrentOdomVal(logsFromDB.get(i).getCurrentOdomVal());
//            // Copies fillup amount from DB to the list
//            entry.setFuelTopupAmount(logsFromDB.get(i).getFuelTopupAmount());
//            // Copies record date from DB to the list
//            entry.setRecordDate(logsFromDB.get(i).getRecordDate());
//            // Copies partial fill data from DB to the list
//            entry.setPartialFill(logsFromDB.get(i).getPartialFill());
//            // Copies id from DB to the list
//            entry.setItemID(logsFromDB.get(i).getItemID());
//
//            // Adds the temp FuelLog to the adapter-attached arraylist
//            fuelLogArrayList.add(entry);
//            // Notifies the adapter the dataset has changed
//            listAdapter.notifyDataSetChanged();
//        }
//
//        // Returns the number of entries within the DB
//        return logsFromDB.size();
//    }
}