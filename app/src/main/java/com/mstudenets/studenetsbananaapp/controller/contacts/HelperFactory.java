package com.mstudenets.studenetsbananaapp.controller.contacts;


import android.content.Context;

import com.j256.ormlite.android.apptools.OpenHelperManager;

public class HelperFactory
{
    private static DatabaseHelper databaseHelper;

    public static DatabaseHelper getDatabaseHelper() {
        return databaseHelper;
    }

    public static void setHelper(Context context) {
        //databaseHelper = OpenHelperManager.getHelper(context, Contact.class);
    }

    public static void releaseHelper() {
        OpenHelperManager.releaseHelper();
        databaseHelper = null;
    }
}
