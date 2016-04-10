package jisuto.drawerapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import jisuto.drawerapp.tab.ImagesFragment;
import jisuto.drawerapp.utils.SingletonCarrier;

public class DrawerActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private TabAdapter tabAdapter;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private int tab_count = 3;
    private FragmentManager mFragmentManager;
    private FragmentTransaction mFragmentTransaction;
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SingletonCarrier.init(getApplicationContext());
        setContentView(R.layout.activity_drawer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);
        //mNavigationView.getMenu().getItem(0).setChecked(true); //initialize drawer menu

        mFragmentManager = getSupportFragmentManager();
        mFragmentTransaction = mFragmentManager.beginTransaction();

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        viewPager = (ViewPager) findViewById(R.id.viewpager);

        tabAdapter = new TabAdapter(mFragmentManager, tab_count);
        tabLayout.setOnTabSelectedListener(new SelectionListener());
        viewPager.setAdapter(tabAdapter);
        tabLayout.setupWithViewPager(viewPager);


    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.drawer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.nav_gallery) {
            TabLayout.Tab tab = tabLayout.getTabAt(0);
            tab.select();
        } else if (id == R.id.nav_yaphotos) {
            TabLayout.Tab tab = tabLayout.getTabAt(1);
            tab.select();
        } else if (id == R.id.nav_cache) {
            TabLayout.Tab tab = tabLayout.getTabAt(2);
            tab.select();
        } else if (id == R.id.nav_settings) {

        } else if (id == R.id.nav_report) {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            String email = getString(R.string.author_email);
            intent.putExtra(Intent.EXTRA_EMAIL, new String []{ email});
            intent.putExtra(Intent.EXTRA_SUBJECT, "Report");

            startActivity(Intent.createChooser(intent, "Send Email"));
        }

        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    class TabAdapter extends FragmentPagerAdapter {

        public final int TAB_COUNT;
        Fragment mGallery;
        Fragment mYaPhotos;
        Fragment mCache;

        public TabAdapter(FragmentManager fm, int tab_count) {
            super(fm);
            TAB_COUNT = tab_count;

            SingletonCarrier loaderCarrier = SingletonCarrier.getInstance();

            mGallery = ImagesFragment.newInstance(loaderCarrier.getGalleryImageLoader());
            //mYaPhotos = new InternetImagesFragment();
            mYaPhotos = ImagesFragment.newInstance(loaderCarrier.getInternetImageLoader());
            mCache = ImagesFragment.newInstance(loaderCarrier.getCacheImageLoader());
        }

        /**
         * Return fragment with respect to Position .
         */

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return mGallery;
                case 1:
                    return mYaPhotos;
                case 2:
                    return mCache;
            }
            return null;
        }

        @Override
        public int getCount() {

            return TAB_COUNT;

        }

        /**
         * This method returns the title of the tab according to the position.
         */

        @Override
        public CharSequence getPageTitle(int position) {

            switch (position) {
                case 0:
                    return "Галерея";
                case 1:
                    return "ЯФотки";
                case 2:
                    return "Кэш";
            }
            return null;
        }
    }

    class SelectionListener implements TabLayout.OnTabSelectedListener {

        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            mNavigationView.getMenu().getItem(tab.getPosition()).setChecked(true);
            // TODO: backward checking currently does not work.
        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {

        }

        @Override
        public void onTabReselected(TabLayout.Tab tab) {

        }
    }

}
