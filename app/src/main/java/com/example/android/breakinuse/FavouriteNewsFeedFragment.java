package com.example.android.breakinuse;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.breakinuse.newsProvider.NewsContract;
import com.example.android.breakinuse.utilities.Utility;

import java.util.Set;

public class FavouriteNewsFeedFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private NewsFeedAdapter mNewsFeedAdapter;
    private static final int LOADER_ID_FAVOURITES = 1;
    private static final String TAG = FavouriteNewsFeedFragment.class.getName();
    private RecyclerView mRecyclerView;
    private Context mContext;
    private Cursor mCursor;

    public FavouriteNewsFeedFragment(){

        super();
        mContext = getActivity();
        mCursor = null;

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_news_feed, container, false);
        mContext = getActivity();

        Set<String> favouriteTopicsSet = Utility.getFavouriteTopicsSet(mContext);
        if (!favouriteTopicsSet.isEmpty()){

            mCursor = mContext.getContentResolver().query(NewsContract.NewsFeed.CONTENT_URI,
                            null,
                            NewsContract.NewsFeed.COLUMN_ARTICLEID + " NOT IN (?,?) ",
                            new String[]{"DummyNewsFeedArticleID","DummyFavouriteNewsFeedArticleID"},
                            null);

            if ((mCursor != null) && (mCursor.moveToFirst())){

                Uri uri = NewsContract.NewsFeed.buildNewsFeedUri("DummyFavouriteNewsFeedArticleID");
                mContext.getContentResolver().delete(uri, null, null);
                String[] selectionArgs = new String[favouriteTopicsSet.size()];
                StringBuilder selection = new StringBuilder();

                int index = 0;
                for (String iterator : favouriteTopicsSet){

                    selectionArgs[index++] = iterator;
                    selection.append("?");
                    selection.append(",");

                }
                selection = new StringBuilder(selection.substring(0,selection.length()-1));

                mCursor =  mContext.getContentResolver().query(NewsContract.NewsFeed.CONTENT_URI,
                                null,
                                NewsContract.NewsFeed.COLUMN_SECTIONID + " IN (" + selection.toString() +")",
                                selectionArgs,
                                null);

                if (!((mCursor != null) && (mCursor.moveToFirst()))){

                    mCursor = mContext.getContentResolver().query(NewsContract.NewsFeed.CONTENT_URI,
                                    null,
                                    NewsContract.NewsFeed.COLUMN_ARTICLEID + " = ? ",
                                    new String[]{"DummyFavouriteNewsFeedArticleID"},
                                    null);

                    if (!((mCursor != null) && (mCursor.moveToFirst()))){

                        ContentValues newsFeedTestValues_set1 = new ContentValues();
                        newsFeedTestValues_set1.put(NewsContract.NewsFeed.COLUMN_ARTICLEID, "DummyFavouriteNewsFeedArticleID");
                        newsFeedTestValues_set1.put(NewsContract.NewsFeed.COLUMN_SECTIONID, "DummyFavouriteNewsFeedSectionID");
                        newsFeedTestValues_set1.put(NewsContract.NewsFeed.COLUMN_APIURL, "DummyFavouriteNewsFeedAPIURL");
                        newsFeedTestValues_set1.put(NewsContract.NewsFeed.COLUMN_WEBURL, "http://www.theguardian.com/");
                        newsFeedTestValues_set1.put(NewsContract.NewsFeed.COLUMN_WEBTITLE, "No FavouriteFeed to display");
                        newsFeedTestValues_set1.put(NewsContract.NewsFeed.COLUMN_TRAILTEXT,
                                "No FavouriteFeed to display. No matching articles may be available or you need to refresh after connecting to internet.");
                        newsFeedTestValues_set1.put(NewsContract.NewsFeed.COLUMN_SAVEDFLAG, "0");
                        newsFeedTestValues_set1.put(NewsContract.NewsFeed.COLUMN_PUBLISHDATE, Utility.getYesterdayDate());
                        mContext.getContentResolver().insert(NewsContract.NewsFeed.CONTENT_URI, newsFeedTestValues_set1);
                        mCursor = mContext.getContentResolver().query(NewsContract.NewsFeed.CONTENT_URI,
                                        null,
                                        NewsContract.NewsFeed.COLUMN_ARTICLEID + " = ? ",
                                        new String[]{"DummyFavouriteNewsFeedArticleID"},
                                        null);

                    }

                } else {

                    mCursor = mContext.getContentResolver().query(NewsContract.NewsFeed.CONTENT_URI,
                                    null,
                                    NewsContract.NewsFeed.COLUMN_ARTICLEID + " = ? ",
                                    new String[]{"DummyFavouriteNewsFeedArticleID"},
                                    null);

                    if (!((mCursor != null) && (mCursor.moveToFirst()))){

                        ContentValues newsFeedTestValues_set1 = new ContentValues();
                        newsFeedTestValues_set1.put(NewsContract.NewsFeed.COLUMN_ARTICLEID, "DummyFavouriteNewsFeedArticleID");
                        newsFeedTestValues_set1.put(NewsContract.NewsFeed.COLUMN_SECTIONID, "DummyFavouriteNewsFeedSectionID");
                        newsFeedTestValues_set1.put(NewsContract.NewsFeed.COLUMN_APIURL, "DummyFavouriteNewsFeedAPIURL");
                        newsFeedTestValues_set1.put(NewsContract.NewsFeed.COLUMN_WEBURL, "http://www.theguardian.com/");
                        newsFeedTestValues_set1.put(NewsContract.NewsFeed.COLUMN_WEBTITLE, "No FavouriteFeed to display");
                        newsFeedTestValues_set1.put(NewsContract.NewsFeed.COLUMN_TRAILTEXT,
                                "No FavouriteFeed to display. No matching articles may be available or you need to refresh after connecting to internet.");
                        newsFeedTestValues_set1.put(NewsContract.NewsFeed.COLUMN_SAVEDFLAG, "0");
                        newsFeedTestValues_set1.put(NewsContract.NewsFeed.COLUMN_PUBLISHDATE, Utility.getYesterdayDate());
                        mContext.getContentResolver().insert(NewsContract.NewsFeed.CONTENT_URI, newsFeedTestValues_set1);
                        mCursor = mContext.getContentResolver().query(NewsContract.NewsFeed.CONTENT_URI,
                                        null,
                                        NewsContract.NewsFeed.COLUMN_ARTICLEID + " = ? ",
                                        new String[]{"DummyFavouriteNewsFeedArticleID"},
                                        null);
                    }

                }

            } else {

                mCursor = mContext.getContentResolver().query(NewsContract.NewsFeed.CONTENT_URI,
                        null,
                        NewsContract.NewsFeed.COLUMN_ARTICLEID + " = ? ",
                        new String[]{"DummyFavouriteNewsFeedArticleID"},
                        null);

                if (!((mCursor != null) && (mCursor.moveToFirst()))){

                    ContentValues newsFeedTestValues_set1 = new ContentValues();
                    newsFeedTestValues_set1.put(NewsContract.NewsFeed.COLUMN_ARTICLEID, "DummyFavouriteNewsFeedArticleID");
                    newsFeedTestValues_set1.put(NewsContract.NewsFeed.COLUMN_SECTIONID, "DummyFavouriteNewsFeedSectionID");
                    newsFeedTestValues_set1.put(NewsContract.NewsFeed.COLUMN_APIURL, "DummyFavouriteNewsFeedAPIURL");
                    newsFeedTestValues_set1.put(NewsContract.NewsFeed.COLUMN_WEBURL, "http://www.theguardian.com/");
                    newsFeedTestValues_set1.put(NewsContract.NewsFeed.COLUMN_WEBTITLE, "No FavouriteFeed to display");
                    newsFeedTestValues_set1.put(NewsContract.NewsFeed.COLUMN_TRAILTEXT,
                            "No FavouriteFeed to display. No matching articles may be available or you need to refresh after connecting to internet.");
                    newsFeedTestValues_set1.put(NewsContract.NewsFeed.COLUMN_SAVEDFLAG, "0");
                    newsFeedTestValues_set1.put(NewsContract.NewsFeed.COLUMN_PUBLISHDATE, Utility.getYesterdayDate());
                    mContext.getContentResolver().insert(NewsContract.NewsFeed.CONTENT_URI, newsFeedTestValues_set1);
                    mCursor = mContext.getContentResolver().query(NewsContract.NewsFeed.CONTENT_URI,
                                    null,
                                    NewsContract.NewsFeed.COLUMN_ARTICLEID + " = ? ",
                                    new String[]{"DummyFavouriteNewsFeedArticleID"},
                                    null);

                }

            }

        } else {

            mCursor = mContext.getContentResolver().query(NewsContract.NewsFeed.CONTENT_URI,
                            null,
                            NewsContract.NewsFeed.COLUMN_ARTICLEID + " = ? ",
                            new String[]{"DummyFavouriteNewsFeedArticleID"},
                            null);

            if (!((mCursor != null) && (mCursor.moveToFirst()))){

                ContentValues newsFeedTestValues_set1 = new ContentValues();
                newsFeedTestValues_set1.put(NewsContract.NewsFeed.COLUMN_ARTICLEID, "DummyFavouriteNewsFeedArticleID");
                newsFeedTestValues_set1.put(NewsContract.NewsFeed.COLUMN_SECTIONID, "DummyFavouriteNewsFeedSectionID");
                newsFeedTestValues_set1.put(NewsContract.NewsFeed.COLUMN_APIURL, "DummyFavouriteNewsFeedAPIURL");
                newsFeedTestValues_set1.put(NewsContract.NewsFeed.COLUMN_WEBURL, "http://www.theguardian.com/");
                newsFeedTestValues_set1.put(NewsContract.NewsFeed.COLUMN_WEBTITLE, "No FavouriteFeed to display");
                newsFeedTestValues_set1.put(NewsContract.NewsFeed.COLUMN_TRAILTEXT,
                        "No FavouriteFeed to display. No matching articles may be available or you need to refresh after connecting to internet.");
                newsFeedTestValues_set1.put(NewsContract.NewsFeed.COLUMN_SAVEDFLAG, "0");
                newsFeedTestValues_set1.put(NewsContract.NewsFeed.COLUMN_PUBLISHDATE, Utility.getYesterdayDate());
                mContext.getContentResolver().insert(NewsContract.NewsFeed.CONTENT_URI, newsFeedTestValues_set1);
                mCursor = mContext.getContentResolver().query(NewsContract.NewsFeed.CONTENT_URI,
                                null,
                                NewsContract.NewsFeed.COLUMN_ARTICLEID + " = ? ",
                                new String[]{"DummyFavouriteNewsFeedArticleID"},
                                null);
            }

        }


        getLoaderManager().initLoader(LOADER_ID_FAVOURITES, null, this);


        if (mCursor != null) {

            mCursor.moveToFirst();

        }
        mNewsFeedAdapter = new NewsFeedAdapter(getActivity(),mCursor);

        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        mRecyclerView = (RecyclerView)rootView.findViewById(R.id.newsFeed_recyclerView);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mNewsFeedAdapter);

        return rootView;

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        CursorLoader cursorLoader;

        switch(id){

            case LOADER_ID_FAVOURITES:

                Set<String> favouriteTopicsSet = Utility.getFavouriteTopicsSet(mContext);
                if (!favouriteTopicsSet.isEmpty()){

                    mCursor = mContext.getContentResolver().query(NewsContract.NewsFeed.CONTENT_URI,
                                    null,
                                    NewsContract.NewsFeed.COLUMN_ARTICLEID + " NOT IN (?,?) ",
                                    new String[]{"DummyNewsFeedArticleID","DummyFavouriteNewsFeedArticleID"},
                                    null);

                    if ((mCursor != null) && (mCursor.moveToFirst())){

                        Uri uri = NewsContract.NewsFeed.buildNewsFeedUri("DummyFavouriteNewsFeedArticleID");
                        mContext.getContentResolver().delete(uri, null, null);
                        String[] selectionArgs = new String[favouriteTopicsSet.size()];
                        StringBuilder selection = new StringBuilder();

                        int index = 0;
                        for (String iterator : favouriteTopicsSet){

                            selectionArgs[index++] = iterator;
                            selection.append("?");
                            selection.append(",");

                        }
                        selection = new StringBuilder(selection.substring(0,selection.length()-1));

                        mCursor =  mContext.getContentResolver().query(NewsContract.NewsFeed.CONTENT_URI,
                                        null,
                                        NewsContract.NewsFeed.COLUMN_SECTIONID + " IN (" + selection.toString() +")",
                                        selectionArgs,
                                        null);

                        if (!((mCursor != null) && (mCursor.moveToFirst()))){

                            mCursor = mContext.getContentResolver().query(NewsContract.NewsFeed.CONTENT_URI,
                                            null,
                                            NewsContract.NewsFeed.COLUMN_ARTICLEID + " = ? ",
                                            new String[]{"DummyFavouriteNewsFeedArticleID"},
                                            null);

                            if (!((mCursor != null) && (mCursor.moveToFirst()))){

                                ContentValues newsFeedTestValues_set1 = new ContentValues();
                                newsFeedTestValues_set1.put(NewsContract.NewsFeed.COLUMN_ARTICLEID, "DummyFavouriteNewsFeedArticleID");
                                newsFeedTestValues_set1.put(NewsContract.NewsFeed.COLUMN_SECTIONID, "DummyFavouriteNewsFeedSectionID");
                                newsFeedTestValues_set1.put(NewsContract.NewsFeed.COLUMN_APIURL, "DummyFavouriteNewsFeedAPIURL");
                                newsFeedTestValues_set1.put(NewsContract.NewsFeed.COLUMN_WEBURL, "http://www.theguardian.com/");
                                newsFeedTestValues_set1.put(NewsContract.NewsFeed.COLUMN_WEBTITLE, "No FavouriteFeed to display");
                                newsFeedTestValues_set1.put(NewsContract.NewsFeed.COLUMN_TRAILTEXT,
                                        "No FavouriteFeed to display. No matching articles may be available or you need to refresh after connecting to internet.");
                                newsFeedTestValues_set1.put(NewsContract.NewsFeed.COLUMN_SAVEDFLAG, "0");
                                newsFeedTestValues_set1.put(NewsContract.NewsFeed.COLUMN_PUBLISHDATE, Utility.getYesterdayDate());
                                mContext.getContentResolver().insert(NewsContract.NewsFeed.CONTENT_URI, newsFeedTestValues_set1);
                                cursorLoader = new CursorLoader(mContext,
                                        NewsContract.NewsFeed.CONTENT_URI,
                                        null,
                                        NewsContract.NewsFeed.COLUMN_ARTICLEID + " = ? ",
                                        new String[]{"DummyFavouriteNewsFeedArticleID"},
                                        null);

                            } else {

                                cursorLoader = new CursorLoader(mContext,
                                                NewsContract.NewsFeed.CONTENT_URI,
                                                null,
                                                NewsContract.NewsFeed.COLUMN_ARTICLEID + " = ? ",
                                                new String[]{"DummyFavouriteNewsFeedArticleID"},
                                                null);

                            }

                        } else {

                            cursorLoader =  new CursorLoader(mContext,
                                                NewsContract.NewsFeed.CONTENT_URI,
                                                null,
                                                NewsContract.NewsFeed.COLUMN_SECTIONID + " IN (" + selection.toString() +")",
                                                selectionArgs,
                                                null);

                        }

                    } else {

                        mCursor = mContext.getContentResolver().query(NewsContract.NewsFeed.CONTENT_URI,
                                        null,
                                        NewsContract.NewsFeed.COLUMN_ARTICLEID + " = ? ",
                                        new String[]{"DummyFavouriteNewsFeedArticleID"},
                                        null);

                        if (!((mCursor != null) && (mCursor.moveToFirst()))){

                            ContentValues newsFeedTestValues_set1 = new ContentValues();
                            newsFeedTestValues_set1.put(NewsContract.NewsFeed.COLUMN_ARTICLEID, "DummyFavouriteNewsFeedArticleID");
                            newsFeedTestValues_set1.put(NewsContract.NewsFeed.COLUMN_SECTIONID, "DummyFavouriteNewsFeedSectionID");
                            newsFeedTestValues_set1.put(NewsContract.NewsFeed.COLUMN_APIURL, "DummyFavouriteNewsFeedAPIURL");
                            newsFeedTestValues_set1.put(NewsContract.NewsFeed.COLUMN_WEBURL, "http://www.theguardian.com/");
                            newsFeedTestValues_set1.put(NewsContract.NewsFeed.COLUMN_WEBTITLE, "No FavouriteFeed to display");
                            newsFeedTestValues_set1.put(NewsContract.NewsFeed.COLUMN_TRAILTEXT,
                                    "No FavouriteFeed to display. No matching articles may be available or you need to refresh after connecting to internet.");
                            newsFeedTestValues_set1.put(NewsContract.NewsFeed.COLUMN_SAVEDFLAG, "0");
                            newsFeedTestValues_set1.put(NewsContract.NewsFeed.COLUMN_PUBLISHDATE, Utility.getYesterdayDate());
                            mContext.getContentResolver().insert(NewsContract.NewsFeed.CONTENT_URI, newsFeedTestValues_set1);
                            cursorLoader = new CursorLoader(mContext,
                                                NewsContract.NewsFeed.CONTENT_URI,
                                                null,
                                                NewsContract.NewsFeed.COLUMN_ARTICLEID + " = ? ",
                                                new String[]{"DummyFavouriteNewsFeedArticleID"},
                                                null);

                        } else {

                            cursorLoader = new CursorLoader(mContext,
                                                NewsContract.NewsFeed.CONTENT_URI,
                                                null,
                                                NewsContract.NewsFeed.COLUMN_ARTICLEID + " = ? ",
                                                new String[]{"DummyFavouriteNewsFeedArticleID"},
                                                null);

                        }

                    }

                } else {

                    mCursor = mContext.getContentResolver().query(NewsContract.NewsFeed.CONTENT_URI,
                                    null,
                                    NewsContract.NewsFeed.COLUMN_ARTICLEID + " = ? ",
                                    new String[]{"DummyFavouriteNewsFeedArticleID"},
                                    null);

                    if (!((mCursor != null) && (mCursor.moveToFirst()))){

                        ContentValues newsFeedTestValues_set1 = new ContentValues();
                        newsFeedTestValues_set1.put(NewsContract.NewsFeed.COLUMN_ARTICLEID, "DummyFavouriteNewsFeedArticleID");
                        newsFeedTestValues_set1.put(NewsContract.NewsFeed.COLUMN_SECTIONID, "DummyFavouriteNewsFeedSectionID");
                        newsFeedTestValues_set1.put(NewsContract.NewsFeed.COLUMN_APIURL, "DummyFavouriteNewsFeedAPIURL");
                        newsFeedTestValues_set1.put(NewsContract.NewsFeed.COLUMN_WEBURL, "http://www.theguardian.com/");
                        newsFeedTestValues_set1.put(NewsContract.NewsFeed.COLUMN_WEBTITLE, "No FavouriteFeed to display");
                        newsFeedTestValues_set1.put(NewsContract.NewsFeed.COLUMN_TRAILTEXT,
                                "No FavouriteFeed to display. No matching articles may be available or you need to refresh after connecting to internet.");
                        newsFeedTestValues_set1.put(NewsContract.NewsFeed.COLUMN_SAVEDFLAG, "0");
                        newsFeedTestValues_set1.put(NewsContract.NewsFeed.COLUMN_PUBLISHDATE, Utility.getYesterdayDate());
                        mContext.getContentResolver().insert(NewsContract.NewsFeed.CONTENT_URI, newsFeedTestValues_set1);
                        cursorLoader = new CursorLoader(mContext,
                                            NewsContract.NewsFeed.CONTENT_URI,
                                            null,
                                            NewsContract.NewsFeed.COLUMN_ARTICLEID + " = ? ",
                                            new String[]{"DummyFavouriteNewsFeedArticleID"},
                                            null);

                    } else {

                        cursorLoader = new CursorLoader(mContext,
                                            NewsContract.NewsFeed.CONTENT_URI,
                                            null,
                                            NewsContract.NewsFeed.COLUMN_ARTICLEID + " = ? ",
                                            new String[]{"DummyFavouriteNewsFeedArticleID"},
                                            null);

                    }

                }

                break;

            default:

                throw new UnsupportedOperationException("Unknown Loader ID used: " + String.valueOf(id));

        }

        return cursorLoader;

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        if ((data != null) && (data.moveToFirst())){

            if (data.getCount() >= 2 ){

                Uri uri = NewsContract.NewsFeed.buildNewsFeedUri("DummyFavouriteNewsFeedArticleID");
                getActivity().getContentResolver().delete(uri, null, null);

            }

        }
        mNewsFeedAdapter = new NewsFeedAdapter(getActivity(),data);
        mRecyclerView.setAdapter(mNewsFeedAdapter);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        mNewsFeedAdapter.swapCursor(null);

    }

    @Override
    public void onDetach() {

        super.onDetach();
        mCursor.close();

    }


    @Override
    public void onResume() {

        super.onResume();
        getLoaderManager().restartLoader(LOADER_ID_FAVOURITES, null, this);

    }

}
