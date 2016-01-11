package com.zenoyuki.flavorhythm.gaslogger;

import android.app.Application;

import java.sql.SQLException;

import data.DataAccessObject;

/**
 * Created by ZYuki on 1/11/2016.
 */
public class ApplicationCommons extends Application {

    public DataAccessObject mDataAO;

    @Override
    public void onCreate() {
        mDataAO = new DataAccessObject(getApplicationContext());
        openDatabaseAndTry();
        super.onCreate();
    }

    @Override
    public void onTerminate() {
        mDataAO.close();
        super.onTerminate();
    }

    private void openDatabaseAndTry() {
        try {
            mDataAO.open();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }
}
