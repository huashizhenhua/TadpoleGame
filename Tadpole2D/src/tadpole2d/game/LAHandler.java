package tadpole2d.game;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.util.DisplayMetrics;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

//��������
public class LAHandler implements ILAHandler {
    private int width, height;

    private ILAScreen screen;

    private boolean isInstance;

    private Activity activity;

    private Context context;

    private Window window;

    private WindowManager windowManager;

    private View view;

    public LAHandler(Activity activity, View view) {
        this.activity = activity;
        this.context = activity.getApplicationContext();
        this.window = activity.getWindow();
        this.windowManager = activity.getWindowManager();
        this.view = view;
        Dimension d = getScreenDimension();
        this.width = d.getWidth();
        this.height = d.getHeight();
    }

    // 设置全屏
    public void setFullScreen() {
        activity.requestWindowFeature(Window.FEATURE_NO_TITLE);
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }
    
    public void setLandScape(boolean flag) {
        if (flag) {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    public synchronized ILAScreen getScreen() {
        return screen;
    }

    //���õ�ǰ����
    public synchronized void setScreen(final ILAScreen screen) {
        if (screen == null) {
            this.isInstance = false;
            throw new RuntimeException("Cannot create a ILAScreen iWnstance!");
        }
        Thread.yield();
        this.screen = screen;
        this.isInstance = true;
    }

    //��ô������
    public Dimension getScreenDimension() {
        DisplayMetrics dm = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(dm);
        return new Dimension((int) dm.xdpi, (int) dm.ydpi, (int) dm.widthPixels, (int) dm.heightPixels);
    }

    public boolean onLongClick(View v) {
        return isInstance ? screen.onLongClick(v) : false;
    }

    public boolean onTouch(View v, MotionEvent event) {
        return isInstance ? screen.onTouch(v, event) : false;
    }

    public boolean onKey(View v, int keyCode, KeyEvent event) {
        return isInstance ? screen.onKey(v, keyCode, event) : false;
    }

    public void onClick(View v) {
        if (isInstance) {
            screen.onClick(v);
        }
    }

    public void onFocusChange(View v, boolean hasFocus) {
        if (isInstance) {
            screen.onFocusChange(v, hasFocus);
        }
    }

    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        if (isInstance) {
            screen.onCreateContextMenu(menu, v, menuInfo);
        }
    }

    public View getView() {
        return view;
    }

    public Activity getActivity() {
        return activity;
    }

    public Context getContext() {
        return context;
    }

    public Window getWindow() {
        return window;
    }

    public WindowManager getWindowManager() {
        return windowManager;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}