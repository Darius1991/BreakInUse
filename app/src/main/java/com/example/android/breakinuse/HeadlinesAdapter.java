package com.example.android.breakinuse;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.breakinuse.Utilities.Utility;

public class HeadlinesAdapter extends RecyclerView.Adapter<HeadlinesAdapter.ViewHolder> {

    private static Utility.NewsFeedItem[] mNewsFeedItemArray;
    private static Context mContext;
    private static final String TAG = HeadlinesAdapter.class.getName();

    public HeadlinesAdapter(Context context, Utility.NewsFeedItem[] tempNewsFeedItemArray){
        super();
        this.mNewsFeedItemArray = tempNewsFeedItemArray;
        this.mContext = context;
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

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
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
