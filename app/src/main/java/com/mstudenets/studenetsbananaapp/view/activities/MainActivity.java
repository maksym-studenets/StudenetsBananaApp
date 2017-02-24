package com.mstudenets.studenetsbananaapp.view.activities;

import android.Manifest;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.mstudenets.studenetsbananaapp.R;
import com.mstudenets.studenetsbananaapp.controller.secure.SecurePreferences;
import com.mstudenets.studenetsbananaapp.model.Contact;
import com.mstudenets.studenetsbananaapp.view.fragments.ContactBookFragment;
import com.mstudenets.studenetsbananaapp.view.fragments.ContactsFragment;
import com.mstudenets.studenetsbananaapp.view.fragments.MapsFragment;
import com.mstudenets.studenetsbananaapp.view.fragments.WeatherFragment;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener
{
    private static final String PREF = "ACCOUNT";
    private static final int PERMISSION_REQUEST_READ_CONTACTS = 100;

    private int tabIndex;

    private SecurePreferences sharedPreferences;
    private SearchView searchView;


    @Override
    protected void onCreate(Bundle savedState) {
        super.onCreate(savedState);
        sharedPreferences = new SecurePreferences(this, PREF, LoginActivity.SECURE_KEY);
        String username = checkUser();
        setContentView(R.layout.activity_main);
        initializeNavbar(username);

        checkContactPermission();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);

        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.menu_search_button)
                .getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener()
        {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return true;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        tabIndex = item.getItemId();
        FragmentTransaction fragmentTransaction;

        switch (tabIndex) {
            case R.id.nav_contacts:
                ContactsFragment contactsFragment = new ContactsFragment();
                fragmentTransaction = getSupportFragmentManager()
                        .beginTransaction();
                fragmentTransaction.replace(R.id.activity_main_fragment_container,
                        contactsFragment);
                fragmentTransaction.commit();
                searchView.setVisibility(View.VISIBLE);
                break;
            case R.id.nav_weather:
                WeatherFragment weatherFragment = new WeatherFragment();
                fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.activity_main_fragment_container,
                        weatherFragment);
                fragmentTransaction.commit();
                searchView.setVisibility(View.INVISIBLE);
                break;
            case R.id.nav_maps:
                MapsFragment mapsFragment = new MapsFragment();
                fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.activity_main_fragment_container,
                        mapsFragment);
                fragmentTransaction.commit();
                searchView.setVisibility(View.INVISIBLE);
                break;
            case R.id.nav_logout:
                logout();
                return true;
            default:
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                    @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_READ_CONTACTS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initializeFragment();
            } else {
                Toast.makeText(this, "Contacts permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private String checkUser() {
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn");
        if (!isLoggedIn) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
        return sharedPreferences.getString("username");
    }

    private void checkContactPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_CONTACTS)) {
                Toast.makeText(this, "We need this permission to display your contacts list",
                        Toast.LENGTH_LONG).show();
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_CONTACTS},
                        PERMISSION_REQUEST_READ_CONTACTS);
            }
        } else {
            initializeFragment();
        }
    }

    private void initializeNavbar(String username) {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);
        TextView usernameText = (TextView) header.findViewById(R.id.navbar_username_text);
        usernameText.setText(username);
    }

    private void initializeFragment() {
        ContactsFragment contactsFragment = new ContactsFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.activity_main_fragment_container, contactsFragment);
        fragmentTransaction.commit();
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            ArrayList<Contact> phoneBookContacts =
                    new ContactBookFragment().getPhoneBookContacts();
        }
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();
        sharedPreferences.putBoolean(false);
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        Toast.makeText(this, R.string.activity_main_logout_successful, Toast.LENGTH_SHORT).show();
    }

    /*
    private static final int PERMISSION_REQUEST_READ_CONTACTS = 100;
    private static final int PERMISSION_REQUEST_CALL_PHONE = 101;

    private SecurePreferences sharedPreferences;
    private SearchView searchView;
    private static final String PREF = "ACCOUNT";
    private int tabIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        sharedPreferences = new SecurePreferences(this, PREF, LoginActivity.SECURE_KEY);
        String username = checkUser();
        setContentView(R.layout.activity_main);
        initializeNavbar(username);

        //checkContactsPermission();
        initializeFragment();

        handleIntent(getIntent());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);

        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.menu_search_button)
                .getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener()
        {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return true;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        tabIndex = item.getItemId();
        FragmentTransaction fragmentTransaction;

        switch (tabIndex) {
            case R.id.nav_contacts:
                ContactsFragment contactsFragment = new ContactsFragment();
                fragmentTransaction = getSupportFragmentManager()
                        .beginTransaction();
                fragmentTransaction.replace(R.id.activity_main_fragment_container,
                        contactsFragment);
                fragmentTransaction.commit();
                searchView.setVisibility(View.VISIBLE);
                break;
            case R.id.nav_weather:
                WeatherFragment weatherFragment = new WeatherFragment();
                fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.activity_main_fragment_container,
                        weatherFragment);
                fragmentTransaction.commit();
                searchView.setVisibility(View.INVISIBLE);
                break;
            case R.id.nav_maps:
                MapsFragment mapsFragment = new MapsFragment();
                fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.activity_main_fragment_container,
                        mapsFragment);
                fragmentTransaction.commit();
                searchView.setVisibility(View.INVISIBLE);
                break;
            case R.id.nav_logout:
                logout();
                return true;
            default:
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void checkContactsPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_CONTACTS)) {
                Toast.makeText(this, "We need this permissions to show your contacts list",
                        Toast.LENGTH_LONG).show();
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[] { Manifest.permission.READ_CONTACTS },
                        PERMISSION_REQUEST_READ_CONTACTS);
            }
        } else {

        }
    }

    public void checkCallPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.CALL_PHONE)) {
                Toast.makeText(this, "We need this permissions to call your contacts from the app",
                        Toast.LENGTH_LONG).show();
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[] { Manifest.permission.CALL_PHONE },
                        PERMISSION_REQUEST_CALL_PHONE);
            }
        }
    }

    private void initializeNavbar(String username) {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);
        TextView usernameText = (TextView) header.findViewById(R.id.navbar_username_text);
        usernameText.setText(username);
    }

    private void initializeFragment() {
        ContactsFragment contactsFragment = new ContactsFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.activity_main_fragment_container, contactsFragment);
        fragmentTransaction.commit();
    }

    private String checkUser() {
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn");
        if (!isLoggedIn) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
        return sharedPreferences.getString("username");
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();
        sharedPreferences.putBoolean(false);
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        Toast.makeText(this, R.string.activity_main_logout_successful, Toast.LENGTH_SHORT).show();
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {

            String query = intent.getStringExtra(SearchManager.QUERY);
            ArrayList<Contact> phoneBookContacts =
                    new ContactBookFragment().getPhonebookContacts();
        }
    }
    */
}
