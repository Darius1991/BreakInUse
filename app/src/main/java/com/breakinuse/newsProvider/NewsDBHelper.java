package com.breakinuse.newsProvider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class NewsDBHelper extends SQLiteOpenHelper {

    private final String TAG  = NewsDBHelper.class.getName();
    private static final int DATABSE_VERSION = 2;
    public static final String DATABASE_NAME = "breakInUse.db";

    public NewsDBHelper(Context context){
        super(context, DATABASE_NAME, null, DATABSE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        final String SQL_CREATE_NEWSFEED_TABLE = "CREATE TABLE " + NewsContract.NewsFeed.TABLE_NAME
                                                    + " ("
                                                    + NewsContract.NewsFeed._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                                                    + NewsContract.NewsFeed.COLUMN_ARTICLEID + " TEXT NOT NULL,"
                                                    + NewsContract.NewsFeed.COLUMN_SECTIONID + " TEXT NOT NULL,"
                                                    + NewsContract.NewsFeed.COLUMN_APIURL + " TEXT NOT NULL,"
                                                    + NewsContract.NewsFeed.COLUMN_WEBURL + " TEXT NOT NULL,"
                                                    + NewsContract.NewsFeed.COLUMN_WEBTITLE + " TEXT NOT NULL,"
                                                    + NewsContract.NewsFeed.COLUMN_TRAILTEXT + " TEXT NOT NULL,"
                                                    + NewsContract.NewsFeed.COLUMN_IMAGEURL + " TEXT NOT NULL,"
                                                    + NewsContract.NewsFeed.COLUMN_SAVEDFLAG + " TEXT NOT NULL,"
                                                    + NewsContract.NewsFeed.COLUMN_PUBLISHDATE + " TEXT NOT NULL,"
                                                    + NewsContract.NewsFeed.COLUMN_THUMBNAILURL + " TEXT NOT NULL,"
                                                    + NewsContract.NewsFeed.COLUMN_BYLINE + " TEXT NOT NULL,"
                                                    + "UNIQUE (" + NewsContract.NewsFeed.COLUMN_ARTICLEID
                                                    + ") ON CONFLICT REPLACE);";

        final String SQL_CREATE_NEWSARTICLE_TABLE = "CREATE TABLE " + NewsContract.NewsArticle.TABLE_NAME
                                                        + " ("
                                                        + NewsContract.NewsArticle._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                                                        + NewsContract.NewsArticle.COLUMN_NEWSFEED_KEY + " INTEGER NOT NULL,"
                                                        + NewsContract.NewsArticle.COLUMN_WEBURL + " TEXT NOT NULL,"
                                                        + NewsContract.NewsArticle.COLUMN_ARTICLEID + " TEXT NOT NULL,"
                                                        + NewsContract.NewsArticle.COLUMN_SECTIONID + " TEXT NOT NULL,"
                                                        + NewsContract.NewsArticle.COLUMN_HEADLINE + " TEXT NOT NULL,"
                                                        + NewsContract.NewsArticle.COLUMN_TRAILTEXT + " TEXT NOT NULL,"
                                                        + NewsContract.NewsArticle.COLUMN_IMAGEURL + " TEXT NOT NULL,"
                                                        + NewsContract.NewsArticle.COLUMN_HTML_BODY + " TEXT NOT NULL,"
                                                        + NewsContract.NewsArticle.COLUMN_BYLINE + " TEXT NOT NULL,"
                                                        + NewsContract.NewsArticle.COLUMN_DOWNLOADFLAG + " TEXT NOT NULL,"
                                                        + NewsContract.NewsArticle.COLUMN_THUMBNAILURL + " TEXT NOT NULL,"
                                                        + NewsContract.NewsArticle.COLUMN_APIURL + " TEXT NOT NULL,"
                                                        + "FOREIGN KEY (" + NewsContract.NewsArticle.COLUMN_NEWSFEED_KEY
                                                        + ") REFERENCES "
                                                        + NewsContract.NewsFeed.TABLE_NAME + " ("
                                                        + NewsContract.NewsFeed._ID + "),"
                                                        + "UNIQUE (" + NewsContract.NewsArticle.COLUMN_ARTICLEID
                                                        + ") ON CONFLICT REPLACE);";

        db.execSQL(SQL_CREATE_NEWSFEED_TABLE);
        db.execSQL(SQL_CREATE_NEWSARTICLE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS" + NewsContract.NewsFeed.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS" + NewsContract.NewsArticle.TABLE_NAME);
        onCreate(db);

    }
}
