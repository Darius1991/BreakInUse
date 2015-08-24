package com.example.android.breakinuse.Utilities;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.example.android.breakinuse.NewsFeedFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Set;

public class GetNewsTask extends AsyncTask<Void,Void,Utility.NewsFeedItem[]> {

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

        this.mContext = tempContext;
        this.mFragment = (NewsFeedFragment) tempFragment;
        this.mTopicsQuery = new StringBuilder();

    }

    @Override
    protected Utility.NewsFeedItem[] doInBackground(Void... params) {

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

                return getNewsFeedItemFromJSON(responsePage);

            } catch (JSONException e) {

                e.printStackTrace();
                return null;

            }

        } else if (pageCount  == 1){

            try {

                return getNewsFeedItemFromJSON(responsePage);

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
        this.API_KEY_QUERY_PARAM = "api-key";
        this.SECTION_QUERY_PARAM = "section";
        this.FIELDS_QUERY_PARAM = "show-fields";
        this.ID_QUERY_PARAM = "ids";
        this.API_KEY = "tknk2ue9anxtt3d3zthr4j4b";
        this.API_SCHEME = "https";
        this.API_AUTHORITY = "content.guardianapis.com";
        this.API_CONTENT_END_POINT = "search";
        this.TAGS_QUERY_PARAM = "show-tags";
        this.ELEMENTS_QUERY_PARAM = "show-elements";
        this.FROMDATE_QUERY_PARAM = "from-date";
        this.TODATE_QUERY_PARAM = "to-date";
        this.ORDER_QUERY_PARAM = "order-by";
        this.PAGESIZE_QUERY_PARAM = "page-size";
        this.PAGE_QUERY_PARAM = "page";

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
    protected void onPostExecute(Utility.NewsFeedItem[] newsFeedItemArray) {

        super.onPostExecute(newsFeedItemArray);

        if (newsFeedItemArray!= null) {

            mFragment.notifyDataSetChanged(newsFeedItemArray);
        }

    }

    private boolean isResponseStatusOk(JSONObject responsePage) throws JSONException{

        return responsePage.getJSONObject("response").getString("status").equals("ok");

    }

    private int getPageCount(JSONObject responsePage) throws JSONException{

        return responsePage.getJSONObject("response").getInt("pages");

    }

    private Utility.NewsFeedItem[] getNewsFeedItemFromJSON(JSONObject[] responsePage) throws JSONException {

        int pageCount = responsePage[0].getJSONObject("response").getInt("pages");
        int pageIndex, webTitleIndex;
        JSONArray newsFeedItemJSONArray;
        Utility.NewsFeedItem[] newsFeedItemArray = new Utility.NewsFeedItem[responsePage[0]
                .getJSONObject("response")
                .getInt("total")];

        for (int index = 0; index < responsePage[0].getJSONObject("response").getInt("total"); ++index){

            newsFeedItemArray[index] = new Utility.NewsFeedItem();

        }

        for (pageIndex = 0; pageIndex <= pageCount-1; ++pageIndex){

            webTitleIndex = 0;
            newsFeedItemJSONArray = responsePage[pageIndex].getJSONObject("response").getJSONArray("results");

            for ( ;webTitleIndex < newsFeedItemJSONArray.length() ; ++webTitleIndex){

                newsFeedItemArray[webTitleIndex + (pageIndex) * 20].webTitle = (newsFeedItemJSONArray.getJSONObject(webTitleIndex).getString("webTitle"));
                newsFeedItemArray[webTitleIndex + (pageIndex)*20].webURL = (newsFeedItemJSONArray.getJSONObject(webTitleIndex).getString("webUrl"));
                newsFeedItemArray[webTitleIndex + (pageIndex)*20].apiURL = (newsFeedItemJSONArray.getJSONObject(webTitleIndex).getString("apiUrl"));
                newsFeedItemArray[webTitleIndex + (pageIndex)*20].trailText = (newsFeedItemJSONArray.getJSONObject(webTitleIndex)
                                                                                    .getJSONObject("fields").getString("trailText"));
                newsFeedItemArray[webTitleIndex + (pageIndex)*20].articleID = (newsFeedItemJSONArray.getJSONObject(webTitleIndex).getString("id"));
                newsFeedItemArray[webTitleIndex + (pageIndex)*20].sectionID = (newsFeedItemJSONArray.getJSONObject(webTitleIndex).getString("sectionId"));

            }
        }
        return newsFeedItemArray;
    }

}

