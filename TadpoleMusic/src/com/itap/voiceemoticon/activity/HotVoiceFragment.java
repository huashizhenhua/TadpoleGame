
package com.itap.voiceemoticon.activity;

import android.app.Activity;
import android.app.Dialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;

import com.itap.voiceemoticon.R;
import com.itap.voiceemoticon.VEApplication;
import com.itap.voiceemoticon.adapter.VoiceAdapter;
import com.itap.voiceemoticon.api.PageList;
import com.itap.voiceemoticon.api.Voice;
import com.itap.voiceemoticon.third.WeixinHelper;
import com.itap.voiceemoticon.widget.PageListView;
import com.itap.voiceemoticon.widget.WeixinAlert;
import com.itap.voiceemoticon.widget.WeixinAlert.OnAlertSelectId;
import com.tencent.mm.sdk.MMSharedPreferences;
import com.umeng.common.net.o;

import org.tadpoleframework.app.AlertDialog;
import org.tadpoleframework.widget.SwitchButton;
import org.tadpoleframework.widget.adapter.AdapterCallback;

public class HotVoiceFragment {
    private PageListView<Voice> mListView;

    private VoiceAdapter mVoiceAdapter;

    private MainActivity mActivity;

    public HotVoiceFragment(MainActivity activity) {
        mActivity = activity;
    }

    public View onCreateView(LayoutInflater inflater) {
        mListView = new PageListView<Voice>(mActivity) {
            @Override
            public PageList<Voice> onLoadPageList(int startIndex, int maxResult) {
                return VEApplication.getVoiceEmoticonApi().getHostVoicesList(startIndex, maxResult);
            }
        };
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long arg3) {
                Log.d(VEApplication.TAG, "HotVoice Fragment onItemClick ");
                Voice item = (Voice)mVoiceAdapter.getItem(pos);
                VEApplication.getMusicPlayer(mActivity).playMusic(item.url, item.title);
            }
        });
        mVoiceAdapter = new VoiceAdapter(mActivity);
        mVoiceAdapter.setCallback(mActivity);
        mVoiceAdapter.setListView(mListView);
        mListView.setAdapter(mVoiceAdapter);

        mListView.doLoad();
        return mListView;
    }

}
