package com.mstudenets.studenetsbananaapp.controller.database;


import android.content.Context;
import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.mstudenets.studenetsbananaapp.App;
import com.mstudenets.studenetsbananaapp.model.Contact;
import com.mstudenets.studenetsbananaapp.model.MyMapMarker;

import java.sql.SQLException;
import java.util.ArrayList;

public class DatabaseOperationManager
{
    private Dao<Contact, Integer> contactDao;
    private Dao<MyMapMarker, Integer> mapMarkerDao;
    private DatabaseHelper databaseHelper = App.getDatabaseHelper();

    public DatabaseOperationManager(Context context) {
        try {
            contactDao = databaseHelper.getContactsDao();
            mapMarkerDao = databaseHelper.getMapMarkersDao();
        } catch (SQLException e) {
            e.printStackTrace();
            Log.e(DatabaseOperationManager.class.getName(),
                    " --- error getting Dao object");
            throw new RuntimeException(e);
        }
    }

    public Dao<Contact, Integer> getContactDao() {
        try {
            return databaseHelper.getContactsDao();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
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

    public ArrayList<Contact> selectContactsFromDatabase() {
        try {
            QueryBuilder<Contact, Integer> queryBuilder = contactDao.queryBuilder();
            PreparedQuery<Contact> preparedQuery = queryBuilder.prepare();
            return (ArrayList<Contact>) contactDao.query(preparedQuery);
        } catch (SQLException e) {
            e.printStackTrace();
            Log.e(DatabaseOperationManager.class.getName(),
                    " --- error retrieving data from the database ");
            return null;
            //throw new RuntimeException(e);
        }
    }

    public ArrayList<MyMapMarker> selectMarkersFromDatabase() {
        try {
            QueryBuilder<MyMapMarker, Integer> queryBuilder = mapMarkerDao.queryBuilder();
            PreparedQuery<MyMapMarker> preparedQuery = queryBuilder.prepare();
            return (ArrayList<MyMapMarker>) mapMarkerDao.query(preparedQuery);
        } catch (SQLException e) {
            e.printStackTrace();
            Log.e(DatabaseOperationManager.class.getName(),
                    " --- error retrieving data from the database ");
            return null;
            //throw new RuntimeException(e);
        }
    }

    public boolean addContact(Contact contact) {
        try {
            contactDao.create(contact);
            Log.i(DatabaseOperationManager.class.getName(),
                    " --- successfully added contact ");
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            Log.e(DatabaseOperationManager.class.getName(),
                    " --- error adding contact ");
            return false;
        }
    }

    public boolean addMarker(MyMapMarker mapMarker) {
        try {
            mapMarkerDao.create(mapMarker);
            Log.i(DatabaseOperationManager.class.getName(),
                    " --- successfully added map marker ");
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            Log.e(DatabaseOperationManager.class.getName(),
                    " --- error adding marker ");
            return false;
        }
    }

    public boolean deleteContact(int id) {
        try {
            Dao<Contact, Integer> dao = contactDao;
            DeleteBuilder<Contact, Integer> deleteBuilder = dao.deleteBuilder();
            deleteBuilder.where().eq("id", id);
            deleteBuilder.delete();
            Log.i(DatabaseOperationManager.class.getName(),
                    " --- successfully deleted contact ");
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            Log.e(DatabaseOperationManager.class.getName(),
                    " --- error deleting record ");
            return false;
        }
    }

    public boolean deleteMarker(int id) {
        try {
            Dao<MyMapMarker, Integer> dao = mapMarkerDao;
            DeleteBuilder<MyMapMarker, Integer> deleteBuilder = dao.deleteBuilder();
            deleteBuilder.where().eq("id", id);
            deleteBuilder.delete();
            Log.i(DatabaseOperationManager.class.getName(),
                    " --- successfully deleted marker ");
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            Log.e(DatabaseOperationManager.class.getName(),
                    " --- error deleting marker ");
            return false;
        }
    }

    public boolean updateContact(Contact contact) {
        try {
            int id = contact.getId();
            UpdateBuilder<Contact, Integer> updateBuilder = contactDao.updateBuilder();
            updateBuilder.where().eq("id", id);
            updateBuilder.updateColumnValue("name", contact.getName());
            updateBuilder.updateColumnValue("phone", contact.getPhoneNumber());
            updateBuilder.update();
            Log.i(DatabaseOperationManager.class.getName(),
                    " --- successfully updated record ");
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            Log.e(DatabaseOperationManager.class.getName(),
                    " --- error updating record ");
            return false;
        }
    }

    public boolean updateMarker(MyMapMarker mapMarker) {
        try {
            int id = mapMarker.getId();
            UpdateBuilder<Contact, Integer> updateBuilder = contactDao.updateBuilder();
            updateBuilder.where().eq("id", id);
            updateBuilder.updateColumnValue("latitude", mapMarker.getLatitude());
            updateBuilder.updateColumnValue("longitude", mapMarker.getLongitude());
            updateBuilder.updateColumnValue("title", mapMarker.getTitle());
            updateBuilder.updateColumnValue("description", mapMarker.getDescription());
            updateBuilder.update();
            Log.i(DatabaseOperationManager.class.getName(),
                    " --- successfully updated marker ");
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            Log.e(DatabaseOperationManager.class.getName(),
                    " --- error updating marker ");
            return false;
        }
    }
}
