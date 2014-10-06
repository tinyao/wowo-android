
package com.wowo.ui.fragment;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.wowo.R;
import com.wowo.model.Category;
import com.wowo.ui.MainActivity;
import com.wowo.ui.adapter.DrawerAdapter;

/**
 * Created by Issac on 7/18/13.
 */
public class DrawerFragment extends Fragment {
    private ListView mListView;

    private DrawerAdapter mAdapter;

    private MainActivity mActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mActivity = (MainActivity) getActivity();
        View contentView = inflater.inflate(R.layout.fragment_drawer, null);
        mListView = (ListView) contentView.findViewById(R.id.listView);
        mAdapter = new DrawerAdapter(mListView);
        mListView.setAdapter(mAdapter);
        mListView.setItemChecked(0, true);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mListView.setItemChecked(position, true);
                mActivity.setCategory(Category.values()[position]);
            }
        });
        return contentView;
    }
}
