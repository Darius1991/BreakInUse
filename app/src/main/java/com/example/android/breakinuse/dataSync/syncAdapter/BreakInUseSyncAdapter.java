package com.example.android.breakinuse.dataSync.syncAdapter;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.example.android.breakinuse.R;
import com.example.android.breakinuse.newsProvider.NewsContract;
import com.example.android.breakinuse.utilities.Utility;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Set;

public class BreakInUseSyncAdapter extends AbstractThreadedSyncAdapter {

    ContentResolver mContentResolver;
    private static final String TAG = BreakInUseSyncAdapter.class.getName();
    private Context mContext;
    private StringBuilder mTopicsQuery;
    private static final int SYNC_INTERVAL = 60 * 180;
    private static final int SYNC_FLEXTIME = SYNC_INTERVAL/3;


    public BreakInUseSyncAdapter(Context context, boolean autoInitialize) {

        super(context, autoInitialize);
        mContext = context;
        mContentResolver = mContext.getContentResolver();
        mTopicsQuery = new StringBuilder();

    }

    public BreakInUseSyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {

        super(context, autoInitialize, allowParallelSyncs);
        mContext = context;
        mContentResolver = mContext.getContentResolver();
        mTopicsQuery = new StringBuilder();

    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {

        /*----FETCHING newsFeedItems FROM GUARDIAN API & INSERTING IN NewsFeed TABLE----*/
        /*----FETCHING newsFeedItems FROM GUARDIAN API & INSERTING IN NewsFeed TABLE----*/
        /*----FETCHING newsFeedItems FROM GUARDIAN API & INSERTING IN NewsFeed TABLE----*/
        /*----FETCHING newsFeedItems FROM GUARDIAN API & INSERTING IN NewsFeed TABLE----*/

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
        final int PAGE_SIZE = 20;

        Set<String> defaultFavouriteTopicsSet = Utility.getDefaultFavouriteTopicsSet(mContext);

        for (String iterator : defaultFavouriteTopicsSet) {

            if (iterator != null){

                mTopicsQuery.append(iterator);
                mTopicsQuery.append("|");

            }


        }
        mTopicsQuery = new StringBuilder(mTopicsQuery.substring(0,(mTopicsQuery.length()-1)));

        StringBuilder responseString =  new StringBuilder();
        String holder;
        Uri.Builder builder = new Uri.Builder();
        URL url;
        URLConnection urlConnection;
        String fromDate = Utility.getCurrentDate();
        int pageCount = 0;
        JSONObject[] responsePage = new JSONObject[1];
        BufferedReader reader = null;

        try {

            builder.scheme(API_SCHEME)
                    .authority(API_AUTHORITY)
                    .appendPath(API_CONTENT_END_POINT)
                    .appendQueryParameter(API_KEY_QUERY_PARAM, API_KEY)
                    .appendQueryParameter(FIELDS_QUERY_PARAM, "trailText,body,byline,headline,main,thumbnail")
                    .appendQueryParameter(PAGESIZE_QUERY_PARAM, String.valueOf(PAGE_SIZE))
                    .appendQueryParameter(ORDER_QUERY_PARAM, "newest")
                    .appendQueryParameter(PAGE_QUERY_PARAM,String.valueOf(1))
                    .appendQueryParameter(FROMDATE_QUERY_PARAM, fromDate)
                    .appendQueryParameter(SECTION_QUERY_PARAM, mTopicsQuery.toString())
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
                    return;

                }
            }
        }

        if (responseString.length() != 0){

            try {

                if (!isResponseStatusOk(responsePage[0])){

                    return;

                }

            } catch (Exception e) {

                e.printStackTrace();
                return;

            }
            try {

                pageCount = getPageCount(responsePage[0]);

            } catch (Exception e) {

                e.printStackTrace();
                return;

            }

        } else {

            return;

        }

        try {

            insertNewsFeedItemsInNewsFeedTableFromJSON(responsePage);

        } catch (Exception e) {

            e.printStackTrace();
            return;

        }

        /*----DELETING OLD ARTICLES FROM NewsFeed TABLE----*/
        /*----DELETING OLD ARTICLES FROM NewsFeed TABLE----*/
        /*----DELETING OLD ARTICLES FROM NewsFeed TABLE----*/
        /*----DELETING OLD ARTICLES FROM NewsFeed TABLE----*/

        mContentResolver.delete(NewsContract.NewsFeed.NEWSFEED_READURI,
                    NewsContract.NewsFeed.COLUMN_PUBLISHDATE + " != ?",
                    new String[]{Utility.getCurrentDate()});

        /*----FETCHING DATA FOR TO-BE-SAVED ARTICLES & INSERTING IN NewsArticle TABLE----*/
        /*----FETCHING DATA FOR TO-BE-SAVED ARTICLES & INSERTING IN NewsArticle TABLE----*/
        /*----FETCHING DATA FOR TO-BE-SAVED ARTICLES & INSERTING IN NewsArticle TABLE----*/
        /*----FETCHING DATA FOR TO-BE-SAVED ARTICLES & INSERTING IN NewsArticle TABLE----*/

        Cursor cursor = mContentResolver.query(NewsContract.NewsArticle.NEWSARTICLE_URI,
                            null,
                            NewsContract.NewsArticle.COLUMN_DOWNLOADFLAG + " = ?",
                            new String[]{"0"},
                            null);

        responseString =  new StringBuilder();
        reader = null;
        int index = 0;
        int downloadCount = 0;
        int articleIDColumnIndex = cursor.getColumnIndex(NewsContract.NewsArticle.COLUMN_ARTICLEID);
        int newsFeedIDColumnIndex = cursor.getColumnIndex(NewsContract.NewsArticle.COLUMN_NEWSFEED_KEY);
        ArrayList<Utility.NewsArticleWithNewsFeedID> newsArticleArrayList = new ArrayList<>();

        if ((cursor != null) && (cursor.moveToFirst())){

            downloadCount = cursor.getCount();

            for (index = 0; index < downloadCount; ++index){

                builder.clearQuery();

                try {

                    builder.appendQueryParameter(API_KEY_QUERY_PARAM, API_KEY)
                            .appendQueryParameter(FIELDS_QUERY_PARAM, "trailText,body,byline,headline,main,thumbnail")
                            .appendQueryParameter(PAGESIZE_QUERY_PARAM, String.valueOf(PAGE_SIZE))
                            .appendQueryParameter(ORDER_QUERY_PARAM, "newest")
                            .appendQueryParameter(ID_QUERY_PARAM, cursor.getString(articleIDColumnIndex))
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
                                            cursor.getInt(newsFeedIDColumnIndex)));
                    reader.close();
                    responseString.delete(0, responseString.length());

                } catch (Exception e) {

                    e.printStackTrace();

                } finally {

                    if (reader !=null){

                        try {

                            reader.close();

                        } catch (Exception e) {

                            e.printStackTrace();
                            return;

                        }
                    }
                }

                cursor.moveToNext();

            }

            try {

                updateSavedNewsArticlesFromJSON(newsArticleArrayList);

            } catch (Exception e) {

                e.printStackTrace();
                return;

            }

        }

    }

    private boolean isResponseStatusOk(JSONObject responsePage) throws Exception {

        return responsePage.getJSONObject("response").getString("status").equals("ok");

    }

    private int getPageCount(JSONObject responsePage) throws Exception{

        return responsePage.getJSONObject("response").getInt("pages");

    }

    private int insertNewsFeedItemsInNewsFeedTableFromJSON(JSONObject[] responsePage) throws Exception {

        int pageCount = 1; /*responsePage[0].getJSONObject("response").getInt("pages");*/
        int pageIndex, webTitleIndex, arrayIndex, responseCount;
        responseCount = 20;/*responsePage[0].getJSONObject("response").getInt("total");*/
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
                arrayIndex = webTitleIndex + ((pageIndex) * 20);

                newsFeedContentValues[arrayIndex].put(NewsContract.NewsFeed.COLUMN_WEBTITLE,
                        newsFeedItemJSONObject.getString("webTitle"));
                Log.d(TAG, newsFeedItemJSONObject.getString("webTitle"));
                newsFeedContentValues[arrayIndex].put(NewsContract.NewsFeed.COLUMN_WEBURL,
                        newsFeedItemJSONObject.getString("webUrl"));
                newsFeedContentValues[arrayIndex].put(NewsContract.NewsFeed.COLUMN_APIURL,
                        newsFeedItemJSONObject.getString("apiUrl"));
                newsFeedContentValues[arrayIndex].put(NewsContract.NewsFeed.COLUMN_TRAILTEXT,
                        newsFeedItemJSONObject.getJSONObject("fields").getString("trailText"));
                newsFeedContentValues[arrayIndex].put(NewsContract.NewsFeed.COLUMN_BYLINE,
                        newsFeedItemJSONObject.getJSONObject("fields").getString("byline"));
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

        return mContentResolver.bulkInsert(NewsContract.NewsFeed.NEWSFEED_READURI, newsFeedContentValues);

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
            try {

                contentValues[index].put(NewsContract.NewsArticle.COLUMN_THUMBNAILURL,
                        newsArticle.getJSONObject("fields").getString("thumbnail"));

            } catch (Exception e){

                contentValues[index].put(NewsContract.NewsArticle.COLUMN_THUMBNAILURL,
                        "http://vignette3.wikia.nocookie.net/wiisportsresortwalkthrough/images/6/60/No_Image_Available.png");

            }
            contentValues[index].put(NewsContract.NewsArticle.COLUMN_IMAGEURL,
                    Utility.getImageURLFromMainHTML(newsArticle.getJSONObject("fields").getString("main")));
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

            rowUpdateFlag = mContentResolver.update(NewsContract.NewsArticle.NEWSARTICLE_URI,
                                                        contentValues[index],
                                                        NewsContract.NewsArticle.COLUMN_ARTICLEID + " = ?",
                                                        new String[]{newsArticle.getString("id")});
            if (rowUpdateFlag > 0){

                ++rowsUpdated;

            }

        }

        return rowsUpdated;

    }



    /**
     * Helper method to schedule the sync adapter periodic execution
     */
    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {

        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);

        } else {

            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);

        }

    }

    /**
     * Helper method to have the sync adapter sync immediately
     * @param context The context used to access the account service
     */
    public static void syncImmediately(Context context) {

        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);

    }

    /**
     * Helper method to get the fake account to be used with SyncAdapter, or make a new one
     * if the fake account doesn't exist yet.  If we make a new account, we call the
     * onAccountCreated method so we can initialize things.
     *
     * @param context The context used to access the account service
     * @return a fake account.
     */
    public static Account getSyncAccount(Context context) {
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        try{

            // If the password doesn't exist, the account doesn't exist
            if ( null == accountManager.getPassword(newAccount) ) {

        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
                if (!accountManager.addAccountExplicitly(newAccount, "", null)) {

                    return null;

                }
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call ContentResolver.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */

                onAccountCreated(newAccount, context);
            }

            return newAccount;

        }catch (Exception e){

            e.printStackTrace();



        }

        return newAccount;



    }

    private static void onAccountCreated(Account newAccount, Context context) {
        /*
         * Since we've created an account
         */
        BreakInUseSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);

        /*
         * Without calling setSyncAutomatically, our periodic sync will not be enabled.
         */
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);

        /*
         * Finally, let's do a sync to get things started
         */
        syncImmediately(context);
    }

    public static void initializeSyncAdapter(Context context) {

        getSyncAccount(context);

    }

}
