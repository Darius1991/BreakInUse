package com.example.android.breakinuse.NewsProvider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

public class NewsProvider extends ContentProvider {

    private static final String TAG = NewsProvider.class.getName();
    private static NewsDBHelper mNewsDBHelper;
    public static final int NEWS_FEED = 100;
    public static final int NEWS_ARTICLE = 101;
    public static final int NEWS_FEED_WITH_ARTICLEID = 102;
    public static final int NEWS_ARTICLE_WITH_ARTICLEID = 103;
    private static SQLiteQueryBuilder mQueryBuilder;
    private static UriMatcher mUriMatcher;

    @Override
    public boolean onCreate() {
        mQueryBuilder = new SQLiteQueryBuilder();
        mNewsDBHelper = new NewsDBHelper(getContext());
        mUriMatcher = buildUriMatcher();
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        final int uriMatchResult = mUriMatcher.match(uri);
        Cursor cursor;

        switch (uriMatchResult){

            case NEWS_FEED:

                cursor = getNewsFeedCursor(uri, projection, selection, selectionArgs, sortOrder);
                break;

            case NEWS_FEED_WITH_ARTICLEID:

                cursor = getNewsFeedWithArticleIDCursor(uri, projection, selection, selectionArgs, sortOrder);
                break;

            case NEWS_ARTICLE:

                cursor = getNewsArticleCursor(uri, projection, selection, selectionArgs, sortOrder);
                break;

            case NEWS_ARTICLE_WITH_ARTICLEID:

                cursor = getNewsArticleWithArticleIDCursor(uri, projection, selection, selectionArgs, sortOrder);
                break;

            default:

                throw new UnsupportedOperationException("Error: Unknown URI - " + uri);

        }

        if (cursor != null) {
            cursor.setNotificationUri(getContext().getContentResolver(),uri);
        }
        return cursor;

    }

    private Cursor getNewsFeedCursor(Uri uri, String[] projection, String selection,
                                     String[] selectionArgs, String sortOrder){

        mQueryBuilder.setTables(NewsContract.NewsFeed.TABLE_NAME);
        return mQueryBuilder.query(mNewsDBHelper.getReadableDatabase(),projection,selection,
                                    selectionArgs,null,null,sortOrder);

    }

    private Cursor getNewsFeedWithArticleIDCursor(Uri uri, String[] projection, String selection,
                                                  String[] selectionArgs, String sortOrder){

        String[] articleID = new String[1];
        articleID[0] = NewsContract.NewsFeed.getArticleIDFromURI(uri);
        String filter = NewsContract.NewsFeed.COLUMN_ARTICLEID + " = ? ";
        mQueryBuilder.setTables(NewsContract.NewsFeed.TABLE_NAME);
        return mQueryBuilder.query(mNewsDBHelper.getReadableDatabase(),projection,filter,articleID,
                                    null,null,sortOrder);

    }

    private Cursor getNewsArticleCursor(Uri uri, String[] projection, String selection,
                                        String[] selectionArgs, String sortOrder){

        mQueryBuilder.setTables(NewsContract.NewsArticle.TABLE_NAME);
        return mQueryBuilder.query(mNewsDBHelper.getReadableDatabase(),projection,selection,
                                    selectionArgs,null,null,sortOrder);
    }

    private Cursor getNewsArticleWithArticleIDCursor(Uri uri, String[] projection, String selection,
                                                     String[] selectionArgs, String sortOrder){

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
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
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
}
