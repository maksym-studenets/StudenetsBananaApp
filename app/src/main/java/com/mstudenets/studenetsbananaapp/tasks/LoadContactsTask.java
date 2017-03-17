package com.mstudenets.studenetsbananaapp.tasks;


import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.ContactsContract;

import com.mstudenets.studenetsbananaapp.model.Contact;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class LoadContactsTask extends AsyncTask<Void, Void, ArrayList<Contact>>
{
    private Context context;
    private ArrayList<Contact> contacts = new ArrayList<>();
    private ProgressDialog progressDialog;

    public LoadContactsTask(Context context) {
        this.context = context;
        progressDialog = new ProgressDialog(context);
    }

    @Override
    protected void onPreExecute() {
        progressDialog.setTitle("Loading contacts");
        progressDialog.setMessage("Please wait while we fetch your contacts list");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    @Override
    protected ArrayList<Contact> doInBackground(Void... params) {
        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);
        try {
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    String id = cursor.getString(cursor.getColumnIndex(ContactsContract
                            .Contacts._ID));
                    String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts
                            .DISPLAY_NAME));

                    if (cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts
                            .HAS_PHONE_NUMBER)) > 0) {
                        Cursor contactCursor = contentResolver.query(
                                ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                                new String[]{id}, null);
                        while (contactCursor.moveToNext()) {
                            String phoneNumber = contactCursor.getString(contactCursor
                                    .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            contacts.add(new Contact(name, phoneNumber));
                        }
                        contactCursor.close();
                    }
                }
            }
        cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Collections.sort(contacts, new ContactComparator());
        return contacts;
    }

    @Override
    protected void onPostExecute(ArrayList<Contact> contacts) {
        progressDialog.dismiss();
    }

    private class ContactComparator implements Comparator<Contact>
    {
        @Override
        public int compare(Contact o1, Contact o2) {
            return o1.getName().compareToIgnoreCase(o2.getName());
        }
    }
}
