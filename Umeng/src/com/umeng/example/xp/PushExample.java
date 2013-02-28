package com.umeng.example.xp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;

import com.umeng.ui.BaseSinglePaneActivity;
import com.umeng.xp.common.ExchangeConstants;
import com.umeng.xp.controller.ExchangeDataService;
import com.umeng.xp.controller.XpListenersCenter;
import com.umeng.xp.controller.XpListenersCenter.FloatDialogListener;
import com.umeng.xp.view.ExchangeViewManager;
import com.umeng.xp.view.FloatDialogConfig;

/**
 * 友盟应用联盟 SDK 集成示例。 请在http://www.umeng.com注册成为友盟应用联盟系统用户。
 * 
 * @author lucas
 *
 */
public class PushExample extends BaseSinglePaneActivity {
	PushBasicExample mFragment;
	@Override
	protected Fragment onCreatePane() {
		mFragment = new PushBasicExample();
		return mFragment;
	}
	
	public static class PushBasicExample extends Fragment {
		Context mContext;
		private ExchangeViewManager vMgr;
		
		@Override
	    public void onAttach(Activity activity) {
	        super.onAttach(activity);
	        mContext = activity;
	    }
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			com.umeng.common.Log.LOG = true;
			View view = new View(getActivity());
			view.setBackgroundColor(Color.DKGRAY);
			ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
			view.setLayoutParams(lp);
			
			final FloatDialogListener pushListener = new XpListenersCenter.FloatDialogListener(){
				@Override
				public void onStart() {
					Log.d("TestData", "onStart");
				}

				@Override
				public void onPrepared(int status) {
					Log.d("TestData", "onPrepared "+status);
					
				}

				@Override
				public boolean onConfirmClickWithCallBackUrl(String msg) {
					startActivity(new Intent(getActivity(),
							ContainerHeaderExample.class));
					return super.onConfirmClickWithCallBackUrl(msg);
				}
			};
			
			//Push Ad 弹出代码
			ExchangeDataService es = new ExchangeDataService("40473");
			vMgr = new ExchangeViewManager(mContext, es);
			FloatDialogConfig config = new FloatDialogConfig().setTimeout(6000).setDelay(true).setListener(pushListener).setDelayProgress(30).setNativeFlag(1);
			
			vMgr.setFloatDialogConfig(config);
			vMgr.addView(null, ExchangeConstants.type_float_dialog);
		
			return view;
		}
		
		public void oriChanged(int orientation){
			if(vMgr != null)
				vMgr.onOrientationChanaged(orientation);
		}
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		if(mFragment !=  null) mFragment.oriChanged(newConfig.orientation);
	}
	
	
}