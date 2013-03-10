package com.tadpolemusic.activity.dialog;


import com.tadpolemusic.R;

import android.content.Context;
import android.widget.CheckBox;
import android.widget.TextView;

public class LocalMusicDeleteDialog extends BaseAlertDialog {

    private CheckBox mCheckBox;
    private TextView mTextViewTip;

    public LocalMusicDeleteDialog(Context context) {
        super(context);
        setContent(R.layout.dialog_alert_local_del);
        mCheckBox = (CheckBox) findViewById(R.id.checkbox);
        mTextViewTip = (TextView) findViewById(R.id.textview_tip);
    }

    public boolean isNeedToDeleteFile() {
        return mCheckBox.isChecked();
    }

    public void setTip(String str) {
        mTextViewTip.setText(str);
    }

}
