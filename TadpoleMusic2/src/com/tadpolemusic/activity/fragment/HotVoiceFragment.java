package com.tadpolemusic.activity.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.itap.voiceemoticon.api.PageList;
import com.itap.voiceemoticon.api.Voice;
import com.itap.voiceemoticon.widget.PageListView;
import com.tadpolemusic.VEApplication;
import com.tadpolemusic.adapter.ListViewAdapter;
import com.tadpolemusic.adapter.VoiceAdapter;

public class HotVoiceFragment extends Fragment {
    private PageListView<Voice> mListView;
    private ListViewAdapter<Voice> mVoiceAdapter;


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
                VEApplication.getMusicPlayer(getActivity()).playMusic(item.url, item.title);
            }
        });
        mVoiceAdapter = new VoiceAdapter(getActivity());
        mVoiceAdapter.setListView(mListView);
        mListView.setAdapter(mVoiceAdapter);

        mListView.doLoad();
        return mListView;
    }
}
