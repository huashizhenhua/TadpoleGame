package com.tadpolemusic.activity.dialog;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import com.tadpolemusic.R;


/**
 * 
 * the base dialog for this application .
 * 
 * background fade in and fade out
 * 
 * popup center in and popup center out
 * 
 * <br>==========================
 * <br> author：Zenip
 * <br> email：lxyczh@gmail.com
 * <br> create：2013-3-8
 * <br>==========================
 */
public class BaseAlertDialog extends Dialog {

    private ViewGroup mDialogCenter;
    private ViewGroup mHeader;
    private ViewGroup mContent;
    private TextView mTextViewTitle;
    private ViewGroup mDialogRootLayout;
    private OnClickListener mPositiveListener;
    private OnClickListener mNegativeListener;

    private Button mBtnPositive;
    private Button mBtnNegative;

    public BaseAlertDialog(final Context context) {
        super(context, R.style.Dialog_Alert);
        setContentView(R.layout.dialog_alert);

        final BaseAlertDialog me = this;

        mDialogRootLayout = (ViewGroup) this.findViewById(R.id.dialog_background);
        mDialogCenter = (ViewGroup) this.findViewById(R.id.dialog_center);
        mTextViewTitle = (TextView) this.findViewById(R.id.title);
        mHeader = (ViewGroup) this.findViewById(R.id.header);
        mContent = (ViewGroup) this.findViewById(R.id.content);

        // positive
        mBtnPositive = (Button) this.findViewById(R.id.btn_positive);
        mBtnPositive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DialogInterface.OnClickListener outListener = mPositiveListener;
                if (outListener != null) {
                    outListener.onClick(me, 0);
                }
                me.dismissInternal();
            }
        });


        // Negative 
        mBtnNegative = (Button) this.findViewById(R.id.btn_negative);
        mBtnNegative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DialogInterface.OnClickListener outListener = mNegativeListener;
                if (outListener != null) {
                    outListener.onClick(me, 1);
                }
                me.dismissInternal();
            }
        });

        //defaut config
        this.setCancelable(true);
    }



    public void setTitle(String s) {
        mTextViewTitle.setText(s);
    }

    public void setContent(View v) {
        mContent.removeAllViews();
        mContent.addView(v);
    }

    public void setContent(int resId) {
        mContent.removeAllViews();
        View v = LayoutInflater.from(getContext()).inflate(resId, null);
        mContent.addView(v);
    }

    public BaseAlertDialog setPositiveButtonListener(DialogInterface.OnClickListener listener) {
        mPositiveListener = listener;
        return this;
    }

    public BaseAlertDialog setNegativeButtonListenter(DialogInterface.OnClickListener listener) {
        mNegativeListener = listener;
        return this;
    }

    public void dismissInternal() {
        final BaseAlertDialog me = this;
        mDialogCenter.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.popup_center_scale_out));
        Animation ani = AnimationUtils.loadAnimation(getContext(), R.anim.fade_out);
        ani.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                BaseAlertDialog.super.dismiss();
            }
        });
        mDialogRootLayout.startAnimation(ani);
    }

    @Override
    public void show() {
        super.show();
        mDialogCenter.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.popup_center_scale_in));
        mDialogRootLayout.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.fade_in));
    }
}
