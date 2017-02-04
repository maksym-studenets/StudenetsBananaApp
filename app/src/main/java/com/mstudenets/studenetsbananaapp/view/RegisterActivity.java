package com.mstudenets.studenetsbananaapp.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.mstudenets.studenetsbananaapp.R;
import com.mstudenets.studenetsbananaapp.controller.Registration;
import com.mstudenets.studenetsbananaapp.model.User;

public class RegisterActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        final EditText usernameEdit = (EditText) findViewById(R.id.activity_register_edit_username);
        final EditText passwordEdit = (EditText) findViewById(R.id.activity_register_edit_password);

        Button registerButton = (Button) findViewById(R.id.activity_register_button_register);
        registerButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                String mUsername = usernameEdit.getText().toString();
                String mPassword = passwordEdit.getText().toString();

                if (!mUsername.equals("") && !mPassword.equals("")) {
                    User user = new User(mUsername, mPassword);
                    Registration.registerUser(user, getApplicationContext());
                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(RegisterActivity.this, "Fill in all the fields",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
