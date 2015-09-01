package com.example.android.breakinuse.Utilities;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.example.android.breakinuse.NewsFeedFragment;
import com.example.android.breakinuse.NewsProvider.NewsContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Set;

public class GetNewsTask extends AsyncTask<Void,Void,Integer> {

    private NewsFeedFragment mFragment;
    private Context mContext;
    private String API_KEY_QUERY_PARAM;
    private String SECTION_QUERY_PARAM;
    private String FIELDS_QUERY_PARAM;
    private String ID_QUERY_PARAM;
    private String API_KEY;
    private String API_SCHEME;
    private String API_AUTHORITY;
    private String API_CONTENT_END_POINT;
    private String TAGS_QUERY_PARAM;
    private String ELEMENTS_QUERY_PARAM;
    private String FROMDATE_QUERY_PARAM;
    private String TODATE_QUERY_PARAM;
    private String ORDER_QUERY_PARAM;
    private String PAGESIZE_QUERY_PARAM;
    private String PAGE_QUERY_PARAM;
    private StringBuilder mTopicsQuery;

    public GetNewsTask(Context tempContext, Fragment tempFragment){

        mContext = tempContext;
        mFragment = (NewsFeedFragment) tempFragment;
        mTopicsQuery = new StringBuilder();

    }

    @Override
    protected Integer doInBackground(Void... params) {

        final int PAGE_SIZE = 20;
        StringBuilder reponseString =  new StringBuilder();
        String holder;
        Uri.Builder builder = new Uri.Builder();
        URL url;
        URLConnection urlConnection;
        String fromDate = Utility.getCurrentDate();
        int pageCount = 0;
        JSONObject[] responsePage = new JSONObject[1];
        BufferedReader reader = null;

        try {

            builder.scheme(this.API_SCHEME)
                    .authority(this.API_AUTHORITY)
                    .appendPath(this.API_CONTENT_END_POINT)
                    .appendQueryParameter(this.API_KEY_QUERY_PARAM, this.API_KEY)
                    .appendQueryParameter(this.FIELDS_QUERY_PARAM, "trailText")
                    .appendQueryParameter(this.PAGESIZE_QUERY_PARAM, String.valueOf(PAGE_SIZE))
                    .appendQueryParameter(this.ORDER_QUERY_PARAM, "relevance")
                    .appendQueryParameter(this.FROMDATE_QUERY_PARAM, fromDate)
                    .appendQueryParameter(this.SECTION_QUERY_PARAM,mTopicsQuery.toString())
                    .build();

            url = new URL(builder.toString());
            urlConnection = url.openConnection();
            reader = new BufferedReader(new InputStreamReader
                    (urlConnection.getInputStream()));

            while (( holder = reader.readLine()) != null){

                reponseString.append(holder);

            }

            responsePage[0] = new JSONObject(reponseString.toString());
            reader.close();

        } catch (IOException | JSONException e) {

            e.printStackTrace();

        } finally {

            if (reader !=null){

                try {

                    reader.close();

                } catch (IOException e) {

                    e.printStackTrace();
                    return null;

                }
            }
        }

        if (reponseString.length() != 0){

            try {

                if (!isResponseStatusOk(responsePage[0])){

                    return null;

                }
            } catch (JSONException e) {

                e.printStackTrace();
                return null;

            }
            try {

                pageCount = getPageCount(responsePage[0]);

            } catch (JSONException e) {

                e.printStackTrace();
                return null;

            }

        } else {

            return null;

        }

        if (pageCount > 1){

            JSONObject[] tempArray = new JSONObject[pageCount];
            tempArray[0] = responsePage[0];
            responsePage = tempArray;

            for (int index=2; index <= pageCount; ++index){

                reponseString.delete(0, reponseString.length());
                builder.clearQuery();

                try {

                    builder.appendQueryParameter(this.API_KEY_QUERY_PARAM, this.API_KEY)
                            .appendQueryParameter(this.FIELDS_QUERY_PARAM, "trailText")
                            .appendQueryParameter(this.PAGESIZE_QUERY_PARAM, String.valueOf(PAGE_SIZE))
                            .appendQueryParameter(this.PAGE_QUERY_PARAM, String.valueOf(index))
                            .appendQueryParameter(this.ORDER_QUERY_PARAM, "relevance")
                            .appendQueryParameter(this.FROMDATE_QUERY_PARAM,fromDate)
                            .appendQueryParameter(this.SECTION_QUERY_PARAM,mTopicsQuery.toString())
                            .build();

                    url = new URL(builder.toString());
                    urlConnection = url.openConnection();
                    reader = new BufferedReader(new InputStreamReader
                            (urlConnection.getInputStream()));

                    while (( holder = reader.readLine()) != null){

                        reponseString.append(holder);

                    }

                    responsePage[index-1] = new JSONObject(reponseString.toString());
                    reader.close();

                } catch (IOException | JSONException e) {

                    e.printStackTrace();

                } finally {

                    if (reader !=null){

                        try {

                            reader.close();

                        } catch (IOException e) {

                            e.printStackTrace();
                            return null;

                        }
                    }
                }
            }

            reponseString.delete(0, reponseString.length());

            try {

                return insertNewsFeedItemsInDBFromJSON(responsePage);

            } catch (JSONException e) {

                e.printStackTrace();
                return null;

            }

        } else if (pageCount  == 1){

            try {

                return insertNewsFeedItemsInDBFromJSON(responsePage);

            } catch (JSONException e) {

                e.printStackTrace();
                return null;

            }

        } else {

            return null;

        }

    }

    @Override
    protected void onPreExecute() {

        super.onPreExecute();
        API_KEY_QUERY_PARAM = "api-key";
        SECTION_QUERY_PARAM = "section";
        FIELDS_QUERY_PARAM = "show-fields";
        ID_QUERY_PARAM = "ids";
        API_KEY = "tknk2ue9anxtt3d3zthr4j4b";
        API_SCHEME = "https";
        API_AUTHORITY = "content.guardianapis.com";
        API_CONTENT_END_POINT = "search";
        TAGS_QUERY_PARAM = "show-tags";
        ELEMENTS_QUERY_PARAM = "show-elements";
        FROMDATE_QUERY_PARAM = "from-date";
        TODATE_QUERY_PARAM = "to-date";
        ORDER_QUERY_PARAM = "order-by";
        PAGESIZE_QUERY_PARAM = "page-size";
        PAGE_QUERY_PARAM = "page";

        Bundle newsTypeBundle = mFragment.getArguments();
        String newsType = newsTypeBundle.getString("NewsType");
        Set<String> defaultFavouriteTopicsSet = Utility.getDefaultFavouriteTopicsSet(mContext);

        if (newsType != null){

            if (newsType.equals("All")){

                for (String iterator : defaultFavouriteTopicsSet) {

                    if (iterator != null){

                        mTopicsQuery.append(iterator);
                        mTopicsQuery.append("|");

                    }


                }

            } else if (newsType.equals("Favourites")) {

                Set<String> favouriteTopicsSet = Utility.getFavouriteTopicsSet(mContext);

                for (String iterator : favouriteTopicsSet) {

                    if (iterator != null){

                        mTopicsQuery.append(iterator);
                        mTopicsQuery.append("|");

                    }

                }
            }
            mTopicsQuery = new StringBuilder(mTopicsQuery.substring(0,(mTopicsQuery.length()-1)));

        } else {

            for (String iterator : defaultFavouriteTopicsSet) {

                if (iterator != null){

                    mTopicsQuery.append(iterator);
                    mTopicsQuery.append("|");

                }

            }
            mTopicsQuery = new StringBuilder(mTopicsQuery.substring(0,(mTopicsQuery.length()-1)));

        }


    }

    @Override
    protected void onPostExecute(Integer rowsInserted) {

        super.onPostExecute(rowsInserted);
        if (rowsInserted > 0){



        }

    }

    private boolean isResponseStatusOk(JSONObject responsePage) throws JSONException{

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

