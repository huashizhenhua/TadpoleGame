
package com.itap.voiceemoticon.activity.fragment;

import com.itap.voiceemoticon.R;
import com.itap.voiceemoticon.activity.MainActivity;
import com.umeng.newxp.controller.ExchangeDataService;
import com.umeng.newxp.view.ExchangeViewManager;

import net.youmi.android.AdManager;
import net.youmi.android.diy.banner.DiyAdSize;
import net.youmi.android.diy.banner.DiyBanner;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;

public class AppRecommendFragment extends BaseFragment{

    private MainActivity mActivity;

    public AppRecommendFragment(MainActivity activity) {
        mActivity = activity;
    }

    public View onCreateView(LayoutInflater inflater) {
        // 初始化应用的发布ID和密钥，以及设置测试模式
        AdManager.getInstance(mActivity).init("f4c12ac956d1bdb6","ebaec11e527854aa", false); 
        
        ViewGroup viewGroup = (ViewGroup)inflater.inflate(R.layout.activity_recommend, null);
        
     // 获取要嵌入迷你广告条的布局
        RelativeLayout adLayout = (RelativeLayout)viewGroup.findViewById(R.id.AdLayout);
        // demo 1 迷你Banner : 宽满屏，高32dp
        DiyBanner banner = new DiyBanner(mActivity, DiyAdSize.SIZE_MATCH_SCREENx32);// 传入高度为32dp的AdSize来定义迷你Banner
        // 将积分Banner加入到布局中
        adLayout.addView(banner);

        ViewGroup fatherLayout = (ViewGroup)viewGroup.findViewById(R.id.ad);
        ListView listView = (ListView)viewGroup.findViewById(R.id.list);
        ExchangeViewManager exchangeViewManager = new ExchangeViewManager(mActivity,
                new ExchangeDataService());
        exchangeViewManager.addView(fatherLayout, listView);
        return viewGroup;
    }
    
}
