package com.mstudenets.studenetsbananaapp.controller.maps;

import android.content.Context;

import com.j256.ormlite.android.apptools.OpenHelperManager;

/**
 * Created by maksym on 01.03.17.
 */

public class MapHelperFactory
{
    private static MapDatabaseHelper mapDatabaseHelper;

    public static MapDatabaseHelper getMapDatabaseHelper() {
        return mapDatabaseHelper;
    }

    public static void setHelper(Context context) {
        //databaseHelper = OpenHelperManager.getHelper(context, Contact.class);
    }

    public static void releaseHelper() {
        OpenHelperManager.releaseHelper();
        mapDatabaseHelper = null;
    }
}
