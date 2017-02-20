package com.mstudenets.studenetsbananaapp.controller.fruits;


import android.content.Context;
import android.widget.Toast;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.mstudenets.studenetsbananaapp.model.Fruit;

import java.sql.SQLException;
import java.util.ArrayList;

class DatabaseOperationManager
{
    private final Context context;
    private Dao<Fruit, Integer> fruitDao;
    private final DatabaseHelper databaseHelper;
    private ArrayList<Fruit> fruits;

    public DatabaseOperationManager(Context context) {
        this.context = context;
        databaseHelper = new DatabaseHelper(context);
        try {
            fruitDao = databaseHelper.getFruitDao();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public Dao<Fruit, Integer> getFruitDao() {
        try {
            return databaseHelper.getFruitDao();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    private ArrayList<Fruit> selectFromDatabase() {
        try {
            QueryBuilder<Fruit, Integer> queryBuilder = fruitDao.queryBuilder();
            PreparedQuery<Fruit> preparedQuery = queryBuilder.prepare();
            fruits = (ArrayList<Fruit>) fruitDao.query(preparedQuery);
            return fruits;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public void addRow(Fruit fruit) {
        try {
            fruitDao.create(fruit);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public boolean addRowOptimized(Fruit fruit) {
        try {
            fruitDao.create(fruit);
            Toast.makeText(context, "Successfully added row to DB", Toast.LENGTH_SHORT).show();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void deleteRow(int id) {
        try {
            Dao<Fruit, Integer> dao = fruitDao;
            DeleteBuilder<Fruit, Integer> deleteBuilder = dao.deleteBuilder();
            deleteBuilder.where().eq("id", id);
            deleteBuilder.delete();
            fruits = selectFromDatabase();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public boolean deleteRowOptimized(int id) {
        try {
            Dao<Fruit, Integer> dao = fruitDao;
            DeleteBuilder<Fruit, Integer> deleteBuilder = dao.deleteBuilder();
            deleteBuilder.where().eq("id", id);
            deleteBuilder.delete();
            Toast.makeText(context, "Successfully deleted row from DB", Toast.LENGTH_SHORT).show();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void updateRow(int id, Fruit fruit) {
        try {
            UpdateBuilder<Fruit, Integer> updateBuilder = fruitDao.updateBuilder();
            updateBuilder.where().eq("id", id);
            updateBuilder.updateColumnValue("name", fruit.getName());
            updateBuilder.updateColumnValue("country", fruit.getCountry());
            updateBuilder.updateColumnValue("price", fruit.getPrice());
            updateBuilder.update();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean updateRowOptimized(Fruit fruit) {
        try {
            int id = fruit.getId();
            UpdateBuilder<Fruit, Integer> updateBuilder = fruitDao.updateBuilder();
            updateBuilder.where().eq("id", id);
            updateBuilder.updateColumnValue("name", fruit.getName());
            updateBuilder.updateColumnValue("country", fruit.getCountry());
            updateBuilder.updateColumnValue("price", fruit.getPrice());
            updateBuilder.update();
            Toast.makeText(context, "Successfully updated row", Toast.LENGTH_SHORT).show();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
