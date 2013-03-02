package com.tadpolemusic.activity.fragment.menu;

import java.util.ArrayList;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.tadpolemusic.R;
import com.tadpolemusic.activity.fragment.AbsMenuFragment;
import com.tadpolemusic.adapter.BaseListAdapter;
import com.tadpolemusic.adapter.MyMusicAdapter;
import com.tadpolemusic.adapter.MyMusicItem;


public class LeftMenuFragment extends AbsMenuFragment {

    private ArrayList<MyMusicItem> localItems;
    private int curSelectedItem = -1;
    private MyMusicAdapter mAdapterLocal;
    private MyMusicAdapter mAdapterNetwork;

    public LeftMenuFragment(ArrayList<MyMusicItem> localMusicItems) {
        localItems = localMusicItems;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.sliding_menu_left, null);
        initLocalMusic(view);
        initNetworkMusic(view);
        return view;
    }

    public ArrayList<MyMusicItem> loadLocalItems() {
        return localItems;
    }

    public ArrayList<MyMusicItem> loadNetworkItems() {
        ArrayList<MyMusicItem> myMusicList = new ArrayList<MyMusicItem>();

        MyMusicItem item = new MyMusicItem();
        item.iconDefaultResId = R.drawable.ic_launcher;
        item.text = "本地音乐";
        myMusicList.add(item);

        item = new MyMusicItem();
        item.iconDefaultResId = R.drawable.ic_launcher;
        item.text = "网络音乐";
        myMusicList.add(item);

        // 制造假数据
        for (int i = 0, len = 4; i < len; i++) {
            item = new MyMusicItem();
            item.text = "text" + i;
            item.iconDefaultResId = R.drawable.ic_launcher;
            myMusicList.add(item);
        }
        return myMusicList;
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
        mAdapterLocal = new MyMusicAdapter(getActivity());
        final ArrayList<MyMusicItem> myMusicList = loadLocalItems();
        mAdapterLocal.setList(myMusicList);
        gridViewMusic.setAdapter(mAdapterLocal);
        gridViewMusic.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View view, int postion, long arg3) {
                // update ui
                mAdapterLocal.setSelectedPostion(postion);
                mAdapterNetwork.setSelectedPostion(mAdapterNetwork.INVALID_POSITION);

                // item action
                MyMusicItem item = myMusicList.get(postion);
                if (MyMusicItem.Action.REPLEACE_CENTER.equals(item.action)) {
                    getLeftMenuControll().setCenterContent(item);
                    getLeftMenuControll().scrollToCenter();
                }
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
        mAdapterNetwork = new MyMusicAdapter(getActivity());
        final ArrayList<MyMusicItem> myMusicList = loadNetworkItems();
        mAdapterNetwork.setList(myMusicList);
        gridViewMusic.setAdapter(mAdapterNetwork);
        gridViewMusic.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int postion, long arg3) {
                // update ui
                mAdapterLocal.setSelectedPostion(mAdapterLocal.INVALID_POSITION);
                mAdapterNetwork.setSelectedPostion(postion);

                // item action
                MyMusicItem item = myMusicList.get(postion);
                getLeftMenuControll().scrollToCenter();
            }
        });
    }
}
