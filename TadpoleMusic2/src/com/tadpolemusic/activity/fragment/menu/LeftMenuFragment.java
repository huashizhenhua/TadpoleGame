package com.tadpolemusic.activity.fragment.menu;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.tadpolemusic.R;
import com.tadpolemusic.activity.fragment.AbsMenuFragment;
import com.tadpolemusic.adapter.MyMusicAdapter;
import com.tadpolemusic.adapter.MyMusicItem;


public class LeftMenuFragment extends AbsMenuFragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	System.out.println("---------mFragmentLeftMenu-----------");
    	View view = inflater.inflate(R.layout.sliding_menu_left, null);
        initLocalMusic(view);
        initNetworkMusic(view);
        return view;
    }

    public ArrayList<MyMusicItem> loadMyMusicItems() {
        ArrayList<MyMusicItem> myMusicList = new ArrayList<MyMusicItem>();

        MyMusicItem item = new MyMusicItem();
        item.iconDrawableId = R.drawable.ic_launcher;
        item.text = "本地音乐";
        myMusicList.add(item);

        item = new MyMusicItem();
        item.iconDrawableId = R.drawable.ic_launcher;
        item.text = "网络音乐";
        myMusicList.add(item);

        // 制造假数据
        for (int i = 0, len = 4; i < len; i++) {
            item = new MyMusicItem();
            item.text = "text" + i;
            item.iconDrawableId = R.drawable.ic_launcher;
            myMusicList.add(item);
        }
        return myMusicList;
    }

    public ArrayList<MyMusicItem> networkMusicItems() {
        return loadMyMusicItems();
    }

    /**
     * local music
     */
    public void initLocalMusic(View view) {
        View viewMusic = view.findViewById(R.id.pane_local_music);
        GridView gridViewMusic = (GridView) (viewMusic.findViewById(R.id.grid_view_my_music));

        // section
        TextView textViewSection = (TextView) viewMusic.findViewById(R.id.text_view_section);
        textViewSection.setText(R.string.sliding_menu_left_local_music);

        // gridview
        MyMusicAdapter adapter = new MyMusicAdapter(getActivity());
        final ArrayList<MyMusicItem> myMusicList = loadMyMusicItems();
        adapter.setList(myMusicList);
        gridViewMusic.setAdapter(adapter);
        gridViewMusic.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View view, int postion, long arg3) {
                ImageView imageViewIcon = (ImageView) view.findViewById(R.id.image_view_icon);
                imageViewIcon.setImageResource(android.R.drawable.ic_menu_add);

                Toast.makeText(getActivity(), "position" + postion, Toast.LENGTH_LONG).show();
                MyMusicItem item = myMusicList.get(postion);
                getActivityInterface().setTitle(item.text);
                getActivityInterface().scrollToCenter();
            }
        });
    }

    /**
     * network music
     */
    public void initNetworkMusic(View view) {
        View viewMusic = view.findViewById(R.id.pane_network_music);
        // section
        TextView textViewSection = (TextView) viewMusic.findViewById(R.id.text_view_section);
        textViewSection.setText(R.string.sliding_menu_left_network_music);

        // gridview
        GridView gridViewMusic = (GridView) (viewMusic.findViewById(R.id.grid_view_my_music));
        MyMusicAdapter adapter = new MyMusicAdapter(getActivity());
        final ArrayList<MyMusicItem> myMusicList = loadMyMusicItems();
        adapter.setList(myMusicList);
        gridViewMusic.setAdapter(adapter);
        gridViewMusic.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int postion, long arg3) {
                Toast.makeText(getActivity(), "position" + postion, Toast.LENGTH_LONG).show();
                MyMusicItem item = myMusicList.get(postion);
                getActivityInterface().setTitle(item.text);
                getActivityInterface().scrollToCenter();
            }
        });
    }
}
