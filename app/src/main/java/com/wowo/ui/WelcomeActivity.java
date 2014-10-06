package com.wowo.ui;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.avos.avoscloud.AVUser;
import com.wowo.R;

import org.goodev.helpviewpager.CirclePageIndicator;
import org.goodev.helpviewpager.HelpFragmentPagerAdapter;
import org.goodev.helpviewpager.OnLastPageListener;
import org.goodev.helpviewpager.PageIndicator;

public class WelcomeActivity extends FragmentActivity implements View.OnClickListener{

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_welcome);
        SlidePagerAdapter mAdapter = new SlidePagerAdapter(getSupportFragmentManager());

        ViewPager mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setOffscreenPageLimit(4);
        mPager.setAdapter(mAdapter);

        PageIndicator mIndicator = (CirclePageIndicator) findViewById(R.id.indicator);
        mIndicator.setViewPager(mPager);

        mIndicator.setOnLastPageListener(new OnLastPageListener() {
            @Override
            public void onLastPage() {
            }
        });

        findViewById(R.id.btn_sign_up).setOnClickListener(this);
        findViewById(R.id.btn_sign_in).setOnClickListener(this);

    }

    protected static final int[] CONTENT
            = new int[]{R.drawable.guide_1, R.drawable.guide_2,
            R.drawable.guide_3, R.drawable.guide_4};

    static final String[] TITLE = new String[]{"方便的沟通工具", "输入文字自动旋转", "连续语音识别", "随手画一画"};
    static final String[] DESC = new String[]{"心声，是帮助听障者与健全人沟通的工具。您可以借助手机和语音识别技术，来方便地与他人沟通。屏幕分上下两个部分，上半部分由健全人使用，下半部分由您来使用",
            "你可以在输入框中打字，同时会将文字旋转180度实时展示给对方。再也不用写一大段再给别人看了",
            "不像‘基本对话’按一次讲一句，在这里，只要点‘开始识别’，就可以持续自动识别，您可以它给电视、电影、优酷等翻译字幕。练习发音，甚至打电话",
            "当你没有代笔，又想快速跟对方笔谈。写完还可以自动旋转给对方看"};

    @Override
    public void onClick(View view) {
        Intent signinIntent = new Intent(this, LoginActivity.class);
        switch (view.getId()) {
            case R.id.btn_sign_in:
                signinIntent.putExtra("is_login", true);
                break;
             case R.id.btn_sign_up:
                signinIntent.putExtra("is_login", false);
                break;
        }
        startActivity(signinIntent);
    }


    /**
     *  SlideFragment 用于viewpager的单页
     */
    public static class SlideFragment extends Fragment {

        static final String KEY_CONTENT = "TestFragment:Content";
        int imageResid = 0;
        String title = "";
        String desc = "";

        public static SlideFragment newInstance(int resId, String title, String desc) {
            SlideFragment fragment = new SlideFragment();
            fragment.imageResid = resId;
            fragment.title = title;
            fragment.desc = desc;
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            if ((savedInstanceState != null) && savedInstanceState.containsKey(KEY_CONTENT)) {
                imageResid = savedInstanceState.getInt(KEY_CONTENT);
            }

            ViewGroup viewGroup = (ViewGroup) getActivity().getLayoutInflater().inflate(
                    R.layout.guide_slide_item, null);

            TextView titleV = (TextView) viewGroup.findViewById(R.id.slide_title);
            TextView descV = (TextView) viewGroup.findViewById(R.id.slide_desc);

            titleV.setText(title);
            descV.setText(desc);

            Log.d("DEBUG", "slide: " + imageResid);

            LinearLayout layout = new LinearLayout(getActivity());
            layout.setLayoutParams(
                    new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT));
            layout.setGravity(Gravity.CENTER);
            layout.addView(viewGroup);

            return layout;
        }

        @Override
        public void onSaveInstanceState(Bundle outState) {
            super.onSaveInstanceState(outState);
            outState.putInt(KEY_CONTENT, imageResid);
        }
    }


    public class SlidePagerAdapter extends HelpFragmentPagerAdapter {

        private int mCount = 4;

        public SlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getHelpItem(int position) {
            return SlideFragment.newInstance(CONTENT[position % 4], TITLE[position % 4], DESC[position % 4]);
        }

        @Override
        public int getHelpCount() {
            return mCount;
        }

        public void setCount(int count) {
            if (count > 0 && count <= 10) {
                mCount = count;
                notifyDataSetChanged();
            }
        }
    }


}