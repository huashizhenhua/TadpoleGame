package com.itap.voiceemoticon.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.Adapter;
import android.widget.HeaderViewListAdapter;
import android.widget.ListView;
import android.widget.SectionIndexer;
import android.widget.TextView;

public class IndexBar extends View {
    private char[] mCharArr = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '?' };
    private SectionIndexer mSectionIndexter = null;
    private TextView mDialogText;
    private ListView mListView;
    private Bitmap mbitmap;
    private int mType = 1;
    private int mColor = 0xff858c94;
    private int mBgColor = 0xFFDDDDDD;
    private int mCurrentSelectIndex = -1;
    private int mLastSelectIndex = -1;

    public IndexBar(Context context) {
        super(context);
        init();
    }
    
    public void setCurrentSection(char setctionLetter){
        setctionLetter = Character.toUpperCase(setctionLetter);
        for(int i = 0, len = mCharArr.length; i < len; i++){
            if(setctionLetter == mCharArr[i]){
                mCurrentSelectIndex = i;
                postInvalidate();
                break;
            }
        }
    }

    public IndexBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public IndexBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }



    public void setListView(ListView listView) {
        mListView = listView;
        Adapter adapter = listView.getAdapter();
        try {
            if (adapter instanceof SectionIndexer) {
                mSectionIndexter = (SectionIndexer) adapter;
            } else {
                adapter = ((HeaderViewListAdapter) adapter).getWrappedAdapter();
                mSectionIndexter = (SectionIndexer) adapter;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void init() {
        createDialogText();
        mbitmap = BitmapFactory.decodeResource(getResources(), android.R.drawable.ic_menu_search);
    }

    private void createDialogText() {
        mDialogText = new TextView(getContext());
        mDialogText.setGravity(Gravity.CENTER_HORIZONTAL);
        mDialogText.setVisibility(View.INVISIBLE);
        mDialogText.setTextSize(34 * TypedValue.COMPLEX_UNIT_SP);
        WindowManager mWindowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams(120, LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.TYPE_APPLICATION, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);
        mWindowManager.addView(mDialogText, lp);

    }

    public void setTextView(TextView mDialogText) {
        this.mDialogText = mDialogText;
    }

    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);

        int eventY = (int) event.getY();

        int idx = eventY / (getMeasuredHeight() / mCharArr.length);

        if (idx >= mCharArr.length) {
            idx = mCharArr.length - 1;

        } else if (idx < 0) {
            idx = 0;
        }

        mLastSelectIndex = mCurrentSelectIndex;
        mCurrentSelectIndex = idx;


        if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE) {
//            setBackgroundColor(0xFFFFFFFF);
            if (mSectionIndexter == null) {
                mSectionIndexter = (SectionIndexer) mListView.getAdapter();
            }

            int position = mSectionIndexter.getPositionForSection(mCharArr[idx]);
            if (position == -1) {

                return true;
            } else {
                mDialogText.setVisibility(View.VISIBLE);
                mDialogText.setText(String.valueOf(mCharArr[idx]));
            }
            mListView.setSelection(position);
        } else {
            mDialogText.setVisibility(View.INVISIBLE);

        }
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (mLastSelectIndex != mCurrentSelectIndex) {
                this.invalidate();
            }
//            setBackgroundDrawable(new ColorDrawable(0x00000000));
        }
        return true;
    }


    public float getFontHeight(Paint paint) {
        Rect bounds = new Rect();
        paint.getTextBounds("A", 0, 1, bounds);
        return bounds.height();
    }

    protected void onDraw(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(mColor);
        paint.setTextAlign(Align.CENTER);

        final int width = getMeasuredWidth();
        int x = width / 2;
        int y = 0;
        int textBaseLine = y;

        if (mCharArr.length > 0) {
            float height = getMeasuredHeight() / (mCharArr.length);
            paint.setTextSize(height - 2);
            float fontHeight = getFontHeight(paint);

            for (int i = 0; i < mCharArr.length; i++) {
                y = (int) (i * height);
                textBaseLine = (int) (y + height);
                if (mCurrentSelectIndex == i) {
                    Rect rect = new Rect();
                    rect.set(0, y, 0 + width, y + (int) height);
                    Paint bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
                    bgPaint.setColor(mBgColor);
                    bgPaint.setStyle(Style.FILL);
                    canvas.drawRect(rect, bgPaint);
                }
                canvas.drawText(String.valueOf(mCharArr[i]), x, textBaseLine - ((height - fontHeight) / 2), paint);
            }
        }
        super.onDraw(canvas);
    }
}