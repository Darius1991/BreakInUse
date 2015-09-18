package com.example.android.breakinuse;

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
import android.widget.TextView;

import com.example.android.breakinuse.newsProvider.NewsContract;
import com.example.android.breakinuse.recyclerViewAdapter.SavedNewsFeedAdapter;

public class SavedNewsFeedFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private final String TAG = SavedNewsFeedFragment.class.getName();
    private SavedNewsFeedAdapter mSavedNewsFeedAdapter;
    private static final int LOADER_ID_SAVED_NEWS_ARTICLES = 2;
    private RecyclerView mRecyclerView;
    private Context mContext;
    private TextView mSavedNewsFeedTextView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_saved_news_feed, container, false);
        mSavedNewsFeedTextView = (TextView) rootView.findViewById(R.id.savedNewsFeed_textView);
        mContext = getActivity();

        getLoaderManager().initLoader(LOADER_ID_SAVED_NEWS_ARTICLES, null, this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        mRecyclerView = (RecyclerView)rootView.findViewById(R.id.savedNewsFeed_recyclerView);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);

        return rootView;

    }

    @Override
    public void onResume() {

        super.onResume();
        getLoaderManager().restartLoader(LOADER_ID_SAVED_NEWS_ARTICLES, null, this);

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        CursorLoader cursorLoader;
        switch(id){

            case LOADER_ID_SAVED_NEWS_ARTICLES:

                cursorLoader = new CursorLoader(mContext,NewsContract.NewsArticle.NEWSARTICLE_URI,null,null,null,null);
                break;

            default:

                throw new UnsupportedOperationException("Unknown Loader ID used: " + String.valueOf(id));

        }

        return cursorLoader;

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        if ((data != null) && (data.moveToFirst())){

            mSavedNewsFeedTextView.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);

        } else {

            mSavedNewsFeedTextView.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);

        }

        mSavedNewsFeedAdapter = new SavedNewsFeedAdapter(mContext,data);
        mRecyclerView.setAdapter(mSavedNewsFeedAdapter);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        if (mSavedNewsFeedAdapter != null){

            mSavedNewsFeedAdapter.swapCursor(null);

        }

    }

}
