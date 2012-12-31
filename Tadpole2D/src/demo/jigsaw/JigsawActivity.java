package demo.jigsaw;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.zip.Inflater;

import cn.uc.gamesdk.UCLogLevel;

import tadpole2d.game.GLog;
import tadpole2d.game.LAGameView;
import tadpole2d.util.AppUtil;
import ucgamesdk.example.R;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.SimpleAdapter;

/**
 * 拼图游戏
 * 
 * @author Administrator
 * 
 */
public class JigsawActivity extends Activity {

    /**
     * 游戏主布局
     */
    private LinearLayout mGameContainer;

    /**
     * 游戏视图
     */
    private LAGameView mGameView;


    public void onCreate(Bundle b) {
        super.onCreate(b);
        this.setContentView(R.layout.splashscreen); //设置启动画面
        startGame();
    }

    private void startGame() {
        this.setContentView(R.layout.jigsaw);
        mGameView = new LAGameView(this);

        // 拼图游戏
        mGameView.setScreen(new JigsawScreen(mGameView, "jinian.jpg", "over.png", 4, 7));
        mGameView.setShowFPS(true);

        LayoutParams gameViewLLP = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
        mGameContainer = (LinearLayout) this.findViewById(R.id.layoutGameContainer);
        mGameContainer.addView(mGameView, gameViewLLP);
        mGameView.startPaint();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    }

    @Override
    protected void onStart() {
        GLog.d("GameActivity", "----------onStart---------");
        if (mGameView != null) {
            mGameView.setRunning(true);
        }
        super.onRestart();
    }

    @Override
    protected void onResume() {
        GLog.d("GameActivity", "----------onResume---------");
        if (mGameView != null) {
            mGameView.setRunning(true);
        }
        super.onResume();
    }

    protected void onPause() {
        GLog.d("GameActivity", "----------onPause---------");
        if (mGameView != null) {
            mGameView.setRunning(false);
        }
        super.onPause();
    }

    protected void onStop() {
        GLog.d("GameActivity", "----------onStop---------");
        if (mGameView != null) {
            mGameView.setRunning(false);
        }
        super.onStop();
    }

    protected void onDestroy() {
        GLog.d("GameActivity", "----------onDestroy---------");
        try {
            if (mGameView != null) {
                mGameView.setRunning(false);
                Thread.sleep(16);
            }
            super.onDestroy();
        } catch (Exception e) {
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_MENU) {
            GridView gridView = (GridView) LayoutInflater.from(this).inflate(R.layout.jigsaw_menu_grid, null);
            //生成动态数组，并且转入数据  
            ArrayList<HashMap<String, Object>> itemList = new ArrayList<HashMap<String, Object>>();
            
            // 计时设置
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("id", UIConfig.ID_TIME_SETTING);//添加图像资源的ID  
            map.put("item_text", UIConfig.TEXT_TIME_SETTING);//添加图像资源的ID  
            itemList.add(map);

            // 行列设置
            map = new HashMap<String, Object>();
            map.put("id", UIConfig.ID_TIME_SETTING);//添加图像资源的ID  
            map.put("item_text", UIConfig.TEXT_TIME_SETTING);//按序号做ItemText
            itemList.add(map);
            
            // 行列设置
            map = new HashMap<String, Object>();
            map.put("id", UIConfig.ID_TIME_SETTING);//添加图像资源的ID  
            map.put("item_text", UIConfig.TEXT_TIME_SETTING);//按序号做ItemText
            itemList.add(map);

            // 行列设置
            map = new HashMap<String, Object>();
            map.put("id", UIConfig.ID_TIME_SETTING);//添加图像资源的ID  
            map.put("item_text", UIConfig.TEXT_TIME_SETTING);//按序号做ItemText
            itemList.add(map);
            
            int[] idArr = { R.id.item_text, R.id.item_text1 };
            String[] nameArr = { "item_text", "item_text1" };
            SimpleAdapter adapter = new SimpleAdapter(this, itemList, R.layout.jigsaw_menu_grid_item, nameArr, idArr);
            gridView.setAdapter(adapter);
            gridView.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                    AppUtil.toast(JigsawActivity.this, "setOnItemSelectedListener item = " + arg2);
                    GLog.d("ddddd", "setOnItemClickListener item = ");
                }
            });
            createPopMenu(gridView).showAtLocation(mGameContainer, Gravity.BOTTOM, 0, 100);
        }
        return super.onKeyUp(keyCode, event);
    }

    public PopupWindow createPopMenu(View winContentView) {
        Context context = this;
        Resources resources = this.getResources();
        PopupWindow popWin = new PopupWindow(context);
        popWin.setTouchable(true);
        popWin.setFocusable(true);
        popWin.setWidth(LayoutParams.WRAP_CONTENT);
        popWin.setHeight(LayoutParams.WRAP_CONTENT);
//        popWin.setBackgroundDrawable(resources.getDrawable(R.drawable.jigsaw_menu_bg));
        // contentView.setBackgroundColor(R.color.page_window_bgcolor);
        // window.setBackgroundDrawable(new
        // ColorDrawable(res.getColor(R.color.page_window_bgcolor)));
        ;
        //设置PopupWindow显示和隐藏时的动画
        //        popWin.setAnimationStyle(R.style.AnimationFade);
        //设置PopupWindow的大小（宽度和高度）
        //        window.setWidth(res.getDimensionPixelSize(R.dimen.page_window_width));
        //        window.setHeight(res.getDimensionPixelSize(R.dimen.page_window_height));
        //设置PopupWindow的内容view
        popWin.setContentView(winContentView);
        //设置PopupWindow外部区域是否可触摸
        popWin.setOutsideTouchable(true);
        return popWin;
    }
}