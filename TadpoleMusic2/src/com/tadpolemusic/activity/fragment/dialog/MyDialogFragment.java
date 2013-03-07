package com.tadpolemusic.activity.fragment.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;

public class MyDialogFragment extends DialogFragment {

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int title = getArguments().getInt("title");
        final MyDialogFragment me = this;
        return new AlertDialog.Builder(getActivity()).setIcon(android.R.drawable.ic_menu_add).setTitle(title).setPositiveButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                me.doPositiveClick();
            }
        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                me.doNegativeClick();
            }
        }).create();
    }


    public void showDialog() {
        DialogFragment newFragment = new DialogFragment();
        newFragment.show(getFragmentManager(), "dialog");
    }

    public void doPositiveClick() {
        // Do stuff here. 
        Log.i("FragmentAlertDialog", "Positive click!");
    }

    public void doNegativeClick() {
        // Do stuff here. 
        Log.i("FragmentAlertDialog", "Negative click!");
    }
}
