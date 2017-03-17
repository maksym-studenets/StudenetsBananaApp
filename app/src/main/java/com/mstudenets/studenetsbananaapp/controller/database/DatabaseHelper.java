package com.mstudenets.studenetsbananaapp.controller.database;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.mstudenets.studenetsbananaapp.model.Contact;
import com.mstudenets.studenetsbananaapp.model.MyMapMarker;

import java.sql.SQLException;

public class DatabaseHelper extends OrmLiteSqliteOpenHelper
{
    private static final String DATABASE_NAME = "banana.db";
    private static final int DATABASE_VERSION = 1;

    private Dao<Contact, Integer> contactsDao = null;
    private Dao<MyMapMarker, Integer> mapMarkersDao = null;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, Contact.class);
            TableUtils.createTable(connectionSource, MyMapMarker.class);
            Log.i(DatabaseHelper.class.getName(), " --- DB created successfully");
        } catch (SQLException e) {
            e.printStackTrace();
            Log.e(DatabaseHelper.class.getName(), " --- Error creating database");
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource,
                          int oldVersion, int newVersion) {
        try {
            TableUtils.dropTable(connectionSource, Contact.class, true);
            TableUtils.dropTable(connectionSource, MyMapMarker.class, true);
            Log.i(DatabaseHelper.class.getName(), " --- DB dropped");
            onCreate(database, connectionSource);
            Log.i(DatabaseHelper.class.getName(), " --- DB created successfully");
        } catch (SQLException e) {
            e.printStackTrace();
            Log.e(DatabaseHelper.class.getName(), " --- Error upgrading database");
            throw new RuntimeException(e);
        }
    }

    public Dao<Contact, Integer> getContactsDao() throws SQLException {
        if (contactsDao == null)
            contactsDao = getDao(Contact.class);
        return contactsDao;
    }

    public Dao<MyMapMarker, Integer> getMapMarkersDao() throws SQLException {
        if (mapMarkersDao == null)
            mapMarkersDao = getDao(MyMapMarker.class);
        return mapMarkersDao;
    }

    @Override
    public void close() {
        super.close();
        contactsDao = null;
        mapMarkersDao = null;
    }


    /*
    private static final String DATABASE_NAME = "banana.db";
    private static final int DATABASE_VERSION = 1;

    private Dao<Contact, Integer> contactsDao = null;
    private Dao<MyMapMarker, Integer> mapMarkersDao = null;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, Contact.class);
            TableUtils.createTable(connectionSource, MyMapMarker.class);
            Log.i(DatabaseHelper.class.getName(), " --- DBs created successfully");
        } catch (SQLException e) {
            e.printStackTrace();
            Log.e(DatabaseHelper.class.getName(), " --- Error creating databases");
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource,
                          int oldVersion, int newVersion) {
        try {
            TableUtils.dropTable(connectionSource, Contact.class, true);
            TableUtils.dropTable(connectionSource, MyMapMarker.class, true);
            Log.i(DatabaseHelper.class.getName(), " --- DBs dropped");
            onCreate(database, connectionSource);
            Log.i(DatabaseHelper.class.getName(), " --- DB created successfully");
        } catch (SQLException e) {
            e.printStackTrace();
            Log.e(DatabaseHelper.class.getName(), " --- Error upgrading database");
            throw new RuntimeException(e);
        }
    }

    public Dao<Contact, Integer> getContactsDao() throws SQLException {
        if (contactsDao == null)
            contactsDao = getDao(Contact.class);
        return contactsDao;
    }

    public Dao<MyMapMarker, Integer> getMapMarkerDao() throws SQLException {
        if (mapMarkersDao == null)
            mapMarkersDao = getDao(MyMapMarker.class);
        return mapMarkersDao;
    }

    @Override
    public void close() {
        super.close();
        contactsDao = null;
        mapMarkersDao = null;
    }
    */
}
