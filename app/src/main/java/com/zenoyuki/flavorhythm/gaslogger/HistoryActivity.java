package com.zenoyuki.flavorhythm.gaslogger;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import data.DataAdapter;
import data.DatabaseHandler;
import model.FuelLog;

public class HistoryActivity extends AppCompatActivity {
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        refreshData();
    }

    private DatabaseHandler db;
    private ArrayList<FuelLog> fuelLogArrayList = new ArrayList<>();
    private DataAdapter dataAdapter;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        listView = (ListView)findViewById(R.id.LV_history);
        listView.setEmptyView(findViewById(R.id.empty_txt_emptyListText));

        refreshData();
    }

    private void refreshData() {
        fuelLogArrayList.clear();
        db = new DatabaseHandler(getApplicationContext());

        ArrayList<FuelLog> logsFromDB = db.getAllEntries();
        for(int i = 0; i < logsFromDB.size(); i++) {
            FuelLog entry = new FuelLog();
            entry.setCurrentOdomVal(logsFromDB.get(i).getCurrentOdomVal());
            entry.setFuelTopupAmount(logsFromDB.get(i).getFuelTopupAmount());
            entry.setRecordDate(logsFromDB.get(i).getRecordDate());
            entry.setItemID(logsFromDB.get(i).getItemID());

            fuelLogArrayList.add(entry);
        }

        db.close();

        dataAdapter = new DataAdapter(HistoryActivity.this, R.layout.history_row, fuelLogArrayList);
        listView.setAdapter(dataAdapter);
        dataAdapter.notifyDataSetChanged();
    }
}
