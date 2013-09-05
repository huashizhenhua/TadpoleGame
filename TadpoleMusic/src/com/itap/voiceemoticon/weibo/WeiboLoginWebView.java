package com.itap.voiceemoticon.weibo;

import org.tadpoleframework.view.TadpoleWebView;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.weibo.sdk.android.Oauth2AccessToken;
import com.weibo.sdk.android.WeiboException;
import com.weibo.sdk.android.WeiboParameters;
import com.zenip.weibo.sdk.android.util.Utility;

public class WeiboLoginWebView extends TadpoleWebView {

	public static final String KEY_TOKEN = "access_token";

	public static final String KEY_EXPIRES = "expires_in";

	public static final String KEY_REFRESHTOKEN = "refresh_token";

	public static String URL_OAUTH2_ACCESS_AUTHORIZE = "https://open.weibo.cn/oauth2/authorize";

	private IWeiboLoginListener mListener;

	private Oauth2AccessToken mToken;

	public WeiboLoginWebView(Context context) {
		super(context);
		init();
	}

	public WeiboLoginWebView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public WeiboLoginWebView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public void setLoginListener(IWeiboLoginListener listener) {
		mListener = listener;
	}

	private void init() {
		getSettings().setSaveFormData(false);
		getSettings().setSavePassword(false);
		setVerticalScrollBarEnabled(false);
		setHorizontalScrollBarEnabled(false);
		getSettings().setJavaScriptEnabled(true);
		setWebViewClient(new WeiboWebViewClient());
	}

	private class WeiboWebViewClient extends WebViewClient {

		private static final String TAG = "WeiboWebViewClient";

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			Log.d(TAG, "Redirect URL: " + url);
			if (url.startsWith("sms:")) { // 针对webview里的短信注册流程，需要在此单独处理sms协议
				Intent sendIntent = new Intent(Intent.ACTION_VIEW);
				sendIntent.putExtra("address", url.replace("sms:", ""));
				sendIntent.setType("vnd.android-dir/mms-sms");
				view.getContext().startActivity(sendIntent);
				return true;
			}
			return super.shouldOverrideUrlLoading(view, url);
		}

		@Override
		public void onReceivedError(WebView view, int errorCode,
				String description, String failingUrl) {
			Log.d(TAG, "onReceivedError URL: " + failingUrl);
			super.onReceivedError(view, errorCode, description, failingUrl);
			if (null == mListener) {
				return;
			}
			mListener.onWebViewError(errorCode, failingUrl, description);
		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			Log.d(TAG, "onPageStarted URL: " + url);
			if (url.startsWith(WeiboConfig.REDIRECT_URL)) {
				handleRedirectUrl(view, url);
				view.stopLoading();
				return;
			}
			super.onPageStarted(view, url, favicon);
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			Log.d(TAG, "onPageFinished URL: " + url);
			super.onPageFinished(view, url);
		}

		public void onReceivedSslError(WebView view, SslErrorHandler handler,
				SslError error) {
			handler.proceed();
		}

	}

	private void handleRedirectUrl(WebView view, String url) {
		if (null == mListener) {
			return;
		}

		Bundle values = Utility.parseUrl(url);

		String error = values.getString("error");
		String error_code = values.getString("error_code");

		System.out.println("handleRedirectUrl error = " + error
				+ ", error_code = " + error_code);

		if (error == null && error_code == null) {
			handleNoError(values);
		} else if (error.equals("access_denied")) {
			// 用户或授权服务器拒绝授予数据访问权限
			mListener.onCancel();
		} else {
			if (error_code == null) {
				mListener.onWeiboException(new WeiboException(error, 0));
			} else {
				mListener.onWeiboException(new WeiboException(error, Integer
						.parseInt(error_code)));
			}
		}
	}

	private void handleNoError(Bundle values) {
		if (null == mToken) {
			mToken = new Oauth2AccessToken();
		}
		mToken.setToken(values.getString(KEY_TOKEN));
		mToken.setExpiresIn(values.getString(KEY_EXPIRES));
		mToken.setRefreshToken(values.getString(KEY_REFRESHTOKEN));
		if (mToken.isSessionValid()) {
			Log.d("Weibo-authorize",
					"Login Success! access_token=" + mToken.getToken()
							+ " expires=" + mToken.getExpiresTime()
							+ " refresh_token=" + mToken.getRefreshToken());
			mListener.onComplete(mToken);
		} else {
			Log.d("Weibo-authorize", "Failed to receive access token");
			mListener.onWeiboException(new WeiboException(
					"Failed to receive access token."));
		}

	}

	public void login() {
		Context context = getContext();
		WeiboParameters parameters = new WeiboParameters();
		parameters.add("client_id", WeiboConfig.CONSUMER_KEY);
		parameters.add("response_type", "token");
		parameters.add("redirect_uri", WeiboConfig.REDIRECT_URL);
		parameters.add("display", "mobile");

		CookieSyncManager.createInstance(getContext());
		CookieManager cookieManager = CookieManager.getInstance();
		cookieManager.removeAllCookie();

		Oauth2AccessToken token = LoginAcountManager.getInstance()
				.getLastLoginAccessToken();

		System.out.println("accessToken = " + token);

		if (token != null && token.isSessionValid()) {
			parameters.add(KEY_TOKEN, token.getToken());
		}
		String url = URL_OAUTH2_ACCESS_AUTHORIZE + "?"
				+ Utility.encodeUrl(parameters);

		if (context.checkCallingOrSelfPermission(Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
			Utility.showAlert(context, "Error",
					"Application requires permission to access the Internet");
		} else {
			loadUrl(url);
		}
	}
}
