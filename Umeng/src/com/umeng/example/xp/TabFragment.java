package com.umeng.example.xp;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabWidget;
import android.widget.TextView;

import com.umeng.example.R;
import com.umeng.ui.BaseSinglePaneActivity;
import com.umeng.xp.controller.ExchangeDataService;
import com.umeng.xp.view.ExchangeViewManager;

public class TabFragment extends BaseSinglePaneActivity {
	@Override
	protected Fragment onCreatePane() {
		return new TabsFragment();
	}

	public static class TabsFragment extends Fragment implements OnTabChangeListener {
		public static final String TAB_APP = "精品应用";
		public static final String TAB_WEB = "精彩网站";

		private View mRoot;
		private TabHost mTabHost;
		private int mCurrentTab;
		private TabWidget mTabWidget;
		private Context mContext;

		@Override
		public void onAttach(Activity activity) {
			super.onAttach(activity);
			mContext = activity;
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			mRoot = inflater.inflate(R.layout.umeng_example_xp_tabfragment, container, false);
			mTabHost = (TabHost) mRoot.findViewById(android.R.id.tabhost);
			mTabWidget = (TabWidget) mRoot.findViewById(android.R.id.tabs);
			setupTabs();
			return mRoot;
		}

		@Override
		public void onActivityCreated(Bundle savedInstanceState) {
			super.onActivityCreated(savedInstanceState);
			setRetainInstance(true);

			mTabHost.setOnTabChangedListener(this);
			mTabHost.setCurrentTab(mCurrentTab);

			ListView l1 = (ListView) mRoot.findViewById(R.id.list_1);
			ListView l2 = (ListView) mRoot.findViewById(R.id.list_2);

			ViewGroup vg1 = (ViewGroup) mRoot.findViewById(R.id.father1);
			ViewGroup vg2 = (ViewGroup) mRoot.findViewById(R.id.father2);

			ExchangeDataService exchangeDataService1 = new ExchangeDataService();
			exchangeDataService1.setKeywords(TAB_APP);
			new ExchangeViewManager(mContext, exchangeDataService1).addView(vg1, l1);
			ExchangeDataService exchangeDataService2 = new ExchangeDataService();
			exchangeDataService2.setKeywords(TAB_WEB);
			exchangeDataService2.show_progress_wheel = false;
			new ExchangeViewManager(mContext, exchangeDataService2).addView(vg2, l2);
		}

		private void setupTabs() {
			mTabHost.setup(); // must call this before adding  tabs!

			View view1 = LayoutInflater.from(mContext).inflate(
					R.layout.umeng_example_tab_indicator, null);
			((TextView) view1.findViewById(R.id.umeng_example_tab_text)).setText(TAB_APP);
			View view2 = LayoutInflater.from(mContext).inflate(
					R.layout.umeng_example_tab_indicator, null);
			((TextView) view2.findViewById(R.id.umeng_example_tab_text)).setText(TAB_WEB);

			mTabHost.addTab(mTabHost.newTabSpec(TAB_APP).setIndicator(view1)
					.setContent(R.id.list_1));
			mTabHost.addTab(mTabHost.newTabSpec(TAB_WEB).setIndicator(view2)
					.setContent(R.id.list_2));

		}

		@Override
		public void onTabChanged(String tabId) {
			if (TAB_APP.equals(tabId)) {
				mTabWidget.setBackgroundResource(R.drawable.umeng_example_two_tab_left);
				return;
			}
			if (TAB_WEB.equals(tabId)) {
				mTabWidget.setBackgroundResource(R.drawable.umeng_example_two_tab_right);
				return;
			}
		}
	}

}
