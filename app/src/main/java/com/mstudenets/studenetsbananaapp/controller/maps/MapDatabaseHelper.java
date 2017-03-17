package com.mstudenets.studenetsbananaapp.controller.maps;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.mstudenets.studenetsbananaapp.controller.database.DatabaseHelper;
import com.mstudenets.studenetsbananaapp.model.MyMapMarker;

import java.sql.SQLException;


@Deprecated
public class MapDatabaseHelper extends OrmLiteSqliteOpenHelper
{
    private static final String DATABASE_NAME = "contacts.db";
    private static final int DATABASE_VERSION = 1;

    private Dao<MyMapMarker, Integer> mapMarkerDao;

    public MapDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
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

    public Dao<MyMapMarker, Integer> getMarkersDao() throws SQLException {
        if (mapMarkerDao == null)
            mapMarkerDao = getDao(MyMapMarker.class);
        return mapMarkerDao;
    }

    @Override
    public void close() {
        super.close();
        mapMarkerDao = null;
    }
}
