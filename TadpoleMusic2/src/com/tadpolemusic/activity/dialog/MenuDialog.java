package com.tadpolemusic.activity.dialog;

import java.util.ArrayList;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.DataSetObserver;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.text.AndroidCharacter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.quickactionbar.QuickAction;
import com.quickactionbar.QuickActionGrid;
import com.tadpolemusic.R;
import com.tadpolemusic.VEApplication;
import com.tadpolemusic.adapter.GridViewAdapter;
import com.tadpolemusic.media.service.MusicPlayerProxy;
import com.umeng.analytics.i;

public class MenuDialog extends Dialog {

    private AudioManager mAudioManager;
    private SeekBar mSound;
    private ImageView mIconSound;
    private SeekBar mBrightness;
    private ImageView mIconBright;
    private GridView mGridViewBottom;

    public MenuDialog(final Context context) {
        super(context, R.style.Dialog_Transparent);
        setContentView(R.layout.main_dialog_menu);

        // ---------------------------------------
        // Sound Controll
        // ----------------------------------------

        mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        mSound = (SeekBar) findViewById(R.id.seekbar_sound);
        mIconSound = (ImageView) findViewById(R.id.imageview_sound);
        final int volumeMax = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        final int volumeCur = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        mSound.setMax(volumeMax);
        mSound.setProgress(volumeCur);
        mSound.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, seekBar.getProgress(), 0);

                if (seekBar.getProgress() > 0) {
                    mIconSound.setImageResource(R.drawable.volume_sound);
                } else {
                    mIconSound.setImageResource(R.drawable.volume_mute);
                }

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
        mIconBright = (ImageView) findViewById(R.id.imageview_brightness);
        mBrightness.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // 取得当前进度
                int tmpInt = seekBar.getProgress();
                // 当进度小于80时，设置成80，防止太黑看不见的后果。
                if (tmpInt < 80) {
                    mIconBright.setImageResource(R.drawable.brightness_dim);
                    tmpInt = 80;
                } else {
                    mIconBright.setImageResource(R.drawable.brightness_bright);
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

        // ---------------------------------------
        // GridView Menu
        // ----------------------------------------s
        final ArrayList<MenuDialog.GridItem> list = new ArrayList<MenuDialog.GridItem>();
        list.add(new GridItem(R.drawable.ic_menu_scan_default, "扫描歌曲"));
        list.add(new GridItem(R.drawable.ic_menu_sleep_mode_default, "睡眠定时"));
        list.add(new GridItem(R.drawable.ic_menu_setting_default, "播放模式"));
        list.add(new GridItem(R.drawable.ic_menu_exit_default, "退出"));
        final LayoutInflater inflater = LayoutInflater.from(context);
        mGridViewBottom = (GridView) this.findViewById(R.id.gridview_bottom);

        mGridViewBottom.setAdapter(new BaseAdapter() {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = inflater.inflate(R.layout.main_dialog_menu_grid_item, null);
                TextView textView = (TextView) view.findViewById(R.id.textview);
                GridItem item = list.get(position);
                textView.setCompoundDrawablesWithIntrinsicBounds(null, view.getContext().getResources().getDrawable(item.iconResId), null, null);
                textView.setText(item.text);
                return view;
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public Object getItem(int position) {
                return list.get(position);
            }

            @Override
            public int getCount() {
                return list.size();
            }
        });


        final MenuDialog me = this;

        mGridViewBottom.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long arg3) {
                switch (pos) {
                case 0:
                    break;
                case 1:
                    new PlayListDialog(context).show();
                    break;
                case 2:
                    PlayModeSelectDialog dialog = new PlayModeSelectDialog(context);
                    MusicPlayerProxy proxy = VEApplication.getMusicPlayer(context);
                    dialog.setPlayMode(proxy.getPlayMode());
                    dialog.setPositiveButtonListener(new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            MusicPlayerProxy proxy = VEApplication.getMusicPlayer(context);
                            PlayModeSelectDialog d = (PlayModeSelectDialog) dialog;
                            proxy.setPlayMode(d.getPlayMode());
                        }
                    });
                    me.dismiss();
                    dialog.show();
                    break;
                case 3:
                    System.exit(0);
                    break;
                default:
                    break;
                }
            }
        });

    }

    public void showPlayMode(Context context, View anchor) {
        QuickActionGrid qaGrid = new QuickActionGrid(context);
        qaGrid.setNumColumns(4);

        QuickAction qaAll = new QuickAction(context, R.drawable.widget_playmode_repeate_all_default, "全部循环");
        QuickAction qaRandom = new QuickAction(context, R.drawable.widget_playmode_repeate_random_default, "随机播放");
        QuickAction qaSingle = new QuickAction(context, R.drawable.widget_playmode_repeate_single_default, "单曲循环");
        QuickAction qaSequence = new QuickAction(context, R.drawable.widget_playmode_sequence_default, "顺序播放");

        qaGrid.addQuickAction(qaAll);
        qaGrid.addQuickAction(qaRandom);
        qaGrid.addQuickAction(qaSingle);
        qaGrid.addQuickAction(qaSequence);

        qaGrid.show(anchor);
    }

    public class GridItem {
        int iconResId;
        String text;

        public GridItem(int iconResId, String text) {
            this.iconResId = iconResId;
            this.text = text;
        }
    }
}
