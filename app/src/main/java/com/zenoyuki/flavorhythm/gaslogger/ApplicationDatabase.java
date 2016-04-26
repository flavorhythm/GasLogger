package com.zenoyuki.flavorhythm.gaslogger;

import android.app.Application;

import java.sql.SQLException;

import data.DataAccessObject;

/**
 * Created by ZYuki on 1/11/2016.
 */
public class ApplicationDatabase extends Application {
    /***********************************************************************************************
     * GLOBAL VARIABLES
     **********************************************************************************************/
    /**PUBLIC VARIABLES**/
    // Public DataAccessObject so the entire application has access to the DB
    public DataAccessObject dataAccess;

    /***********************************************************************************************
     * OVERRIDE METHODS
     **********************************************************************************************/
    /**Sets up the DataAccessObject whenever the Application is initialized**/
    @Override
    public void onCreate() {
        // Instantiates a new DataAccessObject
        // Param1: this application's context
        dataAccess = new DataAccessObject(getApplicationContext());

        // Opens DB
        openDatabaseAndTry();

        // This method's Super call
        super.onCreate();
    }

    /**Closes the DataAccessObject whenever the Application is terminated**/
    @Override
    public void onTerminate() {
        // Closes DB
        dataAccess.close();

        // This method's Super call
        super.onTerminate();
    }

    /***********************************************************************************************
     * PRIVATE METHODS
     **********************************************************************************************/
    /**Encapsulates Database Open in a try-catch**/
    private void openDatabaseAndTry() {
        // Try-catch block
        try {
            // Opens DB
            dataAccess.open();
        // Catches an SQLException
        } catch(SQLException e) {
            // Prints the stack trace of the exception
            e.printStackTrace();
        }
    }
}
