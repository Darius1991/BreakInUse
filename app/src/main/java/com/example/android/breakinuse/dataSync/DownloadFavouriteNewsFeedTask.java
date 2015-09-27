package com.example.android.breakinuse.dataSync;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.android.breakinuse.NewsFeedActivity;
import com.example.android.breakinuse.newsProvider.NewsContract;
import com.example.android.breakinuse.utilities.Utility;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Set;

public class DownloadFavouriteNewsFeedTask extends AsyncTask<Void,Void,Void> {

    private Cursor mCursor;
    private Context mContext;
    private final int PAGE_SIZE = 20;
    private static final String TAG = DownloadNewsFeedTask.class.getName();
    private ProgressBar mLoadMoreIndicator;
    private OnDownloadTaskFinishedListener mOnDownloadTaskFinishedListener;

    public DownloadFavouriteNewsFeedTask(Context context, ProgressBar loadMoreIndicator, Fragment fragment){

        mContext = context;
        mLoadMoreIndicator = loadMoreIndicator;
        mOnDownloadTaskFinishedListener = (OnDownloadTaskFinishedListener)fragment;

    }

    @Override
    protected Void doInBackground(Void... params) {

        final String API_KEY_QUERY_PARAM = "api-key";
        final String SECTION_QUERY_PARAM = "section";
        final String FIELDS_QUERY_PARAM = "show-fields";
        final String ID_QUERY_PARAM = "ids";
        final String API_KEY = "tknk2ue9anxtt3d3zthr4j4b";
        final String API_SCHEME = "https";
        final String API_AUTHORITY = "content.guardianapis.com";
        final String API_CONTENT_END_POINT = "search";
        final String TAGS_QUERY_PARAM = "show-tags";
        final String ELEMENTS_QUERY_PARAM = "show-elements";
        final String FROMDATE_QUERY_PARAM = "from-date";
        final String TODATE_QUERY_PARAM = "to-date";
        final String ORDER_QUERY_PARAM = "order-by";
        final String PAGESIZE_QUERY_PARAM = "page-size";
        final String PAGE_QUERY_PARAM = "page";

        StringBuilder topicsQuery = new StringBuilder();
        Set<String> favouriteTopicsSet = Utility.getFavouriteTopicsSet(mContext);
        if (favouriteTopicsSet.isEmpty()){

            mOnDownloadTaskFinishedListener.onDownloadTaskFinished("caughtException");
            return null;

        }
        for (String iterator : favouriteTopicsSet) {

            if (iterator != null){

                topicsQuery.append(iterator);
                topicsQuery.append("|");

            }

        }
        topicsQuery = new StringBuilder(topicsQuery.substring(0,(topicsQuery.length()-1)));

        StringBuilder responseString =  new StringBuilder();
        String holder;
        Uri.Builder builder = new Uri.Builder();
        URL url;
        URLConnection urlConnection;
        String fromDate = Utility.getCurrentDate();
        int articleCount = 0;
        JSONObject[] responsePage = new JSONObject[1];
        BufferedReader reader = null;
        int dbNewsFeedItemCount = 0;

        String[] selectionArgs = new String[favouriteTopicsSet.size()];
        StringBuilder selection = new StringBuilder();

        int index = 0;
        for (String iterator : favouriteTopicsSet){

            selectionArgs[index++] = iterator;
            selection.append("?");
            selection.append(",");

        }
        selection = new StringBuilder(selection.substring(0,selection.length()-1));

        mCursor = mContext.getContentResolver().query(NewsContract.NewsFeed.NEWSFEED_READURI,
                null,
                NewsContract.NewsFeed.COLUMN_SECTIONID + " IN (" + selection.toString() +")",
                selectionArgs,
                null);

        if (mCursor != null){

            if (mCursor.moveToFirst()){

                dbNewsFeedItemCount = mCursor.getCount();

                try {

                    builder.scheme(API_SCHEME)
                            .authority(API_AUTHORITY)
                            .appendPath(API_CONTENT_END_POINT)
                            .appendQueryParameter(API_KEY_QUERY_PARAM, API_KEY)
                            .appendQueryParameter(FIELDS_QUERY_PARAM, "trailText,body,byline,headline,main,thumbnail")
                            .appendQueryParameter(PAGESIZE_QUERY_PARAM, String.valueOf(1))
                            .appendQueryParameter(ORDER_QUERY_PARAM, "relevance")
                            .appendQueryParameter(PAGE_QUERY_PARAM,String.valueOf(1))
                            .appendQueryParameter(SECTION_QUERY_PARAM, topicsQuery.toString())
                            .build();

                    url = new URL(builder.toString());
                    urlConnection = url.openConnection();
                    reader = new BufferedReader(new InputStreamReader
                            (urlConnection.getInputStream()));

                    while (( holder = reader.readLine()) != null){

                        responseString.append(holder);

                    }

                    responsePage[0] = new JSONObject(responseString.toString());
                    reader.close();

                } catch (Exception e) {

                    e.printStackTrace();

                } finally {

                    if (reader !=null){

                        try {

                            reader.close();

                        } catch (Exception e) {

                            e.printStackTrace();
                            mOnDownloadTaskFinishedListener.onDownloadTaskFinished("caughtException");
                            return null;

                        }
                    }
                }

                if (responseString.length() != 0){

                    try {

                        if (!isResponseStatusOk(responsePage[0])){

                            mOnDownloadTaskFinishedListener.onDownloadTaskFinished("caughtException");
                            return null;

                        }

                    } catch (Exception e) {

                        e.printStackTrace();
                        mOnDownloadTaskFinishedListener.onDownloadTaskFinished("caughtException");
                        return null;

                    }
                    try {

                        articleCount = getPageCount(responsePage[0]);

                    } catch (Exception e) {

                        e.printStackTrace();
                        mOnDownloadTaskFinishedListener.onDownloadTaskFinished("caughtException");
                        return null;

                    }

                } else {

                    mOnDownloadTaskFinishedListener.onDownloadTaskFinished("caughtException");
                    return null;

                }

                if (articleCount <= dbNewsFeedItemCount){

                    ((NewsFeedActivity)mContext).runOnUiThread(new Runnable() {

                        public void run() {

                            Utility.makeToast(mContext, "No more Articles available", Toast.LENGTH_SHORT);

                        }

                    });
                    mOnDownloadTaskFinishedListener.onDownloadTaskFinished("caughtException");
                    return null;

                }

                if ((dbNewsFeedItemCount % PAGE_SIZE) == 0){

                    responseString.delete(0, responseString.length());
                    builder.clearQuery();

                    try {

                        builder.appendQueryParameter(API_KEY_QUERY_PARAM, API_KEY)
                                .appendQueryParameter(FIELDS_QUERY_PARAM, "trailText,body,byline,headline,main,thumbnail")
                                .appendQueryParameter(PAGESIZE_QUERY_PARAM, String.valueOf(PAGE_SIZE))
                                .appendQueryParameter(PAGE_QUERY_PARAM, String.valueOf((dbNewsFeedItemCount / PAGE_SIZE)+1))
                                .appendQueryParameter(ORDER_QUERY_PARAM, "newest")
                                .appendQueryParameter(SECTION_QUERY_PARAM, topicsQuery.toString())
                                .build();

                        url = new URL(builder.toString());
                        urlConnection = url.openConnection();
                        reader = new BufferedReader(new InputStreamReader
                                (urlConnection.getInputStream()));

                        while (( holder = reader.readLine()) != null){

                            responseString.append(holder);

                        }

                        responsePage[0] = new JSONObject(responseString.toString());
                        reader.close();

                    } catch (Exception e) {

                        e.printStackTrace();

                    } finally {

                        if (reader !=null){

                            try {

                                reader.close();

                            } catch (Exception e) {

                                e.printStackTrace();
                                mOnDownloadTaskFinishedListener.onDownloadTaskFinished("caughtException");
                                return null;

                            }
                        }
                    }

                    responseString.delete(0, responseString.length());

                    try {

                        insertNewsFeedItemsInNewsFeedTableFromJSON(responsePage,1);

                    } catch (Exception e) {

                        e.printStackTrace();
                        mOnDownloadTaskFinishedListener.onDownloadTaskFinished("caughtException");
                        return null;

                    }

                } else {

                    if (Math.floor(dbNewsFeedItemCount/PAGE_SIZE) == Math.floor(articleCount/PAGE_SIZE)){

                        responseString.delete(0, responseString.length());
                        builder.clearQuery();

                        try {

                            builder.appendQueryParameter(API_KEY_QUERY_PARAM, API_KEY)
                                    .appendQueryParameter(FIELDS_QUERY_PARAM, "trailText,body,byline,headline,main,thumbnail")
                                    .appendQueryParameter(PAGESIZE_QUERY_PARAM, String.valueOf(PAGE_SIZE))
                                    .appendQueryParameter(PAGE_QUERY_PARAM, String.valueOf((dbNewsFeedItemCount / PAGE_SIZE)+1))
                                    .appendQueryParameter(ORDER_QUERY_PARAM, "newest")
                                    .appendQueryParameter(SECTION_QUERY_PARAM, topicsQuery.toString())
                                    .build();

                            url = new URL(builder.toString());
                            urlConnection = url.openConnection();
                            reader = new BufferedReader(new InputStreamReader
                                    (urlConnection.getInputStream()));

                            while (( holder = reader.readLine()) != null){

                                responseString.append(holder);

                            }

                            responsePage[0] = new JSONObject(responseString.toString());
                            reader.close();

                        } catch (Exception e) {

                            e.printStackTrace();

                        } finally {

                            if (reader !=null){

                                try {

                                    reader.close();

                                } catch (Exception e) {

                                    e.printStackTrace();
                                    mOnDownloadTaskFinishedListener.onDownloadTaskFinished("caughtException");
                                    return null;

                                }
                            }
                        }

                        responseString.delete(0, responseString.length());

                        try {

                            insertNewsFeedItemsInNewsFeedTableFromJSON(responsePage,1);

                        } catch (Exception e) {

                            e.printStackTrace();
                            mOnDownloadTaskFinishedListener.onDownloadTaskFinished("caughtException");
                            return null;

                        }

                    } else {

                        JSONObject[] tempArray = new JSONObject[2];
                        tempArray[0] = responsePage[0];
                        responsePage = tempArray;

                        for ( index=1; index <= 2; ++index){

                            responseString.delete(0, responseString.length());
                            builder.clearQuery();

                            try {

                                builder.appendQueryParameter(API_KEY_QUERY_PARAM, API_KEY)
                                        .appendQueryParameter(FIELDS_QUERY_PARAM, "trailText,body,byline,headline,main,thumbnail")
                                        .appendQueryParameter(PAGESIZE_QUERY_PARAM, String.valueOf(PAGE_SIZE))
                                        .appendQueryParameter(PAGE_QUERY_PARAM, String.valueOf((dbNewsFeedItemCount/PAGE_SIZE)+index))
                                        .appendQueryParameter(ORDER_QUERY_PARAM, "newest")
                                        .appendQueryParameter(SECTION_QUERY_PARAM, topicsQuery.toString())
                                        .build();

                                url = new URL(builder.toString());
                                urlConnection = url.openConnection();
                                reader = new BufferedReader(new InputStreamReader
                                        (urlConnection.getInputStream()));

                                while (( holder = reader.readLine()) != null){

                                    responseString.append(holder);

                                }

                                responsePage[index-1] = new JSONObject(responseString.toString());
                                reader.close();

                            } catch (Exception e) {

                                e.printStackTrace();

                            } finally {

                                if (reader !=null){

                                    try {

                                        reader.close();

                                    } catch (Exception e) {

                                        e.printStackTrace();
                                        mOnDownloadTaskFinishedListener.onDownloadTaskFinished("caughtException");
                                        return null;

                                    }
                                }
                            }
                        }

                        responseString.delete(0, responseString.length());

                        try {

                            insertNewsFeedItemsInNewsFeedTableFromJSON(responsePage,2);

                        } catch (Exception e) {

                            e.printStackTrace();
                            mOnDownloadTaskFinishedListener.onDownloadTaskFinished("caughtException");
                            return null;

                        }

                    }

                }

            } else {

                Utility.updateNewsFeed(mContext);
                mOnDownloadTaskFinishedListener.onDownloadTaskFinished("caughtException");
                return null;

            }

        } else {

            Utility.updateNewsFeed(mContext);
            mOnDownloadTaskFinishedListener.onDownloadTaskFinished("caughtException");
            return null;

        }

        return null;

    }

    @Override
    protected void onPostExecute(Void aVoid) {

        super.onPostExecute(aVoid);
        mLoadMoreIndicator.setVisibility(View.GONE);
        if (mCursor != null){

            mCursor.close();

        }

    }

    private boolean isResponseStatusOk(JSONObject responsePage) throws Exception {

        return responsePage.getJSONObject("response").getString("status").equals("ok");

    }

    private int getPageCount(JSONObject responsePage) throws Exception{

        return responsePage.getJSONObject("response").getInt("pages");

    }

    private int insertNewsFeedItemsInNewsFeedTableFromJSON(JSONObject[] responsePage, int pageCount) throws Exception {

//        int pageCount = responsePage[0].getJSONObject("response").getInt("pages");
        int pageIndex, webTitleIndex, arrayIndex, responseCount;
        responseCount = 2*PAGE_SIZE; /*responsePage[0].getJSONObject("response").getInt("total");*/
        ContentValues[] newsFeedContentValues = new ContentValues[responseCount];
        String currentDate = Utility.getCurrentDate();
        JSONArray newsFeedItemJSONArray;
        JSONObject newsFeedItemJSONObject;

        for (int index = 0; index < responseCount; ++index){

            newsFeedContentValues[index] = new ContentValues();

        }

        for (pageIndex = 0; pageIndex <= pageCount-1; ++pageIndex){

            webTitleIndex = 0;
            newsFeedItemJSONArray = responsePage[pageIndex].getJSONObject("response").getJSONArray("results");

            for ( ;webTitleIndex < newsFeedItemJSONArray.length() ; ++webTitleIndex){

                newsFeedItemJSONObject = newsFeedItemJSONArray.getJSONObject(webTitleIndex);
                arrayIndex = webTitleIndex + ((pageIndex) * PAGE_SIZE);

                newsFeedContentValues[arrayIndex].put(NewsContract.NewsFeed.COLUMN_WEBTITLE,
                        newsFeedItemJSONObject.getString("webTitle"));
                newsFeedContentValues[arrayIndex].put(NewsContract.NewsFeed.COLUMN_WEBURL,
                        newsFeedItemJSONObject.getString("webUrl"));
                newsFeedContentValues[arrayIndex].put(NewsContract.NewsFeed.COLUMN_APIURL,
                        newsFeedItemJSONObject.getString("apiUrl"));
                newsFeedContentValues[arrayIndex].put(NewsContract.NewsFeed.COLUMN_TRAILTEXT,
                        newsFeedItemJSONObject.getJSONObject("fields").getString("trailText"));
                try {

                    newsFeedContentValues[arrayIndex].put(NewsContract.NewsFeed.COLUMN_BYLINE,
                            newsFeedItemJSONObject.getJSONObject("fields").getString("byline"));

                }catch (Exception e){

                    e.printStackTrace();
                    newsFeedContentValues[arrayIndex].put(NewsContract.NewsFeed.COLUMN_BYLINE,"Author");

                }

                try {

                    newsFeedContentValues[arrayIndex].put(NewsContract.NewsArticle.COLUMN_THUMBNAILURL,
                            newsFeedItemJSONObject.getJSONObject("fields").getString("thumbnail"));

                } catch (Exception e){

                    newsFeedContentValues[arrayIndex].put(NewsContract.NewsArticle.COLUMN_THUMBNAILURL,
                            "http://vignette3.wikia.nocookie.net/wiisportsresortwalkthrough/images/6/60/No_Image_Available.png");

                }
                newsFeedContentValues[arrayIndex].put(NewsContract.NewsArticle.COLUMN_IMAGEURL,
                        Utility.getImageURLFromMainHTML(newsFeedItemJSONObject.getJSONObject("fields").getString("main")));
                newsFeedContentValues[arrayIndex].put(NewsContract.NewsFeed.COLUMN_ARTICLEID,
                        newsFeedItemJSONObject.getString("id"));
                newsFeedContentValues[arrayIndex].put(NewsContract.NewsFeed.COLUMN_SECTIONID,
                        newsFeedItemJSONObject.getString("sectionId"));
                newsFeedContentValues[arrayIndex].put(NewsContract.NewsFeed.COLUMN_SAVEDFLAG, "0");
                newsFeedContentValues[arrayIndex].put(NewsContract.NewsFeed.COLUMN_PUBLISHDATE, currentDate);

            }
        }

        return mContext.getContentResolver().bulkInsert(NewsContract.NewsFeed.NEWSFEED_READURI, newsFeedContentValues);

    }

    public interface  OnDownloadTaskFinishedListener{

        void onDownloadTaskFinished(String taskStatus);

    }

}
