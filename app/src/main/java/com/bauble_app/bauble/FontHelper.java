package com.bauble_app.bauble;

import android.content.Context;
import android.graphics.Typeface;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by princ on 4/5/2017.
 */

public class FontHelper {
    static Map<String, Typeface> typefaceMap = new HashMap<>();

    // Define custom Typefaces from .ttf files in assets/fonts/
    private static void loadTypeface(String typefaceName, Context context) {
        try {
            Typeface tf = Typeface.createFromAsset(context
                            .getAssets(),
                    "fonts/" + typefaceName + ".ttf");
            typefaceMap.put(typefaceName, tf);
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    public static Typeface getTypeface(String typefaceName, Context context) {
        if (!typefaceMap.containsKey(typefaceName)) {
            loadTypeface(typefaceName, context);
        }
        return typefaceMap.get(typefaceName);
    }
}
