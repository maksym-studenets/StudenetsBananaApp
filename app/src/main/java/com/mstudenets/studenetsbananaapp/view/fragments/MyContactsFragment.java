package com.mstudenets.studenetsbananaapp.view.fragments;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.mstudenets.studenetsbananaapp.R;
import com.mstudenets.studenetsbananaapp.controller.contacts.MyContactsAdapter;
import com.mstudenets.studenetsbananaapp.controller.database.DatabaseOperationManager;
import com.mstudenets.studenetsbananaapp.model.Contact;

import java.util.ArrayList;


public class MyContactsFragment extends ContactsFragment
{
    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 110;

    private ArrayList<Contact> myContacts = new ArrayList<>();
    private DatabaseOperationManager operationManager;
    private MyContactsAdapter adapter;
    private RecyclerView contactsView;
    private AlertDialog.Builder alertDialog;
    private View view;
    private EditText nameEdit, phoneEdit;
    private boolean add = false;
    private int editPosition;

    public MyContactsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_contacts, parent, false);
        FloatingActionButton fab = (FloatingActionButton) view
                .findViewById(R.id.my_contacts_fab_add);
        contactsView = (RecyclerView) view.findViewById(R.id.my_contacts_recyclerview);

        operationManager = new DatabaseOperationManager(getContext());
        operationManager.getContactDao();

        myContacts = operationManager.selectContactsFromDatabase();

        initializeRecyclerView();
        initializeDialog();

        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                add = true;
                removeView();
                alertDialog.setTitle(R.string.dialog_add_title);
                alertDialog.show();
            }
        });

        return view;
    }

    public ArrayList<Contact> getMyContacts() {
        return myContacts;
    }

    private void initializeRecyclerView() {
        adapter = new MyContactsAdapter(myContacts, getContext(), this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        contactsView.setLayoutManager(layoutManager);
        DividerItemDecoration itemDecoration = new DividerItemDecoration(getContext(),
                layoutManager.getOrientation());
        contactsView.addItemDecoration(itemDecoration);
        adapter.createHelperCallback();
        contactsView.setAdapter(adapter);
    }

    private void initializeDialog() {
        alertDialog = new AlertDialog.Builder(getContext());
        view = getActivity().getLayoutInflater().inflate(R.layout.main_contact_dialog, null);
        alertDialog.setView(view);

        nameEdit = (EditText) view.findViewById(R.id.dialog_contacts_edit_name);
        phoneEdit = (EditText) view.findViewById(R.id.dialog_contacts_edit_phone);

        alertDialog.setPositiveButton(R.string.dialog_positive_button,
                new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String name = nameEdit.getText().toString();
                        String phone = phoneEdit.getText().toString();

                        if (add) {
                            add = false;
                            Contact contact = new Contact(name, phone);
                            boolean isSuccessful = operationManager.addContact(contact);
                            if (isSuccessful) {
                                adapter.addItem(contact);
                                dialog.dismiss();
                            }
                            nameEdit.setText("");
                            phoneEdit.setText("");
                        } else {
                            int id = myContacts.get(editPosition).getId();
                            Contact contact = new Contact(id, name, phone);
                            boolean isSuccessful = operationManager.updateContact(contact);
                            if (isSuccessful) {
                                myContacts.set(editPosition, contact);
                                adapter.notifyDataSetChanged();
                                dialog.dismiss();
                            }

                            nameEdit.setText("");
                            phoneEdit.setText("");
                        }
                    }
                });
        alertDialog.setNegativeButton(R.string.dialog_negative_button,
                new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        nameEdit.setText("");
                        phoneEdit.setText("");
                    }
                });
    }

    private void removeView() {
        if (view.getParent() != null)
            ((ViewGroup) view.getParent()).removeView(view);
    }




    /*
    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 110;

    private ArrayList<Contact> myContacts = new ArrayList<>();
    private DatabaseOperationManager operationManager;
    private MyContactsAdapter adapter;
    private RecyclerView contactsView;
    private AlertDialog.Builder alertDialog;
    private View view;
    private EditText nameEdit, phoneEdit;
    private boolean add = false;
    private int editPosition;

    public MyContactsFragment() {
    }

    public ArrayList<Contact> getMyContacts() {
        return myContacts;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_my_contacts, container, false);

        FloatingActionButton fab = (FloatingActionButton) view
                .findViewById(R.id.my_contacts_fab_add);
        contactsView = (RecyclerView) view.findViewById(R.id.my_contacts_recyclerview);
        operationManager = new DatabaseOperationManager(getContext());
        operationManager.getContactDao();


        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                add = true;
                removeView();
                alertDialog.setTitle(R.string.dialog_add_title);
                alertDialog.show();
            }
        });

        initializeRecyclerView();
        initializeDialog();

        myContacts = operationManager.selectContactsFromDatabase();
        initializeRecyclerView();
        //checkPermissions();

        return view;
    }

    private void initializeRecyclerView() {
        adapter = new MyContactsAdapter(myContacts, getContext());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        contactsView.setLayoutManager(layoutManager);
        DividerItemDecoration itemDecoration = new DividerItemDecoration(getContext(),
                layoutManager.getOrientation());
        contactsView.addItemDecoration(itemDecoration);
        adapter.createHelperCallback();
        contactsView.setAdapter(adapter);
    }

    private void initializeDialog() {
        alertDialog = new AlertDialog.Builder(getContext());
        view = getActivity().getLayoutInflater().inflate(R.layout.main_contact_dialog, null);
        alertDialog.setView(view);

        nameEdit = (EditText) view.findViewById(R.id.dialog_contacts_edit_name);
        phoneEdit = (EditText) view.findViewById(R.id.dialog_contacts_edit_phone);

        alertDialog.setPositiveButton(R.string.dialog_positive_button,
                new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String name = nameEdit.getText().toString();
                        String phone = phoneEdit.getText().toString();

                        if (add) {
                            add = false;
                            Contact contact = new Contact(name, phone);
                            boolean isSuccessful = operationManager.addContact(contact);
                            if (isSuccessful) {
                                adapter.addItem(contact);
                                dialog.dismiss();
                            }
                            nameEdit.setText("");
                            phoneEdit.setText("");
                        } else {
                            int id = myContacts.get(editPosition).getId();
                            Contact contact = new Contact(id, name, phone);
                            boolean isSuccessful = operationManager.updateContact(contact);
                            if (isSuccessful) {
                                myContacts.set(editPosition, contact);
                                adapter.notifyDataSetChanged();
                                dialog.dismiss();
                            }

                            nameEdit.setText("");
                            phoneEdit.setText("");
                        }
                    }
                });
        alertDialog.setNegativeButton(R.string.dialog_negative_button,
                new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        nameEdit.setText("");
                        phoneEdit.setText("");
                    }
                });
    }

    private void removeView() {
        if (view.getParent() != null)
            ((ViewGroup) view.getParent()).removeView(view);
    }

    private void checkPermissions() throws NullPointerException {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.CALL_PHONE)) {
                ViewPager viewPager = (ViewPager)
                        getView().findViewById(R.id.fragment_contacts_viewpager);
                Snackbar.make(viewPager,
                        "The app needs a permission to read your phone's contacts",
                        Snackbar.LENGTH_LONG).show();
            } else {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[] {Manifest.permission.CALL_PHONE},
                        MY_PERMISSIONS_REQUEST_CALL_PHONE);
            }
        }
    }
    */
}
