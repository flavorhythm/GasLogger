package com.zenoyuki.flavorhythm.gaslogger;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import com.mobsandgeeks.saripaar.QuickRule;
import com.mobsandgeeks.saripaar.Rule;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.DecimalMin;
import com.mobsandgeeks.saripaar.annotation.Min;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Order;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import data.DatabaseHandler;
import model.FuelLog;

public class MainActivity extends AppCompatActivity {

    private TextView mpgText;

    private DatabaseHandler db;

	private int minMileage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mpgText = (TextView)findViewById(R.id.TV_display_mpg_value);
        mpgText.setText(updateAverage());

        FloatingActionButton addBtn = (FloatingActionButton)findViewById(R.id.FAB_add);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogBuilder();
            }
        });
    }

    private String updateAverage() {
        db = new DatabaseHandler(getApplicationContext());
        db.getReadableDatabase();
        ArrayList<FuelLog> fuelLogArrayList = db.getAllEntries();

        if(fuelLogArrayList.size() > 1) {
        	minMileage = fuelLogArrayList.get(fuelLogArrayList.size() - 1).getCurrentOdomVal();
            int mileage = fuelLogArrayList.get(0).getCurrentOdomVal() - minMileage;

            float gasUse = 0;
            for(FuelLog fuelLog : fuelLogArrayList) {
                gasUse += fuelLog.getFuelTopupAmount();
            }

            float mpg = mileage / gasUse;
            DecimalFormat df = new DecimalFormat("###.0");

            db.close();
            return df.format(mpg);
        } else {
            return "0";
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.MI_history:
                startActivity(new Intent(MainActivity.this, HistoryActivity.class));
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void dialogBuilder() {
		FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.commit();

        Fragment previousFragment = getSupportFragmentManager().findFragmentByTag("dialog");
        if(previousFragment != null) {
            fragmentTransaction.remove(previousFragment);
        }

        fragmentTransaction.addToBackStack(null);

        //need to pass minMileage in here. Make changes to class accordingly
        CustomDialogFragment customDialogFragment = CustomDialogFragment.newInstance();
        customDialogFragment.setCancelable(true);

        customDialogFragment.show(getSupportFragmentManager(), "dialog");
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        mpgText.setText(updateAverage());
    }
}