package org.tadpole.common;

import android.util.Log;

/**
 * {@link String#format(String, Object...)}
 * 
 * <br>==========================
 * <br> author：Zenip
 * <br> email：lxyczh@gmail.com
 * <br> create：2013-1-3下午6:54:27
 * <br>==========================
 */
public class TLog {
    
    public static void debug(String tag, String msg, Object... args) {
        Log.d(tag, String.format(msg, args));
    }
}
