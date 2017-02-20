package com.mstudenets.studenetsbananaapp.controller.fruits;


import android.content.Context;

import com.j256.ormlite.android.apptools.OpenHelperManager;

public class DatabaseManager
{
    private static volatile DatabaseManager instance;
    private DatabaseHelper databaseHelper;

    private DatabaseManager() {
    }

    public static DatabaseManager getInstance() {
        if (instance == null) {
            synchronized (DatabaseManager.class) {
                if (instance == null)
                    instance = new DatabaseManager();
            }
        }
        return instance;
    }

    public void initialize(Context context) {
        if (databaseHelper == null)
            databaseHelper = OpenHelperManager.getHelper(context, DatabaseHelper.class);
    }

    public void release() {
        if (databaseHelper != null)
            OpenHelperManager.releaseHelper();
    }

    public DatabaseHelper getDatabaseHelper() {
        return databaseHelper;
    }
}
