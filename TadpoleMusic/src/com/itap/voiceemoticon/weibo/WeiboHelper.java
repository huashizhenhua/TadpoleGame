package com.itap.voiceemoticon.weibo;

import java.util.WeakHashMap;

import org.tadpoleframework.thread.ForegroundThread;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;

import com.itap.voiceemoticon.VEApplication;
import com.itap.voiceemoticon.activity.LoginActivity;
import com.itap.voiceemoticon.activity.MainActivity;
import com.itap.voiceemoticon.activity.Notification;
import com.itap.voiceemoticon.activity.NotificationCenter;
import com.itap.voiceemoticon.activity.NotificationID;
import com.sina.weibo.sdk.WeiboSDK;
import com.sina.weibo.sdk.api.IWeiboAPI;
import com.weibo.sdk.android.Oauth2AccessToken;
import com.weibo.sdk.android.Weibo;
import com.weibo.sdk.android.WeiboAuthListener;
import com.weibo.sdk.android.WeiboDialogError;
import com.weibo.sdk.android.WeiboException;
import com.weibo.sdk.android.sso.SsoHandler;

public class WeiboHelper {

	private static final String TAG = "WeiboHelper";

	public static final String APP_KEY = "1828437997";

	public static final String REDIRECT_URL = "http://www.sina.com";

	public static final String KEY_TOKEN = "access_token";

	public static final String KEY_EXPIRES = "expires_in";

	public static final String KEY_REFRESHTOKEN = "refresh_token";

	public IWeiboAPI sWeiboApi = null;

	public Weibo mWeibo;

	public WeiboLoginListener mListener;

	private Context mContext;

	private static WeiboHelper sInstance = null;

	public static void init(Context context) {
		sInstance = new WeiboHelper(context);
	}

	public static WeiboHelper getInstance() {
		return sInstance;
	}

	public static VEAccount getVEAccount() {
		LoginAccount loginAccount = LoginAcountManager.getInstance()
				.getLastLoginAccount();
		
		if(null == loginAccount) {
			return null;
					
		}
		VEAccount account = new VEAccount();
		account.uid = String.valueOf(loginAccount.uid);
		account.platform = VEAccount.PLATFORM_WEIBO;
		return account;
	}
	
	public boolean isLogin() {
	    return LoginAcountManager.getInstance().isLogin();
	}

	private WeiboHelper(Context context) {
		// 1 初始化SDK
		sWeiboApi = WeiboSDK.createWeiboAPI(context, WeiboHelper.APP_KEY);
		// 2 注册到新浪微博
		sWeiboApi.registerApp();

		mWeibo = Weibo.getInstance(APP_KEY, REDIRECT_URL, null);

		mListener = new WeiboLoginListener(context);

		mContext = context;
	}

	public WeiboLoginListener getListener() {
		return mListener;
	}

	public void sendMusic(Activity activity, String musicUrl) {
		// MusicObject musicObject = new MusicObject();
		// musicObject.duration = 11;
		// musicObject.description = musicUrl;
		// musicObject.identify = musicUrl;
		// musicObject.dataUrl = musicUrl;
		// musicObject.dataHdUrl = musicUrl;
		// musicObject.h5Url = musicUrl;
		// musicObject.actionUrl = musicUrl;
		// musicObject.defaultText = musicUrl;
		// musicObject.title = musicUrl;
		// musicObject.schema = "";
		// musicObject.thumbData = new byte[]{};

		// TextObject textObject = new TextObject();
		// textObject.text = "sdfsdfdsfsd";
		//
		//
		// WeiboMessage weiboMessage = new WeiboMessage();
		// weiboMessage.mediaObject = textObject;
		//
		// SendMessageToWeiboRequest req = new SendMessageToWeiboRequest();
		// req.transaction = String.valueOf(System.currentTimeMillis());
		// req.message = weiboMessage;
		//
		// VEApplication.sWeiboApi.sendRequest(activity, req);
	}

	public boolean isSupportSSO(Activity activity) {
		SsoHandler ssoHandler = new SsoHandler(activity, mWeibo);
		return false;
	}

	public SsoHandler createSsoHandler(Activity activity) {
		SsoHandler ssoHandler = new SsoHandler(activity, mWeibo);
		return ssoHandler;
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

	public void weiboLoginFinish(final Bundle values, final Object tag) {
		final Message msg = (Message) tag;

		String error = values.getString("error");
		String error_code = values.getString("error_code");
		System.out.println("weiboLoginFinish error = " + error
				+ ", error_code = " + error_code);

		if (error == null && error_code == null) {
			final Oauth2AccessToken token = new Oauth2AccessToken();
			token.setToken(values.getString(KEY_TOKEN));
			token.setExpiresIn(values.getString(KEY_EXPIRES));
			token.setRefreshToken(values.getString(KEY_REFRESHTOKEN));
			if (token.isSessionValid()) {
				Log.d("Weibo-authorize",
						"Login Success! access_token=" + token.getToken()
								+ " expires=" + token.getExpiresTime()
								+ " refresh_token=" + token.getRefreshToken());
			} else {
				Log.d("Weibo-authorize", "Failed to receive access token");
			}

			Log.d(TAG, "weiboLoginFinish what = " + msg);
			ForegroundThread.sHandler.post(new Runnable() {
				@Override
				public void run() {
					saveAccount(token);
					Notification notification = NotificationCenter.obtain(NotificationID.N_LOGIN_FINISH, null);
					NotificationCenter.getInstance().notify(notification);
					MainActivity.start(mContext, msg);
				}
			});
		}

		else if (error.equals("access_denied")) {
			// 用户或授权服务器拒绝授予数据访问权限
		}

		else {
			if (error_code == null) {
			} else {
			}
		}
	}

	public void saveAccount(Oauth2AccessToken token) {
		VEApplication.setSinaToken(token);
		User user;
		try {
			user = Account.getUserPreferCache(token);
			LoginAccount loginAccount = new LoginAccount();
			loginAccount.uid = user.id;
			loginAccount.token = token.getToken();
			loginAccount.expiresTime = token.getExpiresTime();
			LoginAcountManager.getInstance().addOrUpdateLoginAcount(
					loginAccount);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void login(final Activity activity, final Message msg) {
		if (isSupportSSO(activity)) {
			SsoHandler ssoHandler = new SsoHandler(activity, mWeibo);
			ssoHandler.authorize(new WeiboAuthListener() {
				@Override
				public void onWeiboException(WeiboException exception) {
				}

				@Override
				public void onError(WeiboDialogError error) {
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

	public void callback(Activity activity, int requestCode, int resultCode,
			Intent data) {
		final WeakHashMap<Activity, SsoHandler> map = mSsoMap;
		SsoHandler handler = map.get(activity);

		System.out.println("handler = " + handler);
		if (null == handler) {
			return;
		}
		handler.authorizeCallBack(requestCode, resultCode, data);
	}

}
