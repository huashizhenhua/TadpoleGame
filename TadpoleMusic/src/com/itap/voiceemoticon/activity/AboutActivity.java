
package com.itap.voiceemoticon.activity;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.itap.voiceemoticon.R;
import com.itap.voiceemoticon.common.GlobalConst;
import com.itap.voiceemoticon.third.WeixinHelper;
import com.itap.voiceemoticon.widget.WeixinAlert;
import com.itap.voiceemoticon.widget.WeixinAlert.OnAlertSelectId;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.umeng.fb.NotificationType;
import com.umeng.fb.UMFeedbackService;

import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class AboutActivity extends SherlockFragmentActivity implements View.OnClickListener,
        OnAlertSelectId {
    @Override
    protected void onCreate(Bundle bundle) {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        this.setContentView(R.layout.activity_about);

        final AboutActivity me = this;

        // user feedback
        this.findViewById(R.id.btn_user_feedback).setOnClickListener(this);
        // share_to_friend
        this.findViewById(R.id.btn_share_to_friend).setOnClickListener(this);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_user_feedback:
                Toast.makeText(v.getContext(), "陈庆禧", Toast.LENGTH_LONG).show();
                UMFeedbackService.enableNewReplyNotification(this, NotificationType.AlertDialog);
                // 如果您程序界面是iOS风格，我们还提供了左上角的“返回”按钮，用于退出友盟反馈模块。启动友盟反馈模块前，您需要增加如下语句来设置“返回”按钮可见：
                UMFeedbackService.setGoBackButtonVisible();
                UMFeedbackService.openUmengFeedbackSDK(this);
                break;
            case R.id.btn_share_to_friend:
                WeixinAlert.showAlert(this, "分享【语音表情】给好友", "", this, null, false);
                break;
            default:
                break;
        }
    }

    @Override
    public void onClick(Dialog dialog, int whichButton) {
        switch (whichButton) {
            case R.id.qq:
                String title = "微信语音表情好好玩";
                String summary = "这个APP不错！！各种搞笑整蛊语音应有尽有哦！【请点击这条消息下载吧】";
                String targetUrl = "http://voiceemoticon.sinaapp.com/static/download.htm";
                Bundle bundle = new Bundle();
                bundle.putString("title", title);
                bundle.putString("targetUrl", targetUrl);
                bundle.putString("summary", summary);
                // bundle.putString("site", siteUrl.getText() + "");
                bundle.putString("appName", GlobalConst.SHARE_APP_NAME);
                
                Tencent.createInstance("100497165", this).shareToQQ(this, bundle,
                        new IUiListener() {
                            
                            @Override
                            public void onError(UiError e) {
                                System.out.println("shareToQQ:" + "onError code:" + e.errorCode + ", msg:"
                                        + e.errorMessage + ", detail:" + e.errorDetail);
                            }
                            
                            @Override
                            public void onComplete(JSONObject arg0) {
                                System.out.println("shareToQQ:" + "onComplete");
                            }
                            
                            @Override
                            public void onCancel() {
                                System.out.println("shareToQQ" + "onCancel");
                                
                            }
                        });
                
                break;
            case R.id.webchat:
                Toast.makeText(this, "内测版暂时无法分享到微信，请分享到QQ", Toast.LENGTH_LONG).show();
                break;
            case R.id.friends:
                Toast.makeText(this, "内测版暂时无法分享到微信，请分享到QQ", Toast.LENGTH_LONG).show();
                break;
            default:
                break;
        }
        new WeixinHelper(this).sendWebpage("微信语音表情好好玩", "微信语音表情这个APP不错！！",
                "http://voiceemoticon.sinaapp.com/static/play.htm");
    }

}
