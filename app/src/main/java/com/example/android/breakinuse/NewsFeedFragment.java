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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.breakinuse.NewsProvider.NewsContract;
import com.example.android.breakinuse.Utilities.Utility;

import java.util.Set;

public class NewsFeedFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private NewsFeedAdapter mNewsFeedAdapter;
    private static final int LOADER_ID_ALL = 0;
    private static final int LOADER_ID_FAVOURITES = 1;
    private static final String TAG = NewsFeedFragment.class.getName();
    private RecyclerView mRecyclerView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_news_feed, container, false);
        Context context = getActivity();

        Bundle newsTypeBundle = getArguments();
        String newsType = newsTypeBundle.getString("NewsType");
        Cursor cursor = null;

        if (newsType != null){

            if (newsType.equals("All")){

                context.getContentResolver().query(NewsContract.NewsFeed.CONTENT_URI, null, null, null, null);
                getLoaderManager().initLoader(LOADER_ID_ALL, null, this);

                Uri uri = NewsContract.NewsFeed.buildNewsFeedUri("news/1825/jan/31/mainsection.fromthearchive");
                context.getContentResolver().delete(uri, null, null);

                ContentValues newsFeedTestValues_set1 = new ContentValues();
                newsFeedTestValues_set1.put(NewsContract.NewsFeed.COLUMN_ARTICLEID,
                        "news/1825/jan/31/mainsection.fromthearchive");
                newsFeedTestValues_set1.put(NewsContract.NewsFeed.COLUMN_SECTIONID, "football");
                newsFeedTestValues_set1.put(NewsContract.NewsFeed.COLUMN_APIURL,
                        "http://content.guardianapis.com/news/1825/jan/31/mainsection.fromthearchive");
                newsFeedTestValues_set1.put(NewsContract.NewsFeed.COLUMN_WEBURL,
                        "http://www.theguardian.com/news/1825/jan/31/mainsection.fromthearchive");
                newsFeedTestValues_set1.put(NewsContract.NewsFeed.COLUMN_WEBTITLE,
                        "Seven years for a pound of butter");
                newsFeedTestValues_set1.put(NewsContract.NewsFeed.COLUMN_TRAILTEXT,
                        "<b>January 31 1825:</b> On this day in 1825 a selection of British criminals were sentenced. This is how the Guardian reported the news");

                context.getContentResolver().insert(NewsContract.NewsFeed.CONTENT_URI, newsFeedTestValues_set1);
                cursor =  context.getContentResolver().query(NewsContract.NewsFeed.CONTENT_URI,null,null,null,null);

            }else if (newsType.equals("Favourites")){

                context.getContentResolver().query(NewsContract.NewsFeed.CONTENT_URI,null,null,null,null);
                getLoaderManager().initLoader(LOADER_ID_FAVOURITES, null, this);

                Set<String> favouriteTopicsSet = Utility.getFavouriteTopicsSet(getActivity());
                String[] selectionArgs = new String[favouriteTopicsSet.size()];
                StringBuilder selection = new StringBuilder();

                int index = 0;
                for (String iterator : favouriteTopicsSet){

                    selectionArgs[index++] = iterator;
                    selection.append("?");
                    selection.append(",");

                }
                selection = new StringBuilder(selection.substring(0,selection.length()-1));

                cursor =  context.getContentResolver().query(NewsContract.NewsFeed.CONTENT_URI,
                            null,
                            NewsContract.NewsFeed.COLUMN_SECTIONID + " IN (" + selection.toString() +")",
                            selectionArgs,
                            null);

            }

        }

        if (cursor != null) {
            cursor.moveToFirst();
        }
        mNewsFeedAdapter = new NewsFeedAdapter(getActivity(),cursor);

        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        mRecyclerView = (RecyclerView)rootView.findViewById(R.id.newsFeed_recyclerView);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mNewsFeedAdapter);

        return rootView;

    }

//    public void notifyDataSetChanged(Utility.NewsFeedItem[] newsFeedItemArray) {
//
//        if (newsFeedItemArray != null){
//
//            mHeadlinesAdapter = new HeadlinesAdapter(mContext,newsFeedItemArray);
//            mRecyclerView.setAdapter(mHeadlinesAdapter);
//
//        }
//
//    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        CursorLoader cursorLoader;

        switch(id){

            case LOADER_ID_ALL:

                cursorLoader = new CursorLoader(getActivity(),NewsContract.NewsFeed.CONTENT_URI,null,null,null,null);
                break;

            case LOADER_ID_FAVOURITES:

                Set<String> favouriteTopicsSet = Utility.getFavouriteTopicsSet(getActivity());
                String[] selectionArgs = new String[favouriteTopicsSet.size()];
                StringBuilder selection = new StringBuilder();

                int index = 0;
                for (String iterator : favouriteTopicsSet){

                    selectionArgs[index++] = iterator;
                    selection.append("?");
                    selection.append(",");

                }
                selection = new StringBuilder(selection.substring(0,selection.length()-1));

                cursorLoader = new CursorLoader(getActivity(),NewsContract.NewsFeed.CONTENT_URI,null,
                                    NewsContract.NewsFeed.COLUMN_SECTIONID + " IN (" + selection.toString() +")",
                                    selectionArgs,
                                    null);
                break;

            default:

                throw new UnsupportedOperationException("Unknown Loader ID used: " + String.valueOf(id));

        }

        return cursorLoader;

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        mNewsFeedAdapter = new NewsFeedAdapter(getActivity(),data);
        mRecyclerView.setAdapter(mNewsFeedAdapter);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        mNewsFeedAdapter.swapCursor(null);

    }

}
