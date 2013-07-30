
package com.itap.voiceemoticon.widget;

import com.itap.voiceemoticon.R;
import com.itap.voiceemoticon.VEApplication;

import org.tadpoleframework.widget.SwitchButton;
import org.tadpoleframework.widget.SwitchButton.SwitchListener;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface.OnCancelListener;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

public final class WeixinAlert {

    public interface OnAlertSelectId {
        void onClick(Dialog dialog, int whichButton);
    }

    private WeixinAlert() {

    }

    
    public static Dialog showAlert(final Context context, final String title, String exit,
            final OnAlertSelectId alertDo, OnCancelListener cancelListener) {
        return showAlert(context, title, exit, alertDo, cancelListener, true);
    }
    
    /**
     * @param context Context.
     * @param title The title of this AlertDialog can be null .
     * @param items button name list.
     * @param alertDo methods call Id:Button + cancel_Button.
     * @param exit Name can be null.It will be Red Color
     * @return A AlertDialog
     */
    public static Dialog showAlert(final Context context, final String title, String exit,
            final OnAlertSelectId alertDo, OnCancelListener cancelListener, boolean showHideTitleOption) {
        String cancel = context.getString(R.string.app_cancel);
        final Dialog dlg = new Dialog(context, R.style.MMTheme_DataSheet);
        LayoutInflater inflater = (LayoutInflater)context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout layout = (LinearLayout)inflater.inflate(R.layout.alert_dialog_menu_layout,
                null);
        
        View view = layout.findViewById(R.id.options);
        if(showHideTitleOption) {
            view.setVisibility(View.VISIBLE);
        } else {
            view.setVisibility(View.GONE);
        }

        TextView titleView = (TextView)layout.findViewById(R.id.title);
        titleView.setText(title);

        View.OnClickListener listener = new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.cancel:
                        dlg.cancel();
                        break;
                    case R.id.webchat:
                    case R.id.qq:
                    case R.id.friends:
                    case R.id.weibo:
                        alertDo.onClick(dlg, v.getId());
                        dlg.dismiss();
                        break;
                    default:
                        break;
                }
            }
        };
        View viewCancel = layout.findViewById(R.id.cancel);
        viewCancel.setOnClickListener(listener);

        layout.findViewById(R.id.webchat).setOnClickListener(listener);
        layout.findViewById(R.id.qq).setOnClickListener(listener);
        layout.findViewById(R.id.friends).setOnClickListener(listener);
        layout.findViewById(R.id.weibo).setOnClickListener(listener);
        SwitchButton sb = (SwitchButton)layout.findViewById(R.id.switchbtn);
        
        sb.setTurnOn(VEApplication.getHideTitle());
        sb.setSwitchListener(new SwitchListener() {
            @Override
            public void on() {
                VEApplication.setHideTitle(true);
            }
            
            @Override
            public void off() {
                VEApplication.setHideTitle(false);
            }
        });

        final int cFullFillWidth = 10000;
        layout.setMinimumWidth(cFullFillWidth);
        // set a large value put it in bottom
        Window w = dlg.getWindow();
        WindowManager.LayoutParams lp = w.getAttributes();
        lp.x = 0;
        final int cMakeBottom = -1000;
        lp.y = cMakeBottom;
        lp.gravity = Gravity.BOTTOM;
        dlg.onWindowAttributesChanged(lp);
        dlg.setCanceledOnTouchOutside(true);
        if (cancelListener != null) {
            dlg.setOnCancelListener(cancelListener);
        }
        dlg.setContentView(layout);
        dlg.show();
        return dlg;
    }

}
