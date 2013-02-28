package com.tadpolemusic.activity.fragment;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore.Audio.Media;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.itap.voiceemoticon.api.PageList;
import com.itap.voiceemoticon.api.Voice;
import com.itap.voiceemoticon.db.DaoFactory;
import com.itap.voiceemoticon.widget.IndexBar;
import com.tadpolemusic.R;
import com.tadpolemusic.VEApplication;
import com.tadpolemusic.adapter.LocalMusicAdapter;
import com.tadpolemusic.media.LocalMusicItem;
import com.tadpolemusic.media.MediaHelper;

/**
 * <br>==========================
 * <br> author：Zenip
 * <br> email：lxyczh@gmail.com
 * <br> create：2013-1-31
 * <br>==========================
 */
public class LocalMusicFragment extends Fragment {
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
                mAdapter.setSelectPostion(pos);
                Log.d(VEApplication.TAG, "HotVoice Fragment onItemClick ");
                LocalMusicItem item = (LocalMusicItem) mAdapter.getItem(pos);
                VEApplication.getMusicPlayer(getActivity()).playMusic(item.getmFilePath(), item.getFileTitle());
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

    /**
     * comparator that use the first letter ofr Chinese pinyin
     */
    private Comparator<LocalMusicItem> mCommparator = new Comparator<LocalMusicItem>() {
        @Override
        public int compare(LocalMusicItem lhs, LocalMusicItem rhs) {
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
                ArrayList<LocalMusicItem> list = (ArrayList<LocalMusicItem>) msg.obj;
                if (list != null) {
                    Collections.sort(list, mCommparator);
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
                MediaHelper helper = new MediaHelper(me.getActivity());
                ArrayList<LocalMusicItem> list = helper.getLocalMusicList();
                System.out.println("list size = " + list.size());

                message.obj = list;
                mHandler.sendMessage(message);
            }
        }).start();
    }
}
