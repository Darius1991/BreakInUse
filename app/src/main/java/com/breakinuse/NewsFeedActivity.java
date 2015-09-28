package com.breakinuse;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Toast;

import com.breakinuse.dataSync.syncAdapter.BreakInUseSyncAdapter;
import com.breakinuse.utilities.Utility;

public class NewsFeedActivity extends AppCompatActivity {

    private static final String TAG = NewsFeedActivity.class.getName();
    private ViewPager mViewPager;
    private NewsFeedPagerAdapter mNewsFeedPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_feed);
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        Toolbar toolbar = (Toolbar) findViewById(R.id.newsFeedActivity_toolBar);
        setSupportActionBar(toolbar);

        mNewsFeedPagerAdapter = new NewsFeedPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.newsFeedActivity_viewPager);
        mViewPager.setAdapter(mNewsFeedPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.snewsFeedActivity_tabLayout);
        tabLayout.setupWithViewPager(mViewPager);

        BreakInUseSyncAdapter.initializeSyncAdapter(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_news_feed_activity, menu);
        MenuItem menuItem = menu.findItem(R.id.action_user_accounts);

        if (Utility.isUserLoggedIn(this)){

            menuItem.setTitle("Log Out");

        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == android.R.id.home){

            super.onBackPressed();
            return true;

        }

        if (id == R.id.action_user_accounts){

            if (!Utility.isUserLoggedIn(this)){

                Intent intent = new Intent(this,LoginActivity.class);
                startActivity(intent);

            } else {

                if(Utility.logOut(this)){

                    item.setTitle("User Accounts");
                }

            }

        } else if (id == R.id.refresh){

            if (!Utility.isNetworkAvailable(this)){

                Utility.makeToast(this,
                        "We are not able to detect an internet connection.",
                        Toast.LENGTH_SHORT);

            } else {

                Utility.updateNewsFeed(this);

            }

        } else if (id == R.id.action_settings) {

            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);

        }

        return super.onOptionsItemSelected(item);
    }

    public static class NewsFeedPagerAdapter extends FragmentPagerAdapter {

        private String tabTitles[] = new String[] { "All","Followed","Favourites" };
        private static SparseArray<Fragment> mRegisteredArray = new SparseArray<>();

        public NewsFeedPagerAdapter(FragmentManager fm) {

            super(fm);

        }

        @Override
        public Fragment getItem(int i) {

            switch (i) {

                case 0:

                    NewsFeedFragment allNewsFragment = new NewsFeedFragment();
                    return allNewsFragment;

                case 1:

                    FavouriteNewsFeedFragment favouriteNewsFragment = new FavouriteNewsFeedFragment();
                    return favouriteNewsFragment;

                case 2:

                    SavedNewsFeedFragment savedNewsFragment = new SavedNewsFeedFragment();
                    return savedNewsFragment;

            }

            return null;

        }

        @Override
        public int getCount() {

            return 3;

        }

        @Override
        public CharSequence getPageTitle(int position) {

            return tabTitles[position];
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            Fragment fragment = (Fragment) super.instantiateItem(container, position);
            mRegisteredArray.put(position, fragment);
            return fragment;

        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {

            mRegisteredArray.remove(position);
            super.destroyItem(container, position, object);

        }

        public static Fragment getRegisteredFragment(int position) {

            return mRegisteredArray.get(position);

        }

    }

}