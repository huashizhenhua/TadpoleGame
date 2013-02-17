package com.umeng.example.xp;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.umeng.example.R;
import com.umeng.ui.BaseSinglePaneActivity;
import com.umeng.xp.UBroadcastReceiver;
import com.umeng.xp.common.ExchangeConstants;
import com.umeng.xp.controller.ExchangeDataService;
import com.umeng.xp.view.ExchangeViewManager;

/**
 * 小把手展示样例
 * @author Lucas Xu
 *
 */
public class HandlerUfpExample extends BaseSinglePaneActivity {

	/**
	 * 此id 为http://ufp.umeng.com 友盟UFP (Umeng For Publisher) 系统添加的广告位ID。
	 * 相关注册说明请咨询友盟客服。
	 */
	private static final String SLOT_ID_Handler = "40167";
	private UBroadcastReceiver uBroadcastReceiver ;
	@Override
	protected Fragment onCreatePane() {
		return new HandlerExampleFragment(uBroadcastReceiver);
	}
	
	public  class HandlerExampleFragment extends Fragment{
		Context mContext;
		UBroadcastReceiver mBroadcastReceiver;
		
		public HandlerExampleFragment(UBroadcastReceiver uBroadcastReceiver) {
			super();
			mBroadcastReceiver = uBroadcastReceiver;
		}

		@Override
	    public void onAttach(Activity activity) {
	        super.onAttach(activity);
	        mContext = activity;
	    }
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View root = inflater.inflate(
					R.layout.umeng_example_xp_banner_activity, container,
					false);
			com.umeng.common.Log.LOG = true;
			// ViewGroup 
			final ExchangeDataService exchangeDataService3 = new ExchangeDataService(SLOT_ID_Handler);
			
			mBroadcastReceiver = new UBroadcastReceiver(){
				@Override
				public void onShow() {
					super.onShow();
					Toast.makeText(mContext, "ListDialog is show..", Toast.LENGTH_SHORT).show();
				}

				@Override
				public void onDismiss() {
					super.onDismiss();
					Toast.makeText(mContext, "ListDialog is dismiss..", Toast.LENGTH_SHORT).show();
				}
				
			};
			exchangeDataService3.registerBroadcast(mContext, mBroadcastReceiver);
			
			RelativeLayout relayout1 = (RelativeLayout) root.findViewById(R.id.rlayout1);
			exchangeDataService3.setTemplate(0);
			new ExchangeViewManager(mContext, exchangeDataService3)
				.addView(ExchangeConstants.type_list_curtain, relayout1);
			
			return root;
		}
		
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(uBroadcastReceiver != null)
			unregisterReceiver(uBroadcastReceiver);
	}
	
	
	
}