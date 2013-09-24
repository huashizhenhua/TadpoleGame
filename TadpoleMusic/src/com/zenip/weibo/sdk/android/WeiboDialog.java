package com.zenip.weibo.sdk.android;

import org.tadpoleframework.view.TadpoleBrowser;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

import com.weibo.sdk.android.WeiboException;
import com.zenip.weibo.sdk.android.util.Utility;

/**
 * 用来显示用户认证界面的dialog，封装了一个webview，通过redirect地址中的参数来获取accesstoken
 * 
 * @author xiaowei6@staff.sina.com.cn
 */
public class WeiboDialog extends Dialog {

	public final static String TAG = "Weibo-WebView";

	private static FrameLayout.LayoutParams FILL = new FrameLayout.LayoutParams(
			ViewGroup.LayoutParams.FILL_PARENT,
			ViewGroup.LayoutParams.FILL_PARENT);

	private static int theme = android.R.style.Theme_Translucent_NoTitleBar;

	private String mUrl;

	private WebView mWebView;

	private WeiboAuthListener mListener;

	private ProgressDialog mSpinner;

	private TadpoleBrowser mTadpoleBrowser;

	public WeiboDialog(Context context, String url, WeiboAuthListener listener) {
		super(context, theme);
		mUrl = "http://www.baidu.com";
		mListener = listener;

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mSpinner = new ProgressDialog(getContext());
		mSpinner.requestWindowFeature(Window.FEATURE_NO_TITLE);
		mSpinner.setMessage("Loading...");
		mSpinner.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(DialogInterface dialog, int keyCode,
					KeyEvent event) {
				onBack();
				return false;
			}

		});
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFeatureDrawableAlpha(Window.FEATURE_OPTIONS_PANEL,
				0);

		mTadpoleBrowser = createTadpoleBrowser();
		mTadpoleBrowser.setLayoutParams(new LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		setContentView(mTadpoleBrowser);

		mWebView = mTadpoleBrowser.getWebView();

		mTadpoleBrowser.loadUrl(mUrl);
	}

	private TadpoleBrowser createTadpoleBrowser() {
		TadpoleBrowser tadpoleBrowser = new TadpoleBrowser(getContext());
		WebView webView = tadpoleBrowser.getWebView();
		// webView.getSettings().setSaveFormData(false);
		// webView.getSettings().setSavePassword(false);
		// webView.setVerticalScrollBarEnabled(false);
		// webView.setHorizontalScrollBarEnabled(false);
		// webView.getSettings().setJavaScriptEnabled(true);
		// webView.setWebViewClient(new WeiboDialog.WeiboWebViewClient());

		return tadpoleBrowser;
	}

	protected void onBack() {
		try {
			mSpinner.dismiss();
			mWebView.stopLoading();
			mWebView.destroy();
		} catch (Exception e) {
		}
		dismiss();
	}

	private class WeiboWebViewClient extends WebViewClient {

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			Log.d(TAG, "Redirect URL: " + url);
			if (url.startsWith("sms:")) { // 针对webview里的短信注册流程，需要在此单独处理sms协议
				Intent sendIntent = new Intent(Intent.ACTION_VIEW);
				sendIntent.putExtra("address", url.replace("sms:", ""));
				sendIntent.setType("vnd.android-dir/mms-sms");
				WeiboDialog.this.getContext().startActivity(sendIntent);
				return true;
			}
			return super.shouldOverrideUrlLoading(view, url);
		}

		@Override
		public void onReceivedError(WebView view, int errorCode,
				String description, String failingUrl) {
			super.onReceivedError(view, errorCode, description, failingUrl);
			mListener.onError(new WeiboDialogError(description, errorCode,
					failingUrl));
			WeiboDialog.this.dismiss();
		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			Log.d(TAG, "onPageStarted URL: " + url);
			if (url.startsWith(Weibo.redirecturl)) {
				handleRedirectUrl(view, url);
				view.stopLoading();
				WeiboDialog.this.dismiss();
				return;
			}
			super.onPageStarted(view, url, favicon);
			mSpinner.show();
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			Log.d(TAG, "onPageFinished URL: " + url);
			super.onPageFinished(view, url);
			if (mSpinner.isShowing()) {
				mSpinner.dismiss();
			}
		}

		public void onReceivedSslError(WebView view, SslErrorHandler handler,
				SslError error) {
			handler.proceed();
		}

	}

	private void handleRedirectUrl(WebView view, String url) {
		Bundle values = Utility.parseUrl(url);

		String error = values.getString("error");
		String error_code = values.getString("error_code");

		if (error == null && error_code == null) {
			mListener.onComplete(values);
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

}
