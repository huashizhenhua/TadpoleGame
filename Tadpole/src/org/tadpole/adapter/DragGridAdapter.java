package org.tadpole.adapter;

import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.tadpole.common.TLog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Checkable;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class DragGridAdapter extends SimpleAdapter implements IDragGridAdapter {

    private static final String TAG = "DragGridAdapter";
    private Context mContext;
    private List<Map> mData;
    private int mResource;
    private String[] mFrom;
    private int[] mTo;
    private LayoutInflater mInflater;

    public DragGridAdapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to) {
        super(context, data, resource, from, to);
        this.mContext = context;
        this.mData = (List<Map>) data;
        this.mFrom = from;
        this.mTo = to;
        this.mResource = resource;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    public void exchange(int startPosition, int endPosition) {
        TLog.debug(TAG, "exchange startPosition = %d, endPosition = %d", startPosition, endPosition);
        Map startMap = (Map) getItem(startPosition);
        Map endMap = (Map) getItem(endPosition);
        mData.add(startPosition, endMap);
        mData.remove(startPosition + 1);
        mData.add(endPosition, startMap);
        mData.remove(endPosition + 1);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return createViewFromResource(position, convertView, parent, mResource);
    }

    private View createViewFromResource(int position, View convertView, ViewGroup parent, int resource) {
        View v;
        v = mInflater.inflate(resource, null);
        bindView(position, v);
        return v;
    }


    private void bindView(int position, View view) {
        final Map dataSet = mData.get(position);
        if (dataSet == null) {
            return;
        }
        final ViewBinder binder = this.getViewBinder();
        final String[] from = mFrom;
        final int[] to = mTo;
        final int count = to.length;

        for (int i = 0; i < count; i++) {
            final View v = view.findViewById(to[i]);
            if (v != null) {
                final Object data = dataSet.get(from[i]);
                String text = data == null ? "" : data.toString();
                if (text == null) {
                    text = "";
                }

                boolean bound = false;
                if (binder != null) {
                    bound = binder.setViewValue(v, data, text);
                }

                if (!bound) {
                    if (v instanceof Checkable) {
                        if (data instanceof Boolean) {
                            ((Checkable) v).setChecked((Boolean) data);
                        } else if (v instanceof TextView) {
                            // Note: keep the instanceof TextView check at the bottom of these
                            // ifs since a lot of views are TextViews (e.g. CheckBoxes).
                            setViewText((TextView) v, text);
                        } else {
                            throw new IllegalStateException(v.getClass().getName() + " should be bound to a Boolean, not a " + (data == null ? "<unknown type>" : data.getClass()));
                        }
                    } else if (v instanceof TextView) {
                        // Note: keep the instanceof TextView check at the bottom of these
                        // ifs since a lot of views are TextViews (e.g. CheckBoxes).
                        setViewText((TextView) v, text);
                    } else if (v instanceof ImageView) {
                        if (data instanceof Integer) {
                            setViewImage((ImageView) v, (Integer) data);
                        } else {
                            setViewImage((ImageView) v, text);
                        }
                    } else {
                        throw new IllegalStateException(v.getClass().getName() + " is not a " + " view that can be bounds by this SimpleAdapter");
                    }
                }
            }
        }
    }
}
