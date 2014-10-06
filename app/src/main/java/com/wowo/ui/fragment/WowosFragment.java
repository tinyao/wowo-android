package com.wowo.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.FindCallback;
import com.wowo.R;

import com.wowo.WoApplication;
import com.wowo.adapter.WowoAdapter;
import com.wowo.api.WowoApi;
import com.wowo.model.Category;
import com.wowo.model.Wowo;
import com.wowo.ui.MainActivity;
import com.wowo.ui.WowoDetailActivity;
import com.wowo.ui.fragment.dummy.DummyContent;
import com.wowo.view.LoadingFooter;

import java.util.ArrayList;
import java.util.List;

import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher;

public class WowosFragment extends Fragment implements AdapterView.OnItemClickListener,
        PullToRefreshAttacher.OnRefreshListener {

    public static final String EXTRA_CATEGORY = "EXTRA_CATEGORY";

    private OnFragmentInteractionListener mListener;

    private MainActivity mActivity;

    private PullToRefreshAttacher mPullToRefreshAttacher;

    private Category mCategory;

    private ListView mListView;

    private ListAdapter mAdapter;

    private LoadingFooter mLoadingFooter;

    private WowoAdapter adapter;

    private List<Wowo> wowoArray = new ArrayList<Wowo>();

    public static WowosFragment newInstance(Category category) {
        WowosFragment fragment = new WowosFragment();
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_CATEGORY, category.name());
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            parseArgument();
        }

//        // TODO: Change Adapter to display your content
//        mAdapter = new ArrayAdapter<DummyContent.DummyItem>(getActivity(),
//                android.R.layout.simple_list_item_1, android.R.id.text1, DummyContent.ITEMS);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.fragment_wowo, container, false);
        mListView = (ListView) contentView.findViewById(android.R.id.list);

        mPullToRefreshAttacher = ((MainActivity) getActivity()).getPullToRefreshAttacher();
        mPullToRefreshAttacher.setRefreshableView(mListView, this);

        mLoadingFooter = new LoadingFooter(this.getActivity());
        mListView.addFooterView(mLoadingFooter.getView());

        mListView.setAdapter(mAdapter);

        mListView.setOnItemClickListener(this);

//        wowoArray = WoApplication.getAmaList();

        loadData();

        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
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
                        && totalItemCount != mListView.getHeaderViewsCount()
                        + mListView.getFooterViewsCount() && adapter.getCount() > 0) {
                    loadNextPage();
                    Log.d("DEBUG", "start load next page");
                }
            }
        });

        return contentView;
    }

    private void loadData() {
        WowoApi.latestWowo(mCategory, new FindCallback<Wowo>() {
            @Override
            public void done(List<Wowo> wowos, AVException e) {
                if (e == null) {
                    wowoArray.addAll(wowos);
                    adapter = new WowoAdapter(WoApplication.getContext(), wowoArray);
                    mListView.setAdapter(adapter);
                    Log.d("成功", "查询到" + wowos.size() + " 条符合条件的数据");
                } else {
                    Log.d("失败", "查询错误: " +  e.getCode() + e.getStackTrace() + e.getMessage());
                }
            }
        }, true);
    }

    private void loadNextPage() {
        mLoadingFooter.setState(LoadingFooter.State.Loading);
        WowoApi.nextWowo(wowoArray.get(wowoArray.size() - 1), mCategory, new FindCallback<Wowo>() {
            @Override
            public void done(List<Wowo> wowos, AVException e) {
                if (e == null) {
                    wowoArray.addAll(wowos);
                    adapter.notifyDataSetChanged();
                    Log.d("成功", "查询到" + wowos.size() + " 条符合条件的数据");
                    if (wowos == null || wowos.size() == 0) {
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

    private void parseArgument() {
        Bundle bundle = getArguments();
        mCategory = Category.valueOf(bundle.getString(EXTRA_CATEGORY));
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mPullToRefreshAttacher.getHeaderTransformer().onConfigurationChanged(getActivity());
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent viewIntent = new Intent(getActivity(), WowoDetailActivity.class);
        viewIntent.putExtra("wowo", wowoArray.get(position).toMap());
        getActivity().startActivity(viewIntent);
//
//        if (null != mListener) {
//            // Notify the active callbacks interface (the activity, if the
//            // fragment is attached to one) that an item has been selected.
//            mListener.onFragmentInteraction(DummyContent.ITEMS.get(position).id);
//        }
    }

    /**
     * The default content for this Fragment has a TextView that is shown when
     * the list is empty. If you would like to change the text, call this method
     * to supply the text it should use.
     */
    public void setEmptyText(CharSequence emptyText) {
        View emptyView = mListView.getEmptyView();

        if (emptyText instanceof TextView) {
            ((TextView) emptyView).setText(emptyText);
        }
    }

    @Override
    public void onRefreshStarted(View view) {
        mPullToRefreshAttacher.setRefreshing(true);
        WowoApi.latestWowo(mCategory, new FindCallback<Wowo>() {
            @Override
            public void done(List<Wowo> wowos, AVException e) {
                if (e == null) {
                    Log.d("成功", "查询到" + wowos.size() + " 条符合条件的数据");
                    if (wowos.contains(wowoArray.get(0))) {
                        int index = wowos.indexOf(wowoArray.get(0));
                        Toast.makeText(getActivity(), index + "条新消息", Toast.LENGTH_SHORT).show();
                    }
                    wowoArray.clear();
                    wowoArray.addAll(wowos);
                    adapter.notifyDataSetChanged();
                } else {
                    Log.d("DEBUG", "Error: " + e.toString());
                }
                mPullToRefreshAttacher.setRefreshComplete();
            }
        }, false);
    }

    /**
    * This interface must be implemented by activities that contain this
    * fragment to allow an interaction in this fragment to be communicated
    * to the activity and potentially other fragments contained in that
    * activity.
    * <p>
    * See the Android Training lesson <a href=
    * "http://developer.android.com/training/basics/fragments/communicating.html"
    * >Communicating with Other Fragments</a> for more information.
    */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(String id);
    }

}
