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


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_right, null);
        return view;
    }
}
