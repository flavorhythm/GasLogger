package com.zenoyuki.flavorhythm.gaslogger;

import android.content.Intent;
import android.content.SharedPreferences;
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

import data.Constants;
import data.DatabaseHandler;
import model.FuelLog;

public class MainActivity extends AppCompatActivity {

    //CLASS VARIABLES
    private TextView mpgText; //Textview to display average MPG

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mpgText = (TextView)findViewById(R.id.TV_display_mpg_value); //Ties mpgText variable to appropriate view
        mpgText.setText(updateAverage()); //Updates mpgText for the first time with the average milages from DB

        FloatingActionButton addBtn = (FloatingActionButton)findViewById(R.id.FAB_add); //Ties addBtn to appropriate view
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogBuilder();
            }
        }); //Sets action to addBtn when pressed
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

    //Updates MPG value every time focus changes
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        mpgText.setText(updateAverage());
    }

    //Method to update MPG value from DB
    private String updateAverage() {
        //Sets up DB variable and puts all entries into arraylist fuelLogArrayList
        DatabaseHandler db = new DatabaseHandler(getApplicationContext());
        db.getReadableDatabase();
        ArrayList<FuelLog> fuelLogArrayList = db.getAllEntries();
        db.close();

        //If there are entries in the DB, finds the average MPG
        if(fuelLogArrayList.size() == 0) {
            SharedPreferences.Editor editor = getPreferences(MODE_PRIVATE).edit();
            editor.putInt(Constants.MIN_MILEAGE_KEY, 0);

            editor.apply();
        }

        if(fuelLogArrayList.size() > 1) {
            int mileage = fuelLogArrayList.get(0).getCurrentOdomVal() - fuelLogArrayList.get(fuelLogArrayList.size() - 1).getCurrentOdomVal(); //Finds total miles traveled

            //Accumulates all gas topups from DB except for the very first (last item in fuelLogArrayList) entry
            float gasUse = 0;
            for(FuelLog fuelLog : fuelLogArrayList) {gasUse += fuelLog.getFuelTopupAmount();}
            gasUse -= fuelLogArrayList.get(fuelLogArrayList.size() - 1).getFuelTopupAmount();

            //Divides total miles traveled by total gas usage
            float mpg = mileage / gasUse;
            DecimalFormat df = new DecimalFormat("###.0"); //Formats MPG value

            return df.format(mpg);
        } else {
            return "0";
        }
    }

    //Builds a dialog using a fragment
    private void dialogBuilder() {
        //Cleans up any previously open fragments with the tag "dialog"
		FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        Fragment previousFragment = getSupportFragmentManager().findFragmentByTag("dialog");
        if(previousFragment != null) {fragmentTransaction.remove(previousFragment);}

        fragmentTransaction.addToBackStack(null);

        //Creates the dialog and passes minMileage to CustomDialogFragment
        CustomDialogFragment customDialogFragment = CustomDialogFragment.newInstance();
        customDialogFragment.show(fragmentTransaction, "dialog");
    }
}