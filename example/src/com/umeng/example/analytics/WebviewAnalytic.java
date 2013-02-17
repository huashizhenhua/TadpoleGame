package com.umeng.example.analytics;

import com.umeng.analytics.MobclickAgent;
import com.umeng.analytics.MobclickAgentJSInterface;
import com.umeng.example.R;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

public class WebviewAnalytic extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView( R.layout.umeng_example_analytics_webview );
		
		WebView webview = (WebView)findViewById(R.id.webview);
		//important , so that you can use js to call Uemng APIs
		new MobclickAgentJSInterface(this, webview, new WebChromeClient());
		webview.loadUrl("file:///android_asset/demo.html");
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPause(this);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onResume(this);
	}
	
	
}
