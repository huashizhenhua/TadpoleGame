package com.tadpolemusic.activity.fragment;

import com.tadpolemusic.activity.fragment.menu.IMenuControl;

import android.app.Activity;
import android.support.v4.app.Fragment;

/**
 * Fragment for Activity
 * <br>==========================
 * <br> author：Zenip
 * <br> email：lxyczh@gmail.com
 * <br> create：2013-2-20
 * <br>==========================
 */
public abstract class AbsMenuFragment extends Fragment {
    public IMenuControl getLeftMenuControll() {
        Activity act = getActivity();
        if (act == null) {
            throw new RuntimeException("getLeftMenuControll must call after createView call");
        }
        if (!(act instanceof IMenuControl)) {
            throw new RuntimeException("getLeftMenuControll is not a ILeftMenuControl");
        }
        return (IMenuControl) act;
    }


    public void runOnUIThread(Runnable runnable) {
        Activity act = getActivity();
        if (act == null) {
            throw new RuntimeException("runOnUIThread must call after createViewd call");
        }
        act.runOnUiThread(runnable);
    }

}
