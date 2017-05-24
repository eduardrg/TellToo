package com.bauble_app.bauble;

import android.content.res.Resources;
import android.util.TypedValue;

/**
 * Created by princ on 5/20/2017.
 */

public class Utils {
    // LayoutParams takes pixel arguments but we want to specify dp
    public static int getDp(int px, Resources resources) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                px, resources.getDisplayMetrics());
    }

}
