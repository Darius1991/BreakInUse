package com.example.android.breakinuse;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.breakinuse.newsProvider.NewsContract;
import com.example.android.breakinuse.utilities.DownloadNewsArticleTask;
import com.example.android.breakinuse.utilities.Utility;

public class FavouriteNewsFeedAdapter extends RecyclerView.Adapter<FavouriteNewsFeedAdapter.ViewHolder>{

    private CursorAdapter mCursorAdapter;
    private Context mContext;
    private Cursor mOriginalCursor;
    private static final String TAG = NewsFeedAdapter.class.getName();

    public FavouriteNewsFeedAdapter(Context context, Cursor cursor) {

        mContext = context;
        mOriginalCursor = cursor;
        mCursorAdapter = new CursorAdapter(mContext, mOriginalCursor, 0) {

            @Override
            public View newView(Context context, Cursor cursor, ViewGroup parent) {

                return LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.news_feed_item, parent, false);

            }

            @Override
            public void bindView(View view, Context context, Cursor cursor) {

                ViewHolder viewHolder = (ViewHolder) view.getTag();

                int columnIndex = cursor.getColumnIndex(NewsContract.NewsFeed.COLUMN_WEBTITLE);
                viewHolder.mTextView_headlines.setText(cursor.getString(columnIndex));
                columnIndex = cursor.getColumnIndex(NewsContract.NewsFeed.COLUMN_TRAILTEXT);
                viewHolder.mTextView_trailText.setText(cursor.getString(columnIndex));
                columnIndex = cursor.getColumnIndex(NewsContract.NewsFeed.COLUMN_SAVEDFLAG);
                String savedFlag = cursor.getString(columnIndex);
                if (savedFlag.equals("0")){

                    viewHolder.mTextView_saveText.setText(viewHolder.mTextView_saveText_saveButtonText);

                } else {

                    viewHolder.mTextView_saveText.setText(viewHolder.mTextView_saveText_deleteButtonText);

                }

            }

        };

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = mCursorAdapter.newView(mContext, mCursorAdapter.getCursor(), parent);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Cursor cursor = mOriginalCursor;

        if (cursor.moveToPosition(position)) {

            mCursorAdapter.bindView(holder.itemView, mContext, cursor);

        } else {

            mCursorAdapter.bindView(holder.itemView, mContext, mCursorAdapter.getCursor());

        }

    }

    @Override
    public int getItemCount() {

        return mOriginalCursor.getCount();

    }

    public void swapCursor(Cursor cursor) {

        mCursorAdapter.swapCursor(cursor);

    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView mTextView_headlines;
        public TextView mTextView_trailText;
        public TextView mTextView_saveText;
        public String mTextView_saveText_saveButtonText;
        public String mTextView_saveText_deleteButtonText;

        public ViewHolder(View itemView) {

            super(itemView);
            mTextView_headlines = (TextView) itemView.findViewById(R.id.newsFeedItem_headlines_textView);
            mTextView_trailText = (TextView) itemView.findViewById(R.id.newsFeedItem_trailText_textView);
            mTextView_saveText = (TextView) itemView.findViewById(R.id.newsFeedItem_save_textView);;
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

                Cursor tempCursor = mOriginalCursor;
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

            } else {

                if((mTextView_saveText.getText()).equals(mTextView_saveText_deleteButtonText)){

                    Cursor cursor = mOriginalCursor;
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
                            contentValues.put(NewsContract.NewsFeed.COLUMN_SECTIONID,
                                    cursor.getString(cursor.getColumnIndex(NewsContract.NewsFeed.COLUMN_SECTIONID)));
                            contentValues.put(NewsContract.NewsFeed.COLUMN_APIURL,
                                    cursor.getString(cursor.getColumnIndex(NewsContract.NewsFeed.COLUMN_APIURL)));
                            contentValues.put(NewsContract.NewsFeed.COLUMN_WEBURL,
                                    cursor.getString(cursor.getColumnIndex(NewsContract.NewsFeed.COLUMN_WEBURL)));
                            contentValues.put(NewsContract.NewsFeed.COLUMN_WEBTITLE,
                                    cursor.getString(cursor.getColumnIndex(NewsContract.NewsFeed.COLUMN_WEBTITLE)));
                            contentValues.put(NewsContract.NewsFeed.COLUMN_TRAILTEXT,
                                    cursor.getString(cursor.getColumnIndex(NewsContract.NewsFeed.COLUMN_TRAILTEXT)));
                            contentValues.put(NewsContract.NewsFeed.COLUMN_SAVEDFLAG, "0");
                            contentValues.put(NewsContract.NewsFeed.COLUMN_PUBLISHDATE,
                                    cursor.getString(cursor.getColumnIndex(NewsContract.NewsFeed.COLUMN_PUBLISHDATE)));
                            mContext.getContentResolver().update(NewsContract.NewsFeed.FAVOURITE_NEWSFEED_WRITEURI,
                                    contentValues,
                                    NewsContract.NewsFeed.COLUMN_ARTICLEID + " = ?",
                                    new String[]{articleID});
                            mTextView_saveText.setText(mTextView_saveText_saveButtonText);

                        }

                    }

                } else {

                    Cursor cursor = mOriginalCursor;
                    if (cursor != null){

                        if(cursor.moveToPosition(getAdapterPosition())){

                            String articleID = cursor.getString(
                                    cursor.getColumnIndex(NewsContract.NewsArticle.COLUMN_ARTICLEID));

                            ContentValues contentValues = new ContentValues();
                            contentValues.put(NewsContract.NewsArticle.COLUMN_NEWSFEED_KEY,
                                    cursor.getInt(cursor.getColumnIndex(NewsContract.NewsFeed._ID)));
                            contentValues.put(NewsContract.NewsArticle.COLUMN_WEBURL,
                                    cursor.getString(cursor.getColumnIndex(NewsContract.NewsFeed.COLUMN_WEBURL)));
                            contentValues.put(NewsContract.NewsArticle.COLUMN_ARTICLEID,articleID);
                            contentValues.put(NewsContract.NewsArticle.COLUMN_SECTIONID,
                                    cursor.getString(cursor.getColumnIndex(NewsContract.NewsFeed.COLUMN_SECTIONID)));
                            contentValues.put(NewsContract.NewsArticle.COLUMN_HEADLINE,
                                    cursor.getString(cursor.getColumnIndex(NewsContract.NewsFeed.COLUMN_WEBTITLE)));
                            contentValues.put(NewsContract.NewsArticle.COLUMN_DOWNLOADFLAG, "0");
                            contentValues.put(NewsContract.NewsArticle.COLUMN_TRAILTEXT,
                                    cursor.getString(cursor.getColumnIndex(NewsContract.NewsFeed.COLUMN_TRAILTEXT)));
                            contentValues.put(NewsContract.NewsArticle.COLUMN_HTML_BODY, "0");
                            contentValues.put(NewsContract.NewsArticle.COLUMN_BYLINE, "0");
                            mContext.getContentResolver().insert(NewsContract.NewsArticle.NEWSARTICLE_URI, contentValues);

                            if (Utility.isNetworkAvailable(mContext)){

                                new DownloadNewsArticleTask(mContext).execute(articleID);

                            }

                            contentValues.clear();
                            contentValues.put(NewsContract.NewsFeed.COLUMN_ARTICLEID, articleID);
                            contentValues.put(NewsContract.NewsFeed.COLUMN_SECTIONID,
                                    cursor.getString(cursor.getColumnIndex(NewsContract.NewsFeed.COLUMN_SECTIONID)));
                            contentValues.put(NewsContract.NewsFeed.COLUMN_APIURL,
                                    cursor.getString(cursor.getColumnIndex(NewsContract.NewsFeed.COLUMN_APIURL)));
                            contentValues.put(NewsContract.NewsFeed.COLUMN_WEBURL,
                                    cursor.getString(cursor.getColumnIndex(NewsContract.NewsFeed.COLUMN_WEBURL)));
                            contentValues.put(NewsContract.NewsFeed.COLUMN_WEBTITLE,
                                    cursor.getString(cursor.getColumnIndex(NewsContract.NewsFeed.COLUMN_WEBTITLE)));
                            contentValues.put(NewsContract.NewsFeed.COLUMN_TRAILTEXT,
                                    cursor.getString(cursor.getColumnIndex(NewsContract.NewsFeed.COLUMN_TRAILTEXT)));
                            contentValues.put(NewsContract.NewsFeed.COLUMN_SAVEDFLAG, "1");
                            contentValues.put(NewsContract.NewsFeed.COLUMN_PUBLISHDATE,
                                    cursor.getString(cursor.getColumnIndex(NewsContract.NewsFeed.COLUMN_PUBLISHDATE)));

                            mContext.getContentResolver().update(NewsContract.NewsFeed.FAVOURITE_NEWSFEED_WRITEURI,
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
