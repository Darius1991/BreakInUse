package com.example.android.breakinuse.recyclerViewAdapter;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Point;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.breakinuse.NewsArticleActivity;
import com.example.android.breakinuse.R;
import com.example.android.breakinuse.newsProvider.NewsContract;
import com.example.android.breakinuse.dataSync.DownloadNewsArticleTask;
import com.example.android.breakinuse.utilities.Utility;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class NewsFeedAdapter extends CursorRecyclerViewAdapter<NewsFeedAdapter.ViewHolder> {

    private Context mContext;
    private static final String TAG = NewsFeedAdapter.class.getName();

    public NewsFeedAdapter(Context context, Cursor cursor) {

        super(context, cursor);
        mContext = context;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_feed_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(ViewHolder viewholder, Cursor cursor) {

        if ((cursor != null) && ( cursor.getCount() > 0 ) && (cursor.getPosition() < cursor.getCount())){

            viewholder.mTextView_headlines.setText(cursor.getString(5));
            viewholder.mTextView_trailText.setText(cursor.getString(6));

            Picasso.with(mContext)
                    .load(cursor.getString(10))
                    .fit()
                    .centerCrop()
                    .into(viewholder.mImageView_newsFeedItemImage);

            String savedFlag = cursor.getString(8);
            if (savedFlag.equals("0")){

                viewholder.mTextView_saveText.setText(viewholder.mTextView_saveText_saveButtonText);

            } else {

                viewholder.mTextView_saveText.setText(viewholder.mTextView_saveText_deleteButtonText);

            }

        }

    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView mTextView_headlines;
        public TextView mTextView_trailText;
        public TextView mTextView_saveText;
        public ImageView mImageView_newsFeedItemImage;
        public CardView mCardView;
        public String mTextView_saveText_saveButtonText;
        public String mTextView_saveText_deleteButtonText;

        public ViewHolder(View itemView) {

            super(itemView);
            mTextView_headlines = (TextView) itemView.findViewById(R.id.newsFeedItem_headlines_textView);
            mTextView_trailText = (TextView) itemView.findViewById(R.id.newsFeedItem_trailText_textView);
            mTextView_saveText = (TextView) itemView.findViewById(R.id.newsFeedItem_save_textView);
            mImageView_newsFeedItemImage = (ImageView) itemView.findViewById(R.id.newsFeedItem_imageView);
            mCardView = (CardView) itemView.findViewById(R.id.newsFeed_cardView);

            mImageView_newsFeedItemImage.setClickable(true);
            mImageView_newsFeedItemImage.setOnClickListener(this);
            mTextView_headlines.setClickable(true);
            mTextView_headlines.setOnClickListener(this);
            mTextView_trailText.setClickable(true);
            mTextView_trailText.setOnClickListener(this);
            mTextView_saveText.setClickable(true);
            mTextView_saveText.setOnClickListener(this);

            mTextView_saveText_saveButtonText =  mContext.getString(R.string.newsFeedItem_textView_saveText);
            mTextView_saveText_deleteButtonText = mContext.getString(R.string.newsFeedItem_textView_deleteText);

        }

        @Override
        public void onClick(View v) {

            if (v != mTextView_saveText){

                Cursor tempCursor = getCursor();
                if ((tempCursor != null ) && (tempCursor.moveToFirst())){

                    tempCursor.moveToPosition(getAdapterPosition());

                    if (!Utility.isNetworkAvailable(mContext)){

                        Utility.makeToast(mContext,
                                "We are not able to detect an internet connection. Please resolve this before trying to view the article again.",
                                Toast.LENGTH_SHORT);
                        return;

                    }

                    Intent intent = new Intent(mContext, NewsArticleActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    intent.putExtra("ArticleLoadMethod","webURL");
                    intent.putExtra("webURL", tempCursor.getString(4));
                    mContext.startActivity(intent);

                }

            } else {

                if((mTextView_saveText.getText()).equals(mTextView_saveText_deleteButtonText)){

                    Cursor cursor = getCursor();
                    if (cursor != null){

                        if(cursor.moveToPosition(getAdapterPosition())){

                            String articleID = cursor.getString(
                                    cursor.getColumnIndex(NewsContract.NewsArticle.COLUMN_ARTICLEID));
                            mContext.getContentResolver()
                                    .delete(NewsContract.NewsArticle.NEWSARTICLE_URI,
                                            NewsContract.NewsArticle.COLUMN_ARTICLEID + " = ?",
                                            new String[]{articleID});

                            ContentValues contentValues = new ContentValues();
                            contentValues.put(NewsContract.NewsFeed.COLUMN_ARTICLEID,articleID);
                            contentValues.put(NewsContract.NewsFeed.COLUMN_SECTIONID,cursor.getString(2));
                            contentValues.put(NewsContract.NewsFeed.COLUMN_APIURL,cursor.getString(3));
                            contentValues.put(NewsContract.NewsFeed.COLUMN_WEBURL,cursor.getString(4));
                            contentValues.put(NewsContract.NewsFeed.COLUMN_WEBTITLE,cursor.getString(5));
                            contentValues.put(NewsContract.NewsFeed.COLUMN_TRAILTEXT,cursor.getString(6));
                            contentValues.put(NewsContract.NewsFeed.COLUMN_IMAGEURL,cursor.getString(7));
                            contentValues.put(NewsContract.NewsFeed.COLUMN_SAVEDFLAG, "0");
                            contentValues.put(NewsContract.NewsFeed.COLUMN_PUBLISHDATE,cursor.getString(9));
                            contentValues.put(NewsContract.NewsFeed.COLUMN_THUMBNAILURL,cursor.getString(10));
                            mContext.getContentResolver().update(NewsContract.NewsFeed.NEWSFEED_WRITEURI,
                                    contentValues,
                                    NewsContract.NewsFeed.COLUMN_ARTICLEID + " = ?",
                                    new String[]{articleID});
                            mTextView_saveText.setText(mTextView_saveText_saveButtonText);

                        }

                    }

                } else {

                    Cursor cursor = getCursor();
                    if (cursor != null){

                        if(cursor.moveToPosition(getAdapterPosition())){

                            String articleID = cursor.getString(
                                    cursor.getColumnIndex(NewsContract.NewsArticle.COLUMN_ARTICLEID));

                            ContentValues contentValues = new ContentValues();
                            contentValues.put(NewsContract.NewsArticle.COLUMN_NEWSFEED_KEY,cursor.getInt(0));
                            contentValues.put(NewsContract.NewsArticle.COLUMN_ARTICLEID,articleID);
                            contentValues.put(NewsContract.NewsArticle.COLUMN_SECTIONID,cursor.getString(2));
                            contentValues.put(NewsContract.NewsArticle.COLUMN_WEBURL,cursor.getString(4));
                            contentValues.put(NewsContract.NewsArticle.COLUMN_HEADLINE,cursor.getString(5));
                            contentValues.put(NewsContract.NewsArticle.COLUMN_TRAILTEXT,cursor.getString(6));
                            contentValues.put(NewsContract.NewsArticle.COLUMN_IMAGEURL,cursor.getString(7));
                            contentValues.put(NewsContract.NewsArticle.COLUMN_HTML_BODY, "0");
                            contentValues.put(NewsContract.NewsArticle.COLUMN_BYLINE, "0");
                            contentValues.put(NewsContract.NewsArticle.COLUMN_DOWNLOADFLAG, "0");
                            contentValues.put(NewsContract.NewsArticle.COLUMN_THUMBNAILURL,cursor.getString(10));
                            mContext.getContentResolver().insert(NewsContract.NewsArticle.NEWSARTICLE_URI, contentValues);

                            if (Utility.isNetworkAvailable(mContext)){

                                new DownloadNewsArticleTask(mContext).execute(articleID);

                            }

                            contentValues.clear();
                            contentValues.put(NewsContract.NewsFeed.COLUMN_ARTICLEID, articleID);
                            contentValues.put(NewsContract.NewsFeed.COLUMN_SECTIONID,cursor.getString(2));
                            contentValues.put(NewsContract.NewsFeed.COLUMN_APIURL,cursor.getString(3));
                            contentValues.put(NewsContract.NewsFeed.COLUMN_WEBURL,cursor.getString(4));
                            contentValues.put(NewsContract.NewsFeed.COLUMN_WEBTITLE,cursor.getString(5));
                            contentValues.put(NewsContract.NewsFeed.COLUMN_TRAILTEXT,cursor.getString(6));
                            contentValues.put(NewsContract.NewsFeed.COLUMN_IMAGEURL,cursor.getString(7));
                            contentValues.put(NewsContract.NewsFeed.COLUMN_SAVEDFLAG, "1");
                            contentValues.put(NewsContract.NewsFeed.COLUMN_PUBLISHDATE,cursor.getString(9));
                            contentValues.put(NewsContract.NewsFeed.COLUMN_THUMBNAILURL,cursor.getString(10));
                            mContext.getContentResolver().update(NewsContract.NewsFeed.NEWSFEED_WRITEURI,
                                    contentValues,
                                    NewsContract.NewsFeed.COLUMN_ARTICLEID + " = ?",
                                    new String[]{articleID});

                            mTextView_saveText.setText(mTextView_saveText_deleteButtonText);

                        }

                    }

                }

            }

        }

    }

}

