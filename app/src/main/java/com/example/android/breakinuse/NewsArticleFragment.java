package com.example.android.breakinuse;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;


public class NewsArticleFragment extends Fragment {

    private final static String TAG = NewsArticleFragment.class.getName();

    public NewsArticleFragment(){

        setHasOptionsMenu(true);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_news_article,container,false);
        WebView webView = (WebView)rootView.findViewById(R.id.news_article_webView);
        Bundle webURLBundle = getArguments();
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl(webURLBundle.getString("webURL"));
        return rootView;

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.menu_news_article_fragment, menu);
        MenuItem shareActionMenuItem = menu.findItem(R.id.menu_item_share);

        ShareActionProvider shareActionProvider = (ShareActionProvider) MenuItemCompat
                                                    .getActionProvider(shareActionMenuItem);

        if (shareActionProvider != null){

            shareActionProvider.setShareIntent(createNewsArticleShareIntent());

        } else {

            Log.d(TAG,"ShareActionProvider is null.");

        }

    }

    private Intent createNewsArticleShareIntent() {

        Intent shareActionIntent = new Intent(Intent.ACTION_SEND);
        shareActionIntent.setType("text/plain");
        Bundle webURLBundle = getArguments();
        String webURL = webURLBundle.getString("webURL");
        shareActionIntent.putExtra(Intent.EXTRA_TEXT,"Read this Article - " + webURL);
        return shareActionIntent;

    }
}
