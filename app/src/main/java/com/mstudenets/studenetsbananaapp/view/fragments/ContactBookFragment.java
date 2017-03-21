package com.mstudenets.studenetsbananaapp.view.fragments;


import android.Manifest;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.app.SearchManager;
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
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.mstudenets.studenetsbananaapp.R;
import com.mstudenets.studenetsbananaapp.controller.MyContactsAdapter;
import com.mstudenets.studenetsbananaapp.model.Contact;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


/**
 * Fragment that displays contacts from the device's contact book. For these contacts
 * only call action is available. The user is not allowed to modify or delete contact
 * records. Requires {@code READ_CONTACTS} permission to display contacts and
 * {@code CALL_PHONE} to make phone calls to selected contacts.
 * Contacts list is fetched using {@link AsyncTask} to prevent blocking UI thread.
 */
public class ContactBookFragment extends ContactsFragment
{
    //public static final int PERMISSION_REQUEST_READ_CONTACTS = 101;
    private boolean hasContactsPermission;
    private boolean hasCallPhonePermission;
    private String phoneNumber;

    private ArrayList<Contact> phoneBookContacts = new ArrayList<>();
    private ArrayList<Contact> filteredContacts = new ArrayList<>();
    private MyContactsAdapter adapter;
    private SearchView searchView;
    private RecyclerView contactView;

    /**
     * Default constructor for the fragment. Sets {@code setHasOptionsMenu()} to true
     * in order to display search icon on the application bar.
     */
    public ContactBookFragment() {
        setHasOptionsMenu(true);
    }

    /**
     * Inflates fragment view and checks required permissions to fetch user's contacts.
     * If the permission was granted, fetches contacts using {@link AsyncTask}
     *
     * @return view to be inflated to the fragment
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_contact_book, container, false);
        contactView = (RecyclerView) view.findViewById(R.id.contact_book_recyclerview);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            hasContactsPermission = true;
            hasCallPhonePermission = true;
        } else {
            hasContactsPermission = false;
            checkContactsPermission();
            //retrieveContacts();
        }

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (searchView != null)
            searchView.removeView(searchView);
        //searchView.setVisibility(View.INVISIBLE);

    }

    /**
     * Called when fragment resumes after being paused. Checks {@code READ_CONTACTS}
     * permissions in case the user revoked it leaving the application running in the background
     */
    @Override
    public void onResume() {
        super.onResume();
        checkContactsPermission();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        searchView.setVisibility(View.INVISIBLE);
    }

    /**
     * Sets search options menu and handles search query inputs. After receiving query,
     * passes to {@link MyContactsAdapter} {@code Filter} to filter {@link RecyclerView}'s
     * content.
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
        searchView.setVisibility(View.INVISIBLE);

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
                contactView.scrollToPosition(0);
                return false;
            }
        });
    }

    /**
     * Returns list of device's contacts
     *
     * @return device phone book contacts
     */
    public ArrayList<Contact> getPhoneBookContacts() {
        return phoneBookContacts;
    }

    /**
     * Called when the app receives a result of a permission request (on API 23 and higher).
     * Retrieves and displays device phone book contacts if the permission is granted
     * or displays a {@link Toast} message with details if the permission was denied
     */
    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_READ_CONTACTS:
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    hasContactsPermission = true;
                    retrieveContacts();
                } else {
                    hasContactsPermission = false;
                    Toast.makeText(getContext(), "Permission denied", Toast.LENGTH_LONG).show();
                }
                break;
            case PERMISSION_REQUEST_CALL_PHONE:
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    hasCallPhonePermission = true;
                    callPhone(phoneNumber);
                } else {
                    hasCallPhonePermission = false;
                    Toast.makeText(getContext(), "Permission denied", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    /**
     * Checks permission to make phone calls (API 23 and higher). If permission is granted,
     * creates an Intent to perform a phone call.
     *
     * @param phoneNumber contact's telephone number as obtained from the phonebook
     *                    of from the user's input
     * @throws SecurityException on Marshmallow and higher ig
     */
    @Override
    public void callPhone(String phoneNumber) {
        super.callPhone(phoneNumber);
    }

    /**
     * Creates new {@link MyContactsAdapter} adapter. Sets layout manager for contacts
     * {@link RecyclerView}. Adds horizontal item decoration to the contacts view.
     * Sets new {@link MyContactsAdapter} as a {@link RecyclerView} adapter.
     */
    private void initializeRecyclerView() {
        adapter = new MyContactsAdapter(phoneBookContacts, getContext(),
                this, false);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        DividerItemDecoration itemDecoration = new DividerItemDecoration(getContext(),
                layoutManager.getOrientation());
        contactView.addItemDecoration(itemDecoration);
        contactView.setLayoutManager(layoutManager);
        contactView.setAdapter(adapter);
    }


    private void checkContactsPermissionNoReload() {
        if (hasContactsPermission)
            return;
        else {
            if (ContextCompat.checkSelfPermission(getContext(),
                    Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                        Manifest.permission.READ_CONTACTS)) {
                    Toast.makeText(getContext(),
                            "We need this permission to display your contacts list",
                            Toast.LENGTH_LONG).show();
                } else {
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.READ_CONTACTS},
                            PERMISSION_REQUEST_READ_CONTACTS);
                }
            }
        }
    }

    /**
     * Used to request run time permission to read device phone book contacts.
     * If the permission is granted, retrieves contact list.
     */
    private void checkContactsPermission() {
        if (!hasContactsPermission) {
            if (ContextCompat.checkSelfPermission(getContext(),
                    Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                        Manifest.permission.READ_CONTACTS)) {
                    Toast.makeText(getContext(),
                            "We need this permission to display your contacts list",
                            Toast.LENGTH_LONG).show();
                } else {
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.READ_CONTACTS},
                            PERMISSION_REQUEST_READ_CONTACTS);
                }
            } else {
                hasContactsPermission = true;
                retrieveContacts();
            }
        } else {
            hasContactsPermission = true;
            retrieveContacts();
        }
    }

    /**
     * Executes {@link LoadContactsTask} asynchronous task to read device phone book's contact
     * list and initializes {@link RecyclerView} by calling {@code initializeRecyclerView()}.
     */
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

    /**
     * Asynchronous task to retrieve device phone book contacts.
     * Displays an indefinite progress dialog in the form of a spinner and a message
     * informing the user of the operation that is being performed.
     * In the {@code doInBackground()} method, get contacts using {@link ContentResolver}
     * and {@link Cursor}. Creates new {@link Contact} object of the received data
     * and adds the objects to the {@link ArrayList<Contact>}. The {@code ArrayList}
     * is then sorted in ascending order to maintain consistence in the content display.
     * In the {@code onPostExecute()} method, dismisses progress dialog and populates
     * {@link RecyclerView} with contacts
     */
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
                        String name = cursor.getString(cursor.getColumnIndex(ContactsContract
                                .Contacts.DISPLAY_NAME));

                        if (cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts
                                .HAS_PHONE_NUMBER)) > 0) {
                            Cursor contactCursor = contentResolver.query(
                                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                                    new String[]{id}, null);
                            while (contactCursor.moveToNext()) {
                                String phoneNumber = contactCursor.getString(contactCursor
                                        .getColumnIndex(ContactsContract
                                                .CommonDataKinds.Phone.NUMBER));
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

        /**
         * {@link Comparator} interface implementation for {@link Contact} objects.
         * Used for sorting contacts alphabetically in ascending order.
         */
        private class ContactComparator implements Comparator<Contact>
        {
            @Override
            public int compare(Contact o1, Contact o2) {
                return o1.getName().compareToIgnoreCase(o2.getName());
            }
        }
    }
}
