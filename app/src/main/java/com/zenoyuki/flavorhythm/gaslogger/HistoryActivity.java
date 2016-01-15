package com.zenoyuki.flavorhythm.gaslogger;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import data.DataAccessObject;
import data.DataAdapter;
import model.FuelLog;
import fragments.DialogFragmentRouter;
import util.Constants;

public class HistoryActivity extends AppCompatActivity {
    private ListView listView;
    private int listCount = 0;

    private DataAccessObject dataAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        dataAO = ((ApplicationDatabase) getApplication()).mDataAO;

        listView = (ListView) findViewById(R.id.LV_history);
        listView.setEmptyView(findViewById(R.id.empty_txt_emptyListText));

        listCount = refreshData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_history, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.MI_del_all:
                DialogFragmentRouter.instantiateDeleteItemsDF(HistoryActivity.this, listCount > 0 ? Constants.ALL_ID : Constants.NO_ENTRIES);
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        listCount = refreshData();
    }

    private int refreshData() {
        ArrayList<FuelLog> fuelLogArrayList = new ArrayList<>();
        fuelLogArrayList.clear();

        DataAdapter dataAdapter = new DataAdapter(HistoryActivity.this, R.layout.history_row, fuelLogArrayList);
        listView.setAdapter(dataAdapter);

        ArrayList<FuelLog> logsFromDB = dataAO.getAllEntries(null, null);
        for (int i = 0; i < logsFromDB.size(); i++) {
            FuelLog entry = new FuelLog();

            entry.setCurrentOdomVal(logsFromDB.get(i).getCurrentOdomVal());
            entry.setFuelTopupAmount(logsFromDB.get(i).getFuelTopupAmount());
            entry.setRecordDate(logsFromDB.get(i).getRecordDate());
            entry.setPartialFill(logsFromDB.get(i).getPartialFill());
            entry.setItemID(logsFromDB.get(i).getItemID());

            fuelLogArrayList.add(entry);
            dataAdapter.notifyDataSetChanged();
        }

        return logsFromDB.size();
    }
}