package com.mstudenets.studenetsbananaapp.view.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.mstudenets.studenetsbananaapp.R;
import com.mstudenets.studenetsbananaapp.controller.secure.SecurePreferences;

public class LoginActivity extends AppCompatActivity
{
    private String TAG = LoginActivity.class.getSimpleName();

    public static final String PREF_NAME = "ACCOUNT";
    public static final String SECURE_KEY = "SecureKeyBanana022017";
    private SecurePreferences sharedPreferences;
    private EditText usernameEdit;
    private EditText passwordEdit;

    private GoogleApiClient mGoogleApiClient;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sharedPreferences = new SecurePreferences(getApplicationContext(),
                "ACCOUNT", SECURE_KEY);

        usernameEdit = (EditText) findViewById(R.id.activity_login_edit_username);
        passwordEdit = (EditText) findViewById(R.id.activity_login_edit_password);

        Button loginButton = (Button) findViewById(R.id.activity_login_button_login);
        loginButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                loginApp();
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


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(
                GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail().build();
    }

    private void loginApp() {
        String mUsername = null;
        String mPassword = null;

        String username = usernameEdit.getText().toString();
        String password = passwordEdit.getText().toString();

        if (sharedPreferences.containsKey("username"))
            mUsername = sharedPreferences.getString("username");
        if (sharedPreferences.containsKey("password"))
            mPassword = sharedPreferences.getString("password");
        if (sharedPreferences.containsKey("isloggedin")) {
            if (sharedPreferences.getBoolean("isloggedin")) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
            }
        }

        if (mUsername != null && mPassword != null &&
                !mUsername.equals("") && !mPassword.equals("")) {
            if (mUsername.equals(username) && mPassword.equals(password)) {
                sharedPreferences.putBoolean(true);
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                Toast.makeText(LoginActivity.this, "Successfully logged in",
                        Toast.LENGTH_SHORT).show();
            } else {
                        /*Toast.makeText(LoginActivity.this, "Username or password do not match",
                                Toast.LENGTH_LONG).show();
                        Toast.makeText(LoginActivity.this, "Authentication failed",
                                Toast.LENGTH_SHORT).show();
                                */
                ConstraintLayout constraintLayout = (ConstraintLayout)
                        findViewById(R.id.activity_login_root_layout);
                Snackbar.make(constraintLayout,
                        "Username or password does not match. Authentication failed",
                        Snackbar.LENGTH_INDEFINITE).show();
            }
        } else {
            Toast.makeText(LoginActivity.this, "Fill in all the fields",
                    Toast.LENGTH_SHORT).show();
        }
    }
}
