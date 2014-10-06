package com.wowo.ui;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.wowo.R;
import com.wowo.api.WowoApi;
import com.wowo.model.Category;
import com.wowo.model.Comment;
import com.wowo.model.Wowo;
import com.wowo.ui.fragment.DrawerFragment;
import com.wowo.ui.fragment.WowosFragment;

import java.util.List;


public class MainActivity extends BaseActivity implements WowosFragment.OnFragmentInteractionListener{

    private WowosFragment mContentFragment;

    private Category mCategory;

    private Menu mMenu;

    private DrawerLayout mDrawerLayout;

    private ActionBarDrawerToggle mDrawerToggle;

    private PullToRefreshAttacher mPullToRefreshAttacher;

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

        setContentView(R.layout.activity_main);
        setupDrawer();

        PullToRefreshAttacher.Options options = new PullToRefreshAttacher.Options();
        options.headerInAnimation = R.anim.pulldown_fade_in;
        options.headerOutAnimation = R.anim.pulldown_fade_out;
        options.refreshScrollDistance = 0.3f;
//        options.headerLayout = R.layout.pulldown_header;
        mPullToRefreshAttacher = new PullToRefreshAttacher(this, options);

        setCategory(Category.all);

        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.left_drawer, new DrawerFragment()).commit();

        AVObject testObject = new AVObject("TestObject");
        testObject.put("foo", "bar");
        testObject.saveInBackground();

        // update cached liked comments
        WowoApi.likedComments(new FindCallback<Comment>() {
            @Override
            public void done(List<Comment> comments, AVException e) {
                Comment.updateCachedLikedComments(comments);
            }
        });

        // update cached liked wowos
        WowoApi.likedWowos(new FindCallback<Wowo>() {
            @Override
            public void done(List<Wowo> wowos, AVException e) {
                Wowo.updateCachedLikedWowos(wowos);
            }
        });
    }

    private void setupDrawer() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, Gravity.START);
        mDrawerLayout.setScrimColor(Color.argb(100, 0, 0, 0));
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setIcon(R.drawable.ic_launcher);

        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer image to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
        ) {
            public void onDrawerClosed(View view) {
                getActionBar().setTitle(mCategory.getDisplayName());
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle(R.string.app_name);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    protected Fragment onCreatePane() {
        return new WowosFragment();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    public void setCategory(Category category) {
        mDrawerLayout.closeDrawer(Gravity.START);
        if (mCategory == category) {
            return;
        }

        getActionBar().setTitle(category.getDisplayName());

        mPullToRefreshAttacher.setRefreshing(false);
        mCategory = category;
        mContentFragment = WowosFragment.newInstance(category);
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, mContentFragment).commit();
    }

    public PullToRefreshAttacher getPullToRefreshAttacher() {
        return mPullToRefreshAttacher;
    }

    @Override
    public void onFragmentInteraction(String id) {

    }
}
