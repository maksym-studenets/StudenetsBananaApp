package com.mstudenets.studenetsbananaapp.controller.maps;

import android.content.Context;

import com.j256.ormlite.android.apptools.OpenHelperManager;

/**
 * Created by maksym on 01.03.17.
 */

public class MapDatabaseManager
{
    public static volatile MapDatabaseManager instance;
    private MapDatabaseHelper databaseHelper;

    private MapDatabaseManager() {
    }

    public static MapDatabaseManager getInstance() {
        if (instance == null)
            synchronized (MapDatabaseManager.class) {
                if (instance == null)
                    instance = new MapDatabaseManager();
            }
        return instance;
    }

    public void initialize(Context context) {
        if (databaseHelper == null)
            databaseHelper = OpenHelperManager.getHelper(context, MapDatabaseHelper.class);
    }

    public void release() {
        if (databaseHelper != null)
            OpenHelperManager.releaseHelper();
    }

    public MapDatabaseHelper getDatabaseHelper() {
        return databaseHelper;
    }
}
