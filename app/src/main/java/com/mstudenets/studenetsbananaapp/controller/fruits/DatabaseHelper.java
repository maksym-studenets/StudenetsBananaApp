package com.mstudenets.studenetsbananaapp.controller.fruits;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.mstudenets.studenetsbananaapp.model.Fruit;

import java.sql.SQLException;

public class DatabaseHelper extends OrmLiteSqliteOpenHelper
{
    private final String TAG = DatabaseHelper.class.getSimpleName();
    private static final String DATABASE_NAME = "fruit.db";
    private static final int DATABASE_VERSION = 1;
    private Dao<Fruit, Integer> fruitDao = null;
    //private RuntimeExceptionDao<Fruit, Integer> runtimeExceptionDao = null;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, Fruit.class);
            Log.i(TAG, " -- DB table was successfully created");
        } catch (SQLException e) {
            e.printStackTrace();
            Log.e(TAG, " -- Error creating DB table");
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource,
                          int oldVersion, int newVersion) {
        try {
            TableUtils.dropTable(connectionSource, Fruit.class, true);
            Log.i(TAG, " -- DB table was successfully dropped");
            onCreate(database, connectionSource);
            Log.i(TAG, " -- DB table was successfully recreated");
        } catch (SQLException e) {
            e.printStackTrace();
            Log.e(TAG, " -- Error upgrading DB table");
            throw new RuntimeException(e);
        }
    }

    public Dao<Fruit, Integer> getFruitDao() throws SQLException {
        if (fruitDao == null)
            fruitDao = getDao(Fruit.class);
        return fruitDao;
    }

    @Override
    public void close() {
        super.close();
        fruitDao = null;
    }
}
