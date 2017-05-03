package com.bauble_app.bauble.auth;

import android.util.Patterns;

/**
 * Created by princ on 5/2/2017.
 */

class AuthUtils {
    // Returns whether the given email is valid according to the regex specified
    // by Patterns.EMAIL_ADDRESS
    static boolean isValidEmail(String email) {
        return !email.isEmpty() && Patterns.EMAIL_ADDRESS
                .matcher(email).matches();
    }

    // Returns whether the password is at least 6 characters long
    static boolean isValidPassword(String password) {
        return !(password == null) && !password.isEmpty() && (password.length() >= 6);
    }

    // Returns whether the name is non-null and non-empty
    static boolean isValidName(String name) {
        return !(name == null) && !name.isEmpty();
    }

}
