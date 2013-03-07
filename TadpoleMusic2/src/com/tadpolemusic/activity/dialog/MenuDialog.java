package com.tadpolemusic.activity.dialog;

import android.app.Dialog;
import android.content.Context;
import android.media.AudioManager;
import android.provider.Settings;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.tadpolemusic.R;

public class MenuDialog extends Dialog {

    private AudioManager mAudioManager;
    private SeekBar mSound;
    private SeekBar mBrightness;

    public MenuDialog(final Context context) {
        super(context, R.style.Dialog_Fullscreen_Transparent);
        setContentView(R.layout.main_dialog_menu);


        // ---------------------------------------
        // Sound Controll
        // ----------------------------------------

        mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        mSound = (SeekBar) findViewById(R.id.seek_bar_sound);
        final int volumeMax = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        final int volumeCur = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        mSound.setMax(volumeMax);
        mSound.setProgress(volumeCur);
        mSound.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, seekBar.getProgress(), 0);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }
        });



        // ---------------------------------------
        // Brightness Controll
        // ----------------------------------------
        mBrightness = (SeekBar) findViewById(R.id.seek_bar_brightness);
        // 进度条绑定最大亮度，255是最大亮度
        mBrightness.setMax(255);
        // 取得当前亮度
        int normal = Settings.System.getInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, 255);
        // 进度条绑定当前亮度
        mBrightness.setProgress(normal);
        mBrightness.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // 取得当前进度
                int tmpInt = seekBar.getProgress();
                // 当进度小于80时，设置成80，防止太黑看不见的后果。
                if (tmpInt < 80) {
                    tmpInt = 80;
                }
                // 根据当前进度改变亮度
                Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, tmpInt);
                tmpInt = Settings.System.getInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, -1);
                WindowManager.LayoutParams wl = getWindow().getAttributes();
                float tmpFloat = (float) tmpInt / 255;
                if (tmpFloat > 0 && tmpFloat <= 1) {
                    wl.screenBrightness = tmpFloat;
                }
                getWindow().setAttributes(wl);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }
        });


    }
}
