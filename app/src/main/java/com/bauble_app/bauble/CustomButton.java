package com.bauble_app.bauble;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by princ on 4/19/2017.
 */

public class CustomButton extends android.support.v7.widget.AppCompatButton {
    public CustomButton(Context context) {
        super(context);
        applyCustomFont(context);
    }

    public CustomButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        applyCustomFont(context);
    }

    public CustomButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        applyCustomFont(context);
    }

    private void applyCustomFont(Context context) {
        // Find the id of this CustomButton
        int resId = getId();
        // Get the string of the id i.e. "create_set_cover_image"
        String idText = getResources().getResourceName(resId);

        // Style attributes we'll be setting -- these are the defaults unless
        // they are changed in the if/else block below
        int textColor = getResources().getColor(R.color.colorWhite);
        float textSize = 16;
        int textAlignment = 0;
        String typefaceName = "Roboto-Regular";
        boolean allCaps = false;

        // Only supported in API levels >=17
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
            textAlignment = View.TEXT_ALIGNMENT_CENTER;
        }

        // Depending on what the id is, change styles
        if (resId == R.id.create_set_cover_image){
            typefaceName = "Roboto-Bold";
            textColor = getResources().getColor(R.color.colorDarkText);
            textSize = 17;
        }

        // Apply text styles
        Typeface customFont = FontHelper.getTypeface(typefaceName,
                context);
        setTypeface(customFont);
        setTextColor(textColor);
        setTextSize(textSize);
        setAllCaps(allCaps);
        // Only supported in API levels >=17
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            setTextAlignment(textAlignment);
        }
    }

}
