package com.mstudenets.studenetsbananaapp.view.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mstudenets.studenetsbananaapp.R;
import com.mstudenets.studenetsbananaapp.controller.secure.SecurePreferences;

public class LoginActivity extends AppCompatActivity
{
    public static final String PREF_NAME = "ACCOUNT";
    public static final String SECURE_KEY = "SecureKeyBanana022017";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final EditText usernameEdit = (EditText) findViewById(R.id.activity_login_edit_username);
        final EditText passwordEdit = (EditText) findViewById(R.id.activity_login_edit_password);

        Button loginButton = (Button) findViewById(R.id.activity_login_button_login);
        loginButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                SecurePreferences sharedPreferences = new SecurePreferences(getApplicationContext(),
                        "ACCOUNT", SECURE_KEY, true);
                String mUsername = null;
                String mPassword = null;

                String username = usernameEdit.getText().toString();
                String password = passwordEdit.getText().toString();

                if (sharedPreferences.containsKey("username"))
                    mUsername = sharedPreferences.getString("username", "");
                if (sharedPreferences.containsKey("password"))
                    mPassword = sharedPreferences.getString("password", "");
                if (sharedPreferences.containsKey("isloggedin")) {
                    if (sharedPreferences.getBoolean("isloggedin", false)) {
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                }

                if (mUsername != null && mPassword != null &&
                        !mUsername.equals("") && !mPassword.equals("")) {
                    if (mUsername.equals(username) && mPassword.equals(password)) {
                        sharedPreferences.putBoolean("isLoggedIn", true);
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        Toast.makeText(LoginActivity.this, "Successfully logged in",
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "Username or password do not match",
                            Toast.LENGTH_LONG).show();
                    Toast.makeText(LoginActivity.this, "Authentication failed",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        TextView registerText = (TextView) findViewById(R.id.activity_login_text_register_link);
        registerText.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }
}
