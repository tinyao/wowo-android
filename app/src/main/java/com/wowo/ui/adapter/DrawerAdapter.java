
package com.wowo.ui.adapter;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.wowo.R;
import com.wowo.WoApplication;
import com.wowo.model.Category;

/**
 * Created by Issac on 7/18/13.
 */
public class DrawerAdapter extends BaseAdapter {
    private ListView mListView;

    public DrawerAdapter(ListView listView) {
        mListView = listView;
    }

    @Override
    public int getCount() {
        return Category.values().length;
    }

    @Override
    public Category getItem(int position) {
        return Category.values()[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(WoApplication.getContext()).inflate(
                    R.layout.drawer_list_item, null);
        }
        TextView textView = (TextView) convertView.findViewById(android.R.id.text1);
        textView.setText(getItem(position).getDisplayName());
        textView.setSelected(mListView.isItemChecked(position));
        return convertView;
    };
}
