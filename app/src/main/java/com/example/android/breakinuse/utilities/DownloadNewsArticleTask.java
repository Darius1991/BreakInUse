package com.example.android.breakinuse.utilities;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import com.example.android.breakinuse.newsProvider.NewsContract;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class DownloadNewsArticleTask extends AsyncTask<String,Void,Void> {

    private Context mContext;
    private Cursor mCursor;


    public DownloadNewsArticleTask(Context tempContext){

        mContext = tempContext;

    }

    @Override
    protected Void doInBackground(String... params) {

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
                    .appendQueryParameter(FIELDS_QUERY_PARAM, "trailText,body,byline,headline,main,thumbnail")
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

            updateSavedNewsArticlesFromJSON(newsArticleArrayList);

        } catch (Exception e) {

            e.printStackTrace();
            return null;

        }

        return null;
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
            contentValues[index].put(NewsContract.NewsArticle.COLUMN_ARTICLEID,newsArticle.getString("id"));
            contentValues[index].put(NewsContract.NewsArticle.COLUMN_SECTIONID,newsArticle.getString("sectionId"));
            contentValues[index].put(NewsContract.NewsArticle.COLUMN_HEADLINE,
                    newsArticle.getJSONObject("fields").getString("headline"));
            try {

                contentValues[index].put(NewsContract.NewsArticle.COLUMN_IMAGEURL,
                        newsArticle.getJSONObject("fields").getString("thumbnail"));

            } catch (Exception e){

                contentValues[index].put(NewsContract.NewsArticle.COLUMN_IMAGEURL,
                        "http://vignette3.wikia.nocookie.net/wiisportsresortwalkthrough/images/6/60/No_Image_Available.png");

            }
//                    Utility.getImageURLFromMainHTML(newsArticle.getJSONObject("fields").getString("main")));
            contentValues[index].put(NewsContract.NewsArticle.COLUMN_TRAILTEXT,
                    newsArticle.getJSONObject("fields").getString("trailText"));
            htmlBody = new StringBuilder(newsArticle.getJSONObject("fields").getString("body"));
            while ((tagStartPos = htmlBody.indexOf("<figure")) != -1){

                tagEndPos = htmlBody.indexOf("</figure>");
                htmlBody.delete(tagStartPos,tagEndPos+9);

            }
            contentValues[index].put(NewsContract.NewsArticle.COLUMN_HTML_BODY,htmlBody.toString());
            contentValues[index].put(NewsContract.NewsArticle.COLUMN_BYLINE,
                    newsArticle.getJSONObject("fields").getString("byline"));
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
    protected void onPostExecute(Void aVoid) {

        super.onPostExecute(aVoid);
        if (mCursor != null){

            mCursor.close();

        }

    }

}
