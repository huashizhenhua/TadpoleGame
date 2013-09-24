
package com.itap.voiceemoticon.activity.fragment;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import com.google.ads.AdRequest;
import com.itap.voiceemoticon.VEApplication;
import com.itap.voiceemoticon.activity.INotify;
import com.itap.voiceemoticon.activity.MainActivity;
import com.itap.voiceemoticon.activity.Notification;
import com.itap.voiceemoticon.activity.NotificationCenter;
import com.itap.voiceemoticon.activity.NotificationID;
import com.itap.voiceemoticon.adapter.VoiceAdapter;
import com.itap.voiceemoticon.api.PageList;
import com.itap.voiceemoticon.api.Voice;
import com.itap.voiceemoticon.profit.GoogleAdmob;
import com.itap.voiceemoticon.widget.PageListView;

public class HotVoiceFragment extends BaseFragment implements INotify{
    private PageListView<Voice> mListView;

    private VoiceAdapter mVoiceAdapter;

    private MainActivity mActivity;
    

    public HotVoiceFragment(MainActivity activity) {
        mActivity = activity;
    }

    public View onCreateView(LayoutInflater inflater) {
    	NotificationCenter.getInstance().register(this, NotificationID.N_MY_COLLECT_CHANGE);
    	
    	LinearLayout layout = GoogleAdmob.createLayoutWithAd(mActivity);
    	
    	/*
        net.youmi.android.banner.AdView adView = new net.youmi.android.banner.AdView(mActivity, net.youmi.android.banner.AdSize.SIZE_320x50);
        adView.setLayoutParams(lp);
        layout.addView(adView);
        */
        
        mListView = new PageListView<Voice>(mActivity) {
            @Override
            public PageList<Voice> onLoadPageList(int startIndex, int maxResult) {
                System.out.println("onLoadPageList");
                return VEApplication.getVoiceEmoticonApi().getHostVoicesList(startIndex, maxResult);
            }
        };
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long arg3) {
                Log.d(VEApplication.TAG, "HotVoice Fragment onItemClick ");
                Voice item = (Voice)mVoiceAdapter.getItem(pos);
                VEApplication.getMusicPlayer(mActivity).playMusic(item.url, item.title);
            }
        });
        mVoiceAdapter = new VoiceAdapter(mActivity);
        mVoiceAdapter.setCallback(mActivity);
        mVoiceAdapter.setListView(mListView);
        mListView.setAdapter(mVoiceAdapter);

        mListView.doLoad();
        
        mListView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        layout.addView(mListView);
        
        
        return layout;
    }

	@Override
	public void notify(Notification notification) {
		// TODO Auto-generated method stub
		if(NotificationID.N_MY_COLLECT_CHANGE == notification.id){
			mVoiceAdapter.notifyDataSetChanged();
		}	
	}
    

}
