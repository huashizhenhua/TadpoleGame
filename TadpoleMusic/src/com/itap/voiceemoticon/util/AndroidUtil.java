
package com.itap.voiceemoticon.util;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

public class AndroidUtil {
    public static void hideInputMethod(View view) {
        InputMethodManager imm = (InputMethodManager)view.getContext().getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
    
    public static void scoreApp(Activity activity){
        Uri uri = Uri.parse("market://details?id=" + activity.getPackageName());
        Intent intent = new Intent(Intent.ACTION_VIEW,uri);
        try{
            activity.startActivity(intent);
        }catch(ActivityNotFoundException e){
            Toast.makeText(activity, "应用市场调用失败", Toast.LENGTH_SHORT).show();
        }
    } 
}
