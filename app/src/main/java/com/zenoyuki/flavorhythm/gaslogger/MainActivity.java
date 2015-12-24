package com.zenoyuki.flavorhythm.gaslogger;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;

import data.DatabaseHandler;
import model.FuelLog;

public class MainActivity extends AppCompatActivity {

    private TextView mpgText;

    private DatabaseHandler db;
	CustomDialogFragment customDialogFragment;

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

        Fragment previousFragment = getSupportFragmentManager().findFragmentByTag("dialog");
        if(previousFragment != null) {
            fragmentTransaction.remove(previousFragment);
        }

        fragmentTransaction.addToBackStack(null);

		fragmentTransaction.commit();

		if(false) {
			//need to pass minMileage in here. Make changes to class accordingly
			customDialogFragment = CustomDialogFragment.newInstance();
			customDialogFragment.show(getSupportFragmentManager(), "dialog");
		} else {
			fragmentTransaction = getSupportFragmentManager().beginTransaction();
			customDialogFragment = CustomDialogFragment.newInstance();
			fragmentTransaction.add(customDialogFragment, "dialog");
			fragmentTransaction.commit();
		}
    }

	public static void alertSubmitBtnClick() {

	}

	public static void alertDismissBtnClick() {

	}

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        mpgText.setText(updateAverage());
    }
}