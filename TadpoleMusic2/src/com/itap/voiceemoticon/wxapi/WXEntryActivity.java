package com.itap.voiceemoticon.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.tadpolemusic.VEApplication;
import com.tadpolemusic.activity.LeftAndRightActivity;
import com.tencent.mm.sdk.openapi.BaseReq;
import com.tencent.mm.sdk.openapi.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler {
    public static boolean isRunning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(VEApplication.TAG, "---->WXEntryActivity onCreate call isRunning = " + isRunning);
        super.onCreate(savedInstanceState);
        //TODO TRY TO FIX THE MainActivity always recreate by this intent.
        Intent intent = new Intent();
        intent.setClass(this, LeftAndRightActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    @Override
    public void onReq(BaseReq arg0) {
        Log.d(VEApplication.TAG, "---->WXEntryActivity onReq call");

    }

    @Override
    public void onResp(BaseResp arg0) {
        Log.d(VEApplication.TAG, "---->WXEntryActivity onResp call");
    }
}
