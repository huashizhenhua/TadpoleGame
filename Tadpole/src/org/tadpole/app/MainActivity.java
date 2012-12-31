package org.tadpole.app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.tadpole.adapter.BoardPagedAdapter;
import org.tadpole.aidl.IPluginCallback;
import org.tadpole.aidl.PluginServiceConnect;
import org.tadpole.widget.Configure;
import org.tadpole.widget.DragGridView;
import org.tadpole.widget.PagedView;
import org.tadpole.zenip.BoardPageData;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.SimpleAdapter;

public class MainActivity extends BaseActvity {
    private static final String TAG = "MainActivity";
    private static final String SERVICE_NAME = "org.tadpole.service.testservices";
    private static final String MUSIC_SERVICE_NAME = "org.tadpolemusic.pluginservice";

    private PagedView mBoardPagedView;
    private ArrayList<View> mPageViews;

    private ServiceConnection mServiceConnection;
    private PluginServiceConnect mPluginServiceConnect;
    private Boolean mConnectComplete;
    private Context mContext;
    private View mMoveBackgroundView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.board);

        mBoardPagedView = (PagedView) this.findViewById(R.id.page_view_board);
        mMoveBackgroundView = (View) this.findViewById(R.id.move_background);
        new MoveBackground(mMoveBackgroundView).startMove();

        Configure.init(this);

        mPageViews = new ArrayList<View>();
        mContext = this;
        initPage();
        initServices();
        connectService();
    }

    private IPluginCallback clientCallback = new IPluginCallback.Stub() {

        @Override
        public void startActivity(String packageName, String activityClassName, int iCallingPid, Bundle bundle) throws RemoteException {
            Log.d(TAG, "startActivity call");
            Intent intent = new Intent();
            intent.setClassName(packageName, activityClassName);
            MainActivity.this.startActivity(intent);
        }

        @Override
        public boolean isHideLoadingScreen() throws RemoteException {
            // TODO Auto-generated method stub
            return false;
        }


        @Override
        public void handle(int testInt) throws RemoteException {

        }
    };

    private void initServices() {
        Log.i(TAG, "initServices");
        mServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceDisconnected(ComponentName name) {
                // TODO Auto-generated method stub
                Log.i(TAG, "onServiceDisconnected");

            }

            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                Log.i(TAG, "onServiceConnected");
                mPluginServiceConnect = PluginServiceConnect.Stub.asInterface(service);
                Log.i(TAG, "onServiceConnected mPluginServiceConnect = " + mPluginServiceConnect);
                if (mPluginServiceConnect != null) {
                    Log.i(TAG, "onServiceConnected registerCallback = " + clientCallback);
                    try {
                        mPluginServiceConnect.registerCallback(clientCallback);
                    } catch (RemoteException e1) {
                        e1.printStackTrace();
                    }
                }

            }
        };
        mConnectComplete = false;
        mPluginServiceConnect = null;
    }

    private boolean connectService() {
        if (mConnectComplete == true) {
            return true;
        }

        Intent intent = new Intent(MUSIC_SERVICE_NAME);
        if (mContext != null) {
            Log.i(TAG, "begin to connectService	");
            mContext.bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
            mContext.startService(intent);
            mConnectComplete = true;
            return true;
        }
        return false;
    }

    public void disConnectedService() {
        if (mConnectComplete) {
            Intent intent = new Intent(MUSIC_SERVICE_NAME);
            mContext.unbindService(mServiceConnection);
            mContext.stopService(intent);
            mPluginServiceConnect = null;
            mConnectComplete = false;

        }
    }

    @Override
    protected void onDestroy() {
        Log.i(TAG, "--------------onDestroy------------");
        disConnectedService();
        super.onDestroy();
    }

    //----------------ui logic-----------------

    private ArrayList<BoardPageData> loadBoardPageData() {
        String[] titles = { "weico", "tecent_weibo", "qq", "dear qin" };
        ArrayList<BoardPageData> dataList = new ArrayList<BoardPageData>();
        for (int i = 0, len = titles.length; i < len; i++) {
            BoardPageData data = new BoardPageData();
            data.title = titles[i];
            dataList.add(data);
        }
        return dataList;
    }

    private void initPage() {
        ArrayList<BoardPageData> dataList = loadBoardPageData();
        Iterator<BoardPageData> dataIter = dataList.iterator();
        while (dataIter.hasNext()) {
            BoardPageData data = dataIter.next();
            mPageViews.add(buildPageView(data));
        }
        mBoardPagedView.setAdapter(new BoardPagedAdapter(mPageViews));
    }

    private View buildPageView(BoardPageData data) {
        LinearLayout.LayoutParams viewParams = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);

        View rl = LayoutInflater.from(this).inflate(R.layout.board_page, null);

        // disable gridview content scroll
        DragGridView dragGridView = (DragGridView) rl.findViewById(R.id.grid_view_board_page);
        dragGridView.setPageListener(new DragGridView.G_PageListener() {
            @Override
            public void page(int cases, int page) {
            }
        });
        
        //生成动态数组，并且转入数据  
        ArrayList<HashMap<String, Object>> lstImageItem = new ArrayList<HashMap<String, Object>>();
        for (int i = 0; i < 8; i++) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("ItemImage", R.drawable.ic_action_search);//添加图像资源的ID  
            map.put("ItemText", "NO." + String.valueOf(i));//按序号做ItemText  
            lstImageItem.add(map);
        }
        //生成适配器的ImageItem <====> 动态数组的元素，两者一一对应  
        SimpleAdapter saImageItems = new SimpleAdapter(this, //没什么解释  
                lstImageItem,//数据来源   
                R.layout.board_page_griditem,//night_item的XML实现  
                //动态数组与ImageItem对应的子项          
                new String[] { "ItemImage", "ItemText" },
                //ImageItem的XML文件里面的一个ImageView,两个TextView ID  
                new int[] { R.id.ItemImage, R.id.gridItemText });
        dragGridView.setAdapter(saImageItems);
        dragGridView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                Log.i(TAG, "onItemClick");
                if (mPluginServiceConnect != null) {

                    try {
                        mPluginServiceConnect.registerCallback(clientCallback);
                        mPluginServiceConnect.test(222);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        dragGridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> itemView, View arg1, int arg2, long arg3) {
                itemView.setDrawingCacheEnabled(true);
                
                int location[] = new int[2];
                
                Bitmap bitmap = itemView.getDrawingCache();
                itemView.setDrawingCacheEnabled(false);
                ImageView itemViewCopy = new ImageView(MainActivity.this);
                return false;
            }
        });

        rl.setLayoutParams(viewParams);
        return rl;
    }
}
