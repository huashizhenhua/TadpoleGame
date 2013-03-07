package com.quickactionbar;

import java.lang.reflect.Field;
import java.util.List;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Drawable.Callback;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

/**
 * A {@link QuickActionGrid} is an implementation of a {@link QuickActionWidget} that displays {@link QuickAction}s in a grid manner. This is usually used to create
 * a shortcut to jump between different type of information on screen.
 * 
 * @author Benjamin Fellous
 * @author Cyril Mottier
 */
public class QuickActionGrid extends QuickActionWidget {

    private GridView mGridView;


    public QuickActionGrid(Context context) {
        super(context);

        setContentView(R.layout.gd_quick_action_grid);

        final View v = getContentView();
        mGridView = (GridView) v.findViewById(R.id.gdi_grid);
    }


    public void setNumColumns(int numColumns) {
        mGridView.setNumColumns(numColumns);
    }

    @Override
    protected void populateQuickActions(final List<QuickAction> quickActions) {

        mGridView.setAdapter(new BaseAdapter() {

            public View getView(int position, View view, ViewGroup parent) {

                TextView textView = (TextView) view;

                if (view == null) {
                    final LayoutInflater inflater = LayoutInflater.from(getContext());
                    textView = (TextView) inflater.inflate(R.layout.gd_quick_action_grid_item, mGridView, false);
                }

                QuickAction quickAction = quickActions.get(position);
                textView.setText(quickAction.mTitle);
                textView.setCompoundDrawablesWithIntrinsicBounds(null, quickAction.mDrawable, null, null);
                return textView;

            }

            public long getItemId(int position) {
                return position;
            }

            public Object getItem(int position) {
                return null;
            }

            public int getCount() {
                return quickActions.size();
            }
        });

        mGridView.setOnItemClickListener(mInternalItemClickListener);
    }

    @Override
    protected void onMeasureAndLayout(Rect anchorRect, View contentView) {

        contentView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        contentView.measure(MeasureSpec.makeMeasureSpec(getScreenWidth(), MeasureSpec.EXACTLY), LayoutParams.WRAP_CONTENT);

        int rootHeight = contentView.getMeasuredHeight();

        int offsetY = getArrowOffsetY();
        int dyTop = anchorRect.top;
        int dyBottom = getScreenHeight() - anchorRect.bottom;

        boolean onTop = (dyTop > dyBottom);
        int popupY = (onTop) ? anchorRect.top - rootHeight + offsetY : anchorRect.bottom - offsetY;

        int popupX = anchorRect.right - getWidth();

        setWidgetSpecs(popupY, popupX, onTop);
    }

    private OnItemClickListener mInternalItemClickListener = new OnItemClickListener() {
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
            getOnQuickActionClickListener().onQuickActionClicked(QuickActionGrid.this, position);
            if (getDismissOnClick()) {
                dismiss();
            }
        }
    };


    private int getNumColums() {
        try {
            getValue(GridView.class, mGridView, "mNumColumns");
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return 0;
    }


    /**
     * Relect get a object's field value(private/protected/public)
     * 
     * @param clazz
     * @param obj
     * @param fieldName
     * @return
     * @throws IllegalAccessException
     */
    private static Object getValue(Class clazz, Object obj, String fieldName) throws IllegalAccessException {
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            if (fieldName.equals(field.getName())) {
                return field.get(obj);
            }
        }
        return null;
    }

}
