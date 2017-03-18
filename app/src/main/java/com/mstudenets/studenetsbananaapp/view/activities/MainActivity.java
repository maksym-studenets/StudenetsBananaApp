package com.mstudenets.studenetsbananaapp.view.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
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
import com.google.firebase.auth.FirebaseUser;
import com.mstudenets.studenetsbananaapp.R;
import com.mstudenets.studenetsbananaapp.controller.utils.SecurePreferences;
import com.mstudenets.studenetsbananaapp.model.Contact;
import com.mstudenets.studenetsbananaapp.view.fragments.ContactBookFragment;
import com.mstudenets.studenetsbananaapp.view.fragments.ContactsFragment;
import com.mstudenets.studenetsbananaapp.view.fragments.MapsFragment;
import com.mstudenets.studenetsbananaapp.view.fragments.WeatherFragment;

import java.util.ArrayList;

/**
 * Main Activity of the application. Acts as a launcher activity.
 * On start, checks if there is a user who is already signed in, otherwise redirects to
 * the {@link LoginActivity}.
 * This activity acts like a container for fragments which are used to display relevant content.
 * By default, {@link ContactsFragment} is loaded. Fragment transitions are executed when a user
 * selects an appropriate menu item in the navigation drawer.
 * MainActivity provides search functionality for contacts representation.
 */
public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener
{
    private static final String PREF = "ACCOUNT";
    private static final int PERMISSION_REQUEST_READ_CONTACTS = 100;
    private static final int LOGIN_ACTIVITY_DATA_REQUEST = 10;

    private boolean hasPermission = false;
    private String username;

    private SecurePreferences sharedPreferences;
    private SearchView searchView;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedState) {
        super.onCreate(savedState);
        sharedPreferences = new SecurePreferences(this, PREF, LoginActivity.SECURE_KEY);
        checkCurrentUser();
        setContentView(R.layout.activity_main);
        initializeNavbar();
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
        /*
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
        */

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
        int tabIndex = item.getItemId();
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

    /*
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                    @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_READ_CONTACTS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                hasPermission = true;
            } else {
                Toast.makeText(this, "Contacts permission denied", Toast.LENGTH_LONG).show();
                hasPermission = false;
            }
        }
    }
    */

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        /*
        if (requestCode == LOGIN_ACTIVITY_DATA_REQUEST) {
            User user;
            if (resultCode == RESULT_OK)
                user = (User) getIntent().getSerializableExtra("User");
        }
        */
    }

    private void checkCurrentUser() {
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        boolean hasUsernamePref = sharedPreferences.containsKey("username");
        if (hasUsernamePref) username = sharedPreferences.getString("username");
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn") ||
                user != null;
        if (!isLoggedIn) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        } else {
            initializeFragment();
        }
    }

    /*
    private void checkIntent() {
        Intent intent = getIntent();
        if (intent != null) {
            checkContactPermission();
        }
    }
    */

    /*
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
    */


    private void initializeFragment() {
        ContactsFragment contactsFragment = new ContactsFragment();
        //checkContactPermission();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager()
                .beginTransaction();
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

    private void initializeNavbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);
        TextView usernameText = (TextView) header.findViewById(R.id.navbar_username_text);
        String name;
        /*
        if (user != null)
            name = user.getUsername();
            */
        //usernameText.setText(username);
        if (firebaseAuth.getCurrentUser() != null)
            name = firebaseAuth.getCurrentUser().getDisplayName();
        else name = username;
        usernameText.setText(name);
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();
        sharedPreferences.putBoolean(false);
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        Toast.makeText(this, R.string.activity_main_logout_successful, Toast.LENGTH_SHORT).show();
    }
}
