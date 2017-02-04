package com.mstudenets.studenetsbananaapp.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.mstudenets.studenetsbananaapp.R;
import com.mstudenets.studenetsbananaapp.controller.secure.SecurePreferences;

public class MainActivity extends AppCompatActivity
{
    private SecurePreferences sharedPreferences;
    private static final String PREF = "ACCOUNT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPreferences = new SecurePreferences(this, PREF, LoginActivity.SECURE_KEY, true);
        checkUser();

        Toolbar appBar = (Toolbar) findViewById(R.id.activity_main_toolbar);
        setSupportActionBar(appBar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = new MenuInflater(getApplicationContext());
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.activity_main_menu_logout:
                sharedPreferences.putBoolean("isLoggedIn", false);
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                Toast.makeText(this, "Successfully logged out", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }

    private void checkUser() {
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);
        if (!isLoggedIn) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }

        /*
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        if (!sharedPreferences.contains("username") || !sharedPreferences.contains("password")) {
            startActivity(intent);
            //Snackbar.make()
        } else {
            boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);
            if (!isLoggedIn)
                startActivity(intent);
        }*/
    }
}
