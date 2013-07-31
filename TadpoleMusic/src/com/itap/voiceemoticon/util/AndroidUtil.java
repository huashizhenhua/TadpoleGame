
package com.itap.voiceemoticon.util;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class AndroidUtil {

    public static void hideInputMethod(View view) {
        InputMethodManager imm = (InputMethodManager)view.getContext().getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

}
