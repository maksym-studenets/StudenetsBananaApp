package com.mstudenets.studenetsbananaapp.view.fragments;


import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mstudenets.studenetsbananaapp.R;
import com.mstudenets.studenetsbananaapp.controller.contacts.MyContactsAdapter;
import com.mstudenets.studenetsbananaapp.model.Contact;

import java.util.ArrayList;


public class ContactBookFragment extends Fragment /*implements
        LoaderManager.LoaderCallbacks<Cursor>,
        AdapterView.OnItemClickListener*/
{
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
        initializeRecyclerView();

        return view;
    }

    public ArrayList<Contact> getPhonebookContacts() {
        return phonebookContacts;
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

    private void retrieveContacts() throws NullPointerException {
        ContentResolver contentResolver = getActivity().getContentResolver();
        Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);
        if (cursor.getCount() > 0) {
            int i = 0;
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
    }
}
