package com.example.android.breakinuse;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.breakinuse.Utilities.GetCompleteNewsTask;
import com.example.android.breakinuse.Utilities.Utility;

public class NewsFeedActivity extends AppCompatActivity{

    public static Utility.NewsFeedItem[] mNewsFeedItemArray;
    private static RecyclerView mRecyclerView;
    private static HeadlinesAdapter mHeadlinesAdapter;
    private LinearLayoutManager mLayoutManager;
    private static Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mNewsFeedItemArray = new Utility.NewsFeedItem[1];
        mNewsFeedItemArray[0] = new Utility.NewsFeedItem();
        mNewsFeedItemArray[0].webURL = "bllaaa";
        mNewsFeedItemArray[0].apiURL = "blaaa";
        mNewsFeedItemArray[0].webTitle = "blaaa";
        mNewsFeedItemArray[0].trailText = "blaa";
        mNewsFeedItemArray[0].articleID = "blaaa";
        mNewsFeedItemArray[0].sectionID = "blaaa";

        mContext = getApplicationContext();
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView = (RecyclerView)findViewById(R.id.home_recyclerView);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mHeadlinesAdapter = new HeadlinesAdapter(mContext,mNewsFeedItemArray);
        mRecyclerView.setAdapter(mHeadlinesAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        MenuItem menuItem = menu.findItem(R.id.action_user_accounts);
        String LOGIN_METHOD = (getApplicationContext()
                .getSharedPreferences(getString(R.string.preferences_key), Context.MODE_PRIVATE))
                .getString(getString(R.string.login_method), getString(R.string.logged_out));
        if (!LOGIN_METHOD.equals(getString(R.string.logged_out))){
            menuItem.setTitle("Log Out");
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_user_accounts){

            String LOGIN_METHOD = (getApplicationContext()
                    .getSharedPreferences(getString(R.string.preferences_key), Context.MODE_PRIVATE))
                    .getString(getString(R.string.login_method), getString(R.string.logged_out));
            if (LOGIN_METHOD.equals("Logged Out")){
                Intent intent = new Intent(this,LoginActivity.class);
                startActivity(intent);
            } else {
                if(Utility.logOut(getApplicationContext())){
                    item.setTitle("User Accounts");
                }
            }
        }else if (id == R.id.refresh){

            if (!Utility.isNetworkAvailable(getApplicationContext())){
                Utility.makeToast(getApplicationContext(),
                        "We are not able to detect an internet connection.",
                        Toast.LENGTH_SHORT);
            }else {
                new GetCompleteNewsTask(getApplicationContext(),(TextView)findViewById(R.id.home_textView))
                                .execute();
                return true;
            }
        }else if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static void notifyDataSetChanged(Utility.NewsFeedItem[] newsFeedItemArray) {
        if (newsFeedItemArray != null){
            mHeadlinesAdapter = new HeadlinesAdapter(mContext,newsFeedItemArray);
            mRecyclerView.setAdapter(mHeadlinesAdapter);
        }

    }

}
