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
import com.example.android.breakinuse.dataSync.DownloadNewsArticleTask;
import com.example.android.breakinuse.newsProvider.NewsContract;
import com.example.android.breakinuse.utilities.Utility;
import com.squareup.picasso.Picasso;

public class FavouriteNewsFeedAdapter extends CursorRecyclerViewAdapter<FavouriteNewsFeedAdapter.ViewHolder>{

    private Context mContext;
    private static final String TAG = NewsFeedAdapter.class.getName();

    public FavouriteNewsFeedAdapter(Context context, Cursor cursor) {

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
            viewholder.mTextView_author.setText(cursor.getString(11));
            viewholder.mTextView_sectionID.setText(cursor.getString(2));
            viewholder.mTextView_saveText.setVisibility(View.GONE);

            Picasso.with(mContext)
                    .load(cursor.getString(10))
                    .fit()
                    .centerCrop()
                    .into(viewholder.mImageView_newsFeedItemImage);

            String savedFlag = cursor.getString(8);
            if (savedFlag.equals("0")){

                viewholder.mTextView_saveText.setText(viewholder.mTextView_saveText_saveButtonText);
                viewholder.mImageView_saveButton.setImageResource(viewholder.mImageView_saveButton_saveImage);

            } else {

                viewholder.mTextView_saveText.setText(viewholder.mTextView_saveText_deleteButtonText);
                viewholder.mImageView_saveButton.setImageResource(viewholder.mImageView_saveButton_deleteImage);

            }

        }

    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView mTextView_headlines;
        public TextView mTextView_trailText;
        public TextView mTextView_saveText;
        public ImageView mImageView_newsFeedItemImage;
        public String mTextView_saveText_saveButtonText;
        public String mTextView_saveText_deleteButtonText;

        public ImageView mImageView_saveButton;
        public ImageView mImageView_shareButton;
        public TextView mTextView_author;
        public TextView mTextView_sectionID;

        public int mImageView_saveButton_saveImage;
        public int mImageView_saveButton_deleteImage;

        public ViewHolder(View itemView) {

            super(itemView);
            mTextView_headlines = (TextView) itemView.findViewById(R.id.newsFeedItem_headlines_textView);
            mTextView_trailText = (TextView) itemView.findViewById(R.id.newsFeedItem_trailText_textView);
            mTextView_saveText = (TextView) itemView.findViewById(R.id.newsFeedItem_save_textView);
            mImageView_newsFeedItemImage = (ImageView) itemView.findViewById(R.id.newsFeedItem_imageView);

            mImageView_saveButton = (ImageView) itemView.findViewById(R.id.newsFeedItem_saveButton);
            mImageView_shareButton = (ImageView) itemView.findViewById(R.id.newsFeedItem_shareButton);
            mTextView_author = (TextView) itemView.findViewById(R.id.newsFeedItem_author);
            mTextView_sectionID = (TextView) itemView.findViewById(R.id.newsFeedItem_sectionID);

            mImageView_saveButton_saveImage = mContext
                                                .getResources()
                                                .getIdentifier("ic_bookmark_border_black_24dp",
                                                        "drawable",
                                                        mContext.getPackageName());

            mImageView_saveButton_deleteImage = mContext
                                                .getResources()
                                                .getIdentifier("ic_bookmark_black_24dp",
                                                        "drawable",
                                                        mContext.getPackageName());


            mImageView_newsFeedItemImage.setClickable(true);
            mImageView_newsFeedItemImage.setOnClickListener(this);
            mTextView_trailText.setClickable(true);
            mTextView_trailText.setOnClickListener(this);
            mImageView_saveButton.setClickable(true);
            mImageView_saveButton.setOnClickListener(this);
            mTextView_headlines.setClickable(true);
            mTextView_headlines.setOnClickListener(this);
            mImageView_shareButton.setClickable(true);
            mImageView_shareButton.setOnClickListener(this);

            mTextView_saveText_saveButtonText =  mContext.getString(R.string.newsFeedItem_textView_saveText);
            mTextView_saveText_deleteButtonText = mContext.getString(R.string.newsFeedItem_textView_deleteText);

        }

        @Override
        public void onClick(View v) {

            Cursor tempCursor = getCursor();
            if ((v == mTextView_headlines) || (v == mTextView_trailText) || (v == mImageView_newsFeedItemImage)){

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

                    int columnIndex = tempCursor.getColumnIndex(NewsContract.NewsFeed.COLUMN_WEBURL);;
                    intent.putExtra("ArticleLoadMethod","webURL");
                    intent.putExtra("webURL", tempCursor.getString(columnIndex));
                    mContext.startActivity(intent);

                }

            } else if (v == mImageView_saveButton){

                if((mTextView_saveText.getText()).equals(mTextView_saveText_deleteButtonText)){

                    if (tempCursor != null){

                        if(tempCursor.moveToPosition(getAdapterPosition())){

                            String articleID = tempCursor.getString(
                                    tempCursor.getColumnIndex(NewsContract.NewsArticle.COLUMN_ARTICLEID));
                            mContext.getContentResolver()
                                    .delete(NewsContract.NewsArticle.NEWSARTICLE_URI,
                                            NewsContract.NewsArticle.COLUMN_ARTICLEID + " = ?",
                                            new String[]{articleID});

                            ContentValues contentValues = new ContentValues();
                            contentValues.put(NewsContract.NewsFeed.COLUMN_ARTICLEID,articleID);
                            contentValues.put(NewsContract.NewsFeed.COLUMN_SECTIONID,tempCursor.getString(2));
                            contentValues.put(NewsContract.NewsFeed.COLUMN_APIURL,tempCursor.getString(3));
                            contentValues.put(NewsContract.NewsFeed.COLUMN_WEBURL,tempCursor.getString(4));
                            contentValues.put(NewsContract.NewsFeed.COLUMN_WEBTITLE,tempCursor.getString(5));
                            contentValues.put(NewsContract.NewsFeed.COLUMN_TRAILTEXT,tempCursor.getString(6));
                            contentValues.put(NewsContract.NewsFeed.COLUMN_IMAGEURL,tempCursor.getString(7));
                            contentValues.put(NewsContract.NewsFeed.COLUMN_SAVEDFLAG, "0");
                            contentValues.put(NewsContract.NewsFeed.COLUMN_PUBLISHDATE,tempCursor.getString(9));
                            contentValues.put(NewsContract.NewsFeed.COLUMN_THUMBNAILURL,tempCursor.getString(10));
                            contentValues.put(NewsContract.NewsFeed.COLUMN_BYLINE,tempCursor.getString(11));
                            mContext.getContentResolver().update(NewsContract.NewsFeed.FAVOURITE_NEWSFEED_READURI,
                                    contentValues,
                                    NewsContract.NewsFeed.COLUMN_ARTICLEID + " = ?",
                                    new String[]{articleID});
                            mTextView_saveText.setText(mTextView_saveText_saveButtonText);
                            mImageView_saveButton.setImageResource(mImageView_saveButton_saveImage);

                        }

                    }

                } else {

                    if (tempCursor != null){

                        if(tempCursor.moveToPosition(getAdapterPosition())){

                            String articleID = tempCursor.getString(
                                    tempCursor.getColumnIndex(NewsContract.NewsArticle.COLUMN_ARTICLEID));

                            ContentValues contentValues = new ContentValues();
                            contentValues.put(NewsContract.NewsArticle.COLUMN_NEWSFEED_KEY,tempCursor.getInt(0));
                            contentValues.put(NewsContract.NewsArticle.COLUMN_ARTICLEID,articleID);
                            contentValues.put(NewsContract.NewsArticle.COLUMN_SECTIONID,tempCursor.getString(2));
                            contentValues.put(NewsContract.NewsArticle.COLUMN_WEBURL,tempCursor.getString(4));
                            contentValues.put(NewsContract.NewsArticle.COLUMN_HEADLINE,tempCursor.getString(5));
                            contentValues.put(NewsContract.NewsArticle.COLUMN_TRAILTEXT,tempCursor.getString(6));
                            contentValues.put(NewsContract.NewsArticle.COLUMN_IMAGEURL,tempCursor.getString(7));
                            contentValues.put(NewsContract.NewsArticle.COLUMN_HTML_BODY, "0");
                            contentValues.put(NewsContract.NewsArticle.COLUMN_BYLINE, tempCursor.getString(11));
                            contentValues.put(NewsContract.NewsArticle.COLUMN_DOWNLOADFLAG, "0");
                            contentValues.put(NewsContract.NewsArticle.COLUMN_THUMBNAILURL,tempCursor.getString(10));
                            contentValues.put(NewsContract.NewsArticle.COLUMN_APIURL,tempCursor.getString(3));
                            mContext.getContentResolver().insert(NewsContract.NewsArticle.NEWSARTICLE_URI, contentValues);

                            if (Utility.isNetworkAvailable(mContext)){

                                new DownloadNewsArticleTask(mContext).execute(articleID);

                            }

                            contentValues.clear();
                            contentValues.put(NewsContract.NewsFeed.COLUMN_ARTICLEID, articleID);
                            contentValues.put(NewsContract.NewsFeed.COLUMN_SECTIONID,tempCursor.getString(2));
                            contentValues.put(NewsContract.NewsFeed.COLUMN_APIURL,tempCursor.getString(3));
                            contentValues.put(NewsContract.NewsFeed.COLUMN_WEBURL,tempCursor.getString(4));
                            contentValues.put(NewsContract.NewsFeed.COLUMN_WEBTITLE,tempCursor.getString(5));
                            contentValues.put(NewsContract.NewsFeed.COLUMN_TRAILTEXT,tempCursor.getString(6));
                            contentValues.put(NewsContract.NewsFeed.COLUMN_IMAGEURL,tempCursor.getString(7));
                            contentValues.put(NewsContract.NewsFeed.COLUMN_SAVEDFLAG, "1");
                            contentValues.put(NewsContract.NewsFeed.COLUMN_PUBLISHDATE,tempCursor.getString(9));
                            contentValues.put(NewsContract.NewsFeed.COLUMN_THUMBNAILURL, tempCursor.getString(10));
                            contentValues.put(NewsContract.NewsFeed.COLUMN_BYLINE, tempCursor.getString(11));
                            mContext.getContentResolver().update(NewsContract.NewsFeed.FAVOURITE_NEWSFEED_READURI,
                                    contentValues,
                                    NewsContract.NewsFeed.COLUMN_ARTICLEID + " = ?",
                                    new String[]{articleID});

                            mTextView_saveText.setText(mTextView_saveText_deleteButtonText);
                            mImageView_saveButton.setImageResource(mImageView_saveButton_deleteImage);
                            Utility.makeToast(mContext, "Article saved to favourites", Toast.LENGTH_SHORT);

                        }

                    }

                }

            } else {

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
