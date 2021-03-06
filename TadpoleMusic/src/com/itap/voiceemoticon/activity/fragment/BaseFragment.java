
package com.itap.voiceemoticon.activity.fragment;

import android.view.LayoutInflater;
import android.view.View;

public abstract class BaseFragment {

    private View mView;

    public BaseFragment() {
    }

    public void createContent(LayoutInflater inflater) {
        mView = onCreateView(inflater);
    }

    public View getContent() {
        return mView;
    }

    /**
     * onCreate
     */
    public void onCreate() {

    }

    /**
     * onDestory
     */
    public void onDestory() {

    }

    public abstract View onCreateView(LayoutInflater inflater);

}
