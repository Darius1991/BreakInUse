package com.example.android.breakinuse.recyclerViewAdapter;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
            viewholder.mTextView_author.setText("By - " + cursor.getString(9));
            switch (cursor.getString(4)){

                case "world":

                    viewholder.mTextView_sectionID.setText("World News");
                    break;

                case "politics":

                    viewholder.mTextView_sectionID.setText("Politics");
                    break;

                case "sport":

                    viewholder.mTextView_sectionID.setText("Sports");
                    break;

                case "football":

                    viewholder.mTextView_sectionID.setText("Football");
                    break;

                case "culture":

                    viewholder.mTextView_sectionID.setText("Culture");
                    break;

                case "business":

                    viewholder.mTextView_sectionID.setText("Business");
                    break;

                case "lifeandstyle":

                    viewholder.mTextView_sectionID.setText("Lifestyle");
                    break;

                case "fashion":

                    viewholder.mTextView_sectionID.setText("Fashion");
                    break;

                case "environment":

                    viewholder.mTextView_sectionID.setText("Environment");
                    break;

                case "technology":

                    viewholder.mTextView_sectionID.setText("Tech");
                    break;

                case "books":

                    viewholder.mTextView_sectionID.setText("Books");
                    break;

                case "film":

                    viewholder.mTextView_sectionID.setText("Movies");
                    break;

                case "travel":

                    viewholder.mTextView_sectionID.setText("Travel");
                    break;

                default:

                    viewholder.mTextView_sectionID.setText("News");
                    break;

            }

        }

    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView mTextView_headlines;
        public TextView mTextView_trailText;
        public ImageView mImageView_deleteButton;
        public CardView mCardView;
        public ImageView mImageView_shareButton;
        public TextView mTextView_author;
        public TextView mTextView_sectionID;

        public ViewHolder(View itemView) {

            super(itemView);
            mTextView_headlines = (TextView)itemView.findViewById(R.id.savedNewsFeedItem_headlines_textView);
            mTextView_trailText = (TextView)itemView.findViewById(R.id.savedNewsFeedItem_trailText_textView);
            mImageView_deleteButton = (ImageView)itemView.findViewById(R.id.savedNewsFeedItem_deleteButton);
            mCardView = (CardView) itemView.findViewById(R.id.savedNewsFeed_cardView);
            mImageView_shareButton = (ImageView) itemView.findViewById(R.id.savedNewsFeedItem_shareButton);
            mTextView_author = (TextView) itemView.findViewById(R.id.savedNewsFeedItem_author);
            mTextView_sectionID = (TextView) itemView.findViewById(R.id.savedNewsFeedItem_sectionID);

            mTextView_headlines.setClickable(true);
            mTextView_headlines.setOnClickListener(this);
            mTextView_trailText.setClickable(true);
            mTextView_trailText.setOnClickListener(this);
            mImageView_deleteButton.setClickable(true);
            mImageView_deleteButton.setOnClickListener(this);
            mImageView_shareButton.setClickable(true);
            mImageView_shareButton.setOnClickListener(this);


        }

        @Override
        public void onClick(View v) {

            Cursor tempCursor = getCursor();
            if ((v == mTextView_headlines) || (v == mTextView_trailText)) {

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

            } else if (v == mImageView_deleteButton) {

                if ((tempCursor != null ) && (tempCursor.moveToFirst())){

                    if (tempCursor.moveToPosition(getAdapterPosition())){

                        mContext.getContentResolver().delete(NewsContract.NewsArticle.NEWSARTICLE_URI,
                                                                NewsContract.NewsArticle.COLUMN_ARTICLEID + " =? ",
                                                                new String[]{tempCursor.getString(tempCursor.getColumnIndex(NewsContract.NewsArticle.COLUMN_ARTICLEID))});

                        tempCursor = mContext.getContentResolver().query(NewsContract.NewsFeed.NEWSFEED_READURI,
                                        null,
                                        NewsContract.NewsArticle.COLUMN_ARTICLEID + " =? ",
                                        new String[]{tempCursor.getString(tempCursor.getColumnIndex(NewsContract.NewsArticle.COLUMN_ARTICLEID))},
                                        null);

                        if ((tempCursor != null) && (tempCursor.moveToFirst())){

                            ContentValues contentValues = new ContentValues();
                            contentValues.put(NewsContract.NewsFeed.COLUMN_ARTICLEID,tempCursor.getString(1));
                            contentValues.put(NewsContract.NewsFeed.COLUMN_SECTIONID,tempCursor.getString(2));
                            contentValues.put(NewsContract.NewsFeed.COLUMN_APIURL,tempCursor.getString(3));
                            contentValues.put(NewsContract.NewsFeed.COLUMN_WEBURL,tempCursor.getString(4));
                            contentValues.put(NewsContract.NewsFeed.COLUMN_WEBTITLE,tempCursor.getString(5));
                            contentValues.put(NewsContract.NewsFeed.COLUMN_TRAILTEXT,tempCursor.getString(6));
                            contentValues.put(NewsContract.NewsFeed.COLUMN_IMAGEURL,tempCursor.getString(7));
                            contentValues.put(NewsContract.NewsFeed.COLUMN_SAVEDFLAG, "0");
                            contentValues.put(NewsContract.NewsFeed.COLUMN_PUBLISHDATE,tempCursor.getString(9));
                            contentValues.put(NewsContract.NewsFeed.COLUMN_THUMBNAILURL, tempCursor.getString(10));
                            contentValues.put(NewsContract.NewsFeed.COLUMN_BYLINE, tempCursor.getString(11));
                            mContext.getContentResolver().update(NewsContract.NewsFeed.NEWSFEED_READURI,
                                    contentValues,
                                    NewsContract.NewsFeed.COLUMN_ARTICLEID + " = ?",
                                    new String[]{tempCursor.getString(1)});

                            Utility.makeToast(mContext,"Article deleted", Toast.LENGTH_SHORT);

                        }

                    }

                }

            } else if (v == mImageView_shareButton){

                if ((tempCursor != null ) && (tempCursor.moveToFirst())){

                    if (tempCursor.moveToPosition(getAdapterPosition())){

                        Intent sendIntent = new Intent();
                        sendIntent.setAction(Intent.ACTION_SEND);
                        sendIntent.putExtra(Intent.EXTRA_TEXT, "Read this article - " + tempCursor.getString(2));
                        sendIntent.setType("text/plain");
                        mContext.startActivity(Intent.createChooser(sendIntent, mContext.getResources().getText(R.string.shareDialogBoxText)));

                    }
                }

            }

        }

    }

}
