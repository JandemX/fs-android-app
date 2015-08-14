package edu.hm.cs.fs.app.ui;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.fk07.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import edu.hm.cs.fs.app.ui.blackboard.BlackBoardFragment;
import edu.hm.cs.fs.app.ui.timetable.TimetableFragment;
import edu.hm.cs.fs.app.ui.fs.PresenceFragment;
import edu.hm.cs.fs.app.ui.home.HomeFragment;
import edu.hm.cs.fs.app.ui.info.InfoFragment;
import edu.hm.cs.fs.app.ui.job.JobFragment;
import edu.hm.cs.fs.app.ui.meal.MealFragment;
import edu.hm.cs.fs.app.ui.publictransport.PublicTransportTabFragment;
import edu.hm.cs.fs.app.ui.roomsearch.RoomSearchFragment;
import edu.hm.cs.fs.app.util.BaseFragment;
import edu.hm.cs.fs.app.util.Navigator;

/**
 * @author Fabio
 */
public class MainActivity extends AppCompatActivity implements DrawerLayout.DrawerListener,
        NavigationView.OnNavigationItemSelectedListener {
    private static final String NAV_ITEM_ID = "navItemId";

    @Bind(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    @Bind(R.id.navigation_view)
    NavigationView mNavigationView;
    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    private static Navigator mNavigator;
    private ActionBarDrawerToggle mDrawerToggle;
    @IdRes
    private int mCurrentMenuItem;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setupToolbar();
        setupNavigationDrawer();
        initNavigator();

        // load saved navigation state if present
        if (null == savedInstanceState) {
            mCurrentMenuItem = R.id.menu_home;
        } else {
            mCurrentMenuItem = savedInstanceState.getInt(NAV_ITEM_ID);
        }

        if (findViewById(R.id.container_detail) != null) {
            mNavigator.setDetailContainer(R.id.container_detail);
        } else {
            mNavigator.setDetailContainer(-1);
        }

        onNavigationItemSelected(mNavigationView.getMenu().findItem(mCurrentMenuItem));
    }

    private void setupToolbar() {
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar == null) {
            return;
        }
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
    }

    private void setupNavigationDrawer() {
        mDrawerLayout.setDrawerListener(this);
        //TODO look at documantation => homepage do I really need like that?
        mDrawerToggle = new ActionBarDrawerToggle(this
                , mDrawerLayout
                , mToolbar
                , R.string.navigation_drawer_open
                , R.string.navigation_drawer_close);

        mDrawerToggle.syncState();
        mNavigationView.setNavigationItemSelectedListener(this);
    }

    private void initNavigator() {
        mNavigator = new Navigator(this, getSupportFragmentManager(), R.id.container);
    }

    private void setNewRootFragment(BaseFragment fragment) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            if (fragment.hasCustomToolbar()) {
                actionBar.hide();
            } else {
                actionBar.show();
            }
        }
        mNavigator.setRootFragment(fragment);
        mDrawerLayout.closeDrawers();
    }

    public Navigator getNavigator() {
        return mNavigator;
    }

    public Toolbar getToolbar() {
        return mToolbar;
    }

    @Override
    protected void onPostCreate(final Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        if (item.getItemId() == android.support.v7.appcompat.R.id.home) {
            return mDrawerToggle.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConfigurationChanged(final Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onDrawerSlide(View drawerView, float slideOffset) {
        mDrawerToggle.onDrawerSlide(drawerView, slideOffset);
    }

    public void openDrawer() {
        mDrawerLayout.openDrawer(GravityCompat.START);
    }

    @Override
    public void onDrawerOpened(View drawerView) {
        mDrawerToggle.onDrawerOpened(drawerView);
    }

    @Override
    public void onDrawerClosed(View drawerView) {
        mDrawerToggle.onDrawerClosed(drawerView);
    }

    @Override
    public void onDrawerStateChanged(int newState) {
        mDrawerToggle.onDrawerStateChanged(newState);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        @IdRes int id = menuItem.getItemId();
        if (id == mCurrentMenuItem) {
            mDrawerLayout.closeDrawers();
            return false;
        }
        switch (id) {
            // My study
            case R.id.menu_home:
                setNewRootFragment(new HomeFragment());
                break;

            case R.id.menu_blackboard:
                setNewRootFragment(new BlackBoardFragment());
                break;

            case R.id.menu_timetable:
                setNewRootFragment(new TimetableFragment());
                break;

            case R.id.menu_roomsearch:
                setNewRootFragment(new RoomSearchFragment());
                break;

            // Student council
            case R.id.menu_news:
                //setNewRootFragment(FlexibleSpaceFragment.newInstance());
                break;

            case R.id.menu_presence:
                setNewRootFragment(new PresenceFragment());
                break;

            // Offers
            case R.id.menu_food:
                setNewRootFragment(new MealFragment());
                break;

            case R.id.menu_jobs:
                setNewRootFragment(new JobFragment());
                break;

            case R.id.menu_mvv:
                setNewRootFragment(new PublicTransportTabFragment());
                break;

            // Others
            case R.id.menu_info:
                setNewRootFragment(new InfoFragment());
                break;

            case R.id.menu_feedback:
                final Intent intent = new Intent(Intent.ACTION_SEND, Uri.parse("mailto:"));
                intent.setType("message/rfc822");
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"app@fs.cs.hm.edu"});
                startActivity(intent);
                return false;
        }
        mCurrentMenuItem = id;
        menuItem.setChecked(true);
        return false;
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onSaveInstanceState(final Bundle outState) {
        outState.putInt(NAV_ITEM_ID, mCurrentMenuItem);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void finish() {
        mNavigator = null;
        super.finish();
    }
}
