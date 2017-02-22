package com.mstudenets.studenetsbananaapp.model;


import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Comparator;

@DatabaseTable(tableName = "contacts")
public class Contact implements Comparator<Contact>
{
    @DatabaseField(columnName = "id", dataType = DataType.INTEGER, generatedId = true)
    private int id;
    @DatabaseField(columnName = "name", dataType = DataType.STRING)
    private String name;
    @DatabaseField(columnName = "phone", dataType = DataType.STRING)
    private String phoneNumber;

    public Contact() {
    }

    public Contact(String name, String phoneNumber) {
        this.name = name;
        this.phoneNumber = phoneNumber;
    }

    public Contact(int id, String name, String phoneNumber) {
        this.id = id;
        this.name = name;
        this.phoneNumber = phoneNumber;
    }

    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    @Override
    public int compare(Contact o1, Contact o2) {
        return o1.name.compareToIgnoreCase(o2.name);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
