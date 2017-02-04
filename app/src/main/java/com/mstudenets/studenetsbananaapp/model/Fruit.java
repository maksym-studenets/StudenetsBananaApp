package com.mstudenets.studenetsbananaapp.model;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "fruits")
public class Fruit
{
    @DatabaseField(columnName = "id", generatedId = true)
    private int id;

    @DatabaseField(columnName = "name", dataType = DataType.STRING, canBeNull = false)
    private String name;

    @DatabaseField(columnName = "country", dataType = DataType.STRING)
    private String country;

    @DatabaseField(columnName = "price", dataType = DataType.DOUBLE)
    private double price;

    public Fruit() {
    }

    public Fruit(int id, String name, String country, double price) {
        this.id = id;
        this.name = name;
        this.country = country;
        this.price = price;
    }

    public Fruit(String name, String country, double price) {

        this.name = name;
        this.country = country;
        this.price = price;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
