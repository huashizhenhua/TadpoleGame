package com.tadpolemusic.activity.fragment.center;

import java.util.ArrayList;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;

import com.itap.voiceemoticon.widget.SearchPageListView;
import com.tadpolemusic.R;
import com.tadpolemusic.VEApplication;
import com.tadpolemusic.adapter.PullToRefreshListViewAdapter;
import com.tadpolemusic.adapter.VoiceAdapter;
import com.tadpolemusic.api.Voice;

public class SearchFragment {
    private Activity mActivity;

    public SearchFragment(Activity activity) {
        mActivity = activity;
    }

    private static final int HANDLER_FILL_LIST = 1;
    private SearchPageListView<Voice> mListView;
    private ImageButton mBtnSearch;
    private EditText mEdSearch;
    private PullToRefreshListViewAdapter<Voice> mVoiceAdapter;


    public View onCreateView(LayoutInflater inflater) {
        View view = inflater.inflate(R.layout.tab_search, null);

        mListView = (SearchPageListView<Voice>) view.findViewById(R.id.list_view_Search);
        mBtnSearch = (ImageButton) view.findViewById(R.id.btn_search);
        mEdSearch = (EditText) view.findViewById(R.id.edit_text_search);


        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long arg3) {
                Log.d(VEApplication.TAG, "HotVoice Fragment onItemClick ");
                Voice item = (Voice) mVoiceAdapter.getItem(pos);
                VEApplication.getMusicPlayer(mActivity).play(pos);
            }
        });


        mVoiceAdapter = new VoiceAdapter(mActivity);
        mVoiceAdapter.setList(new ArrayList<Voice>(0));
        mVoiceAdapter.setListView(mListView);
        mListView.setAdapter(mVoiceAdapter);

        mBtnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String key = mEdSearch.getEditableText().toString();
                mListView.doSearch(key);
            }
        });
        return view;
    }

}
