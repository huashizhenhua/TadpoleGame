package com.tadpolemusic.activity.fragment.center;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.itap.voiceemoticon.widget.IndexBar;
import com.tadpolemusic.R;
import com.tadpolemusic.VEApplication;
import com.tadpolemusic.activity.fragment.AbsCenterContent;
import com.tadpolemusic.adapter.LocalMusicAdapter;
import com.tadpolemusic.media.LocalMusicItem;
import com.tadpolemusic.media.MediaQueryHelper;
import com.tadpolemusic.media.MusicData;
import com.tadpolemusic.media.PlayAsyncTask;

/**
 * <br>==========================
 * <br> author：Zenip
 * <br> email：lxyczh@gmail.com
 * <br> create：2013-1-31
 * <br>==========================
 */
public class LocalMusicFragment extends AbsCenterContent {
    private static final int HANDLER_FILL_LIST = 1;

    private ListView mListView;
    private IndexBar mIndexBar;
    private LocalMusicAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_local_music, null);
        mListView = (ListView) view.findViewById(R.id.list_view_local_music);
        mIndexBar = (IndexBar) view.findViewById(R.id.index_bar_local_music);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long arg3) {
                Log.d(VEApplication.TAG, "HotVoice Fragment onItemClick ");
                new PlayAsyncTask(getActivity(), mAdapter.getList(), "local_music").execute(pos);
            }
        });

        mAdapter = new LocalMusicAdapter(getActivity());
        mAdapter.setListView(mListView);
        mListView.setOnScrollListener(mAdapter);
        mListView.setAdapter(mAdapter);
        mIndexBar.setListView(mListView);

        mAdapter.setOnSectionChangeListener(new LocalMusicAdapter.OnSectionChangeListener() {
            @Override
            public void handle(char letter) {
                mIndexBar.setCurrentSection(letter);
            }
        });

        loadData();
        return view;
    }

    @Override
    public void onDestroy() {
        Log.d(VEApplication.TAG, "---------Destroy-----------");
        super.onDestroy();
    }

    /**
     * comparator that use the first letter ofr Chinese pinyin
     */
    private Comparator<MusicData> mCommparator = new Comparator<MusicData>() {
        @Override
        public int compare(MusicData lhs, MusicData rhs) {
            if (lhs.getFirstLetterInUpcase().equals("?")) {
                return 1;
            }
            if (rhs.getFirstLetterInUpcase().equals("?")) {
                return -1;
            }
            return lhs.getFirstLetterInUpcase().compareTo(rhs.getFirstLetterInUpcase());
        }
    };


    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
            case HANDLER_FILL_LIST:
                ArrayList<MusicData> list = (ArrayList<MusicData>) msg.obj;
                if (list != null) {
                    mAdapter.setList(list);
                    mAdapter.notifyDataSetChanged();
                }
                break;
            default:
                break;
            }
            return false;
        }
    });

    private void loadData() {
        final LocalMusicFragment me = this;
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message message = Message.obtain();
                message.what = HANDLER_FILL_LIST;
                MediaQueryHelper helper = new MediaQueryHelper(me.getActivity());
                ArrayList<MusicData> list = helper.getLocalMusicDatas();

                // sort
                Collections.sort(list, mCommparator);

                // put int musicplay
                VEApplication.getMusicPlayer(me.getActivity()).refreshMusicList("localmusic", list);

                System.out.println("list size = " + list.size());
                message.obj = list;
                mHandler.sendMessage(message);
            }
        }).start();
    }

    @Override
    public String geTitle() {
        return "本地音乐";
    }

    @Override
    public String getUniqueId() {
        return getClass().getName();
    }

    @Override
    public void onMusicPlayingIndexChange(int index) {
        mAdapter.setSelectedPostion(index);
        mAdapter.notifyDataSetChanged();
    }
}
