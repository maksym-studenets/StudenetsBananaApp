package com.mstudenets.studenetsbananaapp.model;


import java.util.Comparator;

public class Contact implements Comparator<Contact>
{
    private String name;
    private String phoneNumber;

    public Contact() {
    }

    public Contact(String name, String phoneNumber) {
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
}
