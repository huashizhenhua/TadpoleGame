package com.itap.voiceemoticon.weibo;

import com.weibo.sdk.android.Oauth2AccessToken;
import com.weibo.sdk.android.WeiboException;

import android.content.Context;

public class WeiboLoginListener implements IWeiboLoginListener {

	private Context mContext;

	public WeiboLoginListener(Context context) {
		mContext = context;
	}

	@Override
	public void onComplete(Oauth2AccessToken token) {
	}

	@Override
	public void onWeiboException(WeiboException e) {
	}

	@Override
	public void onWebViewError(int errorCode, String failingUrl,
			String description) {
	}

	@Override
	public void onCancel() {
	}
}
