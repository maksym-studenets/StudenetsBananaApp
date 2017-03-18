package com.mstudenets.studenetsbananaapp.view.fragments;


import android.Manifest;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.mstudenets.studenetsbananaapp.R;
import com.mstudenets.studenetsbananaapp.controller.MyContactsAdapter;
import com.mstudenets.studenetsbananaapp.model.Contact;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class ContactBookFragment extends ContactsFragment
{
    public static final int PERMISSION_REQUEST_READ_CONTACTS = 101;

    private boolean hasContactsPermission;

    private ArrayList<Contact> phoneBookContacts = new ArrayList<>();
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

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            hasContactsPermission = true;
        } else {
            checkContactsPermission();
        }
        retrieveContacts();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        checkContactsPermission();
    }

    public ArrayList<Contact> getPhoneBookContacts() {
        return phoneBookContacts;
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_READ_CONTACTS) {
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                hasContactsPermission = true;
                retrieveContacts();
            } else {
                hasContactsPermission = false;
                Toast.makeText(getContext(), "Permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void initializeRecyclerView() {
        MyContactsAdapter adapter = new MyContactsAdapter(phoneBookContacts, getContext(), false);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        DividerItemDecoration itemDecoration = new DividerItemDecoration(getContext(),
                layoutManager.getOrientation());
        contactView.addItemDecoration(itemDecoration);
        contactView.setLayoutManager(layoutManager);
        contactView.setAdapter(adapter);
    }

    private void checkContactsPermission() {
        if (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.READ_CONTACTS)) {
                Toast.makeText(getContext(),
                        "We need this permission to display your contacts list",
                        Toast.LENGTH_LONG).show();
            } else {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[] {Manifest.permission.READ_CONTACTS},
                        PERMISSION_REQUEST_READ_CONTACTS);
            }
        } else {
            hasContactsPermission = true;
            retrieveContacts();
        }
    }

    private void retrieveContacts() {
        if (hasContactsPermission) {
            LoadContactsTask loadContactsTask = new LoadContactsTask(getContext());
            try {
                loadContactsTask.execute();
                initializeRecyclerView();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "Error executing task", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getContext(), "Permission was denied", Toast.LENGTH_LONG).show();
        }
    }

    private class LoadContactsTask extends AsyncTask<Void, Void, Void>
    {
        private Context context;
        private ArrayList<Contact> contacts = new ArrayList<>();
        private ProgressDialog progressDialog;

        LoadContactsTask(Context context) {
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
        protected Void doInBackground(Void... params) {
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
            return null;
        }


        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            phoneBookContacts = contacts;
            initializeRecyclerView();
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

    /*
    private class SetAdapterTask extends AsyncTask<Void, Void, Void> {
        MyContactsAdapter adapter;
        LinearLayoutManager layoutManager;
        DividerItemDecoration itemDecoration;
        @Override
        protected Void doInBackground(Void... params) {
            adapter = new MyContactsAdapter(phoneBookContacts, getContext(), false);
            layoutManager = new LinearLayoutManager(getContext());
            itemDecoration = new DividerItemDecoration(getContext(),
                    layoutManager.getOrientation());
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            contactView.addItemDecoration(itemDecoration);
            contactView.setLayoutManager(layoutManager);
            contactView.setAdapter(adapter);
        }
    }
    */
}
