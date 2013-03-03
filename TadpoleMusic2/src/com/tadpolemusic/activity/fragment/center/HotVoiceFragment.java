package com.tadpolemusic.activity.fragment.center;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.itap.voiceemoticon.api.PageList;
import com.itap.voiceemoticon.api.Voice;
import com.itap.voiceemoticon.widget.PageListView;
import com.tadpolemusic.VEApplication;
import com.tadpolemusic.activity.fragment.AbsCenterContent;
import com.tadpolemusic.adapter.PullToRefreshListViewAdapter;
import com.tadpolemusic.adapter.VoiceAdapter;

public class HotVoiceFragment extends AbsCenterContent {
    private PageListView<Voice> mListView;
    private PullToRefreshListViewAdapter<Voice> mVoiceAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mListView = new PageListView<Voice>(getActivity()) {
            @Override
            public PageList<Voice> onLoadPageList(int startIndex, int maxResult) {
                return VEApplication.getVoiceEmoticonApi().getHostVoicesList(startIndex, maxResult);
            }
        };
        

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long arg3) {
                Log.d(VEApplication.TAG, "HotVoice Fragment onItemClick ");
                Voice item = (Voice) mVoiceAdapter.getItem(pos);
                VEApplication.getMusicPlayer(getActivity()).play(pos);
            }
        });

        mVoiceAdapter = new VoiceAdapter(getActivity());
        mVoiceAdapter.setListView(mListView);
        mListView.setAdapter(mVoiceAdapter);
        
        mListView.doLoad();

        return mListView;
    }

    @Override
    public String geTitle() {
        return "热门语音";
    }


    @Override
    public String getUniqueId() {
        return getClass().getName();
    }

    @Override
    public void onMusicPlayingIndexChange(int index) {
    }
}
