package com.umeng.example.xp;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.umeng.example.R;
import com.umeng.ui.BaseSinglePaneActivity;
import com.umeng.xp.UBroadcastReceiver;
import com.umeng.xp.common.ExchangeConstants;
import com.umeng.xp.controller.ExchangeDataService;
import com.umeng.xp.view.ExchangeViewManager;

/**
 * 小把手展示样例
 * 
 * @author Lucas Xu
 * 
 */
public class HandlerExample extends BaseSinglePaneActivity {
	private static ExchangeDataService exchangeDataService;
	private static UBroadcastReceiver uBroadcastReceiver;

	@Override
	protected Fragment onCreatePane() {
		return new HandlerExampleFragment();
	}

	public  static class HandlerExampleFragment extends Fragment {
		Context mContext;

		@Override
		public void onAttach(Activity activity) {
			super.onAttach(activity);
			mContext = activity;
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View root = inflater.inflate(R.layout.umeng_example_xp_banner_activity, container,
					false);
			/**
			 * 两种方式的逻辑不同 模式1 请求数据在现实ImageView 之前 模式2 请求数据将在ImageView 点击之后
			 */

			//SDK定义图片，将无视云端配置图片
			exchangeDataService = new ExchangeDataService();
			uBroadcastReceiver = new UBroadcastReceiver() {
				@Override
				public void onDismiss() {
					super.onDismiss();
					Toast.makeText(mContext, "ListDialog dismiss...", 1).show();
				}

				@Override
				public void onShow() {
					super.onShow();
					Toast.makeText(mContext, "ListDialog show...", 1).show();
				}
			};
			exchangeDataService.registerBroadcast(mContext, uBroadcastReceiver);
			ImageView imageview2 = (ImageView) root.findViewById(R.id.imageview2);
			ExchangeViewManager viewManager = new ExchangeViewManager(mContext, exchangeDataService);
			viewManager.addView(ExchangeConstants.type_list_curtain, imageview2, mContext
					.getResources().getDrawable(R.drawable.umeng_example_handler));

			return root;
		}

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (uBroadcastReceiver != null) exchangeDataService.unregisterBroadcast(this,
				uBroadcastReceiver);

	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}
}