package com.example.android.breakinuse;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.breakinuse.NewsProvider.NewsDBHelper;
import com.example.android.breakinuse.Utilities.Utility;

public class NewsFeedFragment extends Fragment {

    public Utility.NewsFeedItem[] mNewsFeedItemArray;
    private RecyclerView mRecyclerView;
    private HeadlinesAdapter mHeadlinesAdapter;
    private Context mContext;
    private SQLiteDatabase db;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_news_feed, container, false);

        Bundle newsTypeBundle = getArguments();
        String newsType = newsTypeBundle.getString("NewsType");

        mNewsFeedItemArray = new Utility.NewsFeedItem[1];
        mNewsFeedItemArray[0] = new Utility.NewsFeedItem();
        mNewsFeedItemArray[0].webURL = "https://google.com";
        mNewsFeedItemArray[0].apiURL = newsType;
        mNewsFeedItemArray[0].webTitle = newsType;
        mNewsFeedItemArray[0].trailText = newsType;
        mNewsFeedItemArray[0].articleID = newsType;
        mNewsFeedItemArray[0].sectionID = newsType;

        mContext = getActivity();
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        mRecyclerView = (RecyclerView)rootView.findViewById(R.id.newsFeed_recyclerView);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        mHeadlinesAdapter = new HeadlinesAdapter(mContext,mNewsFeedItemArray);
        mRecyclerView.setAdapter(mHeadlinesAdapter);

        return rootView;

    }

    public void notifyDataSetChanged(Utility.NewsFeedItem[] newsFeedItemArray) {

        if (newsFeedItemArray != null){

            mHeadlinesAdapter = new HeadlinesAdapter(mContext,newsFeedItemArray);
            mRecyclerView.setAdapter(mHeadlinesAdapter);

        }

    }

    public class HeadlinesAdapter extends RecyclerView.Adapter<HeadlinesAdapter.ViewHolder> {

        private Utility.NewsFeedItem[] mNewsFeedItemArray;
        private Context mContext;
        private final String TAG = HeadlinesAdapter.class.getName();

        public HeadlinesAdapter(Context context, Utility.NewsFeedItem[] tempNewsFeedItemArray){
            super();
            mNewsFeedItemArray = tempNewsFeedItemArray;
            mContext = context;
        }

        @Override
        public HeadlinesAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

            View v = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.news_feed_item,viewGroup,false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            viewHolder.mTextView_headlines.setText(mNewsFeedItemArray[i].webTitle);
            viewHolder.mTextView_trailText.setText(mNewsFeedItemArray[i].trailText);
        }

        @Override
        public int getItemCount() {
            return mNewsFeedItemArray.length;
        }

        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
            // each data item is just a string in this case
            public TextView mTextView_headlines;
            public TextView mTextView_trailText;
            public ViewHolder(View v) {
                super(v);
                v.setClickable(true);
                v.setOnClickListener(this);
                mTextView_headlines = (TextView) v.findViewById(R.id.newsFeedItem_headlines_textView);
                mTextView_trailText = (TextView) v.findViewById(R.id.newsFeedItem_trailText_textView);
            }

            @Override
            public void onClick(View v) {

                Intent intent = new Intent(mContext,NewsArticleActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("webURL", mNewsFeedItemArray[getAdapterPosition()].webURL);
                mContext.startActivity(intent);
            }
        }

    }


}
