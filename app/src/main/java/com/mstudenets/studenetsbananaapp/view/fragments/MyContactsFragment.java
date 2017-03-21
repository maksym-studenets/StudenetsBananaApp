package com.mstudenets.studenetsbananaapp.view.fragments;


import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.mstudenets.studenetsbananaapp.R;
import com.mstudenets.studenetsbananaapp.controller.MyContactsAdapter;
import com.mstudenets.studenetsbananaapp.controller.database.DatabaseOperationManager;
import com.mstudenets.studenetsbananaapp.model.Contact;

import java.util.ArrayList;


public class MyContactsFragment extends ContactsFragment
{
    private ArrayList<Contact> myContacts = new ArrayList<>();
    private DatabaseOperationManager operationManager;
    private ContactsFragment parentFragment;
    private MyContactsAdapter adapter;
    private RecyclerView contactsView;
    private FloatingActionButton fab;
    private AlertDialog.Builder addDialog;
    private SearchView searchView;
    private View view;
    private EditText nameEdit, phoneEdit;

    private boolean hasCallPhonePermission;
    private String phoneNumber;

    /**
     * Required public constructor
     */
    public MyContactsFragment() {
        setHasOptionsMenu(true);
        this.parentFragment = (ContactsFragment) getParentFragment();
    }

    /**
     * Required method to create fragment with a view.
     * Additionally, this method initializes {@link RecyclerView} components
     * and basic actions like item decorations, swipe actions etc. Sets click listener
     * for the floating action button to add new contact to the database and display it in
     * a RecyclerView
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_contacts, parent, false);
        fab = (FloatingActionButton) view
                .findViewById(R.id.my_contacts_fab_add);
        contactsView = (RecyclerView) view.findViewById(R.id.my_contacts_recyclerview);

        operationManager = new DatabaseOperationManager();
        operationManager.getContactDao();
        myContacts = operationManager.selectContactsFromDatabase();

        initializeRecyclerView();
        initializeDialog();
        addFabCloseAction();
        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                removeView();
                addDialog.setTitle(R.string.dialog_add_title);
                addDialog.show();
            }
        });

        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        searchView.setVisibility(View.INVISIBLE);
    }

    /**
     * Adds search functionality to the fragment
     *
     * @since 1.3
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {

        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);

        SearchManager searchManager = (SearchManager)
                getActivity().getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.menu_search_button).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(
                getActivity().getComponentName()));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener()
        {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.length() > 0) {
                    newText = newText.toLowerCase();
                    adapter.getFilter().filter(newText);
                }
                contactsView.scrollToPosition(0);
                return false;
            }
        });
    }

    @Override
    public void callPhone(String phoneNumber) {
        super.callPhone(phoneNumber);
        /*
        this.phoneNumber = phoneNumber;
        checkCallPhonePermission();
        if (hasCallPhonePermission) {
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:" + phoneNumber));
            startActivity(callIntent);
        }
        */
    }

    private void initializeRecyclerView() {
        adapter = new MyContactsAdapter(myContacts, getContext(), this, true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        contactsView.setLayoutManager(layoutManager);
        DividerItemDecoration itemDecoration = new DividerItemDecoration(getContext(),
                layoutManager.getOrientation());
        contactsView.addItemDecoration(itemDecoration);
        contactsView.setAdapter(adapter);
        initializeSwipeAction();
        //registerForContextMenu(contactsView);
    }

    private void initializeDialog() {
        addDialog = new AlertDialog.Builder(getContext());
        view = getActivity().getLayoutInflater().inflate(R.layout.main_contact_dialog, null);
        addDialog.setView(view);

        nameEdit = (EditText) view.findViewById(R.id.dialog_contacts_edit_name);
        phoneEdit = (EditText) view.findViewById(R.id.dialog_contacts_edit_phone);

        addDialog.setPositiveButton(R.string.dialog_positive_button,
                new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String name = nameEdit.getText().toString();
                        String phone = phoneEdit.getText().toString();
                        Contact contact = new Contact(name, phone);
                        boolean isSuccessful = operationManager.addContact(contact);
                        if (isSuccessful) {
                            adapter.addItem(contact);
                            dialog.dismiss();
                        } else {
                            dialog.dismiss();
                        }
                        nameEdit.setText("");
                        phoneEdit.setText("");
                    }
                });
        addDialog.setNegativeButton(R.string.dialog_negative_button,
                new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        nameEdit.setText("");
                        phoneEdit.setText("");
                    }
                });
    }

    private void initializeSwipeAction() {
        ItemTouchHelper.SimpleCallback simpleCallback =
                new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT)
                {
                    @Override
                    public boolean onMove(RecyclerView recyclerView,
                                          RecyclerView.ViewHolder viewHolder,
                                          RecyclerView.ViewHolder target) {
                        return false;
                    }

                    @Override
                    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                        int position = viewHolder.getAdapterPosition();
                        int id = myContacts.get(position).getId();
                        boolean isSuccessful = operationManager.deleteContact(id);
                        if (isSuccessful)
                            adapter.removeItem(position);
                        else
                            Toast.makeText(getContext(), "Failed to delete", Toast.LENGTH_SHORT)
                                    .show();
                    }
                };
        ItemTouchHelper touchHelper = new ItemTouchHelper(simpleCallback);
        touchHelper.attachToRecyclerView(contactsView);
    }

    private void addFabCloseAction() {
        contactsView.addOnScrollListener(new RecyclerView.OnScrollListener()
        {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    fab.hide();
                } else if (dy < 0) {
                    fab.show();
                }
            }
        });
    }

    private void removeView() {
        if (view.getParent() != null)
            ((ViewGroup) view.getParent()).removeView(view);
    }
}
