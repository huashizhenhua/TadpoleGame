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

public class HyperlinkTextExample extends BaseSinglePaneActivity {
	@Override
	protected Fragment onCreatePane() {
		return new HyperlinkTextExampleFragment();
	}
	
	public static class HyperlinkTextExampleFragment extends Fragment{
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
					R.layout.umeng_example_xp_hyperlinktext_activity, container,
					false);
			
			ExchangeViewManager em = new ExchangeViewManager(mContext, new ExchangeDataService("40244"));
			em.addView((ViewGroup) root.findViewById(R.id.link_root),
					ExchangeConstants.type_hypertextlink_banner);
			

			return root;
		}
		
	}
}