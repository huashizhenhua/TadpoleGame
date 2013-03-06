package com.tadpolemusic.activity.fragment.center;

import java.util.ArrayList;
import java.util.List;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.itap.voiceemoticon.widget.PageListView;
import com.tadpolemusic.VEApplication;
import com.tadpolemusic.activity.fragment.AbsCenterContent;
import com.tadpolemusic.adapter.PullToRefreshListViewAdapter;
import com.tadpolemusic.adapter.VoiceAdapter;
import com.tadpolemusic.api.PageList;
import com.tadpolemusic.api.Voice;
import com.tadpolemusic.media.MusicData;
import com.tadpolemusic.media.PlayAsyncTask;
import com.tadpolemusic.media.PlayListInfo;
import com.tadpolemusic.media.service.MusicPlayerProxy;

public class HotVoiceFragment extends AbsCenterContent {

    private static final String MY_PLAY_LIST_ID = "HotVoiceFragment";

    private PageListView<Voice> mListView;
    private PullToRefreshListViewAdapter<Voice> mVoiceAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final HotVoiceFragment me = this;

        mListView = new PageListView<Voice>(getActivity()) {
            @Override
            public PageList<Voice> onLoadPageList(int startIndex, int maxResult) {
                return VEApplication.getVoiceEmoticonApi().getHostVoicesList(startIndex, maxResult);
            }
        };

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                Log.d(VEApplication.TAG, "HotVoice Fragment onItemClick ");
                me.refreshAndPlay(position);
            }
        });

        mVoiceAdapter = new VoiceAdapter(getActivity());
        mVoiceAdapter.setListView(mListView);
        mListView.setAdapter(mVoiceAdapter);

        mListView.doLoad();

        return mListView;
    }


    public void refreshAndPlay(int position) {
        if(mVoiceAdapter == null){
            return;
        }
        List<? extends MusicData> dataList = mVoiceAdapter.getList();
        new PlayAsyncTask(getActivity(), dataList, MY_PLAY_LIST_ID).execute(position);
        
        
        
//        new AsyncTask<Integer, String, String>() {
//            @Override
//            protected String doInBackground(Integer... params) {
//                final PullToRefreshListViewAdapter<Voice> adapter = mVoiceAdapter;
//
//                if (adapter == null || params.length == 0) {
//                    return "";
//                }
//
//                
//                if (dataList == null) {
//                    return "";
//                }
//
//                int position = params[0];
//
//                final MusicPlayerProxy mpProxy = VEApplication.getMusicPlayer(getActivity());
//                PlayListInfo info = new PlayListInfo();
//                mpProxy.getCurrentPlayListInfo(info);
//
//                boolean needToRefresh = !(MY_PLAY_LIST_ID.equals(info.playListID) && (position < info.listSize));
//                if (needToRefresh) {
//                    mpProxy.refreshMusicList(MY_PLAY_LIST_ID, (List<MusicData>) dataList);
//                }
//                mpProxy.play(position);
//
//                return "";
//            }
//        }.execute(position);
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
