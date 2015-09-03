package com.example.android.breakinuse.service;

import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.example.android.breakinuse.newsProvider.NewsContract;
import com.example.android.breakinuse.utilities.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Set;

public class NewsFeedService extends IntentService {

    private Context mContext;
    private StringBuilder mTopicsQuery;
    private static final String TAG = NewsFeedService.class.getName();

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public NewsFeedService(String name) {

        super(name);

    }

    public NewsFeedService() {

        super("NewsFeedService");
        mContext = this;
        mTopicsQuery = new StringBuilder();

    }

    @Override
    protected void onHandleIntent(Intent intent) {

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

        Set<String> defaultFavouriteTopicsSet = Utility.getDefaultFavouriteTopicsSet(mContext);

        for (String iterator : defaultFavouriteTopicsSet) {

            if (iterator != null){

                mTopicsQuery.append(iterator);
                mTopicsQuery.append("|");

            }


        }
        mTopicsQuery = new StringBuilder(mTopicsQuery.substring(0,(mTopicsQuery.length()-1)));

        final int PAGE_SIZE = 20;
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
                    .appendQueryParameter(FIELDS_QUERY_PARAM, "trailText")
                    .appendQueryParameter(PAGESIZE_QUERY_PARAM, String.valueOf(PAGE_SIZE))
                    .appendQueryParameter(ORDER_QUERY_PARAM, "relevance")
                    .appendQueryParameter(FROMDATE_QUERY_PARAM, fromDate)
                    .appendQueryParameter(SECTION_QUERY_PARAM,mTopicsQuery.toString())
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

        } catch (IOException | JSONException e) {

            e.printStackTrace();

        } finally {

            if (reader !=null){

                try {

                    reader.close();

                } catch (IOException e) {

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

            } catch (JSONException e) {

                e.printStackTrace();
                return;

            }
            try {

                pageCount = getPageCount(responsePage[0]);

            } catch (JSONException e) {

                e.printStackTrace();
                return;

            }

        } else {

            return;

        }

        if (pageCount > 1){

            JSONObject[] tempArray = new JSONObject[pageCount];
            tempArray[0] = responsePage[0];
            responsePage = tempArray;

            for (int index=2; index <= pageCount; ++index){

                responseString.delete(0, responseString.length());
                builder.clearQuery();

                try {

                    builder.appendQueryParameter(API_KEY_QUERY_PARAM, API_KEY)
                            .appendQueryParameter(FIELDS_QUERY_PARAM, "trailText")
                            .appendQueryParameter(PAGESIZE_QUERY_PARAM, String.valueOf(PAGE_SIZE))
                            .appendQueryParameter(PAGE_QUERY_PARAM, String.valueOf(index))
                            .appendQueryParameter(ORDER_QUERY_PARAM, "relevance")
                            .appendQueryParameter(FROMDATE_QUERY_PARAM,fromDate)
                            .appendQueryParameter(SECTION_QUERY_PARAM,mTopicsQuery.toString())
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

                } catch (IOException | JSONException e) {

                    e.printStackTrace();

                } finally {

                    if (reader !=null){

                        try {

                            reader.close();

                        } catch (IOException e) {

                            e.printStackTrace();
                            return;

                        }
                    }
                }
            }

            responseString.delete(0, responseString.length());

            try {

                insertNewsFeedItemsInDBFromJSON(responsePage);

            } catch (JSONException e) {

                e.printStackTrace();
                return;

            }

        } else if (pageCount  == 1){

            try {

                insertNewsFeedItemsInDBFromJSON(responsePage);

            } catch (JSONException e) {

                e.printStackTrace();
                return;

            }

        } else {

            return;

        }


    }

    private boolean isResponseStatusOk(JSONObject responsePage) throws JSONException {

        return responsePage.getJSONObject("response").getString("status").equals("ok");

    }

    private int getPageCount(JSONObject responsePage) throws JSONException{

        return responsePage.getJSONObject("response").getInt("pages");

    }

    private int insertNewsFeedItemsInDBFromJSON(JSONObject[] responsePage) throws JSONException {

        int pageCount = responsePage[0].getJSONObject("response").getInt("pages");
        int pageIndex, webTitleIndex, arrayIndex, responseCount;
        responseCount = responsePage[0].getJSONObject("response").getInt("total");
        ContentValues[] newsFeedContentValues = new ContentValues[responseCount];
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
                newsFeedContentValues[arrayIndex].put(NewsContract.NewsFeed.COLUMN_WEBURL,
                        newsFeedItemJSONObject.getString("webUrl"));
                newsFeedContentValues[arrayIndex].put(NewsContract.NewsFeed.COLUMN_APIURL,
                        newsFeedItemJSONObject.getString("apiUrl"));
                newsFeedContentValues[arrayIndex].put(NewsContract.NewsFeed.COLUMN_TRAILTEXT,
                        newsFeedItemJSONObject.getJSONObject("fields").getString("trailText"));
                newsFeedContentValues[arrayIndex].put(NewsContract.NewsFeed.COLUMN_ARTICLEID,
                        newsFeedItemJSONObject.getString("id"));
                newsFeedContentValues[arrayIndex].put(NewsContract.NewsFeed.COLUMN_SECTIONID,
                        newsFeedItemJSONObject.getString("sectionId"));

            }
        }

        return mContext.getContentResolver().bulkInsert(NewsContract.NewsFeed.CONTENT_URI,newsFeedContentValues);

    }

}
