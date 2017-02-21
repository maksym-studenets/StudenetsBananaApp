package com.mstudenets.studenetsbananaapp.model;


import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "contacts")
public class MyContact
{
    @DatabaseField(columnName = "name", dataType = DataType.STRING)
    private String name;

    @DatabaseField(columnName = "name", dataType = DataType.STRING)
    private String phoneNumber;

    @DatabaseField(columnName = "name", dataType = DataType.STRING)
    private String email;

    public MyContact() {
    }

    public MyContact(String name, String phoneNumber, String email) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
