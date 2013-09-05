package com.itap.voiceemoticon.weibo;

import org.tadpoleframework.thread.ForegroundThread;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.os.Parcel;
import android.os.Parcelable;

import com.itap.voiceemoticon.VEApplication;
import com.itap.voiceemoticon.activity.LoginActivity;
import com.itap.voiceemoticon.activity.MainActivity;
import com.sina.weibo.sdk.WeiboSDK;
import com.sina.weibo.sdk.api.IWeiboAPI;
import com.weibo.sdk.android.Oauth2AccessToken;
import com.weibo.sdk.android.Weibo;
import com.weibo.sdk.android.WeiboAuthListener;
import com.weibo.sdk.android.WeiboDialogError;
import com.weibo.sdk.android.WeiboException;
import com.weibo.sdk.android.sso.SsoHandler;

public class WeiboHelper {

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

	private WeiboHelper(Context context) {
		// 1 初始化SDK
		sWeiboApi = WeiboSDK.createWeiboAPI(context, WeiboHelper.APP_KEY);
		// 2 注册到新浪微博
		sWeiboApi.registerApp();

		mWeibo = Weibo.getInstance(APP_KEY, REDIRECT_URL, null);

		mListener = new WeiboLoginListener(context);
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
		return true;
	}

	public void sso(Activity activity, final IWeiboLoginListener listener) {
		SsoHandler ssoHandler = new SsoHandler(activity, mWeibo);
		ssoHandler.authorize(new WeiboAuthListener() {

			@Override
			public void onWeiboException(WeiboException arg0) {
				System.out.println("----->onWeiboException");
			}

			@Override
			public void onError(WeiboDialogError arg0) {
				System.out.println("----->onComplete");
			}

			@Override
			public void onComplete(Bundle values) {
				System.out.println("----->onComplete");
				Oauth2AccessToken token = getToken(values);
				listener.onComplete(token);
			}

			@Override
			public void onCancel() {
				System.out.println("----->onCancel");
				listener.onCancel();
			}
		});
	}

	public static Oauth2AccessToken getToken(Bundle values) {
		Oauth2AccessToken token = new Oauth2AccessToken();
		token.setToken(values.getString(KEY_TOKEN));
		token.setExpiresIn(values.getString(KEY_EXPIRES));
		token.setRefreshToken(values.getString(KEY_REFRESHTOKEN));
		
		System.out.println("getToken = " + token);
		
		// if (token.isSessionValid()) {
		// Log.d("Weibo-authorize",
		// "Login Success! access_token=" + token.getToken()
		// + " expires=" + token.getExpiresTime()
		// + " refresh_token=" + token.getRefreshToken());
		// mListener.onComplete(token);
		// } else {
		// Log.d("Weibo-authorize", "Failed to receive access token");
		// mListener.onWeiboException(new WeiboException(
		// "Failed to receive access token."));
		// }
		return token;
	}

	public void weiboLoginFinish(final Oauth2AccessToken token,
			final Message msg) {
		ForegroundThread.sHandler.post(new Runnable() {
			@Override
			public void run() {
				saveAccount(token);
				MainActivity.start(mContext, msg);
			}
		});
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
			sso(activity, new IWeiboLoginListener() {

				@Override
				public void onWeiboException(WeiboException e) {
				}

				@Override
				public void onWebViewError(int errorCode, String failingUrl,
						String description) {
				}

				@Override
				public void onComplete(Oauth2AccessToken token) {
					weiboLoginFinish(token, msg);
				}

				@Override
				public void onCancel() {

				}
			});
		} else {
			LoginActivity.start(activity, msg);
		}
	}
}
