package com.example.android.breakinuse.NewsProvider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

public class NewsProvider extends ContentProvider {

    private static final String TAG = NewsProvider.class.getName();
    private NewsDBHelper mNewsDBHelper;
    public static final int NEWS_FEED = 100;
    public static final int NEWS_ARTICLE = 101;
    public static final int NEWS_FEED_WITH_ARTICLEID = 102;
    public static final int NEWS_ARTICLE_WITH_ARTICLEID = 103;
    private SQLiteQueryBuilder mQueryBuilder;
    private UriMatcher mUriMatcher;

    @Override
    public boolean onCreate() {
        mQueryBuilder = new SQLiteQueryBuilder();
        mNewsDBHelper = new NewsDBHelper(getContext());
        mUriMatcher = buildUriMatcher();
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        final int uriMatchResult = mUriMatcher.match(uri);
        Cursor cursor;

        switch (uriMatchResult){

            case NEWS_FEED:

                cursor = getNewsFeedCursor(projection, selection, selectionArgs, sortOrder);
                break;

            case NEWS_FEED_WITH_ARTICLEID:

                cursor = getNewsFeedWithArticleIDCursor(uri, projection, sortOrder);
                break;

            case NEWS_ARTICLE:

                cursor = getNewsArticleCursor(projection, selection, selectionArgs, sortOrder);
                break;

            case NEWS_ARTICLE_WITH_ARTICLEID:

                cursor = getNewsArticleWithArticleIDCursor(uri, projection, sortOrder);
                break;

            default:

                throw new UnsupportedOperationException("Error: Unknown URI - " + uri);

        }

        if (cursor != null) {

            cursor.setNotificationUri(getContext().getContentResolver(),uri);

        }
        return cursor;

    }

    private Cursor getNewsFeedCursor(String[] projection, String selection,
                                     String[] selectionArgs, String sortOrder){

        mQueryBuilder.setTables(NewsContract.NewsFeed.TABLE_NAME);
        return mQueryBuilder.query(mNewsDBHelper.getReadableDatabase(),projection,selection,
                                    selectionArgs,null,null,sortOrder);

    }

    private Cursor getNewsFeedWithArticleIDCursor(Uri uri, String[] projection, String sortOrder){

        String[] articleID = new String[1];
        articleID[0] = NewsContract.NewsFeed.getArticleIDFromURI(uri);
        String filter = NewsContract.NewsFeed.COLUMN_ARTICLEID + " = ? ";
        mQueryBuilder.setTables(NewsContract.NewsFeed.TABLE_NAME);
        return mQueryBuilder.query(mNewsDBHelper.getReadableDatabase(),projection,filter,articleID,
                                    null,null,sortOrder);

    }

    private Cursor getNewsArticleCursor(String[] projection, String selection,
                                        String[] selectionArgs, String sortOrder){

        mQueryBuilder.setTables(NewsContract.NewsArticle.TABLE_NAME);
        return mQueryBuilder.query(mNewsDBHelper.getReadableDatabase(),projection,selection,
                                    selectionArgs,null,null,sortOrder);
    }

    private Cursor getNewsArticleWithArticleIDCursor(Uri uri, String[] projection, String sortOrder){

        String[] articleID = {NewsContract.NewsArticle.getArticleIDFromURI(uri)};
        String filter = NewsContract.NewsArticle.TABLE_NAME + "." + NewsContract.NewsArticle.COLUMN_ARTICLEID + " = ? ";
        mQueryBuilder.setTables(NewsContract.NewsArticle.TABLE_NAME);
        return mQueryBuilder.query(mNewsDBHelper.getReadableDatabase(),projection,filter,articleID,
                                    null,null,sortOrder);

    }

    @Override
    public String getType(Uri uri) {

        final int matchResult =  mUriMatcher.match(uri);
        String contentType;

        switch (matchResult){

            case NEWS_FEED:

                contentType = NewsContract.NewsFeed.CONTENT_TYPE;
                break;

            case NEWS_FEED_WITH_ARTICLEID:

                contentType = NewsContract.NewsFeed.CONTENT_ITEM_TYPE;
                break;

            case NEWS_ARTICLE:

                contentType = NewsContract.NewsArticle.CONTENT_TYPE;
                break;

            case NEWS_ARTICLE_WITH_ARTICLEID:

                contentType = NewsContract.NewsArticle.CONTENT_ITEM_TYPE;
                break;

            default:

                throw new UnsupportedOperationException("Unkwown URI - " + uri);

        }
        return contentType;

    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {

        final int matchResult = mUriMatcher.match(uri);
        SQLiteDatabase db = mNewsDBHelper.getWritableDatabase();
        long rowID;
        Uri returnUri;

        switch(matchResult){

            case NEWS_FEED:

                Log.d(TAG,String.valueOf(db.isOpen()));
                rowID = db.insertOrThrow(NewsContract.NewsFeed.TABLE_NAME, null, values);
                if (rowID != -1){

                    returnUri = NewsContract.NewsFeed.buildNewsFeedUri(rowID);

                } else {

                    throw new android.database.SQLException("Failed to insert row into " + uri);

                }
                break;

            case NEWS_ARTICLE:

                rowID = db.insertOrThrow(NewsContract.NewsArticle.TABLE_NAME, null, values);
                if (rowID != -1) {

                    returnUri = NewsContract.NewsArticle.buildNewsArticleUri(rowID);

                } else {

                    throw new android.database.SQLException("Failed to insert row into " + uri);

                }
                break;

            default:

                throw new UnsupportedOperationException("Unkwown URI - " + uri);

        }

        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        final int matchResult = mUriMatcher.match(uri);
        SQLiteDatabase db = mNewsDBHelper.getWritableDatabase();
        int rowsDeleted;
        if (selection == null){

            selection = "1";

        }

        switch(matchResult){

            case NEWS_FEED:

                rowsDeleted = db.delete(NewsContract.NewsFeed.TABLE_NAME, selection, selectionArgs);
                break;

            case NEWS_FEED_WITH_ARTICLEID:

                String filter = NewsContract.NewsFeed.COLUMN_ARTICLEID + " = ? ";
                String[] articleID = new String[1];
                articleID[0] = NewsContract.NewsFeed.getArticleIDFromURI(uri);
                rowsDeleted = db.delete(NewsContract.NewsFeed.TABLE_NAME, filter, articleID);
                break;

            case NEWS_ARTICLE:

                rowsDeleted = db.delete(NewsContract.NewsArticle.TABLE_NAME, selection, selectionArgs);
                break;

            case NEWS_ARTICLE_WITH_ARTICLEID:

                String filter_article = NewsContract.NewsArticle.COLUMN_ARTICLEID + " = ? ";
                String[] articleID_article = new String[1];
                articleID_article[0] = NewsContract.NewsArticle.getArticleIDFromURI(uri);
                rowsDeleted = db.delete(NewsContract.NewsArticle.TABLE_NAME, filter_article, articleID_article);
                break;

            default:

                throw new UnsupportedOperationException("Unkwown URI - " + uri);

        }

        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        final int matchResult = mUriMatcher.match(uri);
        SQLiteDatabase db = mNewsDBHelper.getWritableDatabase();
        int rowsUpdated;

        switch (matchResult){

            case NEWS_FEED:

                rowsUpdated = db.update(NewsContract.NewsFeed.TABLE_NAME,values,selection,selectionArgs);
                break;

            case NEWS_FEED_WITH_ARTICLEID:

                String filter = NewsContract.NewsFeed.COLUMN_ARTICLEID + " = ? ";
                String[] articleID = new String[1];
                articleID[0] = NewsContract.NewsFeed.getArticleIDFromURI(uri);
                rowsUpdated = db.update(NewsContract.NewsFeed.TABLE_NAME, values, filter, articleID);
                break;

            case NEWS_ARTICLE:

                rowsUpdated = db.update(NewsContract.NewsArticle.TABLE_NAME,values,selection,selectionArgs);
                break;

            case NEWS_ARTICLE_WITH_ARTICLEID:

                String filter_article = NewsContract.NewsArticle.COLUMN_ARTICLEID + " = ? ";
                String[] articleID_article = new String[1];
                articleID_article[0] = NewsContract.NewsArticle.getArticleIDFromURI(uri);
                rowsUpdated = db.update(NewsContract.NewsArticle.TABLE_NAME, values, filter_article, articleID_article);
                break;

            default:

                throw new UnsupportedOperationException("Unkwown URI - " + uri);

        }

        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    public static UriMatcher buildUriMatcher(){

        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(NewsContract.CONTENT_AUTHORITY,NewsContract.PATH_NEWSFEED,NEWS_FEED);
        uriMatcher.addURI(NewsContract.CONTENT_AUTHORITY,
                NewsContract.PATH_NEWSFEED + "/" + NewsContract.NewsFeed.COLUMN_ARTICLEID + "/*",
                NEWS_FEED_WITH_ARTICLEID);
        uriMatcher.addURI(NewsContract.CONTENT_AUTHORITY,NewsContract.PATH_NEWSARTICLE,NEWS_ARTICLE);
        uriMatcher.addURI(NewsContract.CONTENT_AUTHORITY,
                NewsContract.PATH_NEWSARTICLE + "/" + NewsContract.NewsArticle.COLUMN_ARTICLEID + "/*",
                NEWS_ARTICLE_WITH_ARTICLEID);
        return uriMatcher;

    }

    @Override
    public int bulkInsert(Uri uri, @NonNull ContentValues[] values) {

        final int matchResult = mUriMatcher.match(uri);
        SQLiteDatabase db = mNewsDBHelper.getWritableDatabase();
        int rowsInserted;

        switch (matchResult){

            case NEWS_FEED:

                db.beginTransaction();
                rowsInserted = 0;
                try {

                    for (ContentValues value : values) {

                        long _id = db.insert(NewsContract.NewsFeed.TABLE_NAME, null, value);
                        if (_id != -1) {

                            rowsInserted++;

                        }

                    }

                    db.setTransactionSuccessful();

                } finally {

                    db.endTransaction();

                }
                getContext().getContentResolver().notifyChange(uri, null);
                return rowsInserted;

            case NEWS_ARTICLE:

                db.beginTransaction();
                rowsInserted = 0;
                try {

                    for (ContentValues value : values) {

                        long _id = db.insert(NewsContract.NewsArticle.TABLE_NAME, null, value);
                        if (_id != -1) {

                            rowsInserted++;

                        }

                    }

                    db.setTransactionSuccessful();

                } finally {

                    db.endTransaction();

                }
                getContext().getContentResolver().notifyChange(uri, null);
                return rowsInserted;

            default:

                return super.bulkInsert(uri, values);


        }

    }
}
