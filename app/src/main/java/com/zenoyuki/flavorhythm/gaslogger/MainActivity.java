package com.zenoyuki.flavorhythm.gaslogger;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

import data.DataAccessObject;
import fragment.DialogItemDelete;
import fragment.DialogItemEntry;
import fragment.DialogRouter;
import fragment.FragmentChart;
import fragment.FragmentList;
import model.FuelLog;
import model.MilesPerGal;
import util.Constant;
import util.MpgCalculator;

public class MainActivity extends AppCompatActivity
        implements DialogItemEntry.Callback, DialogItemDelete.Callback, adapter.ListAdapter.Callback {
    //TODO: Display graph, add tabs
    /***********************************************************************************************
     * GLOBAL VARIABLES
     **********************************************************************************************/
    /**PRIVATE VARIABLES**/
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private Toolbar toolbar;
    private FloatingActionButton addBtn;

    private FragmentChart fragChart;
    private FragmentList fragList;

    private DataAccessObject dataAccess;

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

        dataAccess = ((ApplicationDatabase)getApplication()).dataAccess;

        findViewsById();

        setSupportActionBar(toolbar);
        setViewPager();

        // Adds a click listener to the button
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Uses the fragment router (DialogRouter) to open the correct fragment
                // to add an entry
                DialogRouter.showEntryDialog(MainActivity.this, Constant.NEW_ITEM);
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
            case R.id.menu_deleteAll:
                // When the "delete" item is clicked, a dialog fragment opens
                // Param1: this activity
                // Param2: integer value dependent on the number of entries in the DB
                DialogRouter.showDeleteDialog(MainActivity.this);
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

        // Whenever this activity has focus, set the display to the calculated MPG value
        if(hasFocus) {
//            mpgText.setText(MpgCalculator.calculate(getApplicationContext()));
        }
    }

    //TODO: reorganize list creations
    @Override
    public void addLog(FuelLog fuelLog) {
        fragList.addLog(fuelLog);
        fragChart.updateAverage();
    }

//    @Override
//    public void removeLog(int listPos) {
//        fragList.removeLog(listPos);
//        fragChart.updateAverage();
//    }

    @Override
    public void clearList() {
        fragList.clear();
        fragChart.updateAverage();
    }

    @Override
    public void addMpg(MilesPerGal mpg) {fragList.addMpg(mpg);}

    @Override
    public int listSize() {
        return fragList.getCount();
    }

    //    @Override
//    public void removeMpg(int fillupListPos, int fillupListSize) {
//        if(fillupListPos != (fillupListSize - 1)) {fragList.removeMpg(fillupListPos);}
//        if(fillupListPos != 0) {fragList.removeMpg(fillupListPos - 1);}
//    }

    private void findViewsById() {
        toolbar = (Toolbar)findViewById(R.id.main_toolbar);
        viewPager = (ViewPager)findViewById(R.id.main_viewPager);
        tabLayout = (TabLayout)findViewById(R.id.main_tabLayout);
        addBtn = (FloatingActionButton)findViewById(R.id.main_fab_addItem);
    }

    private void setViewPager() {
        ViewPagerAdapter pagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        fragChart = new FragmentChart();
        fragList = new FragmentList();

        pagerAdapter.addFragment(R.string.frag_chart_title, fragChart);
        pagerAdapter.addFragment(R.string.frag_list_title, fragList);

        viewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> fragList = new ArrayList<>();
        private final List<String> titleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager fragMgr) {super(fragMgr);}

        @Override
        public Fragment getItem(int position) {return fragList.get(position);}

        @Override
        public int getCount() {return fragList.size();}

        @Override
        public CharSequence getPageTitle(int position) {return titleList.get(position);}

        public void addFragment(int strResId, Fragment frag) {
            String title = getResources().getString(strResId);

            fragList.add(frag);
            titleList.add(title);
        }
    }
}