package com.developer.musicatiiva.data;

import android.content.Context;
import android.os.Build;


import androidx.annotation.ColorRes;
import androidx.annotation.StringRes;


import com.developer.musicatiiva.utils.ColorUtils;


import static com.afollestad.materialdialogs.util.DialogUtils.getColor;


public class ThemeData {

    private int nameRes;
    private int colorPrimaryRes;
    private int colorAccentRes;
    private int colorBackgroundRes;

    public ThemeData(@StringRes int nameRes, @ColorRes int colorPrimaryRes, @ColorRes int colorAccentRes, @ColorRes int colorBackgroundRes) {
        this.nameRes = nameRes;
        this.colorPrimaryRes = colorPrimaryRes;
        this.colorAccentRes = colorAccentRes;
        this.colorBackgroundRes = colorBackgroundRes;
    }

    public String getName(Context context) {
        return context.getString(nameRes);
    }

    public void apply(Context context) {
        int backgroundColor = getColor(context, colorBackgroundRes);

        boolean isBackgroundDark = ColorUtils.isColorDark(backgroundColor);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
           /* Aesthetic.get()
                    .colorPrimary(getColor(context, colorPrimaryRes))
                    .colorAccent(getColor(context, colorAccentRes))
                    .colorWindowBackground(backgroundColor)
                    .textColorPrimary(getColor(context, isBackgroundDark ? R.color.textColorPrimary : R.color.textColorPrimaryInverse))
                    .textColorPrimaryInverse(getColor(context, isBackgroundDark ? R.color.textColorPrimaryInverse : R.color.textColorPrimary))
                    .textColorSecondary(getColor(context, isBackgroundDark ? R.color.textColorSecondary : R.color.textColorSecondaryInverse))
                    .textColorSecondaryInverse(getColor(context, isBackgroundDark ? R.color.textColorSecondaryInverse : R.color.textColorSecondary))
                    .colorStatusBarAuto()
                    .colorNavigationBarAuto()
                    .apply();*/
        }
    }

}
