package com.tadpolemusic.activity.dialog;

import com.tadpolemusic.R;

import android.app.Dialog;
import android.content.Context;

public class BaseDialog extends Dialog {

    public BaseDialog(final Context context) {
        super(context, R.style.Dialog_Transparent);
        this.setCancelable(false);
        setContentView(R.layout.dialog_alert);
    }
}
