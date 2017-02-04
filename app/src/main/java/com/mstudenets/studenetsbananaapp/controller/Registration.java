package com.mstudenets.studenetsbananaapp.controller;


import android.content.Context;

import com.mstudenets.studenetsbananaapp.controller.secure.SecurePreferences;
import com.mstudenets.studenetsbananaapp.model.User;
import com.mstudenets.studenetsbananaapp.view.LoginActivity;

public class Registration
{
    public static void registerUser(User user, Context context) {
        SecurePreferences preferences = new SecurePreferences(context, LoginActivity.PREF_NAME,
                LoginActivity.SECURE_KEY, true);
        preferences.putString("username", user.getUsername());
        preferences.putString("password", user.getPassword());
        preferences.putBoolean("isLoggedIn", true);
        preferences.commit();

    }
}
