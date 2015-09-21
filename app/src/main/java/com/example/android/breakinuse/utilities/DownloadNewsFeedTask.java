package com.example.android.breakinuse.utilities;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;

/**
 * Created by darius on 21/9/15.
 */
public class DownloadNewsFeedTask extends AsyncTask<Void,Void,Void> {

    private Cursor mCursor;
    private Context mContext;

    public DownloadNewsFeedTask(Context context){

        mContext = context;

    }

    @Override
    protected Void doInBackground(Void... params) {

//        mCursor = mContext.getContentResolver().query();

        return null;

    }

    @Override
    protected void onPostExecute(Void aVoid) {

        super.onPostExecute(aVoid);
        if (mCursor != null){

            mCursor.close();

        }

    }

}
