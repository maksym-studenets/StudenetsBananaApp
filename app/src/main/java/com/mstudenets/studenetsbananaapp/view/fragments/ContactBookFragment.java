package com.mstudenets.studenetsbananaapp.view.fragments;


import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.mstudenets.studenetsbananaapp.R;
import com.mstudenets.studenetsbananaapp.controller.contacts.MyContactsAdapter;
import com.mstudenets.studenetsbananaapp.model.Contact;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.ExecutionException;


public class ContactBookFragment extends ContactsFragment /*implements
        LoaderManager.LoaderCallbacks<Cursor>,
        AdapterView.OnItemClickListener*/
{
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 100;


    private ArrayList<Contact> phoneBookContacts = new ArrayList<>();
    private MyContactsAdapter adapter;
    private RecyclerView contactView;

    public ContactBookFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_contact_book, container, false);
        contactView = (RecyclerView) view.findViewById(R.id.contact_book_recyclerview);
        initializeRecyclerView();

        try {
            phoneBookContacts = new AsyncTask<Void, Void, ArrayList<Contact>>()
            {
                ProgressDialog progressDialog;

                @Override
                protected void onPreExecute() {
                    progressDialog = ProgressDialog.show(getContext(), "Loading contacts",
                            "Please wait while we fetch your data", false, true);
                }

                @Override
                protected ArrayList<Contact> doInBackground(Void... params) {
                    ContentResolver contentResolver = getContext().getContentResolver();
                    Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI,
                            null, null, null, null);
                    if (cursor.getCount() > 0) {
                        while (cursor.moveToNext()) {
                            String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                            String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.
                                    DISPLAY_NAME));

                            if (cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts
                                    .HAS_PHONE_NUMBER)) > 0) {
                                Cursor contactCursor = contentResolver.query(
                                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                                        new String[]{id}, null);
                                while (contactCursor.moveToNext()) {
                                    String phoneNumber = contactCursor.getString(contactCursor.getColumnIndex(
                                            ContactsContract.CommonDataKinds.Phone.NUMBER));
                                    phoneBookContacts.add(new Contact(name, phoneNumber));
                                }
                                contactCursor.close();
                            }
                        }
                    }
                    cursor.close();
                    Collections.sort(phoneBookContacts, new ContactComparator());
                    return phoneBookContacts;
                }

                @Override
                protected void onPostExecute(ArrayList<Contact> contacts) {
                    super.onPostExecute(contacts);
                    progressDialog.dismiss();
                    initializeRecyclerView();
                }
            }.execute().get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Something went wrong. Contacts fetching failed",
                    Toast.LENGTH_LONG).show();
        }

        /*
        LoadContactsTask loadContactsTask = new LoadContactsTask(getContext());
        loadContactsTask.execute();
        try {
            phoneBookContacts = loadContactsTask.get();
            initializeRecyclerView();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Operation failed", Toast.LENGTH_SHORT).show();
        }
        */

        //retrieveContacts();

        return view;
    }

    public ArrayList<Contact> getPhoneBookContacts() {
        return phoneBookContacts;
    }

    private void initializeRecyclerView() {
        adapter = new MyContactsAdapter(phoneBookContacts, getContext(), this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        DividerItemDecoration itemDecoration = new DividerItemDecoration(getContext(),
                layoutManager.getOrientation());
        contactView.addItemDecoration(itemDecoration);
        contactView.setLayoutManager(layoutManager);
        contactView.setAdapter(adapter);
    }

    private void retrieveContacts() {

    }

    private class ContactComparator implements Comparator<Contact>
    {

        @Override
        public int compare(Contact o1, Contact o2) {
            return o1.getName().compareToIgnoreCase(o2.getName());
        }
    }

    /*
    private void retrieveContacts() throws NullPointerException {
        ContentResolver contentResolver = getActivity().getContentResolver();
        Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);
        if (cursor.getCount() > 0) {
            //int i = 0;
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
                        phoneBookContacts.add(new Contact(name, phoneNumber));
                    }
                }
            }
        }
        //phoneCursor.close();
        cursor.close();
    }
*/


    /*
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 100;

    private ArrayList<Contact> phonebookContacts = new ArrayList<>();
    private MyContactsAdapter adapter;
    private RecyclerView contactView;

    public ContactBookFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_contact_book, container, false);
        retrieveContacts();
        contactView = (RecyclerView) view.findViewById(R.id.contact_book_recyclerview);
        //contactView = (ListView) view.findViewById(R.id.contact_book_recyclerview);
        //initializeRecyclerView();

        checkContactsPermission();

        return view;
    }

    public ArrayList<Contact> getPhonebookContacts() {
        return phonebookContacts;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS:
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initializeRecyclerView();
                } else {
                    Toast.makeText(getContext(), "Permission denied", Toast.LENGTH_SHORT).show();
                }
                break;

            case MY_PERMISSIONS_REQUEST_CALL_PHONE:
                ImageButton callButton = (ImageButton)
                        view.findViewById(R.id.contact_item_call_button);
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (callButton != null)
                        callButton.setEnabled(true);
                    Toast.makeText(getContext(), "Permission was granted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Permission denied", Toast.LENGTH_SHORT).show();
                    if (callButton != null)
                        callButton.setEnabled(false);
                }

            default:
                break;
        }
    }

    private void initializeRecyclerView() {
        adapter = new MyContactsAdapter(phonebookContacts, getContext());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        DividerItemDecoration itemDecoration = new DividerItemDecoration(getContext(),
                layoutManager.getOrientation());
        contactView.addItemDecoration(itemDecoration);
        contactView.setLayoutManager(layoutManager);
        contactView.setAdapter(adapter);
    }

    private void checkContactsPermission() throws NullPointerException {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.READ_CONTACTS)) {
                Toast.makeText(getContext(), "We need the permission to retrieve your" +
                        " contacts list", Toast.LENGTH_LONG).show();
            } else {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[] {Manifest.permission.READ_CONTACTS},
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);
            }
        }
    }

    private void retrieveContacts() throws NullPointerException {
        ContentResolver contentResolver = getActivity().getContentResolver();
        Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);
        if (cursor.getCount() > 0) {
            //int i = 0;
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
                        phoneCursor.close();
                    }
                }
            }
        }
        //phoneCursor.close();
        cursor.close();
    }
    */
}
