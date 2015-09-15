package com.example.android.breakinuse;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
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

public class SavedNewsFeedFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private final String TAG = SavedNewsFeedFragment.class.getName();
    private SavedNewsFeedAdapter mSavedNewsFeedAdapter;
    private static final int LOADER_ID_SAVED_NEWS_ARTICLES = 2;
    private RecyclerView mRecyclerView;
    private Context mContext;
    private Cursor mCursor;

    public SavedNewsFeedFragment(){

        super();
        mContext = getActivity();

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_saved_news_feed, container, false);
        mContext = getActivity();

        mCursor = mContext.getContentResolver().query(NewsContract.NewsArticle.CONTENT_URI, null, null, null, null);
        if ((mCursor != null) && (!mCursor.moveToFirst())){

            ContentValues contentValues = new ContentValues();
            contentValues.put(NewsContract.NewsArticle.COLUMN_NEWSFEED_KEY,1000);
            contentValues.put(NewsContract.NewsArticle.COLUMN_WEBURL,"https://www.guardian.com/");
            contentValues.put(NewsContract.NewsArticle.COLUMN_ARTICLEID,"DummySavedNewsArticleID");
            contentValues.put(NewsContract.NewsArticle.COLUMN_SECTIONID,"DummySavedNewsSectionID");
            contentValues.put(NewsContract.NewsArticle.COLUMN_HEADLINE,"No Article saved currently");
            contentValues.put(NewsContract.NewsArticle.COLUMN_TRAILTEXT,"There is no article saved currently. Save an article to read later.");
            contentValues.put(NewsContract.NewsArticle.COLUMN_HTML_BODY,"DummyHTMLBody");
            contentValues.put(NewsContract.NewsArticle.COLUMN_BYLINE,"DummyByline");
            contentValues.put(NewsContract.NewsArticle.COLUMN_DOWNLOADFLAG,"1");
            mContext.getContentResolver().insert(NewsContract.NewsArticle.CONTENT_URI, contentValues);

            mCursor = mContext.getContentResolver().query(NewsContract.NewsArticle.CONTENT_URI, null, null, null, null);

        }

        if (mCursor != null){

            mCursor.moveToFirst();

        }
        getLoaderManager().initLoader(LOADER_ID_SAVED_NEWS_ARTICLES, null, this);

        mSavedNewsFeedAdapter = new SavedNewsFeedAdapter(mContext,mCursor);

        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        mRecyclerView = (RecyclerView)rootView.findViewById(R.id.savedNewsFeed_recyclerView);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mSavedNewsFeedAdapter);

        return rootView;

    }

    @Override
    public void onResume() {

        super.onResume();

        Cursor cursor = mContext.getContentResolver().query(NewsContract.NewsArticle.CONTENT_URI, null, null, null, null);
        if ((cursor != null) && (!cursor.moveToFirst())){

            ContentValues contentValues = new ContentValues();
            contentValues.put(NewsContract.NewsArticle.COLUMN_NEWSFEED_KEY,1000);
            contentValues.put(NewsContract.NewsArticle.COLUMN_WEBURL,"https://www.guardian.com/");
            contentValues.put(NewsContract.NewsArticle.COLUMN_ARTICLEID,"DummySavedNewsArticleID");
            contentValues.put(NewsContract.NewsArticle.COLUMN_SECTIONID,"DummySavedNewsSectionID");
            contentValues.put(NewsContract.NewsArticle.COLUMN_HEADLINE,"No Article saved currently");
            contentValues.put(NewsContract.NewsArticle.COLUMN_TRAILTEXT,"There is no article saved currently. Save an article to read later.");
            contentValues.put(NewsContract.NewsArticle.COLUMN_HTML_BODY,"DummyHTMLBody");
            contentValues.put(NewsContract.NewsArticle.COLUMN_BYLINE,"DummyByline");
            contentValues.put(NewsContract.NewsArticle.COLUMN_DOWNLOADFLAG,"1");
            mContext.getContentResolver().insert(NewsContract.NewsArticle.CONTENT_URI, contentValues);

        }

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        CursorLoader cursorLoader;
        switch(id){

            case LOADER_ID_SAVED_NEWS_ARTICLES:

                cursorLoader = new CursorLoader(mContext,NewsContract.NewsArticle.CONTENT_URI,null,null,null,null);
                break;

            default:

                throw new UnsupportedOperationException("Unknown Loader ID used: " + String.valueOf(id));

        }

        return cursorLoader;

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        if (data.getCount() >= 2){

            mContext.getContentResolver().delete(NewsContract.NewsArticle.CONTENT_URI,
                    NewsContract.NewsArticle.COLUMN_ARTICLEID + " = ?",
                    new String[]{"DummySavedNewsArticleID"});

        }
        mSavedNewsFeedAdapter = new SavedNewsFeedAdapter(mContext,data);
        mRecyclerView.setAdapter(mSavedNewsFeedAdapter);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        mSavedNewsFeedAdapter.swapCursor(null);

    }

    @Override
    public void onDetach() {

        mCursor.close();
        super.onDetach();

    }

}
