package com.example.android.breakinuse;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class NewsArticleFragment extends Fragment {

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


}
