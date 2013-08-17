
package com.itap.voiceemoticon.activity.fragment;

import com.itap.voiceemoticon.VEApplication;
import com.itap.voiceemoticon.activity.MainActivity;
import com.itap.voiceemoticon.adapter.VoiceAdapter;
import com.itap.voiceemoticon.api.PageList;
import com.itap.voiceemoticon.api.Voice;
import com.itap.voiceemoticon.widget.PageListView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;

public class HotVoiceFragment extends BaseFragment{
    private PageListView<Voice> mListView;

    private VoiceAdapter mVoiceAdapter;

    private MainActivity mActivity;

    public HotVoiceFragment(MainActivity activity) {
        mActivity = activity;
    }

    public View onCreateView(LayoutInflater inflater) {
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
        return mListView;
    }
    

}
