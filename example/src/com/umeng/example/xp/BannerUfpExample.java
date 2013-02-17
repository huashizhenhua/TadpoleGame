package com.umeng.example.xp;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.umeng.example.R;
import com.umeng.ui.BaseSinglePaneActivity;
import com.umeng.xp.common.ExchangeConstants;
import com.umeng.xp.controller.ExchangeDataService;
import com.umeng.xp.view.ExchangeViewManager;

/**
 * UFP SDK 集成示例。 请在http://ufp.umeng.com注册成为友盟UFP (Umeng For Publisher) 系统用户。
 * 并添加广告。
 * @author lucas
 *
 */
public class BannerUfpExample extends BaseSinglePaneActivity {
	/**
	 * 此id 为http://ufp.umeng.com 友盟UFP (Umeng For Publisher) 系统添加的广告位ID。
	 * 相关注册说明请咨询友盟客服。
	 */
	private static final String SLOT_ID_BANNER = "40160";
	
	@Override
	protected Fragment onCreatePane() {
		return new BannerExampleFragment();
	}
	
	public static class BannerExampleFragment extends Fragment {
		Context mContext;
		
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
			
			// 找到一个添加banner 的父亲节点，将banner View 附着到这个节点上
			ViewGroup parent = (ViewGroup)root.findViewById(R.id.parent);		
			
			ExchangeDataService service = new ExchangeDataService(SLOT_ID_BANNER);
			ExchangeViewManager viewMgr = new ExchangeViewManager(mContext, service);
			viewMgr.addView(parent, ExchangeConstants.type_standalone_handler);
			
			return root;
		}
	}
}