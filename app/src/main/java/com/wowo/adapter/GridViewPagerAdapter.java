package com.wowo.adapter;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.wowo.R;

/**
 * Created by tinyao on 9/18/14.
 */
public class GridViewPagerAdapter extends FragmentPagerAdapter {

    private int mCount = 3;

    public static TypedArray templates;
    private static OnTemplateSelectedListener mListener;

    public GridViewPagerAdapter(Context context, FragmentManager fm, OnTemplateSelectedListener mListener) {
        super(fm);
        templates = context.getResources().obtainTypedArray(R.array.templates_s);
        this.mListener = mListener;
        Log.d("DEBUG", "templates: " + templates);
    }

    @Override
    public Fragment getItem(int position) {
        return GridViewFragment.newInstance(position);
    }

    @Override
    public int getCount() {
        return mCount;
    }

    public static class GridViewFragment extends Fragment {

        static final String KEY_CONTENT = "GridViewFragment:Content";
        int pageId;

        public static GridViewFragment newInstance(int pageId) {
            GridViewFragment fragment = new GridViewFragment();
            fragment.pageId = pageId;
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            if ((savedInstanceState != null) && savedInstanceState.containsKey(KEY_CONTENT)) {
                pageId = savedInstanceState.getInt(KEY_CONTENT);
            }

            ViewGroup viewGroup = (ViewGroup) getActivity().getLayoutInflater().inflate(
                    R.layout.template_grid, null);

            GridView templateGrid = (GridView) viewGroup.findViewById(R.id.template_grid_view);
            TemplateGridAdapter adapter = new TemplateGridAdapter(inflater.getContext(), templates, pageId);
            templateGrid.setAdapter(adapter);

            Log.d("DEBUG", "slide: " + pageId);

            LinearLayout layout = new LinearLayout(getActivity());
            layout.setLayoutParams(
                    new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            layout.setGravity(Gravity.CENTER_HORIZONTAL);
            layout.addView(viewGroup);

            templateGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                    mListener.OnTemplateSeleted(pageId * 10 + position);
                }
            });


            return layout;
        }

        public class TemplateGridAdapter extends BaseAdapter {

            private TypedArray gridTemplates;
            private LayoutInflater mInflater;
            private int pageId;

            public TemplateGridAdapter(Context context, TypedArray templates, int pageId) {
                mInflater = LayoutInflater.from(context);
                this.gridTemplates = templates;
                this.pageId = pageId;
            }

            @Override
            public int getCount() {
                return 10;
            }

            @Override
            public Object getItem(int position) {
                return gridTemplates.getDrawable(position);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup viewGroup) {
                final ImageView imgView;
                if (convertView == null) {
                    convertView = mInflater.inflate(R.layout.template_item, viewGroup, false);
                    imgView = (ImageView) convertView.findViewById(R.id.tempalte_img);
                    convertView.setTag(imgView);
                } else {
                    imgView = (ImageView) convertView.getTag();
                }

                imgView.setImageDrawable(gridTemplates.getDrawable(pageId * 10 + position));

                return convertView;
            }

        }

        @Override
        public void onSaveInstanceState(Bundle outState) {
            super.onSaveInstanceState(outState);
            outState.putInt(KEY_CONTENT, pageId);
        }
    }

    public static interface OnTemplateSelectedListener {
        public abstract void OnTemplateSeleted(int index);
    }

}
