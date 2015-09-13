package com.example.android.breakinuse;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.breakinuse.newsProvider.NewsContract;

public class SavedNewsFeedAdapter extends RecyclerView.Adapter<SavedNewsFeedAdapter.ViewHolder> {

    private Context mContext;
    private Cursor mOriginalCursor;
    private CursorAdapter mCursorAdapter;

    public SavedNewsFeedAdapter(Context tempContext, Cursor tempCursor) {

        mContext = tempContext;
        mOriginalCursor = tempCursor;

        mCursorAdapter = new CursorAdapter(mContext, mOriginalCursor, 0) {

            @Override
            public View newView(Context context, Cursor cursor, ViewGroup parent) {

                return LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.saved_news_feed_item, parent, false);

            }

            @Override
            public void bindView(View view, Context context, Cursor cursor) {

                ViewHolder viewHolder = (ViewHolder) view.getTag();

                int columnIndex = cursor.getColumnIndex(NewsContract.NewsArticle.COLUMN_HEADLINE);
                viewHolder.mTextView_headlines.setText(cursor.getString(columnIndex));
                columnIndex = cursor.getColumnIndex(NewsContract.NewsArticle.COLUMN_TRAILTEXT);
                viewHolder.mTextView_trailText.setText(cursor.getString(columnIndex));

            }

        };


    }

    @Override
    public SavedNewsFeedAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = mCursorAdapter.newView(mContext, mCursorAdapter.getCursor(), parent);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(SavedNewsFeedAdapter.ViewHolder holder, int position) {

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


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView mTextView_headlines;
        public TextView mTextView_trailText;

        public ViewHolder(View itemView) {

            super(itemView);
            mTextView_headlines = (TextView)itemView.findViewById(R.id.savedNewsFeedItem_headlines_textView);
            mTextView_trailText = (TextView)itemView.findViewById(R.id.savedNewsFeedItem_trailText_textView);
            mTextView_headlines.setClickable(true);
            mTextView_headlines.setOnClickListener(this);
            mTextView_trailText.setClickable(true);
            mTextView_trailText.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {

            Cursor tempCursor = mOriginalCursor;
            if ((tempCursor != null ) && (tempCursor.moveToFirst())){

                tempCursor.moveToPosition(getAdapterPosition());

                //TODO handle clicks


            }

        }

    }

    public void swapCursor(Cursor cursor) {

        mCursorAdapter.swapCursor(cursor);

    }

}
