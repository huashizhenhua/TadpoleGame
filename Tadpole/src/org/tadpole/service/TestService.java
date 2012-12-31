package org.tadpole.service;

import org.tadpole.aidl.IPluginCallback;
import org.tadpole.aidl.IPluginCallback;
import org.tadpole.aidl.PluginServiceConnect;
import org.tadpole.app.PluginActivity;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

public class TestService extends Service {

    private static final String TAG = "PluginService";
    private static IPluginCallback invokerCallback = null;
    private static IPluginCallback clientCallbackHodler = null;

    public static void notify(String msg) {
        Log.i(TAG, "notify clientCallbackHodler = " + clientCallbackHodler);
        if (invokerCallback != null) {
//            invokerCallback.handle(msg);
        }
    }


    @Override
    public IBinder onBind(Intent arg0) {
        Log.i(TAG, "---------------onBind-------------- mBinder = " + mBinder);
        return mBinder;
    }

    private PluginServiceConnect.Stub mBinder = new PluginServiceConnect.Stub() {
        @Override
        public void test(int testInt) throws RemoteException {
            Log.i(TAG, "test");
            Intent intent = new Intent();
            intent.setClass(TestService.this, PluginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            Log.i(TAG, "test end ...");
        }

        @Override
        public void registerCallback(IPluginCallback callback) throws RemoteException {
            Log.i(TAG, "registerCallback");
            invokerCallback = callback;
            Log.i(TAG, "registerCallback end ...");
        }

        @Override
        public void unRegisterCallback(IPluginCallback callback) throws RemoteException {
            Log.i(TAG, "unRegisterCallback");
            invokerCallback = null;
            Log.i(TAG, "unRegisterCallback end ...");
        }

    };

}
