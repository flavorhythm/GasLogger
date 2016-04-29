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
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.github.mikephil.charting.data.Entry;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import data.DataAccessObject;
import fragment.DialogItemDelete;
import fragment.DialogItemEntry;
import fragment.DialogRouter;
import fragment.FragmentChart;
import fragment.FragmentList;
import model.FuelLog;
import util.Constant;

public class MainActivity extends AppCompatActivity
        implements DialogItemEntry.Callback, DialogItemDelete.Callback, FragmentList.Callback,
        FragmentChart.Callback {
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
        //TODO: backup to Drive
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
                DialogRouter.showDeleteDialog(MainActivity.this);
                break;
            default:
                break;
        }

        // Returns the Super call of this method
        return super.onOptionsItemSelected(item);
    }

    @Override
    public List<FuelLog> getLogList() {return dataAccess.getLogList();}

    @Override
    public List<Entry> getEntryList() {return dataAccess.getEntryList();}

    @Override
    public List<String> getLabelList() {return dataAccess.getLabelList();}

    @Override
    public String formattedAvg() {return dataAccess.formattedAvg();}

    @Override
    public Map<String, FuelLog> getVicinity(int entryId) {
        return dataAccess.getVicinity(entryId);
    }

    @Override
    public int addLog(FuelLog fuelLog) {
        long newId = dataAccess.addLog(fuelLog);
        fragList.notifyAdapter();
        notifyChart();

        return Long.valueOf(newId).intValue();
    }

    @Override
    public boolean updateLog(FuelLog fuelLog) {
        boolean updated = dataAccess.updateLog(fuelLog);
        fragList.notifyAdapter();
        notifyChart();

        return updated;
    }

    @Override
    public int clearList() {
        int delCount = dataAccess.clearLogs();
        fragList.notifyAdapter();
        notifyChart();

        return delCount;
    }

    @Override
    public int listSize() {return dataAccess.getLogSize();}

    private void notifyChart() {
        fragChart.updateAvg(dataAccess.formattedAvg());
        fragChart.notifyChart();
    }

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