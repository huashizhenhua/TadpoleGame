package com.tadpolemusic.activity.fragment.menu;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.tadpolemusic.R;
import com.tadpolemusic.adapter.MyMusicAdapter;
import com.tadpolemusic.adapter.MyMusicItem;


public class RightMenuFragment extends Fragment {

    private GridView mGridViewMyMusic;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.sliding_menu_left, null);
        initMyMusic(view);
        return view;
    }

    /**
     * my music
     */
    public void initMyMusic(View view) {
        mGridViewMyMusic = (GridView) (view.findViewById(R.id.grid_view_my_music));
        MyMusicAdapter adapter = new MyMusicAdapter(getActivity());
        ArrayList<MyMusicItem> myMusicList = new ArrayList<MyMusicItem>();
        for (int i = 0, len = 10; i < len; i++) {
            MyMusicItem item = new MyMusicItem();
            item.text = "text" + i;
            item.iconDefaultResId = R.drawable.ic_launcher;
            myMusicList.add(item);
        }
        adapter.setList(myMusicList);
        mGridViewMyMusic.setAdapter(adapter);
        mGridViewMyMusic.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                Toast.makeText(getActivity(), "position" + arg2, Toast.LENGTH_LONG).show();
            }
        });
    }
}
