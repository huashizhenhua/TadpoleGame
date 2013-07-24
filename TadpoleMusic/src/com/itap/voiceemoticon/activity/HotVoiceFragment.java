
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

public class HotVoiceFragment implements AdapterCallback<Voice> {
    private PageListView<Voice> mListView;

    private VoiceAdapter mVoiceAdapter;

    private Activity mActivity;

    public HotVoiceFragment(Activity activity) {
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
        mVoiceAdapter.setCallback(this);
        mVoiceAdapter.setListView(mListView);
        mListView.setAdapter(mVoiceAdapter);

        mListView.doLoad();
        return mListView;
    }

    @Override
    public void onCommand(View view, final  Voice obj, int command) {
        switch (command) {
            case VoiceAdapter.CMD_SHARE:
                WeixinAlert.showAlert(view.getContext(), "发送【" + obj.title + "】", "", new OnAlertSelectId() {
                    @Override
                    public void onClick(Dialog dialog, int whichButton) {
                        boolean isHideTitle = false;
                        SwitchButton sb = (SwitchButton)dialog.findViewById(R.id.switchbtn);
                        isHideTitle = sb.isTurnOn();
                        
                        switch (whichButton) {
                            case R.id.webchat:
                                obj.sendToWeixin(mActivity);
                                break;
                            case R.id.qq:
                                obj.sendToQQ(mActivity, isHideTitle);
                                break;
                            case R.id.friends:
                                obj.sendToFriends(mActivity);
                                break;
                            default:
                                break;
                        }
                        
                        
                    }
                }, null);
                break;
            default:
                break;
        }
    }
}
