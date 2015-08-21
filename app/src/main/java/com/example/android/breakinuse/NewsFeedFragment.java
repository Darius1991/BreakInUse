package com.example.android.breakinuse;

import android.support.v4.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.breakinuse.Utilities.Utility;

public class NewsFeedFragment extends Fragment {

    public static Utility.NewsFeedItem[] mNewsFeedItemArray;
    private static RecyclerView mRecyclerView;
    private static HeadlinesAdapter mHeadlinesAdapter;
    private LinearLayoutManager mLayoutManager;
    private static Context mContext;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_news_feed, container, false);

        mNewsFeedItemArray = new Utility.NewsFeedItem[1];
        mNewsFeedItemArray[0] = new Utility.NewsFeedItem();
        mNewsFeedItemArray[0].webURL = "https://google.com";
        mNewsFeedItemArray[0].apiURL = "blaaa";
        mNewsFeedItemArray[0].webTitle = "blaaa";
        mNewsFeedItemArray[0].trailText = "blaa";
        mNewsFeedItemArray[0].articleID = "blaaa";
        mNewsFeedItemArray[0].sectionID = "blaaa";

        mContext = getActivity();
        mLayoutManager = new LinearLayoutManager(mContext);
        mRecyclerView = (RecyclerView)rootView.findViewById(R.id.newsFeed_recyclerView);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mHeadlinesAdapter = new HeadlinesAdapter(mContext,mNewsFeedItemArray);
        mRecyclerView.setAdapter(mHeadlinesAdapter);

    return rootView;
    }

    public static void notifyDataSetChanged(Utility.NewsFeedItem[] newsFeedItemArray) {
        if (newsFeedItemArray != null){
            mHeadlinesAdapter = new HeadlinesAdapter(mContext,newsFeedItemArray);
            mRecyclerView.setAdapter(mHeadlinesAdapter);
        }

    }

}
