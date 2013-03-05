package com.tadpolemusic;

import android.util.Log;


public class TMLog {
    public static void step(String TAG, String msg) {
        Log.d(VEApplication.APPLICATION_NAME, TAG + ", =====>" + msg);
    }
}
