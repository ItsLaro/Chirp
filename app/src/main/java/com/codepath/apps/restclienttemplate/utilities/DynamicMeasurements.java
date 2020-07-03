package com.codepath.apps.restclienttemplate.utilities;

import android.content.Context;
import android.content.res.TypedArray;

import com.codepath.apps.restclienttemplate.R;

public class DynamicMeasurements {

    public static int getToolbarHeight(Context context) {
        final TypedArray styledAttributes = context.getTheme().obtainStyledAttributes(
                new int[]{R.attr.actionBarSize});
        int toolbarHeight = (int) styledAttributes.getDimension(0, 0);
        styledAttributes.recycle();

        return toolbarHeight;
    }
}