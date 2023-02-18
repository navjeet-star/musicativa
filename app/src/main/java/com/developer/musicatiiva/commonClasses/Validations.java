package com.developer.musicatiiva.commonClasses;

import android.text.TextUtils;
import android.util.Patterns;

/**
 * Created by brst-pc80 on 7/13/18.
 */

public class Validations {

    public static boolean isValidEmail(CharSequence target)
    {
        return !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    public static boolean isValidPhone(CharSequence target)
    {
        return !TextUtils.isEmpty(target) && Patterns.PHONE.matcher(target).matches();
    }
    public static boolean isEmpty(CharSequence target)
    {
        return !TextUtils.isEmpty(target);
    }


}


