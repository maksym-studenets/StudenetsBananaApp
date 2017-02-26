package com.mstudenets.studenetsbananaapp.view.activities;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.mstudenets.studenetsbananaapp.R;
import com.mstudenets.studenetsbananaapp.controller.secure.SecurePreferences;
import com.mstudenets.studenetsbananaapp.model.User;

public class LoginActivity extends AppCompatActivity
{
    private String TAG = LoginActivity.class.getSimpleName();
    private static final int RC_SIGN_IN = 9001;
    private static final int PERMISSION_REQUEST_READ_CONTACTS = 100;
    public static final String PREF_NAME = "ACCOUNT";
    public static final String SECURE_KEY = "SecureKeyBanana022017";

    private boolean hasPermission = false;
    private SecurePreferences sharedPreferences;
    private EditText usernameEdit;
    private EditText passwordEdit;
    private ConstraintLayout rootView;

    private GoogleApiClient mGoogleApiClient;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private CallbackManager mCallbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sharedPreferences = new SecurePreferences(this, PREF_NAME, SECURE_KEY);
        usernameEdit = (EditText) findViewById(R.id.activity_login_edit_username);
        passwordEdit = (EditText) findViewById(R.id.activity_login_edit_password);
        rootView = (ConstraintLayout) findViewById(R.id.activity_login_root_layout);
        final Button loginButton = (Button) findViewById(R.id.activity_login_button_login);
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

        SignInButton googleSignInButton = (SignInButton)
                findViewById(R.id.activity_login_google_sign_in);
        googleSignInButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                signInGoogle();
            }
        });
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(
                GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail().build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener()
                {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Snackbar.make(rootView,
                                "Failed to establish connection. Check your network settings",
                                Snackbar.LENGTH_LONG)
                                .setAction("OPEN SETTINGS", new View.OnClickListener()
                                {
                                    @Override
                                    public void onClick(View v) {
                                        Intent networkSettingsIntent =
                                                new Intent(Settings.ACTION_WIRELESS_SETTINGS);
                                        startActivityForResult(networkSettingsIntent, 0);
                                    }
                                }).show();
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso).build();

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener()
        {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = mAuth.getCurrentUser();
                if (user != null)
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                else
                    Log.d(TAG, "onAuthStateChanged:signed_out");
            }
        };
        mCallbackManager = CallbackManager.Factory.create();
        LoginButton facebookLoginButton = (LoginButton)
                findViewById(R.id.activity_login_facebook_login);
        facebookLoginButton.setReadPermissions("email", "public_profile");
        facebookLoginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>()
        {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, " --- facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {
                Snackbar.make(rootView, R.string.facebook_login_error,
                        Snackbar.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }

        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    /*
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_READ_CONTACTS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                hasPermission = true;
            } else {
                hasPermission = false;
                Toast.makeText(this, "Contacts permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }
    */

    private void loginApp() {
        String username, password;

        if (usernameEdit.getText().toString().equals("") ||
                passwordEdit.getText().toString().equals("")) {
            Toast.makeText(LoginActivity.this, "Please fill all the fields",
                    Toast.LENGTH_SHORT).show();
        } else {
            if (!sharedPreferences.containsKey("username") ||
                    !sharedPreferences.containsKey("password")) {
                Toast.makeText(LoginActivity.this, "User with this credentials does not exist. " +
                                "Authentication failed.",
                        Toast.LENGTH_SHORT).show();
            } else {
                username = usernameEdit.getText().toString();
                password = passwordEdit.getText().toString();

                String mUsername = sharedPreferences.getString("username");
                String mPassword = sharedPreferences.getString("password");

                if (mUsername.equals(username) && mPassword.equals(password)) {
                    sharedPreferences.putBoolean(true);
                    User user = new User(mUsername, mPassword);
                    //checkContactPermission();
                    Intent intent = new Intent(this, MainActivity.class);
                    intent.putExtra("User", user);
                    startActivity(intent);
                } else
                    Toast.makeText(this, "Username or password do not match. Please" +
                            " try again", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void signInGoogle() {
        Intent googleSignInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(googleSignInIntent, RC_SIGN_IN);
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, " --- handleSignInResult:" + result.isSuccess());

        if (result.isSuccess()) {
            GoogleSignInAccount account = result.getSignInAccount();
            firebaseAuthWithGoogle(account);
            updateUi(true);
        } else {
            updateUi(false);
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        Log.d(TAG, " --- firebaseAuthWithGoogle:" + account.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>()
                {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, " --- signInWithCredential:onComplete:" + task.isSuccessful());
                        if (!task.isSuccessful()) {
                            Log.w(TAG, " --- signInWithCredential", task.getException());
                            Snackbar.make(rootView, R.string.google_sign_in_failed,
                                    Snackbar.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, " --- handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>()
                {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, " --- signInWithCredential:onComplete:" + task.isSuccessful());

                        if (!task.isSuccessful()) {
                            Log.w(TAG, " --- signInWithCredential", task.getException());
                            Snackbar.make(rootView, R.string.facebook_login_error,
                                    Snackbar.LENGTH_LONG).show();
                        } else {
                            updateUi(true);
                        }
                    }
                });
    }

    private void updateUi(boolean isSignedIn) {
        if (isSignedIn) {
            sharedPreferences.putBoolean(true);
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        } else {
            Snackbar.make(rootView, R.string.google_sign_in_failed, Snackbar.LENGTH_LONG).show();
        }
    }




    /*
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sharedPreferences = new SecurePreferences(getApplicationContext(),
                "ACCOUNT", SECURE_KEY);

        usernameEdit = (EditText) findViewById(R.id.activity_login_edit_username);
        passwordEdit = (EditText) findViewById(R.id.activity_login_edit_password);
        rootView = (ConstraintLayout) findViewById(R.id.activity_login_root_layout);

        final Button loginButton = (Button) findViewById(R.id.activity_login_button_login);
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

        SignInButton googleSignInButton = (SignInButton)
                findViewById(R.id.activity_login_google_sign_in);
        googleSignInButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                signInGoogle();
            }
        });


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(
                GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail().build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener()
                {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Snackbar.make(rootView, R.string.google_sign_in_connection_failed,
                                Snackbar.LENGTH_LONG).show();
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener()
        {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                //updateUi(user);
            }
        };

        mCallbackManager = CallbackManager.Factory.create();
        LoginButton facebookLoginButton = (LoginButton)
                findViewById(R.id.activity_login_facebook_login);
        facebookLoginButton.setReadPermissions("email", "public_profile");
        facebookLoginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>()
        {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, " --- facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {
                Snackbar.make(rootView, R.string.facebook_login_error,
                        Snackbar.LENGTH_LONG).show();
            }
        });


        checkContactPermission();
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(mAuthListener);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }

        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_READ_CONTACTS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                hasPermission = true;
            } else {
                hasPermission = false;
                Toast.makeText(this, "Contacts permission denied", Toast.LENGTH_LONG).show();
            }
        }
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
                checkContactPermission();
            } else {
                Snackbar.make(rootView,
                        "Username or password does not match. Authentication failed",
                        Snackbar.LENGTH_INDEFINITE).show();
            }
        } else {
            Toast.makeText(LoginActivity.this, "Fill in all the fields",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void signInGoogle() {
        Intent googleSignInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(googleSignInIntent, RC_SIGN_IN);
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, " --- handleSignInResult:" + result.isSuccess());

        if (result.isSuccess()) {
            GoogleSignInAccount account = result.getSignInAccount();
            firebaseAuthWithGoogle(account);
            updateUi(true);
        } else {
            updateUi(false);
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        Log.d(TAG, " --- firebaseAuthWithGoogle:" + account.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>()
                {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, " --- signInWithCredential:onComplete:" + task.isSuccessful());
                        if (!task.isSuccessful()) {
                            Log.w(TAG, " --- signInWithCredential", task.getException());
                            Snackbar.make(rootView, R.string.google_sign_in_failed,
                                    Snackbar.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, " --- handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>()
                {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, " --- signInWithCredential:onComplete:" + task.isSuccessful());

                        if (!task.isSuccessful()) {
                            Log.w(TAG, " --- signInWithCredential", task.getException());
                            Snackbar.make(rootView, R.string.facebook_login_error,
                                    Snackbar.LENGTH_LONG).show();
                        } else {
                            updateUi(true);
                        }
                    }
                });
    }

    private void updateUi(boolean isSignedIn) {
        if (isSignedIn) {
            sharedPreferences.putBoolean(true);
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            checkContactPermission();
        }
        else {
            Snackbar.make(rootView, R.string.google_sign_in_failed, Snackbar.LENGTH_LONG).show();
        }
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
        }
    }
    */
}
