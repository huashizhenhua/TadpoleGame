package com.itap.voiceemoticon.weibo;

import com.weibo.sdk.android.Oauth2AccessToken;
import com.weibo.sdk.android.WeiboException;

public interface IWeiboLoginListener {

	/**
	 * 认证结束后将调用此方法
	 * 
	 * @param values
	 *            Key-value string pairs extracted from the response.
	 *            从responsetext中获取的键值对
	 *            ，键值包括"access_token"，"expires_in"，“refresh_token”
	 */
	public void onComplete(Oauth2AccessToken token);

	/**
	 * 当认证过程中捕获到WeiboException时调用
	 * 
	 * @param e
	 *            WeiboException
	 */
	public void onWeiboException(WeiboException e);

	/**
	 * Oauth2.0认证过程中，当认证对话框中的webview接收数据出现错误时调用此方法
	 * 
	 * @param errorCode
	 * @param failingUrl
	 */
	public void onWebViewError(int errorCode, String failingUrl,
			String description);

	/**
	 * Oauth2.0认证过程中，如果认证窗口被关闭或认证取消时调用
	 */
	public void onCancel();

}
