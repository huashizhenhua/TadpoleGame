package com.itap.voiceemoticon.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.itap.voiceemoticon.R;
import com.itap.voiceemoticon.VEApplication;
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
public class MyCollectFragment {
    private static final int HANDLER_FILL_LIST = 1;

    private ListView mListView;
    private SegmentBar mSegmentBar;
    private ArrayListAdapter<Voice> mVoiceAdapter;
    private Activity mActivity;

    public MyCollectFragment(Activity activity) {
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

                // since we has use header view . just do postion -1

                Log.d(VEApplication.TAG, "HotVoice Fragment onItemClick ");
                Voice item = (Voice) mVoiceAdapter.getItem(pos);
                VEApplication.getMusicPlayer(mActivity).playMusic(item.url, item.title);
            }
        });

        mVoiceAdapter = new MyCollectAdapter(mActivity);
        mVoiceAdapter.setListView(mListView);

        mListView.setAdapter(mVoiceAdapter);
        mSegmentBar.setListView(mListView);

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


                Voice voice = new Voice();
                voice.url = "http://qq.djwma.com/mp3/%E4%B8%AD%E5%9B%BD%E5%A5%BD%E5%A3%B0%E9%9F%B3%E7%B2%BE%E9%80%89%E5%A5%BD%E5%90%AC%E6%AD%8C%E6%9B%B2mp3%E4%B8%8B%E8%BD%BD.mp3";
                voice.title = "中国好声音";
                voiceList.add(0, voice);


                PageList<Voice> pageList = new PageList<Voice>();
                pageList.records = voiceList;
                pageList.totalCount = voiceList.size();

                message.obj = pageList;
                mHandler.sendMessage(message);
            }
        }).start();
    }
}
