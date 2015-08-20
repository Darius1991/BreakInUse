package com.example.android.breakinuse;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.android.breakinuse.Utilities.GetCompleteNewsTask;
import com.example.android.breakinuse.Utilities.Utility;

public class NewsFeedActivity extends AppCompatActivity{

    private final String TAG = NewsFeedActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_feed);
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        NewsFeedFragment fragment = new NewsFeedFragment();
        getFragmentManager()
                .beginTransaction()
                .add(android.R.id.content,fragment)
                .commit();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        MenuItem menuItem = menu.findItem(R.id.action_user_accounts);

        if (Utility.isUserLoggedIn(this)){
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

            if (!Utility.isUserLoggedIn(this)){
                Intent intent = new Intent(this,LoginActivity.class);
                startActivity(intent);
            } else {

                if(Utility.logOut(this)){
                    item.setTitle("User Accounts");
                }
            }
        }else if (id == R.id.refresh){

            if (!Utility.isNetworkAvailable(this)){
                Utility.makeToast(this,
                        "We are not able to detect an internet connection.",
                        Toast.LENGTH_SHORT);
            }else {
                new GetCompleteNewsTask(this)
                                .execute();
                return true;
            }
        }else if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

}
