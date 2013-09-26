
package com.itap.voiceemoticon.weibo;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.WeakHashMap;

import org.tadpoleframework.common.StringUtil;
import org.tadpoleframework.thread.ForegroundThread;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.webkit.CookieSyncManager;
import android.widget.Toast;

import com.itap.voiceemoticon.VEApplication;
import com.itap.voiceemoticon.activity.LoginActivity;
import com.itap.voiceemoticon.activity.MainActivity;
import com.itap.voiceemoticon.activity.Notification;
import com.itap.voiceemoticon.activity.NotificationCenter;
import com.itap.voiceemoticon.activity.NotificationID;
import com.sina.weibo.sdk.WeiboSDK;
import com.sina.weibo.sdk.api.IWeiboAPI;
import com.sina.weibo.sdk.api.MusicObject;
import com.sina.weibo.sdk.api.SendMultiMessageToWeiboRequest;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.weibo.sdk.android.Oauth2AccessToken;
import com.weibo.sdk.android.Weibo;
import com.weibo.sdk.android.WeiboAuthListener;
import com.weibo.sdk.android.WeiboDialogError;
import com.weibo.sdk.android.WeiboException;
import com.weibo.sdk.android.WeiboParameters;
import com.weibo.sdk.android.net.AsyncWeiboRunner;
import com.weibo.sdk.android.net.RequestListener;
import com.weibo.sdk.android.sso.SsoHandler;
import com.weibo.sdk.android.util.Utility;

public class TPAccountManager {

    private static final String TAG = "WeiboHelper";

    public static final String APP_KEY = "1828437997";

    public static final String APP_SECRET = "1828437997";

    public static final String REDIRECT_URL = "http://www.sina.com";

    public static final String KEY_TOKEN = "access_token";

    public static final String KEY_EXPIRES = "expires_in";

    public static final String KEY_REFRESHTOKEN = "refresh_token";

    public IWeiboAPI sWeiboApi = null;

    public Weibo mWeibo;

    public WeiboLoginListener mListener;

    private Context mContext;

    private static TPAccountManager sInstance = null;

    public static void init(Context context) {
        sInstance = new TPAccountManager(context);
    }

    public static TPAccountManager getInstance() {
        return sInstance;
    }

    public static TPAccount getVEAccount() {
        WeiboLoginAccount loginAccount = WeiboLoginAcountManager.getInstance()
                .getLastLoginAccount();

        if (null == loginAccount) {
            return null;

        }
        TPAccount account = new TPAccount();
        account.uid = String.valueOf(loginAccount.uid);
        account.platform = TPAccount.PLATFORM_WEIBO;
        return account;
    }

    public boolean isLogin() {
        return WeiboLoginAcountManager.getInstance().isLogin();
    }

    private TPAccountManager(Context context) {
        // 1 初始化SDK
        sWeiboApi = WeiboSDK.createWeiboAPI(context, TPAccountManager.APP_KEY);
        // 2 注册到新浪微博
        sWeiboApi.registerApp();

        mWeibo = Weibo.getInstance(APP_KEY, REDIRECT_URL, null);

        mListener = new WeiboLoginListener(context);

        mContext = context;
    }

    public WeiboLoginListener getListener() {
        return mListener;
    }

    public void sendMusic(Activity activity, String title, String description, String musicUrl) {
        MusicObject musicObject = new MusicObject();
        musicObject.duration = 11;
        musicObject.description = description;
        musicObject.title = title;
        musicObject.identify = musicUrl;
        musicObject.dataUrl = musicUrl;
        musicObject.dataHdUrl = musicUrl;
        musicObject.h5Url = musicUrl;
        musicObject.actionUrl = musicUrl;
        musicObject.defaultText = title;
        musicObject.schema = musicUrl;
        musicObject.thumbData = new byte[] {};

        TextObject textObject = new TextObject();
        textObject.text = "分享语音表情：" + title + "(" + description + ")";

        WeiboMultiMessage weiboMessage = new WeiboMultiMessage();
        weiboMessage.mediaObject = musicObject;
        weiboMessage.textObject = textObject;

        SendMultiMessageToWeiboRequest req = new SendMultiMessageToWeiboRequest();
        req.transaction = String.valueOf(System.currentTimeMillis());
        req.multiMessage = weiboMessage;

        sWeiboApi.sendRequest(activity, req);
    }

    public boolean isSupportSSO(Activity activity) {
        return sWeiboApi.isWeiboAppInstalled();
    }

    private WeakHashMap<Activity, SsoHandler> mSsoMap = new WeakHashMap<Activity, SsoHandler>();

    public static Oauth2AccessToken getToken(Bundle values) {
        Oauth2AccessToken token = new Oauth2AccessToken();
        token.setToken(values.getString(KEY_TOKEN));
        token.setExpiresIn(values.getString(KEY_EXPIRES));
        token.setRefreshToken(values.getString(KEY_REFRESHTOKEN));
        System.out.println("getToken = " + token);
        return token;
    }

    public void login(final Activity activity, final Message msg) {
        if (isSupportSSO(activity)) {
            SsoHandler ssoHandler = new SsoHandler(activity, mWeibo);
            ssoHandler.authorize(new WeiboAuthListener() {
                @Override
                public void onWeiboException(WeiboException exception) {
                    LoginActivity.start(activity, msg);
                }

                @Override
                public void onError(WeiboDialogError error) {
                    LoginActivity.start(activity, msg);
                }

                @Override
                public void onComplete(Bundle values) {
                    weiboLoginFinish(values, msg);
                }

                @Override
                public void onCancel() {
                }
            });
            mSsoMap.put(activity, ssoHandler);
        } else {
            LoginActivity.start(activity, msg);
        }
    }

    private static String URL_OAUTH2_ACCESS_AUTHORIZE = "https://open.weibo.cn/oauth2";

    private void FtchAccessToken(String authorization_code, final Message msg) {
        
        System.out.println("FtchAccessToken");
        
        WeiboParameters params = new WeiboParameters();
        params.add("client_id", TPAccountManager.APP_KEY);
        params.add("client_secret", TPAccountManager.APP_SECRET);
        params.add("grant_type", "authorization_code");
        params.add("code", authorization_code);
        params.add("redirect_uri", TPAccountManager.REDIRECT_URL);

        AsyncWeiboRunner.request(URL_OAUTH2_ACCESS_AUTHORIZE + "/access_token", params, "POST",
                new RequestListener() {
                    public void onComplete(String response) {
                        System.out.println("FtchAccessToken");
                        final Oauth2AccessToken token = new Oauth2AccessToken(response);
                        handleToken(token, msg);
                    }
                    public void onComplete4binary(ByteArrayOutputStream responseOS) {
                    }

                    public void onIOException(IOException e) {
                        e.printStackTrace();
                    }

                    public void onError(WeiboException e) {
                        e.printStackTrace();
                    }
                });
    }
    
    public void handleToken(final Oauth2AccessToken token, final Message msg) {
        System.out.println("handleToken");
        if (token.isSessionValid()) {
            System.out.println("Login Success! access_token=" + token.getToken() + " expires="
                            + token.getExpiresTime() + " refresh_token="
                            + token.getRefreshToken());
            ForegroundThread.sHandler.post(new Runnable() {
                @Override
                public void run() {
                    saveAccount(token);
                    Notification notification = NotificationCenter.obtain(
                            NotificationID.N_LOGIN_FINISH, null);
                    NotificationCenter.getInstance().notify(notification);
                    MainActivity.start(mContext, msg);
                }
            });
        } else {
            System.out.println("Failed to receive access token");
        }
    }

    public void weiboLoginFinish(final Bundle values, final Object tag) {
        final Message msg = (Message)tag;

        String error = values.getString("error");
        String error_code = values.getString("error_code");
        String code = values.getString("code");
        System.out.println("weiboLoginFinish error = " + error + ", error_code = " + error_code
                + ", code = " + code);

        if (error == null && error_code == null) {
            
            // 走非code流程
            if(StringUtil.isEmpty(code)) {
                String accessToken = values.getString("access_token");
                String refreshToken = values.getString("refresh_token");
                String expiresIn = values.getString("expires_in");
                Oauth2AccessToken token = new Oauth2AccessToken();
                token.setToken(accessToken);
                token.setExpiresIn(expiresIn);
                token.setRefreshToken(refreshToken);
                handleToken(token, msg);
            } 
            
            // 走code流程
            else {
                CookieSyncManager.getInstance().sync();
                FtchAccessToken(code, msg);
            }
        }
        
        else {
            VEApplication.toast("登录失败");
        }
    }

    public void logout() {
        WeiboLoginAcountManager.getInstance().logout();
        NotificationCenter.obtain(NotificationID.N_LOGOUT).notifyToTarget();
    }

    private void saveAccount(Oauth2AccessToken token) {
        VEApplication.setSinaToken(token);
        User user;
        try {
            user = Account.getUserPreferCache(token);
            WeiboLoginAccount loginAccount = new WeiboLoginAccount();
            loginAccount.uid = user.id;
            loginAccount.token = token.getToken();
            loginAccount.expiresTime = token.getExpiresTime();
            WeiboLoginAcountManager.getInstance().addOrUpdateLoginAcount(loginAccount);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void callback(Activity activity, int requestCode, int resultCode, Intent data) {
        final WeakHashMap<Activity, SsoHandler> map = mSsoMap;
        SsoHandler handler = map.get(activity);
        System.out.println("handler = " + handler);
        if (null == handler) {
            return;
        }
        handler.authorizeCallBack(requestCode, resultCode, data);
    }

}
