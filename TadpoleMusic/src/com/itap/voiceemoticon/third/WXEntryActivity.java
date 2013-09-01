package com.itap.voiceemoticon.third;

import com.itap.voiceemoticon.VEApplication;
import com.itap.voiceemoticon.activity.MainActivity;
import com.tencent.mm.sdk.openapi.BaseReq;
import com.tencent.mm.sdk.openapi.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler {
    public static boolean isRunning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(VEApplication.TAG, "---->WXEntryActivity onCreate call isRunning = " + isRunning);
        super.onCreate(savedInstanceState);
        //TODO TRY TO FIX THE MainActivity always recreate by this intent.
        Intent intent = new Intent();
        intent.setClass(this, MainActivity.class);
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
