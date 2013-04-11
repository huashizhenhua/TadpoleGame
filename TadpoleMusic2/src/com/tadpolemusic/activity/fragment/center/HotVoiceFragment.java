package com.tadpolemusic.activity.fragment.center;

import java.util.List;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.itap.voiceemoticon.widget.LoadingUtil;
import com.itap.voiceemoticon.widget.PageListView;
import com.tadpolemusic.VEApplication;
import com.tadpolemusic.activity.fragment.AbsCenterContent;
import com.tadpolemusic.adapter.PullToRefreshListViewAdapter;
import com.tadpolemusic.adapter.VoiceAdapter;
import com.tadpolemusic.api.PageList;
import com.tadpolemusic.api.Voice;
import com.tadpolemusic.media.MusicData;
import com.tadpolemusic.media.PlayAsyncTask;

public class HotVoiceFragment extends AbsCenterContent {

    private static final String MY_PLAY_LIST_ID = "HotVoiceFragment";
    private PageListView<Voice> mListView;
    private PullToRefreshListViewAdapter<Voice> mVoiceAdapter;
    private View mLoadingView;

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        final HotVoiceFragment me = this;

        mListView = new PageListView<Voice>(getActivity()) {
            @Override
            public PageList<Voice> onLoadPageList(int startIndex, int maxResult) {
                PageList<Voice> pageList = VEApplication.getVoiceEmoticonApi().getHostVoicesList(startIndex, maxResult);
                me.getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mLoadingView != null) {
                            container.removeView(mLoadingView);
                            mLoadingView = null;
                        }
                    }
                });
                return pageList;
            }
        };

        // 设置ListView样式
        ListView listView = mListView.getRefreshableView();
        listView.setDivider(null);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                Log.d(VEApplication.TAG, "HotVoice Fragment onItemClick ");
                //since we use headerview , so we must do this.
                position = position - 1;
                me.refreshAndPlay(position);
            }
        });

        mVoiceAdapter = new VoiceAdapter(getActivity());
        mVoiceAdapter.setListView(mListView);
        mListView.setAdapter(mVoiceAdapter);

        mListView.doLoad();


        mLoadingView = LoadingUtil.getLoadingWidget(getActivity());
        container.addView(mLoadingView);

        return mListView;
    }


    public void refreshAndPlay(int position) {
        if (mVoiceAdapter == null) {
            return;
        }
        List<? extends MusicData> dataList = mVoiceAdapter.getList();
        new PlayAsyncTask(getActivity(), dataList, MY_PLAY_LIST_ID).execute(position);
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
