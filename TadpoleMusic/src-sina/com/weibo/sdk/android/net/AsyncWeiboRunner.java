package com.weibo.sdk.android.net;

import android.util.Log;

import com.weibo.sdk.android.WeiboException;
import com.weibo.sdk.android.WeiboParameters;

/**
 * 
 * @author luopeng (luopeng@staff.sina.com.cn)
 */
public class AsyncWeiboRunner {
    protected static final String TAG = null;

    /**
     * 请求接口数据，并在获取到数据后通过RequestListener将responsetext回传给调用者
     * 
     * @param url
     *            服务器地址
     * @param params
     *            存放参数的容器
     * @param httpMethod
     *            "GET"or “POST”
     * @param listener
     *            回调对象
     */
    public static void request(final String url, final WeiboParameters params, final String httpMethod, final RequestListener listener) {
        new Thread() {
            @Override
            public void run() {
                try {
                    Log.d(TAG, "request = " + url);
                    String resp = HttpManager.openUrl(url, httpMethod, params, params.getValue("pic"));
                    Log.d(TAG, "response = " + resp);
                    listener.onComplete(resp);
                } catch (WeiboException e) {
                    e.printStackTrace();
                    listener.onError(e);
                }
            }
        }.start();

    }

}
