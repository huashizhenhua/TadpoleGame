package com.itap.voiceemoticon.activity;

import org.tadpoleframework.app.LoadDialog;
import org.tadpoleframework.thread.ForegroundThread;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.itap.voiceemoticon.MsgDef;
import com.itap.voiceemoticon.R;
import com.itap.voiceemoticon.VEApplication;
import com.itap.voiceemoticon.weibo.Account;
import com.itap.voiceemoticon.weibo.LoginAccount;
import com.itap.voiceemoticon.weibo.LoginAcountManager;
import com.itap.voiceemoticon.weibo.User;
import com.itap.voiceemoticon.weibo.IWeiboLoginListener;
import com.itap.voiceemoticon.weibo.WeiboHelper;
import com.itap.voiceemoticon.weibo.WeiboLoginWebView;
import com.weibo.sdk.android.Oauth2AccessToken;
import com.weibo.sdk.android.WeiboException;

public class LoginActivity extends FragmentActivity implements
		IWeiboLoginListener {

	private static final String TAG = "LoginActivity";

	private static final String KEY_AUTO_LOGIN = "auto_login";

	private static final String KEY_TOKEN = "token";

	private static final String KEY_MESSAGE = "message";

	static final long SPLASH_TIME = 2000;

	private WeiboLoginWebView mWebView;

	private Oauth2AccessToken mToken;

	private Message mMessage = null;

	private Handler mHandler = new Handler(new Handler.Callback() {

		@Override
		public boolean handleMessage(Message msg) {
			if (msg.what == MsgDef.MSG_DIALOG_SHOW) {
				showLoadDialog();
			}
			if (MsgDef.MSG_DIALOG_HIDE == msg.what) {
				hideLoadDialog();
			}
			if (MsgDef.MSG_LOGIN_FINISH == msg.what) {
				setResult(RESULT_OK);
				finish();
			}
			return false;
		}
	});

	public void sendMessage(int what) {
		mHandler.sendEmptyMessage(what);
	}

	private void showLoadDialog() {
		final LoadDialog loadDialog = mLoadDialog;
		try {
			loadDialog.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void hideLoadDialog() {
		final LoadDialog loadDialog = mLoadDialog;
		try {
			loadDialog.hide();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Use Explicit Intent start Activity
	 * 
	 * @param activity
	 * @param uid
	 */
	public static void start(Activity activity, Message message) {
		Intent intent = new Intent();
		intent.setClass(activity, LoginActivity.class);
		intent.putExtra(KEY_MESSAGE, message);
		activity.startActivity(intent);
	}

	public static void startActivityForResult(Activity activity,
			int requestCode, Message message) {
		Intent intent = new Intent();
		intent.setClass(activity, LoginActivity.class);
		intent.putExtra(KEY_MESSAGE, message);
		activity.startActivityForResult(intent, requestCode);
	}

	/**
	 * Use Explicit Intent start Activity
	 * 
	 * @param activity
	 * @param uid
	 */
	public static void startWithOutAutoLogin(Activity activity, Message message) {
		Intent intent = new Intent();
		intent.setClass(activity, LoginActivity.class);
		intent.putExtra(KEY_AUTO_LOGIN, false);
		intent.putExtra(KEY_MESSAGE, message);
		activity.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		mLoadDialog = new LoadDialog(this);

		Intent intent = getIntent();
		boolean autoLogin = intent.getBooleanExtra(KEY_AUTO_LOGIN, true);
		mToken = (Oauth2AccessToken) intent.getSerializableExtra(KEY_TOKEN);
		mMessage = (Message) intent.getParcelableExtra(KEY_MESSAGE);

		if (mToken == null) {
			mToken = LoginAcountManager.getInstance().getLastLoginAccessToken();
		}

		if (autoLogin && null != mToken && mToken.isSessionValid()) {
			finish();
		} else {
			if (null != mToken && !mToken.isSessionValid()) {
				Toast.makeText(this, "登录失败，请重新登录", Toast.LENGTH_LONG);
			}
		}

		if (WeiboHelper.getInstance().isSupportSSO(this)) {
			WeiboHelper.getInstance().sso(this, this);
			return;
		}

		mWebView = (WeiboLoginWebView) findViewById(R.id.webview);
		mWebView.setLoginListener(this);
		mWebView.login();

	}

	private LoadDialog mLoadDialog = null;

	@Override
	public void onComplete(final Oauth2AccessToken token) {
		if (null == token) {
			return;
		}
		sendMessage(MsgDef.MSG_DIALOG_SHOW);
		sendMessage(MsgDef.MSG_LOGIN_FINISH);
		
		WeiboHelper.getInstance().weiboLoginFinish(token, mMessage);
	}

	@Override
	public void finish() {
		super.finish();
		hideLoadDialog();
	}


	@Override
	public void onWeiboException(WeiboException e) {

	}

	public void onWebViewError(int errorCode, String failingUrl,
			String description) {

	}

	@Override
	public void onCancel() {
		finish();
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
	}

	protected void onSaveInstanceState(Bundle outState) {
	}

}
