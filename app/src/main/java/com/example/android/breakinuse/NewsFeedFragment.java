package com.example.android.breakinuse;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
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

public class NewsFeedFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private NewsFeedAdapter mNewsFeedAdapter;
    private static final int LOADER_ID_ALL = 0;
    private static final String TAG = NewsFeedFragment.class.getName();
    private RecyclerView mRecyclerView;
    private Context mContext;
    private Cursor mCursor;

    public NewsFeedFragment(){

        super();
        mContext = getActivity();
        mCursor = null;

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_news_feed, container, false);

        mContext = getActivity();

        mCursor = mContext.getContentResolver().query(NewsContract.NewsFeed.CONTENT_URI,
                        null,
                        NewsContract.NewsFeed.COLUMN_ARTICLEID + " != ? ",
                        new String[]{"DummyFavouriteNewsFeedArticleID"},
                        null);

        if ((mCursor != null) && (mCursor.moveToFirst())) {

            if (mCursor.getCount() >= 2) {

                Uri uri = NewsContract.NewsFeed.buildNewsFeedUri("DummyNewsFeedArticleID");
                mContext.getContentResolver().delete(uri, null, null);

            }

        } else {

            ContentValues newsFeedTestValues_set1 = new ContentValues();
            newsFeedTestValues_set1.put(NewsContract.NewsFeed.COLUMN_ARTICLEID, "DummyNewsFeedArticleID");
            newsFeedTestValues_set1.put(NewsContract.NewsFeed.COLUMN_SECTIONID, "DummyNewsFeedSectionID");
            newsFeedTestValues_set1.put(NewsContract.NewsFeed.COLUMN_APIURL, "DummyNewsFeedAPIURL");
            newsFeedTestValues_set1.put(NewsContract.NewsFeed.COLUMN_WEBURL, "http://www.theguardian.com/");
            newsFeedTestValues_set1.put(NewsContract.NewsFeed.COLUMN_WEBTITLE, "No NewsFeed to display");
            newsFeedTestValues_set1.put(NewsContract.NewsFeed.COLUMN_TRAILTEXT,
                    "No NewsFeed to display. Please check your internet connection and refresh after connecting.");
            newsFeedTestValues_set1.put(NewsContract.NewsFeed.COLUMN_SAVEDFLAG, "0");
            newsFeedTestValues_set1.put(NewsContract.NewsFeed.COLUMN_PUBLISHDATE, Utility.getYesterdayDate());
            mContext.getContentResolver().insert(NewsContract.NewsFeed.CONTENT_URI, newsFeedTestValues_set1);

            mCursor = mContext.getContentResolver().query(NewsContract.NewsFeed.CONTENT_URI,
                            null,
                            NewsContract.NewsFeed.COLUMN_ARTICLEID + " != ? ",
                            new String[]{"DummyFavouriteNewsFeedArticleID"},
                            null);

        }
        getLoaderManager().initLoader(LOADER_ID_ALL, null, this);

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

            case LOADER_ID_ALL:

                cursorLoader = new CursorLoader(getActivity(),NewsContract.NewsFeed.CONTENT_URI,
                                        null,
                                        NewsContract.NewsFeed.COLUMN_ARTICLEID + " != ? ",
                                        new String[]{"DummyFavouriteNewsFeedArticleID"},
                                        null);
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

                Uri uri = NewsContract.NewsFeed.buildNewsFeedUri("DummyNewsFeedArticleID");
                mContext.getContentResolver().delete(uri, null, null);

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
    public void onResume() {

        super.onResume();
        getLoaderManager().restartLoader(LOADER_ID_ALL, null, this);

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {

        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser){

            getLoaderManager().restartLoader(LOADER_ID_ALL, null, this);

        }

    }

    @Override
    public void onDetach() {

        super.onDetach();
        mCursor.close();

    }

}
