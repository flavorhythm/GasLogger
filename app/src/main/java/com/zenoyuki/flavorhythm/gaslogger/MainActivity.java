package com.zenoyuki.flavorhythm.gaslogger;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import fragments.DialogFragmentRouter;
import util.MpgCalculator;

public class MainActivity extends AppCompatActivity {

    /***********************************************************************************************
     * GLOBAL VARIABLES
     **********************************************************************************************/
    /**PRIVATE VARIABLES**/
    // Textview to display average MPG
    private TextView mpgText;

    /***********************************************************************************************
     * OVERRIDE METHODS
     **********************************************************************************************/
    /**Finds views and sets up buttons**/
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //TODO backup to Drive

        // Ties the layout to this activity
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Finds and displays the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Ties mpgText variable to appropriate view
        mpgText = (TextView)findViewById(R.id.TV_display_mpg_value);

        // Ties addBtn to appropriate view
        FloatingActionButton addBtn = (FloatingActionButton)findViewById(R.id.FAB_add);

        // Adds a click listener to the button
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Uses the fragment router (DialogFragmentRouter) to open the correct fragment
                // to add an entry
                DialogFragmentRouter.instantiateDataEntryDF(MainActivity.this);
            }
        });
    }

    /**Creates the menu from layout >> menu_main.xml**/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
        switch(id) {
            case R.id.MI_history:
                // When the "history" item is clicked, HistoryActivity starts
                startActivity(new Intent(MainActivity.this, HistoryActivity.class));
                break;
            default:
                break;
        }

        // Returns the Super call of this method
        return super.onOptionsItemSelected(item);
    }

    /**Changes the mpg value in mpgText every time focus changes to this activity**/
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {

        // The Super call of this method
        super.onWindowFocusChanged(hasFocus);

        // If this screen has focus, then set the display to the calculated MPG value
        if(hasFocus) {
            mpgText.setText(MpgCalculator.calculate(getApplicationContext()));
        }
    }
}