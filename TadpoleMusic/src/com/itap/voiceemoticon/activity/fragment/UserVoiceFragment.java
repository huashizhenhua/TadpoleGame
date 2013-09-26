
package com.itap.voiceemoticon.activity.fragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.tadpoleframework.thread.ForegroundThread;
import org.tadpoleframework.widget.PageListView;
import org.tadpoleframework.widget.adapter.AdapterCallback;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.LinearLayout.LayoutParams;

import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.itap.voiceemoticon.R;
import com.itap.voiceemoticon.VEApplication;
import com.itap.voiceemoticon.activity.INotify;
import com.itap.voiceemoticon.activity.LoginActivity;
import com.itap.voiceemoticon.activity.MainActivity;
import com.itap.voiceemoticon.activity.Notification;
import com.itap.voiceemoticon.activity.NotificationCenter;
import com.itap.voiceemoticon.activity.NotificationID;
import com.itap.voiceemoticon.adapter.MyCollectAdapter;
import com.itap.voiceemoticon.adapter.VoiceAdapter;
import com.itap.voiceemoticon.api.Voice;
import com.itap.voiceemoticon.db.UserVoice;
import com.itap.voiceemoticon.db.UserVoiceModel;
import com.itap.voiceemoticon.profit.GoogleAdmob;
import com.itap.voiceemoticon.util.StringUtil;
import com.itap.voiceemoticon.weibo.WeiboLoginAcountManager;
import com.itap.voiceemoticon.weibo.TPAccount;
import com.itap.voiceemoticon.weibo.TPAccountManager;
import com.itap.voiceemoticon.widget.SegmentBar;

/**
 * <br>=
 * ========================= <br>
 * author：Zenip <br>
 * email：lxyczh@gmail.com <br>
 * create：2013-1-31 <br>=
 * =========================
 */
public class UserVoiceFragment extends BaseFragment implements INotify, AdapterCallback<Voice>,
        OnClickListener {

    private PageListView<Voice> mListView;

    private boolean mFirstLoad = true;

    private View mViewLogin;

    private Button mBtnLogin;

    private SegmentBar mSegmentBar;

    private MyCollectAdapter mVoiceAdapter;

    private MainActivity mActivity;

    private UserVoiceModel mUserVoiceModel;

    public UserVoiceFragment(MainActivity activity) {
        mActivity = activity;
        
        NotificationCenter.getInstance().register(this, NotificationID.N_USERVOICE_MAKE);
        NotificationCenter.getInstance().register(this, NotificationID.N_USERVOICE_MODEL_SAVE);
        NotificationCenter.getInstance().register(this, NotificationID.N_USERVOICE_MODEL_DELETE);
        NotificationCenter.getInstance().register(this, NotificationID.N_LOGIN_FINISH);
        NotificationCenter.getInstance().register(this, NotificationID.N_LOGOUT);
    }

    public View onCreateView(LayoutInflater inflater) {
        LinearLayout layout = GoogleAdmob.createLayoutWithAd(mActivity);

        View view = inflater.inflate(R.layout.tab_my_collect, null);
        mListView = (PageListView)view.findViewById(R.id.list_view_my_collect);
        mSegmentBar = (SegmentBar)view.findViewById(R.id.side_bar_my_collect);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long arg3) {
                pos = pos - 1;

                Log.d(VEApplication.TAG, "HotVoice Fragment onItemClick ");
                Voice item = (Voice)mVoiceAdapter.getItem(pos);
                // 优先使用本地播放路径
                String playUrl = item.localPlayPath;
                if (StringUtil.isBlank(playUrl)) {
                    playUrl = item.url;
                }
                VEApplication.getMusicPlayer(mActivity).playMusic(item.url, item.title);
            }
        });

        mViewLogin = view.findViewById(R.id.login);
        mBtnLogin = (Button)view.findViewById(R.id.btn_login);

        mVoiceAdapter = new MyCollectAdapter(mActivity);
        mVoiceAdapter.setListView(mListView.getRefreshableView());
        mVoiceAdapter.setCallback(this);

        mListView.setOnScrollListener(mVoiceAdapter);
        mListView.setMode(Mode.PULL_FROM_START);
        mListView.setAdapter(mVoiceAdapter);
        mListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase refreshView) {
                loadDataFromRemote();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase refreshView) {

            }
        });
        mSegmentBar.setListView(mListView.getRefreshableView());

        mVoiceAdapter.setOnSectionChangeListener(new MyCollectAdapter.OnSectionChangeListener() {
            @Override
            public void handle(char letter) {
                mSegmentBar.setCurrentSection(letter);
            }
        });

        // state login
        if (TPAccountManager.getInstance().isLogin()) {
            stateLogin();
        }
        // state unlogin
        else {
            stateLogout();
        }

        view.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        layout.addView(view);
        return layout;
    }
    
    
    public void stateLogin() {
        mViewLogin.setVisibility(View.GONE);
        mListView.firePullDownToRefresh();
        TPAccount curAccount = TPAccountManager.getVEAccount();
        String appUid = curAccount.platform + curAccount.uid;
        mUserVoiceModel = new UserVoiceModel(mActivity, appUid);
    }
    
    public void stateLogout() {
        mViewLogin.setVisibility(View.VISIBLE);
        mBtnLogin.setOnClickListener(this);
        mUserVoiceModel = null;
        mVoiceAdapter.clear();
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

        Voice voice = null;
        for (UserVoice item : list) {
            voice = new Voice();
            voice.title = item.title;
            voice.url = item.url;
            voice.localPlayPath = item.path;
            voiceList.add(voice);
        }

        Collections.sort(voiceList, myCollectCommparator);
        mVoiceAdapter.setList(voiceList);
    }

    private void loadDataFromRemote() {
        if (false == TPAccountManager.getInstance().isLogin()) {
            return;
        }


        ForegroundThread.sHandler.post(new Runnable() {
            @Override
            public void run() {
                TPAccount curAccount = TPAccountManager.getVEAccount();
                String appUid = curAccount.platform + curAccount.uid;
                UserVoiceModel userVoiceModel = new UserVoiceModel(mActivity, appUid);

                boolean needLoadFromRemote = true;
                ArrayList<UserVoice> userVoiceList = null;
                // 第一次加载, 尝试使用磁盘缓存
                if (mFirstLoad) {
                    mFirstLoad = false;
                    userVoiceList = userVoiceModel.getAll();
                    if (userVoiceList != null && userVoiceList.size() != 0) {
                        userVoiceList = new ArrayList<UserVoice>(userVoiceList);
                        needLoadFromRemote = false;
                    }
                }

                if (needLoadFromRemote) {
                    userVoiceList = VEApplication.getVoiceEmoticonApi().getList(curAccount.uid,
                            curAccount.platform);
                    // System.out.println("loadDataFromRemote = " +
                    // userVoiceList);
                }

                Voice voice = null;
                ArrayList<Voice> voiceList = new ArrayList<Voice>();
                for (UserVoice item : userVoiceList) {
                    voice = new Voice();
                    voice.title = item.title;
                    voice.url = item.url;
                    voice.localPlayPath = item.path;
                    voiceList.add(voice);
                    userVoiceModel.add(item); // 添加至内存中
                }
                Collections.sort(voiceList, myCollectCommparator);
                postSetList(voiceList);
            }
        });
    }

    public void postSetList(final ArrayList<Voice> voiceList) {
        mActivity.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                mVoiceAdapter.setList(voiceList);
                if (null == mListView) {
                    return;
                }
                mListView.onRefreshComplete();
            }
        });
    }

    @Override
    public void notify(Notification notification) {
        if (notification.id == NotificationID.N_USERVOICE_MODEL_SAVE) {
            mListView.firePullDownToRefresh();
        } 
        
        else if (notification.id == NotificationID.N_LOGIN_FINISH) {
            stateLogin();
            mListView.firePullDownToRefresh();
        }
        
        else if (notification.id == NotificationID.N_LOGOUT ) {
            stateLogout();
        }

        if (notification.id == NotificationID.N_USERVOICE_MODEL_DELETE) {
            loadData();
            System.out.println("notify = " + mUserVoiceModel.getAll());
        }
    }

    @Override
    public void onDestory() {
        super.onDestory();
        System.out.println("UserVoiceFragment.onDestory()");

        NotificationCenter.getInstance().unregister(this, NotificationID.N_USERVOICE_MAKE);
        NotificationCenter.getInstance().unregister(this, NotificationID.N_USERVOICE_MODEL_SAVE);
    }

    @Override
    public void onCommand(View view, final Voice obj, int command, int position) {
        if (command == VoiceAdapter.CMD_DELETE) {

            AlertDialog.Builder ab = new AlertDialog.Builder(mActivity);
            ab.setTitle("确定要删除?");
            ab.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    UserVoice userVoice = new UserVoice();
                    userVoice.title = obj.title;
                    userVoice.url = obj.url;
                    
                    userVoice = mUserVoiceModel.getByEqual(userVoice);
                    System.out.println("userVoice to delete = " + userVoice);
                    
                    mUserVoiceModel.delete(userVoice);

                    if (null == userVoice) {
                        return;
                    }
                    
                    final long userVoiceId = userVoice.id;
                    ForegroundThread.sHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            VEApplication.getVoiceEmoticonApi().delete(new long[] {
                                userVoiceId
                            });
                        }
                    });

                }
            });
            ab.setNegativeButton("取消", null);
            ab.show();
            return;
        }

        mActivity.onCommand(view, obj, command, position);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_login) {
            TPAccountManager.getInstance().login(mActivity, Message.obtain());
        }
    }
}
