package com.itap.voiceemoticon.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.itap.voiceemoticon.R;
import com.itap.voiceemoticon.third.WeixinHelper;
import com.umeng.fb.NotificationType;
import com.umeng.fb.UMFeedbackService;

public class AboutActivity extends SherlockFragmentActivity {
    @Override
    protected void onCreate(Bundle bundle) {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        this.setContentView(R.layout.activity_about);

        final AboutActivity me = this;

        // user feedback
        this.findViewById(R.id.btn_user_feedback).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
                Toast.makeText(v.getContext(), "陈庆禧", Toast.LENGTH_LONG).show();
                UMFeedbackService.enableNewReplyNotification(me, NotificationType.AlertDialog);
                // 如果您程序界面是iOS风格，我们还提供了左上角的“返回”按钮，用于退出友盟反馈模块。启动友盟反馈模块前，您需要增加如下语句来设置“返回”按钮可见：
                UMFeedbackService.setGoBackButtonVisible();
                UMFeedbackService.openUmengFeedbackSDK(me);
            }
        });

        // share_to_friend
        this.findViewById(R.id.btn_share_to_friend).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new WeixinHelper(me).sendWebpage("微信语音表情好好玩", "微信语音表情这个APP不错！！", "http://voiceemoticon.sinaapp.com/static/play.htm");
            }
        });
        super.onCreate(bundle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean result = super.onCreateOptionsMenu(menu);
        return result;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
            finish();
            return true;
        default:
            break;
        }
        return super.onOptionsItemSelected(item);
    }

}
