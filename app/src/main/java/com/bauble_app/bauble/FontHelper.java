package com.bauble_app.bauble;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by princ on 4/5/2017.
 */

class FontHelper {
    private static FontHelper instance;
    Typeface LatoBold;

    // When FontHelper is instantiated, grab custom fonts
    private FontHelper(Context context) {
        setTypefaces(context);
    }

    // Return the existing instance or make one if it does not exist
    // (Singleton pattern)
    static FontHelper getInstance(Context context) {
        if (instance == null) {
            instance = new FontHelper(context);
        }
        return instance;
    }

    // Define custom Typefaces from .ttf files in assets/fonts/
    private void setTypefaces(Context context) {
        LatoBold = Typeface.createFromAsset(context.getAssets(),
                "fonts/Lato-Bold.ttf");
    }
}
