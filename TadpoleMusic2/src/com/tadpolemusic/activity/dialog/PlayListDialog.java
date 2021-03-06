package com.tadpolemusic.activity.dialog;


import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.tadpolemusic.R;
import com.tadpolemusic.TMLog;
import com.tadpolemusic.VEApplication;
import com.tadpolemusic.adapter.ListViewAdapter;
import com.tadpolemusic.media.MusicData;
import com.tadpolemusic.media.MusicPlayState;
import com.tadpolemusic.media.MusicPlayer;
import com.tadpolemusic.media.PlayAsyncTask;
import com.tadpolemusic.media.PlayListInfo;
import com.tadpolemusic.media.service.MusicPlayerProxy;


/**
 * 
 * 
 * <br>==========================
 * <br> author：Zenip
 * <br> email：lxyczh@gmail.com
 * <br> create：2013-3-8
 * <br>==========================
 */
public class PlayListDialog extends Dialog {

    private static final String TAG = "PlayListDialog";

    private ViewGroup mDialogCenter;
    private ViewGroup mHeader;
    private ViewGroup mContent;
    private TextView mTextViewTitle;
    private ViewGroup mBackground;

    private Button mBtnClear;
    private ListView mListViewPlayList;
    private ListViewAdapter<MusicData> mListApdater;


    private List<MusicData> mPlayList;
    private PlayListInfo mCurPlayListInfo;

    private Handler mHandler = new Handler();

    private BroadcastReceiver mMusicPlayerReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            final PlayListDialog me = PlayListDialog.this;
            if (intent.getAction().equals(MusicPlayer.BROCAST_NAME)) {
                final Bundle data = intent.getExtras();
                final int state = data.getInt(MusicPlayState.PLAY_STATE_NAME, MusicPlayState.MPS_INVALID);
                final int playListIndex = data.getInt(MusicPlayState.PLAY_MUSIC_INDEX);
                Log.d(VEApplication.TAG, " onReceive intent action = " + intent.getAction() + ", state = " + state + ", playListIndex = " + playListIndex);
                mHandler.post(new Runnable() {

                    @Override
                    public void run() {
                        final ListViewAdapter adapter = mListApdater;
                        if (adapter != null) {
                            adapter.setSelectedPostion(playListIndex);
                            adapter.notifyDataSetChanged();
                        }
                    }
                });
            }
        }
    };



    public PlayListDialog(final Context context) {
        super(context, R.style.Dialog_Alert);
        setContentView(R.layout.dialog_anchor_play_list);



        final PlayListDialog me = this;

        // button clear 
        mBtnClear = (Button) findViewById(R.id.btn_clear);
        mBtnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                me.dismissInternal();
                new PlayListClearDialog(v.getContext()).setPositiveButtonListener(new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final MusicPlayerProxy musicProxy = VEApplication.getMusicPlayer(context);
                        musicProxy.clearPlayingList();
                        me.loadData(context);
                    }
                }).show();
            }
        });


        // Title
        mTextViewTitle = (TextView) this.findViewById(R.id.title);

        //----------------------------------------------------
        // Play List
        //----------------------------------------------------
        mListViewPlayList = (ListView) findViewById(R.id.listview_play_list);
        mListApdater = new ListViewAdapter<MusicData>((Activity) context) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = LayoutInflater.from(context).inflate(R.layout.list_item_dialog_anchor_play_list, null);
                TextView textViewMusicName = (TextView) view.findViewById(R.id.textview_music_name);
                MusicData item = (MusicData) getItem(position);
                textViewMusicName.setText(item.musicName);


                int color = context.getResources().getColor(R.color.simple_black);
                if (this.getSelectedPostion() == position) {
                    color = context.getResources().getColor(R.color.simple_orange);
                }
                textViewMusicName.setTextColor(color);


                return view;
            }
        };
        mListViewPlayList.setAdapter(mListApdater);
        mListViewPlayList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long arg3) {
                new PlayAsyncTask(context, mPlayList, mCurPlayListInfo.playListID).execute(pos);
                mListApdater.setSelectedPostion(pos);
            }
        });


        //-----------------------------------------------
        // other
        //----------------------------------------------
        mBackground = (ViewGroup) this.findViewById(R.id.dialog_background);
        mDialogCenter = (ViewGroup) this.findViewById(R.id.dialog_center);
        mHeader = (ViewGroup) this.findViewById(R.id.header);
        mContent = (ViewGroup) this.findViewById(R.id.content);
        mBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                me.dismissInternal();
            }
        });

        //defaut config
        this.setCancelable(true);

        loadData(context);
    }

    private void loadData(final Context context) {

        new AsyncTask<String, String, String>() {

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                mListViewPlayList.setSelection(mCurPlayListInfo.playingIndex);
                mListApdater.setList(mPlayList);
                mListApdater.setSelectedPostion(mCurPlayListInfo.playingIndex);
                mListApdater.notifyDataSetChanged();
                mTextViewTitle.setText("播放队列(" + mPlayList.size() + ")");
            }

            @Override
            protected String doInBackground(String... params) {
                MusicPlayerProxy musicProxy = VEApplication.getMusicPlayer(context);
                mPlayList = musicProxy.getFileList();
                TMLog.step(TAG, "playList.length=" + mPlayList.size());
                mCurPlayListInfo = musicProxy.getCurPlayListInfo();
                return "";
            }

        }.execute();
    }



    public void setTitle(String s) {
        mTextViewTitle.setText(s);
    }

    public void dismissInternal() {
        final PlayListDialog me = this;
        mDialogCenter.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.popup_center_scale_out));
        Animation ani = AnimationUtils.loadAnimation(getContext(), R.anim.fade_out);
        ani.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                PlayListDialog.super.dismiss();
            }
        });
        mBackground.startAnimation(ani);
    }

    @Override
    public void show() {
        super.show();
        mDialogCenter.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.popup_center_scale_in));
        mBackground.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.fade_in));
    }


    @Override
    protected void onStart() {
        super.onStart();
        getContext().registerReceiver(mMusicPlayerReceiver, VEApplication.getMusicPlayer(getContext()).getBrocastIntentFilter());
    }

    @Override
    protected void onStop() {
        super.onStop();
        getContext().unregisterReceiver(mMusicPlayerReceiver);
    };

}
