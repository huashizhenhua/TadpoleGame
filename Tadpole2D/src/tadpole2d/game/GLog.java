package tadpole2d.game;

import android.util.Log;

/**
 * 
 * Use @String#format to format log message
 *
 * <br>==========================
 * <br> author：Zenip
 * <br> email：lxyczh@gmail.com
 * <br> create：2012-12-12下午2:47:08
 * <br>==========================
 */
public class GLog {
    public static void d(String tag, String msg, Object... args) {
        Log.d(tag, String.format(msg, args));
    }

    public static void d(String tag, String msg) {
        Log.d(tag, msg);
    }
}
