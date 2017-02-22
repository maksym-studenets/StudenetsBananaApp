package com.mstudenets.studenetsbananaapp.controller.contacts;


import android.content.Context;
import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.mstudenets.studenetsbananaapp.model.Contact;

import java.sql.SQLException;
import java.util.ArrayList;

public class DatabaseOperationManager
{
    private final Context context;
    private Dao<Contact, Integer> contactDao;
    private final DatabaseHelper databaseHelper;
    private ArrayList<Contact> contacts;

    public DatabaseOperationManager(Context context) {
        this.context = context;
        databaseHelper = new DatabaseHelper(context);
        try {
            contactDao = databaseHelper.getContactsDao();
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

    public ArrayList<Contact> selectFromDatabase() {
        try {
            QueryBuilder<Contact, Integer> queryBuilder = contactDao.queryBuilder();
            PreparedQuery<Contact> preparedQuery = queryBuilder.prepare();
            contacts = (ArrayList<Contact>) contactDao.query(preparedQuery);
            return contacts;
        } catch (SQLException e) {
            e.printStackTrace();
            Log.e(DatabaseOperationManager.class.getName(),
                    " --- error retrieving data from the database ");
            throw new RuntimeException(e);
        }
    }

    public boolean addRow(Contact contact) {
        try {
            contactDao.create(contact);
            Log.i(DatabaseOperationManager.class.getName(),
                    " --- successfully added record ");
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            Log.e(DatabaseOperationManager.class.getName(),
                    " --- error adding record ");
            return false;
        }
    }

    public boolean deleteRow(int id) {
        try {
            Dao<Contact, Integer> dao = contactDao;
            DeleteBuilder<Contact, Integer> deleteBuilder = dao.deleteBuilder();
            deleteBuilder.where().eq("id", id);
            deleteBuilder.delete();
            Log.i(DatabaseOperationManager.class.getName(),
                    " --- successfully deleted record ");
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            Log.e(DatabaseOperationManager.class.getName(),
                    " --- error deleting record ");
            return false;
        }
    }

    public boolean updateRow(Contact contact) {
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
}
