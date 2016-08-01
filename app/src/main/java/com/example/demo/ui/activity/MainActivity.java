package com.example.demo.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.demo.R;
import com.example.demo.common.Constants;
import com.example.demo.common.Utils;
import com.example.demo.ui.fragment.AboutFragment;
import com.example.demo.ui.fragment.ExploreFragment;
import com.example.demo.ui.fragment.FavouriteFragment;
import com.example.demo.ui.fragment.HomeFragment;
import com.example.demo.ui.fragment.SettingsFragment;

import timber.log.Timber;

/**
 *  References:
 *  [1] https://guides.codepath.com/android/Fragment-Navigation-Drawer
 *  [2] http://stackoverflow.com/questions/13472258/handling-actionbar-title-with-the-fragment-back-stack
 *  [3] http://stackoverflow.com/questions/17107005/how-to-clear-fragment-backstack-in-android
 */
public class MainActivity extends AppCompatActivity {

    private static final String CURRENT_PAGE_TITLE = "current_page_title";
    private DrawerLayout mDrawer;
    private NavigationView mNavigationView;
    private CoordinatorLayout mLayout;
    private String mCurrentTitle;


    public static void launch(Activity activity) {
        Intent intent = new Intent(activity, MainActivity.class);
        activity.startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mLayout= (CoordinatorLayout) findViewById(R.id.coordinator_layout);

        initToolbar();
        initFab();
        setupDrawer();

        // set the initial fragment on startup, otherwise restore the page title
        if (savedInstanceState == null) {
            displayInitialFragment();
        } else {
            mCurrentTitle = savedInstanceState.getString(CURRENT_PAGE_TITLE);
            setTitle(mCurrentTitle);
        }
    }

    @Override
    public void onBackPressed() {
        // update the page title
        FragmentManager fm = getSupportFragmentManager();
        int count = fm.getBackStackEntryCount();
        if (count <= 1) {
            finish();
        } else{
            mCurrentTitle = fm.getBackStackEntryAt(count - 2).getName();
        }
        super.onBackPressed();
        setTitle(mCurrentTitle);

        // update nav drawer selection
        switch (mCurrentTitle) {
            case "Home":
                mNavigationView.setCheckedItem(R.id.drawer_home);
                break;
            case "Explore":
                mNavigationView.setCheckedItem(R.id.drawer_explore);
                break;
            case "Favourites":
                mNavigationView.setCheckedItem(R.id.drawer_favourite);
                break;
            case "Settings":
                mNavigationView.setCheckedItem(R.id.drawer_settings);
                break;
            case "About":
                mNavigationView.setCheckedItem(R.id.drawer_about);
                break;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(CURRENT_PAGE_TITLE, mCurrentTitle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Utils.showSnackbar(mLayout, "Clicked settings");
                return true;
            case android.R.id.home:
                mDrawer.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    // helper methods
    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() !=null) {
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_drawer);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void initFab() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.showSnackbar(mLayout, "Clicked fab");
            }
        });
    }

    private void setupDrawer() {
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        mNavigationView = (NavigationView) findViewById(R.id.navigation_view);
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                selectDrawerItem(item);
                return true;
            }
        });
    }

    private void selectDrawerItem(MenuItem item) {
        // select the item to instantiate based on the item clicked
        Fragment fragment = null;
        Class fragmentClass;
        switch (item.getItemId()) {
            case R.id.drawer_home:
                fragmentClass = HomeFragment.class;
                break;
            case R.id.drawer_explore:
                fragmentClass = ExploreFragment.class;
                break;
            case R.id.drawer_favourite:
                fragmentClass = FavouriteFragment.class;
                break;
            case R.id.drawer_settings:
                fragmentClass = SettingsFragment.class;
                break;
            case R.id.drawer_about:
                fragmentClass = AboutFragment.class;
                break;
            default:
                fragmentClass = HomeFragment.class;
        }
        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            Timber.e("%s: error loading fragment, %s", Constants.LOG_TAG, e.getMessage());
        }

        // highlight the selected item & update the page title
        item.setChecked(true);
        switch (item.getTitle().toString()) {
            case "Home":
                mCurrentTitle = getString(R.string.nav_menu_title_home);
                break;
            case "Explore":
                mCurrentTitle = getString(R.string.nav_menu_title_explore);
                break;
            case "Favourites":
                mCurrentTitle = getString(R.string.nav_menu_title_favourite);
                break;
            case "Settings":
                mCurrentTitle = getString(R.string.nav_menu_title_settings);
                break;
            case "About":
                mCurrentTitle = getString(R.string.nav_menu_title_about);
                break;
            default:
                mCurrentTitle = getString(R.string.nav_menu_title_home);
        }

        // clear the back stack if adding home fragment again
        FragmentManager fm = getSupportFragmentManager();
        int count = fm.getBackStackEntryCount();
        if (count > 1 && fragmentClass == HomeFragment.class) {
            fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }

        // replacing the existing fragment
        // 'tag' the fragment with the page title, used onBackPressed() to id fragment
        fm.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(mCurrentTitle)
                .commit();

        setTitle(mCurrentTitle); // display title on toolbar
        mDrawer.closeDrawers();
    }

    private void displayInitialFragment() {
        mCurrentTitle = getString(R.string.nav_menu_title_home);
        setTitle(mCurrentTitle);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, HomeFragment.newInstance())
                .addToBackStack(mCurrentTitle)
                .commit();
    }


}