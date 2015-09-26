package com.example.android.breakinuse;

import android.app.Fragment;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.example.android.breakinuse.newsProvider.NewsContract;
import com.example.android.breakinuse.utilities.Utility;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;


public class NewsArticleFragment extends Fragment {

    private final static String TAG = NewsArticleFragment.class.getName();
    private Context mContext;
    private WebView mWebView;
    private Cursor mCursor;
    private ProgressBar mLoadMoreIndicator;

    public NewsArticleFragment(){

        setHasOptionsMenu(true);
        mContext = getActivity();

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mContext = getActivity();
        View rootView = inflater.inflate(R.layout.fragment_news_article, container, false);
        mWebView = (WebView) rootView.findViewById(R.id.news_article_webView);
        mLoadMoreIndicator = (ProgressBar) rootView.findViewById(R.id.newsArticle_loadMoreIndicator);
        mLoadMoreIndicator.setVisibility(View.GONE);

        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.newsArticleFragment_toolBar);
        ((NewsArticleActivity)mContext).setSupportActionBar(toolbar);
        if (((NewsArticleActivity)mContext).getSupportActionBar() != null){

            ((NewsArticleActivity)mContext).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        }

        Bundle bundle = getArguments();
        String articleLoadMethod = bundle.getString("ArticleLoadMethod");
        if (articleLoadMethod != null) {

            if (articleLoadMethod.equals("webURL")){

                mWebView.setWebViewClient(new WebViewClient(){

                    @Override
                    public void onPageStarted(WebView view, String url, Bitmap favicon) {

                        super.onPageStarted(view, url, favicon);
                        mLoadMoreIndicator.setVisibility(View.VISIBLE);

                    }

                    @Override
                    public void onPageFinished(WebView view, String url) {

                        super.onPageFinished(view, url);
                        mLoadMoreIndicator.setVisibility(View.GONE);

                    }

                });
                mWebView.loadUrl(bundle.getString("webURL"));

            } else if (articleLoadMethod.equals("HTMLBody")){

                String articleID = bundle.getString("articleID");
                mCursor = mContext.getContentResolver().query(NewsContract.NewsArticle.NEWSARTICLE_URI,
                        null,
                        NewsContract.NewsArticle.COLUMN_ARTICLEID + " =? ",
                        new String[]{articleID},
                        null);

                if ((mCursor != null) && (mCursor.moveToFirst())){

                    if (mCursor.getString(mCursor.getColumnIndex(NewsContract.NewsArticle.COLUMN_DOWNLOADFLAG)).equals("0")){

                        new DownloadNewsArticleTask().execute(articleID);

                    } else {

                        StringBuilder htmlBody = new StringBuilder();
                        htmlBody.append("<html>");
                        htmlBody.append("<h1>");
                        htmlBody.append(mCursor.getString(mCursor.getColumnIndex(NewsContract.NewsArticle.COLUMN_HEADLINE)));
                        htmlBody.append("</h1>");
                        htmlBody.append("<body>");
                        htmlBody.append(mCursor.getString(mCursor.getColumnIndex(NewsContract.NewsArticle.COLUMN_HTML_BODY)));
                        htmlBody.append("</body>");
                        htmlBody.append("</html>");
                        mWebView.getSettings().setJavaScriptEnabled(true);
                        mWebView.loadDataWithBaseURL("", htmlBody.toString(), "text/html", "UTF-8", "");

                    }

                }

            }

        }

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

    public class DownloadNewsArticleTask extends AsyncTask<String,Void,String> {

        @Override
        protected String doInBackground(String... params) {

            final String articleID = params[0];
            final String API_KEY_QUERY_PARAM = "api-key";
            final String FIELDS_QUERY_PARAM = "show-fields";
            final String ID_QUERY_PARAM = "ids";
            final String API_KEY = "tknk2ue9anxtt3d3zthr4j4b";
            final String API_SCHEME = "https";
            final String API_AUTHORITY = "content.guardianapis.com";
            final String API_CONTENT_END_POINT = "search";
            final String ORDER_QUERY_PARAM = "order-by";
            int newsFeedIDColumnIndex = -1;


            mCursor = mContext.getContentResolver().query(NewsContract.NewsArticle.NEWSARTICLE_URI,
                                null,
                                NewsContract.NewsArticle.COLUMN_ARTICLEID + " =? ",
                                new String[]{articleID},
                                null);

            if ((mCursor != null) && (mCursor.moveToFirst())){

                newsFeedIDColumnIndex = mCursor.getColumnIndex(NewsContract.NewsArticle.COLUMN_NEWSFEED_KEY);

            }

            StringBuilder responseString =  new StringBuilder();
            String holder;
            Uri.Builder builder = new Uri.Builder();
            URL url;
            URLConnection urlConnection;
            ArrayList<Utility.NewsArticleWithNewsFeedID> newsArticleArrayList = new ArrayList<>();
            BufferedReader reader = null;

            try {

                builder.scheme(API_SCHEME)
                        .authority(API_AUTHORITY)
                        .appendPath(API_CONTENT_END_POINT)
                        .appendQueryParameter(API_KEY_QUERY_PARAM, API_KEY)
                        .appendQueryParameter(FIELDS_QUERY_PARAM, "trailText,body,byline,headline,thumbnail")
                        .appendQueryParameter(ORDER_QUERY_PARAM, "relevance")
                        .appendQueryParameter(ID_QUERY_PARAM, articleID)
                        .build();

                url = new URL(builder.toString());
                urlConnection = url.openConnection();
                reader = new BufferedReader(new InputStreamReader
                        (urlConnection.getInputStream()));

                while (( holder = reader.readLine()) != null){

                    responseString.append(holder);

                }

                newsArticleArrayList.add(
                        new Utility.NewsArticleWithNewsFeedID(
                                new JSONObject(responseString.toString()),
                                mCursor.getInt(newsFeedIDColumnIndex)));
                reader.close();

            } catch (Exception e) {

                e.printStackTrace();

            } finally {

                if (reader !=null){

                    try {

                        reader.close();

                    } catch (Exception e) {

                        e.printStackTrace();
                        return null;

                    }
                }
            }

            try {

                if( updateSavedNewsArticlesFromJSON(newsArticleArrayList) == 1){

                    mCursor = mContext.getContentResolver().query(NewsContract.NewsArticle.NEWSARTICLE_URI,
                                    null,
                                    NewsContract.NewsArticle.COLUMN_ARTICLEID + " =? ",
                                    new String[]{articleID},
                                    null);

                    if ((mCursor != null) && (mCursor.moveToFirst())){

                        StringBuilder htmlBody = new StringBuilder();
                        htmlBody.append("<html>");
                        htmlBody.append("<h1>");
                        htmlBody.append(mCursor.getString(mCursor.getColumnIndex(NewsContract.NewsArticle.COLUMN_HEADLINE)));
                        htmlBody.append("</h1>");
                        htmlBody.append("<body>");
                        htmlBody.append(mCursor.getString(mCursor.getColumnIndex(NewsContract.NewsArticle.COLUMN_HTML_BODY)));
                        htmlBody.append("</body>");
                        htmlBody.append("</html>");
                        return htmlBody.toString();

                    } else {

                        return null;

                    }

                } else {
                        
                    return null;

                }

            } catch (Exception                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           e) {

                e.printStackTrace();
                return null;

            }

        }

        private int updateSavedNewsArticlesFromJSON(ArrayList<Utility.NewsArticleWithNewsFeedID> newsArticleArrayList)
                throws Exception{

            int articleCount = newsArticleArrayList.size();
            int index = 0,rowsUpdated = 0, rowUpdateFlag = 0, tagStartPos = 0, tagEndPos = 0;
            ContentValues[] contentValues = new ContentValues[articleCount];
            JSONObject responsePage,newsArticle;
            StringBuilder htmlBody = new StringBuilder();

            for (index = 0; index < articleCount; ++index){

                contentValues[index] = new ContentValues();

            }

            for (index = 0; index < articleCount; ++index){

                responsePage = (newsArticleArrayList.get(index)).newsArticle;
                newsArticle = responsePage.getJSONObject("response").getJSONArray("results").getJSONObject(0);

                contentValues[index].put(NewsContract.NewsArticle.COLUMN_NEWSFEED_KEY,
                        (newsArticleArrayList.get(index)).newsFeedID);
                contentValues[index].put(NewsContract.NewsArticle.COLUMN_WEBURL,newsArticle.getString("webUrl"));
                contentValues[index].put(NewsContract.NewsArticle.COLUMN_APIURL,newsArticle.getString("apiUrl"));
                contentValues[index].put(NewsContract.NewsArticle.COLUMN_ARTICLEID,newsArticle.getString("id"));
                contentValues[index].put(NewsContract.NewsArticle.COLUMN_SECTIONID,newsArticle.getString("sectionId"));
                contentValues[index].put(NewsContract.NewsArticle.COLUMN_HEADLINE,
                        newsArticle.getJSONObject("fields").getString("headline"));
                contentValues[index].put(NewsContract.NewsArticle.COLUMN_TRAILTEXT,
                        newsArticle.getJSONObject("fields").getString("trailText"));
                try {

                    contentValues[index].put(NewsContract.NewsArticle.COLUMN_THUMBNAILURL,
                            newsArticle.getJSONObject("fields").getString("thumbnail"));

                } catch (Exception e){

                    contentValues[index].put(NewsContract.NewsArticle.COLUMN_THUMBNAILURL,
                            "http://vignette3.wikia.nocookie.net/wiisportsresortwalkthrough/images/6/60/No_Image_Available.png");

                }
                contentValues[index].put(NewsContract.NewsArticle.COLUMN_IMAGEURL,
                        Utility.getImageURLFromMainHTML(newsArticle.getJSONObject("fields").getString("main")));
                htmlBody = new StringBuilder(newsArticle.getJSONObject("fields").getString("body"));
                while ((tagStartPos = htmlBody.indexOf("<figure")) != -1){

                    tagEndPos = htmlBody.indexOf("</figure>");
                    htmlBody.delete(tagStartPos,tagEndPos+9);

                }
                contentValues[index].put(NewsContract.NewsArticle.COLUMN_HTML_BODY,htmlBody.toString());
                try {

                    contentValues[index].put(NewsContract.NewsArticle.COLUMN_BYLINE,newsArticle.getJSONObject("fields").getString("byline"));

                } catch(Exception e) {

                    e.printStackTrace();
                    contentValues[index].put(NewsContract.NewsArticle.COLUMN_BYLINE,"Author");

                }

                contentValues[index].put(NewsContract.NewsArticle.COLUMN_DOWNLOADFLAG,"1");

                rowUpdateFlag = mContext.getContentResolver().update(NewsContract.NewsArticle.NEWSARTICLE_URI,
                        contentValues[index],
                        NewsContract.NewsArticle.COLUMN_ARTICLEID + " = ?",
                        new String[]{newsArticle.getString("id")});
                if (rowUpdateFlag > 0){

                    ++rowsUpdated;

                }

            }

            return rowsUpdated;
        }

        @Override
        protected void onPostExecute(String htmlBody) {

            super.onPostExecute(htmlBody);
            if (htmlBody != null){

                mWebView.getSettings().setJavaScriptEnabled(true);
                mWebView.loadDataWithBaseURL("", htmlBody, "text/html", "UTF-8", "");

            } else {

                //TODO handle error

            }

        }

    }

    @Override
    public void onDetach() {

        super.onDetach();
        if (mCursor != null){

            mCursor.close();

        }


    }

}
