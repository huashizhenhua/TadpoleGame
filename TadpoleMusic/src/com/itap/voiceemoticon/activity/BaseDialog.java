package com.itap.voiceemoticon.activity;

import android.app.Dialog;
import android.content.Context;

public class BaseDialog extends Dialog{

    private Object mTag;
    
    public BaseDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    public BaseDialog(Context context, int theme) {
        super(context, theme);
    }

    public BaseDialog(Context context) {
        super(context);
    }
    
    public void setTag(Object obj) {
        mTag = obj;
    }
    
    public Object getTag() {
        return mTag;
    }

}
