
package com.itap.voiceemoticon.activity.fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.itap.voiceemoticon.R;
import com.itap.voiceemoticon.activity.MainActivity;
import com.umeng.newxp.controller.ExchangeDataService;
import com.umeng.newxp.view.ExchangeViewManager;

public class AppRecommendFragment {

    private MainActivity mActivity;

    public AppRecommendFragment(MainActivity activity) {
        mActivity = activity;
    }

    public View onCreateView(LayoutInflater inflater) {
        ViewGroup viewGroup = (ViewGroup)inflater.inflate(R.layout.activity_recommend, null);
        ViewGroup fatherLayout = (ViewGroup)viewGroup.findViewById(R.id.ad);
        ListView listView = (ListView)viewGroup.findViewById(R.id.list);
        ExchangeViewManager exchangeViewManager = new ExchangeViewManager(mActivity,
                new ExchangeDataService());
        exchangeViewManager.addView(fatherLayout, listView);
        return viewGroup;
    }
}
