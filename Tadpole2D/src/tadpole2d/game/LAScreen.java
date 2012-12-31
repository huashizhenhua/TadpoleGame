package tadpole2d.game;

import java.util.Random;
import java.util.UUID;

import cn.uc.gamesdk.c.m;

import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import system.LAGraphicsUtils;

public abstract class LAScreen implements ILAScreen {
    /**
     * 屏幕唯一标识
     */
    private long mId;

    private ILAHandler handler;
    
    private static int idFeed = 1;
    
    private synchronized int genUniqueId(){
        return idFeed++;
    }
    
    public LAScreen() {
        idFeed = genUniqueId();
        this.handler = LASystem.getSystemHandler();
    }

    public long getId() {
        return mId;
    }

    public boolean equal(LAScreen screen) {
        return (screen != null) && (mId == screen.getId());
    }

    public LAImage getLAImage(String fileName) {
        return LAGraphicsUtils.loadLAImage(fileName);
    }

    public LAImage[] getSplitLAImages(String fileName, int row, int col) {
        return getSplitLAImages(getLAImage(fileName), row, col);
    }

    public LAImage[] getSplitLAImages(LAImage image, int row, int col) {
        return LAGraphicsUtils.getSplitImages(image, row, col);
    }

    public synchronized void createUI(LAGraphics g) {
        onDraw(g);
    }

    public synchronized void runTimer(LTimerContext timer) {
        alter(timer);
    }

    public void setScreen(ILAScreen screen) {
        this.handler.setScreen(screen);
    }

    public boolean onKey(View v, int keyCode, KeyEvent event) {
        return onKey(keyCode, event);
    }

    public boolean onKey(int keyCode, KeyEvent e) {
        switch (e.getAction()) {
        case KeyEvent.ACTION_DOWN:
            return onKeyDown(keyCode, e);
        case KeyEvent.ACTION_UP:
            return onKeyUp(keyCode, e);
        }
        return true;
    }

    public void onFocusChange(View v, boolean hasFocus) {
        onFocusChange(hasFocus);
    }

    public void onFocusChange(boolean hasFocus) {
    }

    public boolean onLongClick(View v) {
        return onLongClick();
    }

    public boolean onLongClick() {
        return false;
    }

    public void onClick(View v) {
        onClick();
    }

    public void onClick() {

    }

    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        onCreateContextMenu(menu, menuInfo);
    }

    public void onCreateContextMenu(ContextMenu menu, ContextMenuInfo menuInfo) {

    }

    public boolean onTouch(View v, MotionEvent event) {
        return onTouch(event);
    }

    public boolean onTouch(MotionEvent e) {
        switch (e.getAction()) {
        case MotionEvent.ACTION_DOWN:
            return onTouchDown(e);
        case MotionEvent.ACTION_UP:
            return onTouchUp(e);
        case MotionEvent.ACTION_MOVE:
            return onTouchMove(e);
        }
        return true;
    }

    public abstract void onDraw(LAGraphics g);

    public void alter(LTimerContext timer) {
    }
}