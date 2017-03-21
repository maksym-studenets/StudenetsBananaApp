package com.mstudenets.studenetsbananaapp.controller.maps;


import android.content.Context;
import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.mstudenets.studenetsbananaapp.App;
import com.mstudenets.studenetsbananaapp.controller.database.DatabaseHelper;
import com.mstudenets.studenetsbananaapp.controller.database.DatabaseOperationManager;
import com.mstudenets.studenetsbananaapp.model.MyMapMarker;

import java.sql.SQLException;
import java.util.ArrayList;

@Deprecated
public class MarkersOperationManager
{
    private Dao<MyMapMarker, Integer> mapMarkerDao;
    private App app;
    private final DatabaseHelper databaseHelper = App.getDatabaseHelper();
    //private final MapDatabaseHelper databaseHelper;

    public MarkersOperationManager(Context context) {
        try {
            mapMarkerDao = databaseHelper.getMapMarkersDao();
        } catch (SQLException e) {
            e.printStackTrace();
            Log.e(DatabaseOperationManager.class.getName(),
                    " --- error getting Dao object");
            throw new RuntimeException(e);
        }
    }

    public Dao<MyMapMarker, Integer> getMapMarkerDao() {
        try {
            return databaseHelper.getMapMarkersDao();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public ArrayList<MyMapMarker> selectFromDatabase() {
        try {
            QueryBuilder<MyMapMarker, Integer> queryBuilder = mapMarkerDao.queryBuilder();
            PreparedQuery<MyMapMarker> preparedQuery = queryBuilder.prepare();
            return (ArrayList<MyMapMarker>) mapMarkerDao.query(preparedQuery);
        } catch (SQLException e) {
            e.printStackTrace();
            Log.e(DatabaseOperationManager.class.getName(),
                    " --- error retrieving data from the database ");
            return null;
        }
    }

    public boolean addRow(MyMapMarker myMapMarker) {
        try {
            mapMarkerDao.create(myMapMarker);
            Log.i(MarkersOperationManager.class.getName(),
                    " --- successfully added record ");
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            Log.e(MarkersOperationManager.class.getName(),
                    " --- error adding record ");
            return false;
        }
    }

    public boolean deleteRow(int id) {
        try {
            Dao<MyMapMarker, Integer> dao = mapMarkerDao;
            DeleteBuilder<MyMapMarker, Integer> deleteBuilder = dao.deleteBuilder();
            deleteBuilder.where().eq("id", id);
            deleteBuilder.delete();
            Log.i(MarkersOperationManager.class.getName(),
                    " --- successfully deleted record ");
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            Log.e(MarkersOperationManager.class.getName(),
                    " --- error deleting record ");
            return false;
        }
    }

    public boolean updateRow(MyMapMarker myMapMarker) {
        try {
            int id = myMapMarker.getId();
            UpdateBuilder<MyMapMarker, Integer> updateBuilder = mapMarkerDao.updateBuilder();
            updateBuilder.where().eq("id", id);
            updateBuilder.updateColumnValue("latitude", myMapMarker.getLatitude());
            updateBuilder.updateColumnValue("longitude", myMapMarker.getLongitude());
            updateBuilder.updateColumnValue("title", myMapMarker.getTitle());
            updateBuilder.updateColumnValue("description", myMapMarker.getSnippet());
            updateBuilder.update();
            Log.i(MarkersOperationManager.class.getName(),
                    " --- successfully updated record ");
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            Log.e(MarkersOperationManager.class.getName(),
                    " --- error updating record ");
            return false;
        }
    }
}
