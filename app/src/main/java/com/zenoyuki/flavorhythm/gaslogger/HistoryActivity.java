package com.zenoyuki.flavorhythm.gaslogger;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import java.util.ArrayList;

import data.DataAdapter;
import data.DatabaseHandler;
import model.FuelLog;

public class HistoryActivity extends AppCompatActivity {
    private ArrayList<FuelLog> fuelLogArrayList = new ArrayList<>();
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        listView = (ListView)findViewById(R.id.LV_history);
        listView.setEmptyView(findViewById(R.id.empty_txt_emptyListText));

        refreshData();
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

        switch(id) {
            case R.id.MI_del_all:
                //createDialog
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        refreshData();
    }

    private void refreshData() {
        fuelLogArrayList.clear();
        DatabaseHandler db = new DatabaseHandler(getApplicationContext());
        DataAdapter dataAdapter = new DataAdapter(HistoryActivity.this, R.layout.history_row, fuelLogArrayList);
        listView.setAdapter(dataAdapter);

        ArrayList<FuelLog> logsFromDB = db.getAllEntries();
        for(int i = 0; i < logsFromDB.size(); i++) {
            FuelLog entry = new FuelLog();

            entry.setCurrentOdomVal(logsFromDB.get(i).getCurrentOdomVal());
            entry.setFuelTopupAmount(logsFromDB.get(i).getFuelTopupAmount());
            entry.setRecordDate(logsFromDB.get(i).getRecordDate());
            entry.setItemID(logsFromDB.get(i).getItemID());

            fuelLogArrayList.add(entry);
            dataAdapter.notifyDataSetChanged();
        }

        db.close();
    }
}
