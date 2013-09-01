
package com.itap.voiceemoticon.activity;

import org.tadpoleframework.app.LoadDialogAsyncTask;
import org.tadpoleframework.app.NavSplashScreenActivity;

import android.accounts.Account;
import android.app.Activity;
import android.app.LauncherActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import com.itap.voiceemoticon.R;
import com.itap.voiceemoticon.weibo.LoginAccount;
import com.itap.voiceemoticon.weibo.WeiboLoginListener;
import com.itap.voiceemoticon.weibo.WeiboLoginWebView;
import com.tencent.stat.common.User;
import com.weibo.sdk.android.Oauth2AccessToken;
import com.weibo.sdk.android.WeiboException;

public class LoginActivity extends NavSplashScreenActivity implements WeiboLoginListener {
    private static final String TAG = "LoginActivity";

    private static final String KEY_AUTO_LOGIN = "auto_login";

    private static final String KEY_TOKEN = "token";

    static final long SPLASH_TIME = 2000;

    static final String UID = "uid";

    static final String USER = "user";

    private WeiboLoginWebView mWebView;

    private Oauth2AccessToken mToken;

    private Handler mHandler = new Handler();

    /**
     * Use Explicit Intent start Activity
     * 
     * @param activity
     * @param uid
     */
    public static void start(Activity activity) {
        Intent intent = new Intent();
        intent.setClass(activity, LoginActivity.class);
        activity.startActivity(intent);
    }
    
    public static void startWithToken(Activity activity, Oauth2AccessToken acToken) {
        Intent intent = new Intent();
        intent.setClass(activity, LoginActivity.class);
        intent.putExtra(KEY_TOKEN, acToken);
        activity.startActivity(intent);
    }

    /**
     * Use Explicit Intent start Activity
     * 
     * @param activity
     * @param uid
     */
    public static void startWithOutAutoLogin(Activity activity) {
        Intent intent = new Intent();
        intent.setClass(activity, LoginActivity.class);
        intent.putExtra(KEY_AUTO_LOGIN, false);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getNavBar().setTitle("登录");
        getNavBar().getBtnRight().setVisibility(View.INVISIBLE);

        Intent intent = getIntent();
        boolean autoLogin = intent.getBooleanExtra(KEY_AUTO_LOGIN, true);
        mToken = (Oauth2AccessToken)intent.getSerializableExtra(KEY_TOKEN);

        if (!RuntimeFlags.getInstance().hasSplashShow()) {
            getWindow().setWindowAnimations(R.style.Tadpole_Dialog_SplashScreen);
            showSplashScreen();
            RuntimeFlags.getInstance().markSplashShow();
        }

        
        if (mToken == null) {
            mToken = XTZApplication.getLoginAccountManager().getLastLoginAccessToken();
        }

        if (autoLogin && null != mToken && mToken.isSessionValid()) {
            startLauncherActivity(mToken);
        } else {
            
            if(null != mToken && !mToken.isSessionValid()) {
                Toast.makeText(this, "登录失败，请重新登录", Toast.LENGTH_LONG);
            }
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    removeSplashScreen();
                }
            }, SPLASH_TIME);
        }

        mWebView = (WeiboLoginWebView)findViewById(R.id.webview);
        mWebView.setLoginListener(this);
        mWebView.login();

    }

    @Override
    public void onComplete(Oauth2AccessToken token) {

        System.out.println("onComplete token = " + token);

        startLauncherActivity(token);
    }

    @Override
    public void onWeiboException(WeiboException e) {

    }

    @Override
    public void onWebViewError(int errorCode, String failingUrl, String description) {

    }

    @Override
    public void onCancel() {

    }

    public void startLauncherActivity(final Oauth2AccessToken token) {
        final long startTime = System.currentTimeMillis();
        final Activity me = this;
        XTZApplication.setSinaToken(token);
        
        
        new LoadDialogAsyncTask<String, String, Boolean>(this) {

            protected void onPreExecute() {
                if (Account.hasCache(token) == false) {
                    showLoadDialog();
                }
            };

            @Override
            protected Boolean doInBackground(String... params) {
                try {
                    User user = Account.getUserPreferCache(token);

                    LoginAccount loginAccount = new LoginAccount();
                    loginAccount.uid = user.id;
                    loginAccount.token = token.getToken();
                    loginAccount.expiresTime = token.getExpiresTime();
                    XTZApplication.getLoginAccountManager().addOrUpdateLoginAcount(loginAccount);

                    // 获取用户信息
                    XTZApplication.setCurUser(user);
                    // 获取表情 2013.6.10, 读文件IO太慢，不应该阻塞主流程，故心开辟县城
                    Emotion.cacheEmotionsInBackground();
                    // 获取表情end
                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return false;
            };

            protected void onPostExecute(Boolean result) {
                super.onPostExecute(result);
                hideLoadDialog();

                if (result.booleanValue() == true) {
                    Runnable runable = new Runnable() {
                        @Override
                        public void run() {
                            finish();
                            LauncherActivity.start(me, XTZApplication.getCurUser());
                        }
                    };

                    long timeSpan = System.currentTimeMillis() - startTime;
                    if (isSplashScreenShowing()) {
                        mHandler.postDelayed(runable, SPLASH_TIME - timeSpan);
                    } else {
                        runable.run();
                    }
                } else {
                    Toast.makeText(me, "获取用户信息失败", Toast.LENGTH_LONG).show();
                    removeSplashScreen();
                }
            }
        }.execute("");
    }
    
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    protected void onSaveInstanceState(Bundle outState) {
    }

    @Override
    public void onComplete(com.weibo.sdk.android.Oauth2AccessToken token) {
        // TODO Auto-generated method stub
        
    };

}
