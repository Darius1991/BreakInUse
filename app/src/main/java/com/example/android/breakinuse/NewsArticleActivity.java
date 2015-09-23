package com.example.android.breakinuse;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.example.android.breakinuse.utilities.Utility;

public class NewsArticleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_article);

        NewsArticleFragment newsArticleFragment = new NewsArticleFragment();

        String articleLoadMethod = getIntent().getStringExtra("ArticleLoadMethod");
        if (articleLoadMethod.equals("HTMLBody")){

            Bundle articleIDBundle =  new Bundle();
            String articleID = getIntent().getStringExtra("articleID");
            articleIDBundle.putString("articleID",articleID);
            articleIDBundle.putString("webURL",getIntent().getStringExtra("webURL"));
            articleIDBundle.putString("ArticleLoadMethod",articleLoadMethod);
            newsArticleFragment.setArguments(articleIDBundle);
            getFragmentManager()
                    .beginTransaction()
                    .add(android.R.id.content,newsArticleFragment)
                    .commit();

        } else if (articleLoadMethod.equals("webURL")){

            Bundle webURLBundle =  new Bundle();
            String webURL = getIntent().getStringExtra("webURL");
            webURLBundle.putString("webURL",webURL);
            webURLBundle.putString("ArticleLoadMethod",articleLoadMethod);
            newsArticleFragment.setArguments(webURLBundle);
            getFragmentManager()
                    .beginTransaction()
                    .add(android.R.id.content,newsArticleFragment)
                    .commit();

        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_news_article_activity, menu);
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
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_user_accounts){

            if (!Utility.isUserLoggedIn(this)){

                Intent intent = new Intent(this,LoginActivity.class);
                startActivity(intent);

            } else {

                if(Utility.logOut(this)){

                    item.setTitle("User Accounts");
                }

            }

        } else if (id == R.id.action_settings) {

            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);

        }

        return super.onOptionsItemSelected(item);
    }

}
