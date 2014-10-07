package com.wowo.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.wowo.R;
import com.wowo.WoApplication;
import com.wowo.adapter.WowoAdapter;
import com.wowo.api.WowoApi;
import com.wowo.model.Wowo;
import com.wowo.view.LoadingFooter;

import java.util.List;



public class WowoListActivity extends BaseActivity implements AdapterView.OnItemClickListener {

    private ListView mListV;
    private WowoAdapter adapter;
    private List<Wowo> wowoArray;

//    private PullToRefreshLayout mPullToRefreshLayout;
    private LoadingFooter mLoadingFooter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AVUser currentUser = AVUser.getCurrentUser();
        if (currentUser == null) {
            Intent loginIntent = new Intent(this, WelcomeActivity.class);
            startActivity(loginIntent);
            finish();
            return;
        }

        setContentView(R.layout.activity_post_list);
        mListV = (ListView) findViewById(R.id.list);

//        mPullToRefreshLayout = (PullToRefreshLayout) findViewById(R.id.ptr_layout);
//        ActionBarPullToRefresh.from(this)
//                .allChildrenArePullable()
//                .listener(this)
//                .setup(mPullToRefreshLayout);

        mLoadingFooter = new LoadingFooter(this);
        mListV.addFooterView(mLoadingFooter.getView());

        wowoArray = WoApplication.getAmaList();
        WowoApi.latestWowo(null, null, new FindCallback<Wowo>() {
            @Override
            public void done(List<Wowo> wowos, AVException e) {
                if (e == null) {
                    wowoArray.addAll(wowos);
                    adapter = new WowoAdapter(WowoListActivity.this, wowoArray);
                    mListV.setAdapter(adapter);
                    Log.d("成功", "查询到" + wowos.size() + " 条符合条件的数据");
                } else {
                    Log.d("失败", "查询错误: " + e.getMessage());
                }
            }
        }, true);

        mListV.setOnItemClickListener(this);

        mListV.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                if (mLoadingFooter.getState() == LoadingFooter.State.Loading
                        || mLoadingFooter.getState() == LoadingFooter.State.TheEnd) {
                    return;
                }
                if (firstVisibleItem + visibleItemCount >= totalItemCount
                        && totalItemCount != 0
                        && totalItemCount != mListV.getHeaderViewsCount()
                        + mListV.getFooterViewsCount() && adapter.getCount() > 0) {
                    loadNextPage();
                    Log.d("DEBUG", "start load next page");
                }
            }
        });

    }

    /**
     * 加载更多
     */
    private void loadNextPage() {
        mLoadingFooter.setState(LoadingFooter.State.Loading);
        Log.d("DEBUG", "last: " + wowoArray.get(wowoArray.size()-1));
        WowoApi.nextWowo(wowoArray.get(wowoArray.size()-1), null, null, new FindCallback<Wowo>() {
            @Override
            public void done(List<Wowo> wowos, AVException e) {
                if (e == null) {
                    wowoArray.addAll(wowos);
                    adapter.notifyDataSetChanged();
                    Log.d("成功", "查询到" + wowos.size() + " 条符合条件的数据");
                    if (wowos == null || wowos.size()==0) {
                        mLoadingFooter.setState(LoadingFooter.State.TheEnd, 3000);
                    } else {
                        mLoadingFooter.setState(LoadingFooter.State.Idle, 3000);
                    }
                } else {
                    Log.d("失败", "查询错误: " + e.getMessage());
                    mLoadingFooter.setState(LoadingFooter.State.Idle, 3000);
                }

            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        Intent viewIntent = new Intent(this, WowoDetailActivity.class);
        viewIntent.putExtra("wowo", wowoArray.get(position).toMap());
        startActivity(viewIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.post_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_new_ama:
                Intent toCreateIntent = new Intent(this, CreateWowoActivity.class);
                startActivity(toCreateIntent);
                break;
            case R.id.action_settings:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 下拉刷新触发
     * @param view
     */
//    @Override
//    public void onRefreshStarted(View view) {
//        mPullToRefreshLayout.setRefreshing(true);
//        WowoApi.latestAma(wowoArray.get(0), new FindCallback<Wowo>() {
//            @Override
//            public void done(List<Wowo> wowos, AVException e) {
//                if (e == null) {
//                    Log.d("成功", "查询到" + wowos.size() + " 条符合条件的数据");
//                    if(wowos.contains(wowoArray.get(0))) {
//                        int index = wowos.indexOf(wowoArray.get(0));
//                        Toast.makeText(WowoListActivity.this, index + "条新消息", Toast.LENGTH_SHORT).show();
//                    }
//                    wowoArray.clear();
//                    wowoArray.addAll(wowos);
//                    adapter.notifyDataSetChanged();
//                } else {
//                    Log.d("DEBUG", "Error: " + e.toString());
//                }
//                mPullToRefreshLayout.setRefreshComplete();
//            }
//        }, false);
//    }

}
