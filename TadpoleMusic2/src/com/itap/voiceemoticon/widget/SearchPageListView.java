package com.itap.voiceemoticon.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.ListAdapter;
import android.widget.Toast;

import com.itap.voiceemoticon.api.PageList;
import com.itap.voiceemoticon.api.Voice;
import com.itap.voiceemoticon.util.StringUtil;
import com.tadpolemusic.VEApplication;

public class SearchPageListView<Voice> extends PageListView<Voice> {

    private String mSearchKey = "";

    public SearchPageListView(Context context) {
        super(context);
    }

    public SearchPageListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public SearchPageListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        this.setPullRefreshEnable(false);
        this.setPullLoadEnable(false);
        super.setAdapter(adapter);
    }

    public void doSearch(String key) {
        final String lastKey = mSearchKey;
        mSearchKey = key;
        if (StringUtil.equalsIgnoreCase(key, lastKey)) {
            super.doLoad(false);
        } else {
            super.doLoad(true);
        }
    }

    @Override
    public PageList<Voice> onLoadPageList(int startIndex, int maxResult) {
        final SearchPageListView<Voice> me = this;
        PageList<Voice> pageList = (PageList<Voice>) VEApplication.getVoiceEmoticonApi().searchHostVoices(mSearchKey, startIndex, maxResult);
        if (pageList != null && pageList.records != null && pageList.records.size() > 0) {
            this.post(new Runnable() {
                @Override
                public void run() {
                    me.setPullLoadEnable(true);
                }
            });
        } else {
            this.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(me.getContext(), "客官，已经木有数据了，请搜索其它内容吧", Toast.LENGTH_LONG).show();
                }
            });
        }
        return pageList;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawText("打酱油", 0, 10, new Paint());
        invalidate();
    }
}
