package com.mstudenets.studenetsbananaapp.controller.utils;


import android.content.Context;

import com.mstudenets.studenetsbananaapp.model.User;
import com.mstudenets.studenetsbananaapp.view.activities.LoginActivity;

/**
 * Registration class handles user registration process within the application.
 * The basic model for storing user data in the application using shared preferences. This class
 * performs write operations to shared preferences
 * Class Registration is not instantiated, it consists of only one static method.
 */
public class Registration
{
    /**
     * Method registerUser performs all the actions that are required to handle the
     * process of user registration within the application. The method writes username and
     * password keys to the shared preferences and sets isLoggedIn flag to true, so there is
     * no need for a user to perform a sign in operations. After successful registration,
     * the user is taken directly to the MainActivity.
     *
     * @param user    A User object that contains basic details of a user: username and password
     * @param context Application context
     */
    public static void registerUser(User user, Context context) {
        SecurePreferences preferences = new SecurePreferences(context, LoginActivity.PREF_NAME,
                LoginActivity.SECURE_KEY);
        preferences.putString("username", user.getUsername());
        preferences.putString("password", user.getPassword());
        preferences.putBoolean(true);
        preferences.commit();

    }
}
