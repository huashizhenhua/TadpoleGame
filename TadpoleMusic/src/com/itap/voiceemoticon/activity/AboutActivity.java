
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
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.umeng.fb.FeedbackAgent;
import com.umeng.newxp.common.ExchangeConstants;
import com.umeng.newxp.controller.ExchangeDataService;
import com.umeng.newxp.view.ExchangeViewManager;

import org.json.JSONObject;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

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
        this.findViewById(R.id.btn_app_download).setOnClickListener(this);
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
        final Context context = this;
        switch (v.getId()) {
            case R.id.btn_user_feedback:
                FeedbackAgent agent = new FeedbackAgent(this);
                agent.startFeedbackActivity();
                break;
            case R.id.btn_share_to_friend:
                WeixinAlert.showAlert(this, "分享【语音表情】给好友", "", this, null, false);
                break;
            case R.id.btn_app_download:
                ExchangeDataService service = new ExchangeDataService();
                new ExchangeViewManager(context,service)
                            .addView(ExchangeConstants.type_list_curtain, null);   
                break;
            default:
                break;
        }
    }

    @Override
    public void onClick(Dialog dialog, int whichButton) {

        String title = "【语音表情】好好玩";
        String summary = "这个APP不错！！各种搞笑整蛊语音应有尽有哦！【请点击这条消息下载吧】";
        String targetUrl = "http://voiceemoticon.sinaapp.com/static/download.htm";

        switch (whichButton) {
            case R.id.qq:
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
                                System.out.println("shareToQQ:" + "onError code:" + e.errorCode
                                        + ", msg:" + e.errorMessage + ", detail:" + e.errorDetail);
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
                new WeixinHelper(this).sendWebpage(title, summary, targetUrl,
                        SendMessageToWX.Req.WXSceneSession);
                break;
            case R.id.friends:
                new WeixinHelper(this).sendWebpage(title, summary, targetUrl,
                        SendMessageToWX.Req.WXSceneTimeline);
                break;
            default:
                break;
        }

    }

}
