
package com.itap.voiceemoticon.activity.fragment;

import java.util.ArrayList;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;

import com.itap.voiceemoticon.R;
import com.itap.voiceemoticon.VEApplication;
import com.itap.voiceemoticon.activity.MainActivity;
import com.itap.voiceemoticon.adapter.VoiceAdapter;
import com.itap.voiceemoticon.api.Voice;
import com.itap.voiceemoticon.util.AndroidUtil;
import com.itap.voiceemoticon.widget.SearchPageListView;

public class SearchFragment {
    private MainActivity mActivity;

    public SearchFragment(MainActivity activity) {
        mActivity = activity;
    }

    private SearchPageListView<Voice> mListView;

    private ImageButton mBtnSearch;

    private EditText mEdSearch;

    private VoiceAdapter mVoiceAdapter;

    public View onCreateView(LayoutInflater inflater) {
        View view = inflater.inflate(R.layout.tab_search, null);

        mListView = (SearchPageListView<Voice>)view.findViewById(R.id.list_view_Search);
        mBtnSearch = (ImageButton)view.findViewById(R.id.btn_search);
        mEdSearch = (EditText)view.findViewById(R.id.edit_text_search);
        mEdSearch.setSingleLine();
        mEdSearch.setOnKeyListener(new View.OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    AndroidUtil.hideInputMethod(v);
                    String key = mEdSearch.getEditableText().toString();
                    mListView.doSearch(key);
                }
                return false;
            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long arg3) {
                Log.d(VEApplication.TAG, "HotVoice Fragment onItemClick ");
                Voice item = (Voice)mVoiceAdapter.getItem(pos);
                VEApplication.getMusicPlayer(mActivity).playMusic(item.url, item.title);
            }
        });

        mVoiceAdapter = new VoiceAdapter(mActivity);
        mVoiceAdapter.setList(new ArrayList<Voice>(0));
        mVoiceAdapter.setListView(mListView);
        mVoiceAdapter.setCallback(mActivity);

        mListView.setAdapter(mVoiceAdapter);

        mBtnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AndroidUtil.hideInputMethod(v);
                String key = mEdSearch.getEditableText().toString();
                mListView.doSearch(key);

            }
        });
        return view;
    }

}
