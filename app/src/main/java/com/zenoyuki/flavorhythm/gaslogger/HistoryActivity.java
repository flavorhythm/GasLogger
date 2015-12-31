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

    public class DataAdapter extends ArrayAdapter<FuelLog> {
        Activity activity;
        int layoutResource;
        ArrayList<FuelLog> fuelLogArrayList = new ArrayList<>();

        public DataAdapter(Activity activity, int layoutResource, ArrayList<FuelLog> fuelLogList) {
            super(activity, layoutResource, fuelLogList);

            this.activity = activity;
            this.layoutResource = layoutResource;
            this.fuelLogArrayList = fuelLogList;

            notifyDataSetChanged();
        }

        @Override public int getCount() {return fuelLogArrayList.size();}

        @Override public FuelLog getItem(int position) {return super.getItem(position);}

        @Override public int getPosition(FuelLog item) {return super.getPosition(item);}

        @Override public long getItemId(int position) {return super.getItemId(position);}

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row = convertView;
            ViewHolder viewHolder;

            if(row == null || row.getTag() == null) {
                //Inflates row layout into the row object
                LayoutInflater layoutInflater = LayoutInflater.from(activity);
                row = layoutInflater.inflate(layoutResource, null);
                viewHolder = new ViewHolder();

                //Sets viewHolder view variables to row layout items
                viewHolder.idHolder = (TextView)row.findViewById(R.id.TV_row_timestamp);
                viewHolder.odomHolder = (TextView)row.findViewById(R.id.TV_row_odom);
                viewHolder.gasHolder = (TextView)row.findViewById(R.id.TV_row_gas);
                viewHolder.timestampHolder = (TextView)row.findViewById(R.id.TV_row_timestamp);

                viewHolder.delBtn = (ImageButton)row.findViewById(R.id.IB_delete);
                viewHolder.editBtn = (ImageButton)row.findViewById(R.id.IB_edit);

                row.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder)row.getTag();
            }

            viewHolder.entry = getItem(position);
            viewHolder.odomHolder.setText(String.valueOf(viewHolder.entry.getCurrentOdomVal()));
            viewHolder.gasHolder.setText(String.valueOf(viewHolder.entry.getFuelTopupAmount()));

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
            Date date = new Date(viewHolder.entry.getRecordDate());
            viewHolder.timestampHolder.setText(simpleDateFormat.format(date));

//            viewHolder.idHolder.setText(String.valueOf(viewHolder.entry.getItemID()));

            final int itemID = getItem(position).getItemID();
            final String odomVal = String.valueOf(getItem(position).getCurrentOdomVal());
            viewHolder.delBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                  deleteAlert(String.valueOf(odomVal), itemID);
                }
            });
            viewHolder.editBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

            return row;
        }

        private void deleteAlert(String odomVal, final int itemID) {
            AlertDialog.Builder builder = new AlertDialog.Builder(HistoryActivity.this);
            builder.setTitle("Topup at: " + odomVal + " miles");
            builder.setMessage("Delete?");

            builder.setPositiveButton("Destroy!", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    DatabaseHandler db = new DatabaseHandler(getApplicationContext());
                    db.getWritableDatabase();

                    db.deleteEntry(itemID);
                    notifyDataSetChanged();
                    db.close();
                }
            });
            builder.setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            builder.create().show();
        }
    }

    private class ViewHolder {
        FuelLog entry;

        TextView idHolder;
        TextView odomHolder;
        TextView gasHolder;
        TextView timestampHolder;
        ImageButton delBtn;
        ImageButton editBtn;
    }
}
