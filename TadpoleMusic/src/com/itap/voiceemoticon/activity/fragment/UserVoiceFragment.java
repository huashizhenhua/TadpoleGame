
package com.itap.voiceemoticon.activity.fragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.tadpoleframework.app.AlertDialog;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.itap.voiceemoticon.R;
import com.itap.voiceemoticon.VEApplication;
import com.itap.voiceemoticon.activity.INotify;
import com.itap.voiceemoticon.activity.MainActivity;
import com.itap.voiceemoticon.activity.Notification;
import com.itap.voiceemoticon.activity.NotificationCenter;
import com.itap.voiceemoticon.activity.NotificationID;
import com.itap.voiceemoticon.adapter.MyCollectAdapter;
import com.itap.voiceemoticon.api.Voice;
import com.itap.voiceemoticon.db.UserVoice;
import com.itap.voiceemoticon.db.UserVoiceModel;
import com.itap.voiceemoticon.widget.SegmentBar;
import com.tencent.mm.sdk.openapi.ShowMessageFromWX;

/**
 * <br>=
 * ========================= <br>
 * author：Zenip <br>
 * email：lxyczh@gmail.com <br>
 * create：2013-1-31 <br>=
 * =========================
 */
public class UserVoiceFragment extends BaseFragment implements INotify {

    private ListView mListView;

    private SegmentBar mSegmentBar;

    private MyCollectAdapter mVoiceAdapter;

    private MainActivity mActivity;

    private UserVoiceModel mUserVoiceModel;

    public UserVoiceFragment(MainActivity activity) {
        mActivity = activity;
        mUserVoiceModel = new UserVoiceModel(activity, "");
    }

    public void reloadData() {
        if (mVoiceAdapter != null) {
            this.loadData();
        }
    }

    public View onCreateView(LayoutInflater inflater) {

        NotificationCenter.getInstance().register(this, NotificationID.N_USERVOICE_MAKE);

        View view = inflater.inflate(R.layout.tab_my_collect, null);
        mListView = (ListView)view.findViewById(R.id.list_view_my_collect);
        mSegmentBar = (SegmentBar)view.findViewById(R.id.side_bar_my_collect);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long arg3) {
                Log.d(VEApplication.TAG, "HotVoice Fragment onItemClick ");
                Voice item = (Voice)mVoiceAdapter.getItem(pos);
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
            // System.out.println("lhs fl = " + lhs.getFirstLetter() +
            // ", rhs fl = " + rhs.getFirstLetter());
            if (lhs.getFirstLetter().equals("?")) {
                return 1;
            }

            if (rhs.getFirstLetter().equals("?")) {
                return -1;
            }
            return lhs.getFirstLetter().compareTo(rhs.getFirstLetter());
        }

    };

    private void loadData() {
        ArrayList<UserVoice> list = mUserVoiceModel.getAll();
        ArrayList<Voice> voiceList = new ArrayList<Voice>();

        Voice voice = new Voice();
        for (UserVoice item : list) {
            voice.title = item.title;
            voice.url = item.path;
        }

        Collections.sort(voiceList, myCollectCommparator);
        mVoiceAdapter.setList(voiceList);
    }

    @Override
    public void notify(Notification notification) {
        if (notification.id == NotificationID.N_USERVOICE_MAKE) {
            AlertDialog alertDialog = new AlertDialog(mActivity);
            alertDialog.show();
        }
    }

    @Override
    public void onDestory() {
        super.onDestory();
    }
}
