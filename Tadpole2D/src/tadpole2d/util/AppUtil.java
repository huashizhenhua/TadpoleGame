package tadpole2d.util;

import android.content.Context;
import android.widget.Toast;

/**
 * 
 * usage。
 *
 * <br>==========================
 * <br> author：Zenip
 * <br> email：lxyczh@gmail.com
 * <br> create：2012-12-12下午2:22:25
 * <br>==========================
 */
public class AppUtil {
    public static void toast(Context context, String msg){
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }
}
