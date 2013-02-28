package com.umeng.example.xp;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.umeng.example.R;
import com.umeng.ui.BaseSinglePaneActivity;
import com.umeng.xp.common.ExchangeConstants;
import com.umeng.xp.controller.ExchangeDataService;
import com.umeng.xp.view.ExchangeViewManager;
import com.umeng.xp.view.GridTemplateConfig;

/**
 * 小把手展示样例
 * 
 * @author Lucas Xu
 * 
 */
public class FullIconExample extends BaseSinglePaneActivity {
	@Override
	protected Fragment onCreatePane() {
		return new HandlerExampleFragment();
	}

	public static class HandlerExampleFragment extends Fragment {
		Context mContext;

		@Override
		public void onAttach(Activity activity) {
			super.onAttach(activity);
			mContext = activity;
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View root = inflater.inflate(R.layout.umeng_example_xp_container_activity, container,
					false);

			ExchangeConstants.CONTAINER_AUTOEXPANDED = false;

			ViewGroup fatherLayout = (ViewGroup) root.findViewById(R.id.ad);
			ListView listView = (ListView) root.findViewById(R.id.list);

			ExchangeDataService exchangeDataService = new ExchangeDataService("");
			exchangeDataService.setTemplate(1);
			ExchangeViewManager exchangeViewManager = new ExchangeViewManager(mContext,
					exchangeDataService);
			exchangeViewManager.setGridTemplateConfig(new GridTemplateConfig().setMaxPsize(9).setNumColumns(3).setVerticalSpacing(13));
			exchangeViewManager.addView(fatherLayout, listView);

			return root;
		}
	}
}