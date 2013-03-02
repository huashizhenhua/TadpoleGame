package com.tadpolemusic.activity.fragment;

import com.tadpolemusic.activity.fragment.menu.ILeftMenuControl;

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
    public ILeftMenuControl getLeftMenuControll() {
        Activity act = getActivity();
        if (act == null) {
            throw new RuntimeException("getActivityInterface must call after createViewd call");
        }
        if (!(act instanceof ILeftMenuControl)) {
            throw new RuntimeException("getActivity is not a IActivityInterface");
        }
        return (ILeftMenuControl) act;
    }
}
