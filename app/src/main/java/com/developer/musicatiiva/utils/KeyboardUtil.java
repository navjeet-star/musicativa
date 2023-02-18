package com.developer.musicatiiva.utils;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;

import com.developer.musicatiiva.KeyboardVisibilityListener;
import com.developer.musicatiiva.fragments.ChordFragments;

public class KeyboardUtil {

    public static void setKeyboardVisibilityListener(Activity activity,KeyboardVisibilityListener keyboardVisibilityListener) {
        View contentView = activity.findViewById(android.R.id.content);

        contentView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            private int mPreviousHeight;
            @Override
            public void onGlobalLayout() {
                int newHeight = contentView.getHeight();
                if (mPreviousHeight != 0) {
                    if (mPreviousHeight > newHeight) {
                        // Height decreased: keyboard was shown
                        keyboardVisibilityListener.onKeyboardVisibilityChanged(true);

                    } else if (mPreviousHeight < newHeight) {
                        // Height increased: keyboard was hidden
                        keyboardVisibilityListener.onKeyboardVisibilityChanged(false);
                    } else {
                        // No change
                    }
                }
                mPreviousHeight = newHeight;
            }
        });
    }

     static public boolean isKeyboardOpen(Activity activity){
         InputMethodManager imm = (InputMethodManager) activity
                 .getSystemService(Context.INPUT_METHOD_SERVICE);

         if (imm.isAcceptingText()) {
             return true;
         }
          else
          return false;

    }

    public interface onBackDispatch{
        void onPressed(ChordFragments fragment);
    }
}
