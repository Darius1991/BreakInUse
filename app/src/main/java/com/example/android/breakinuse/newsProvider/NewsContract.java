package com.example.android.breakinuse.newsProvider;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

public class NewsContract {

    public static final String CONTENT_AUTHORITY = "com.example.android.breakinuse";
    public static final String PATH_NEWSFEED_READ = "readNewsFeed";
    public static final String PATH_NEWSARTICLE = "newsArticle";
    public static final String PATH_NEWSFEED_WRITE = "writeNewsFeed";
    public static final String PATH_FAVOURITE_NEWSFEED_READ = "readFavouriteNewsFeed";
    public static final String PATH_FAVOURITE_NEWSFEED_WRITE = "writeFavouriteNewsFeed";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);


    public static final class NewsArticle implements BaseColumns{


        public static final Uri NEWSARTICLE_URI = BASE_CONTENT_URI
                                                        .buildUpon()
                                                        .appendPath(PATH_NEWSARTICLE)
                                                        .build();

        public static final String TABLE_NAME = "NewsArticle";
        public static final String COLUMN_NEWSFEED_KEY = "NewsFeedItem_Key";
        public static final String COLUMN_WEBURL = "WebURL";
        public static final String COLUMN_ARTICLEID = "ArticleID";
        public static final String COLUMN_SECTIONID = "SectionID";
        public static final String COLUMN_HEADLINE = "Headline";
        public static final String COLUMN_TRAILTEXT = "TrailText";
        public static final String COLUMN_HTML_BODY = "HTMLBody";
        public static final String COLUMN_BYLINE = "Byline";
        public static final String COLUMN_IMAGEURL = "ImageURL";
        public static final String COLUMN_DOWNLOADFLAG = "isDownloadedInTable";
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
                                                    + "/" + CONTENT_AUTHORITY
                                                    + "/" + PATH_NEWSARTICLE;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
                                                        + "/" + CONTENT_AUTHORITY
                                                        + "/" + PATH_NEWSARTICLE;

        public static Uri buildNewsArticleUri(long id){

            return ContentUris.withAppendedId(NEWSARTICLE_URI, id);

        }

        public static Uri buildNewsArticleUri(String articleID){

            return NEWSARTICLE_URI
                    .buildUpon()
                    .appendPath(COLUMN_ARTICLEID)
                    .appendPath(articleID)
                    .build();

        }

        public static String getArticleIDFromURI(Uri uri){

            return uri.getPathSegments().get(2);

        }

    }

    public static final class NewsFeed implements BaseColumns{


        public static final Uri NEWSFEED_READURI = BASE_CONTENT_URI
                                                    .buildUpon()
                                                    .appendPath(PATH_NEWSFEED_READ)
                                                    .build();
        public static final Uri NEWSFEED_WRITEURI = BASE_CONTENT_URI
                                                        .buildUpon()
                                                        .appendPath(PATH_NEWSFEED_WRITE)
                                                        .build();
        public static final Uri FAVOURITE_NEWSFEED_READURI = BASE_CONTENT_URI
                                                                .buildUpon()
                                                                .appendPath(PATH_FAVOURITE_NEWSFEED_READ)
                                                                .build();
        public static final Uri FAVOURITE_NEWSFEED_WRITEURI = BASE_CONTENT_URI
                                                                .buildUpon()
                                                                .appendPath(PATH_FAVOURITE_NEWSFEED_WRITE)
                                                                .build();

        public static final String TABLE_NAME = "NewsFeed";
        public static final String COLUMN_ARTICLEID = "ArticleID";
        public static final String COLUMN_SECTIONID = "SectionID";
        public static final String COLUMN_APIURL = "APIURL";
        public static final String COLUMN_WEBURL = "WebURL";
        public static final String COLUMN_IMAGEURL = "ImageURL";
        public static final String COLUMN_WEBTITLE = "WebTitle";
        public static final String COLUMN_TRAILTEXT = "TrailText";
        public final static String COLUMN_SAVEDFLAG = "isSavedInNewsArticleTable";
        public final static String COLUMN_PUBLISHDATE = "articlePublishDate";
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
                                                    + "/" + CONTENT_AUTHORITY
                                                    + "/" + PATH_NEWSFEED_READ;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
                                                        + "/" + CONTENT_AUTHORITY
                                                        + "/" + PATH_NEWSFEED_READ;

        public static Uri buildNewsFeedUri(long id){

            return ContentUris.withAppendedId(NEWSFEED_READURI, id);

        }

        public static Uri buildNewsFeedUri(String articleID){

            return NEWSFEED_READURI
                    .buildUpon()
                    .appendPath(COLUMN_ARTICLEID)
                    .appendPath(articleID)
                    .build();

        }

        public static String getArticleIDFromURI(Uri uri){

            return uri.getPathSegments().get(2);

        }

    }

}
