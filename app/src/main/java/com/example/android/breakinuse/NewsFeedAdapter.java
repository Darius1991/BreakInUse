package com.example.android.breakinuse;

import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.DataSetObservable;
import android.database.DataSetObserver;
import android.os.Handler;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.breakinuse.NewsProvider.NewsContract;

public class NewsFeedAdapter extends RecyclerView.Adapter<NewsFeedAdapter.ViewHolder> {

    private CursorAdapter mCursorAdapter;
    private Context mContext;
    private Cursor mOriginalCursor;
    private static final String TAG = NewsFeedAdapter.class.getName();

    public NewsFeedAdapter(Context context, Cursor cursor) {

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

        public ViewHolder(View itemView) {

            super(itemView);
            itemView.setClickable(true);
            itemView.setOnClickListener(this);
            mTextView_headlines = (TextView) itemView.findViewById(R.id.newsFeedItem_headlines_textView);
            mTextView_trailText = (TextView) itemView.findViewById(R.id.newsFeedItem_trailText_textView);

        }

        @Override
        public void onClick(View v) {

            Intent intent = new Intent(mContext, NewsArticleActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Cursor tempCursor = mOriginalCursor;
            tempCursor.moveToPosition(getAdapterPosition());
            int columnIndex = tempCursor.getColumnIndex(NewsContract.NewsFeed.COLUMN_WEBURL);
            intent.putExtra("webURL", tempCursor.getString(columnIndex));
            mContext.startActivity(intent);

        }
    }


}

