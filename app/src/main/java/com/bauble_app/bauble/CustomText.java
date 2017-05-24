package com.bauble_app.bauble;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

/**
 * Created by princ on 4/19/2017.
 */

public class CustomText extends android.support.v7.widget.AppCompatTextView {
    public CustomText(Context context) {
        super(context);
        applyCustomFont(context);
    }

    public CustomText(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        applyCustomFont(context);
    }

    public CustomText(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        applyCustomFont(context);
    }

    private void applyCustomFont(Context context) {
        // Find the id of this CustomText
        int resId = getId();
        // Get the string of the id i.e. "sign_up_page_title"
        String idText = getResources().getResourceName(resId);

        // Style attributes we'll be setting -- these are the defaults unless
        // they are changed in the if/else block below
        int textColor = getResources().getColor(R.color.colorDarkText);
        float textSize = 13;

        String typefaceName = "Roboto-Regular";

        // Depending on what the id contains, change styles
        if (idText.contains("title2")) {
            typefaceName = "Roboto-Bold";
            textSize = 13;
        } else if (idText.contains("title")) {
            typefaceName = "Roboto-Bold";
            textSize = 17;
        }

        // Apply text styles
        Typeface customFont = FontHelper.getTypeface(typefaceName,
                context);
        setTypeface(customFont);
        setTextColor(textColor);
        setTextSize(textSize);
    }
}
