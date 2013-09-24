package com.itap.voiceemoticon.weibo;

import org.tadpoleframework.view.TadpoleWebView;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import android.os.Message;
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
	
	private Object mTag;

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
			if (url.startsWith(TPAccountManager.REDIRECT_URL)) {
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
	
	public void setTag(Object tag) {
		mTag = tag;
	}

	private void handleRedirectUrl(WebView view, String url) {
		Bundle values = Utility.parseUrl(url);
		TPAccountManager.getInstance().weiboLoginFinish(values, mTag);
	}

	public void login() {
		Context context = getContext();
		WeiboParameters parameters = new WeiboParameters();
		parameters.add("client_id", TPAccountManager.APP_KEY);
		parameters.add("response_type", "code");
		parameters.add("redirect_uri", TPAccountManager.REDIRECT_URL);
		parameters.add("display", "mobile");
		parameters.add("scope", "");
		parameters.add("packagename", context.getPackageName());
		parameters.add("key_hash", com.weibo.sdk.android.util.Utility.getSign(context, context.getPackageName()));

		CookieSyncManager.createInstance(getContext());
		CookieManager cookieManager = CookieManager.getInstance();
		cookieManager.removeAllCookie();

		Oauth2AccessToken token = WeiboLoginAcountManager.getInstance()
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
