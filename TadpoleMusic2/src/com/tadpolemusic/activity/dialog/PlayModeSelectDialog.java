package com.tadpolemusic.activity.dialog;


import com.tadpolemusic.R;
import com.tadpolemusic.media.MusicPlayMode;
import com.tadpolemusic.media.MusicPlayState;

import android.content.Context;
import android.widget.CheckBox;
import android.widget.RadioGroup;
import android.widget.TextView;

public class PlayModeSelectDialog extends BaseAlertDialog {

    private RadioGroup mRadioGroup;

    public PlayModeSelectDialog(Context context) {
        super(context);
        setContent(R.layout.dialog_alert_play_mode_select);
        mRadioGroup = (RadioGroup) findViewById(R.id.radiogroup);
        setTitle("选择播放模式");
    }

    private void check(int buttonId) {
        mRadioGroup.check(buttonId);
    }


    public void setPlayMode(int playMode) {
        int buttonId = 0;
        switch (playMode) {
        case MusicPlayMode.MPM_LIST_LOOP_PLAY:
            buttonId = R.id.radiobtn_playmode_repeate_all;
            break;
        case MusicPlayMode.MPM_ORDER_PLAY:
            buttonId = R.id.radiobtn_playmode_sequence;
            break;
        case MusicPlayMode.MPM_RANDOM_PLAY:
            buttonId = R.id.radiobtn_playmode_repeate_random;
            break;
        case MusicPlayMode.MPM_SINGLE_LOOP_PLAY:
            buttonId = R.id.radiobtn_playmode_repeate_single;
            break;

        default:
            break;
        }

        check(buttonId);

    }

    public int getPlayMode() {
        int buttonId = mRadioGroup.getCheckedRadioButtonId();
        int playMode = 0;
        switch (buttonId) {
        case R.id.radiobtn_playmode_repeate_all:
            playMode = MusicPlayMode.MPM_LIST_LOOP_PLAY;
            break;
        case R.id.radiobtn_playmode_sequence:
            playMode = MusicPlayMode.MPM_ORDER_PLAY;
            break;
        case R.id.radiobtn_playmode_repeate_random:
            playMode = MusicPlayMode.MPM_RANDOM_PLAY;
            break;
        case R.id.radiobtn_playmode_repeate_single:
            playMode = MusicPlayMode.MPM_SINGLE_LOOP_PLAY;
            break;
        default:
            break;
        }
        return playMode;
    }
}
