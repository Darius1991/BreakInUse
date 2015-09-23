package com.example.android.breakinuse.recyclerViewAdapter;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.breakinuse.NewsArticleActivity;
import com.example.android.breakinuse.R;
import com.example.android.breakinuse.newsProvider.NewsContract;
import com.example.android.breakinuse.utilities.Utility;

public class SavedNewsFeedAdapter extends CursorRecyclerViewAdapter<SavedNewsFeedAdapter.ViewHolder> {

    private Context mContext;

    public SavedNewsFeedAdapter(Context tempContext, Cursor tempCursor) {

        super(tempContext, tempCursor);
        mContext = tempContext;

    }

    @Override
    public SavedNewsFeedAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.saved_news_feed_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(SavedNewsFeedAdapter.ViewHolder viewholder, Cursor cursor) {

        if ((cursor != null) && ( cursor.getCount() > 0 ) && (cursor.getPosition() < cursor.getCount())){

            viewholder.mTextView_headlines.setText(cursor.getString(5));
            viewholder.mTextView_trailText.setText(cursor.getString(6));


        }

    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView mTextView_headlines;
        public TextView mTextView_trailText;
        public TextView mTextView_deleteButton;
        public CardView mCardView;

        public ViewHolder(View itemView) {

            super(itemView);
            mTextView_headlines = (TextView)itemView.findViewById(R.id.savedNewsFeedItem_headlines_textView);
            mTextView_trailText = (TextView)itemView.findViewById(R.id.savedNewsFeedItem_trailText_textView);
            mTextView_deleteButton = (TextView)itemView.findViewById(R.id.savedNewsFeedItem_delete_textView);
            mCardView = (CardView) itemView.findViewById(R.id.savedNewsFeed_cardView);

            mTextView_headlines.setClickable(true);
            mTextView_headlines.setOnClickListener(this);
            mTextView_trailText.setClickable(true);
            mTextView_trailText.setOnClickListener(this);
            mTextView_deleteButton.setClickable(true);
            mTextView_deleteButton.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {

            Cursor tempCursor = getCursor();
            if (v != mTextView_deleteButton){

                if ((tempCursor != null ) && (tempCursor.moveToFirst())){

                    if (tempCursor.moveToPosition(getAdapterPosition())){

                        if((!Utility.isNetworkAvailable(mContext))){

                            if(tempCursor.getString(tempCursor.getColumnIndex(NewsContract.NewsArticle.COLUMN_DOWNLOADFLAG)).equals("0")){

                                Utility.makeToast(mContext,
                                        "The article was not downloaded due to absence of internet connection. Please resolve this before trying again.",
                                        Toast.LENGTH_SHORT);
                                return;

                            }

                        }

                        Intent intent = new Intent(mContext, NewsArticleActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("articleID", tempCursor.getString(3));
                        intent.putExtra("webURL",tempCursor.getString(2));
                        intent.putExtra("ArticleLoadMethod","HTMLBody");
                        mContext.startActivity(intent);

                    }

                }

            } else {

                if ((tempCursor != null ) && (tempCursor.moveToFirst())){

                    if (tempCursor.moveToPosition(getAdapterPosition())){

                        mContext.getContentResolver().delete(NewsContract.NewsArticle.NEWSARTICLE_URI,
                                                                NewsContract.NewsArticle.COLUMN_ARTICLEID + " =? ",
                                                                new String[]{tempCursor.getString(tempCursor.getColumnIndex(NewsContract.NewsArticle.COLUMN_ARTICLEID))});


                    }

                }

            }

        }

    }

}
