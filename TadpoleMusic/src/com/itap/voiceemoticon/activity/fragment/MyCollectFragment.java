package com.itap.voiceemoticon.activity.fragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.ListView;

import com.itap.voiceemoticon.R;
import com.itap.voiceemoticon.VEApplication;
import com.itap.voiceemoticon.activity.MainActivity;
import com.itap.voiceemoticon.adapter.ArrayListAdapter;
import com.itap.voiceemoticon.adapter.MyCollectAdapter;
import com.itap.voiceemoticon.api.PageList;
import com.itap.voiceemoticon.api.Voice;
import com.itap.voiceemoticon.db.DaoFactory;
import com.itap.voiceemoticon.widget.SegmentBar;

/**
 * <br>==========================
 * <br> author：Zenip
 * <br> email：lxyczh@gmail.com
 * <br> create：2013-1-31
 * <br>==========================
 */
public class MyCollectFragment extends BaseFragment{
    private static final int HANDLER_FILL_LIST = 1;

    private ListView mListView;
    private SegmentBar mSegmentBar;
    private MyCollectAdapter mVoiceAdapter;
    private MainActivity mActivity;

    public MyCollectFragment(MainActivity activity) {
        mActivity = activity;
    }

    public void reloadData() {
        if (mVoiceAdapter != null) {
            this.loadData();
        }
    }


    public View onCreateView(LayoutInflater inflater) {
        View view = inflater.inflate(R.layout.tab_my_collect, null);
        mListView = (ListView) view.findViewById(R.id.list_view_my_collect);
        mSegmentBar = (SegmentBar) view.findViewById(R.id.side_bar_my_collect);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long arg3) {
                Log.d(VEApplication.TAG, "HotVoice Fragment onItemClick ");
                Voice item = (Voice) mVoiceAdapter.getItem(pos);
                VEApplication.getMusicPlayer(mActivity).playMusic(item.url, item.title);
            }
        });


        mVoiceAdapter = new MyCollectAdapter(mActivity);
        mVoiceAdapter.setListView(mListView);
        mVoiceAdapter.setCallback(mActivity);

        mListView.setOnScrollListener(mVoiceAdapter);
        mListView.setAdapter(mVoiceAdapter);
        mSegmentBar.setListView(mListView);

        mVoiceAdapter.setOnSectionChangeListener(new MyCollectAdapter.OnSectionChangeListener() {
            @Override
            public void handle(char letter) {
                mSegmentBar.setCurrentSection(letter);
            }
        });

        loadData();
        return view;
    }

    /**
     * comparator that use the first letter ofr Chinese pinyin
     */
    private Comparator<Voice> myCollectCommparator = new Comparator<Voice>() {
        @Override
        public int compare(Voice lhs, Voice rhs) {
            //            System.out.println("lhs fl = " + lhs.getFirstLetter() + ", rhs fl = " + rhs.getFirstLetter());
            if (lhs.getFirstLetter().equals("?")) {
                return 1;
            }

            if (rhs.getFirstLetter().equals("?")) {
                return -1;
            }

            return lhs.getFirstLetter().compareTo(rhs.getFirstLetter());
        }

    };


    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
            case HANDLER_FILL_LIST:
                PageList<Voice> pageList = (PageList<Voice>) msg.obj;
                if (pageList != null) {
                    Collections.sort(pageList.records, myCollectCommparator);
                    mVoiceAdapter.setList(pageList.records);
                    mVoiceAdapter.notifyDataSetChanged();
                }
                break;
            default:
                break;
            }
            return false;
        }
    });

    private void loadData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message message = Message.obtain();
                message.what = HANDLER_FILL_LIST;

                ArrayList<Voice> voiceList = DaoFactory.getInstance(mActivity).getVoiceDao().allVoices();
                PageList<Voice> pageList = new PageList<Voice>();
                pageList.records = voiceList;
                pageList.totalCount = voiceList.size();

                message.obj = pageList;
                mHandler.sendMessage(message);
            }
        }).start();
    }
}
