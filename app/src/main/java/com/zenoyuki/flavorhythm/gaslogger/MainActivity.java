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

    //CLASS VARIABLES
    private TextView mpgText; //Textview to display average MPG

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mpgText = (TextView)findViewById(R.id.TV_display_mpg_value); //Ties mpgText variable to appropriate view
        mpgText.setText("0.0");

        FloatingActionButton addBtn = (FloatingActionButton)findViewById(R.id.FAB_add); //Ties addBtn to appropriate view
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragmentRouter.instantiateDataEntryDF(MainActivity.this);
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

        switch(id) {
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

        mpgText.setText(MpgCalculator.calculate(getApplicationContext()));
    }
}