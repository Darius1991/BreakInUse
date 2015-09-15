package com.example.android.breakinuse;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

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
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home){

            Intent intent = NavUtils.getParentActivityIntent(this);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
            NavUtils.navigateUpTo(this, intent);
            return true;

        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
