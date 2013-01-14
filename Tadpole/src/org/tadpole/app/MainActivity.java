package org.tadpole.app;

import java.util.ArrayList;
import java.util.HashMap;

import org.tadpole.adapter.DragGridAdapter;
import org.tadpole.aidl.IPluginCallback;
import org.tadpole.aidl.PluginServiceConnect;
import org.tadpole.common.TLog;
import org.tadpole.util.ListUtil;
import org.tadpole.widget.Configure;
import org.tadpole.widget.DragGridView;
import org.tadpole.widget.DragGridView.G_ItemChangeListener;
import org.tadpole.widget.PagedView;
import org.tadpole.zenip.BoardPageItem;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

public class MainActivity extends BaseActvity {
    private static final String TAG = "MainActivity";
    private static final String SERVICE_NAME = "org.tadpole.service.testservices";
    private static final String MUSIC_SERVICE_NAME = "org.tadpolemusic.pluginservice";

    private PagedView mBoardPagedView;

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
    private ArrayList<ArrayList> mPageDataList = new ArrayList<ArrayList>();
    private ArrayList<DragGridView> mPageViewsList = new ArrayList<DragGridView>();

    public static final int PAGE_SIZE = 8;

    public ArrayList<BoardPageItem> loadBoardDataItems() {
        String[] titles = { "weico", "tecent_weibo", "qq", "dear qin", "tecent_weibo", "qq", "dear qin", "tecent_weibo", "qq", "dear qin", "tecent_weibo", "qq", "dear qin", "tecent_weibo", "qq",
                "dear qin", "tecent_weibo", "qq", "dear qin" };
        TLog.debug(TAG, "loadBoardDataItems itemCount=%d", titles.length);
        ArrayList<BoardPageItem> itemList = new ArrayList<BoardPageItem>();
        for (int i = 0, len = titles.length; i < len; i++) {
            BoardPageItem item = new BoardPageItem();
            item.title = titles[i];
            itemList.add(item);
        }
        return itemList;
    }


    private void initPage() {
        ArrayList<BoardPageItem> itemList = loadBoardDataItems();
        int itemCount = itemList.size();
        Configure.countPages = itemCount % PAGE_SIZE;
        int lastPageItemCount = itemCount / PAGE_SIZE;
        for (int pageIndex = 0; pageIndex < Configure.countPages; pageIndex++) {
            View v = null;
            if (pageIndex != Configure.countPages - 1) {
                v = buildPageView(itemList, pageIndex * PAGE_SIZE, (pageIndex + 1) * PAGE_SIZE);
            } else {
                v = buildPageView(itemList, pageIndex * PAGE_SIZE, pageIndex * PAGE_SIZE + lastPageItemCount);
            }
            mBoardPagedView.addView(v);
        }
    }

    private View buildPageView(ArrayList<BoardPageItem> itemList, int startIndex, int endIndex) {
        TLog.debug(TAG, "buildPageView startIndex=%d endIndex=%d", startIndex, endIndex);
        LinearLayout.LayoutParams LP = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
        View rl = LayoutInflater.from(this).inflate(R.layout.board_page, null);
        rl.setLayoutParams(LP);

        // disable gridview content scroll
        final DragGridView dragGridView = (DragGridView) rl.findViewById(R.id.grid_view_board_page);
        mPageViewsList.add(dragGridView);


        dragGridView.setPageListener(new DragGridView.G_PageListener() {
            @Override
            public void page(int cases, int page) {
                TLog.debug(TAG, "G_PageListener cases = %d, page = %d", cases, page);
                switch (cases) {
                case DragGridView.EVENT_START_DRAG:
                    break;
                case DragGridView.EVENT_END_DRAG:
                    break;
                case DragGridView.EVENT_SLIDING_PAGE:
                    MainActivity.this.mBoardPagedView.snapToScreen(page);
                    dragGridView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Configure.isChangingPage = false;
                        }
                    }, 600);
                    break;
                default:
                    break;
                }
            }
        });

        //生成动态数组，并且转入数据  
        ArrayList<HashMap<String, Object>> pageItems = new ArrayList<HashMap<String, Object>>();
        for (int i = startIndex; i < endIndex; i++) {
            HashMap<String, Object> map = null;
            map = new HashMap<String, Object>();
            map.put("ItemText", itemList.get(i).title);//按序号做ItemText 
            pageItems.add(map);
        }
        mPageDataList.add(pageItems);

        //生成适配器的ImageItem <====> 动态数组的元素，两者一一对应  
        DragGridAdapter dgaAdapter = new DragGridAdapter(this, //没什么解释  
                pageItems,//数据来源   
                R.layout.board_page_griditem,//night_iatem的XML实现  
                //动态数组与ImageItem对应的子项          
                new String[] { "ItemText" },
                //ImageItem的XML文件里面的一个ImageView,两个TextView ID  
                new int[] { R.id.gridItemText });

        dragGridView.setDragGridAdapter(dgaAdapter);
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
        dragGridView.setOnItemChangeListener(new G_ItemChangeListener() {
            @Override
            public void change(int from, int to, int count) {
                ArrayList<Object> fromList = (ArrayList<Object>) mPageDataList.get(Configure.currentPage - count);
                ArrayList<Object> toList = (ArrayList<Object>) mPageDataList.get(Configure.currentPage);

                ListUtil.swapListItem(fromList, toList, from, to);

                mPageViewsList.get(Configure.currentPage).notifyDataSetChanged();
                mPageViewsList.get(Configure.currentPage - count).notifyDataSetChanged();
            }
        });
        return rl;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add("刷新");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}
