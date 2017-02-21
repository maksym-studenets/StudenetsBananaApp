package com.mstudenets.studenetsbananaapp.tasks;


import android.content.ContentResolver;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.ContactsContract;

import com.mstudenets.studenetsbananaapp.model.Contact;

import java.util.ArrayList;

public class LoadContactsTask extends AsyncTask<Void, Void, ArrayList<Contact>>
{
    public LoadContactsTask() {

    }

    @Override
    protected ArrayList<Contact> doInBackground(Void... params) {
        /*
        ContentResolver contentResolver = getActivity().getContentResolver();
        Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.
                        DISPLAY_NAME));

                if (cursor.getInt(cursor.getColumnIndex(
                        ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    Cursor phoneCursor = contentResolver.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    while (phoneCursor.moveToNext()) {
                        String phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER));
                        phonebookContacts.add(new Contact(name, phoneNumber));
                    }
                }
            }
        }
        //phoneCursor.close();
        cursor.close();
        */
        return null;
    }
}
