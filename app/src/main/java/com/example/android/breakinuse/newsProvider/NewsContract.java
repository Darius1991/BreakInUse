package com.example.android.breakinuse.newsProvider;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

public class NewsContract {

    public static final String CONTENT_AUTHORITY = "com.example.android.breakinuse";
    public static final String PATH_NEWSFEED = "newsFeed";
    public static final String PATH_NEWSARTICLE = "newsArticle";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);



    public static final class NewsArticle implements BaseColumns{


        public static final Uri CONTENT_URI = BASE_CONTENT_URI
                .buildUpon()
                .appendPath(PATH_NEWSARTICLE)
                .build();
        public static final String TABLE_NAME = "NewsArticle";
        public static final String COLUMN_NEWSFEED_KEY = "NewsFeedItem_Key";
        public static final String COLUMN_WEBURL = "WebURL";
        public static final String COLUMN_ARTICLEID = "ArticleID";
        public static final String COLUMN_SECTIONID = "SectionID";
        public static final String COLUMN_HEADLINE = "Headline";
        public static final String COLUMN_IMAGESOURCE_HTML = "ImageSource";
        public static final String COLUMN_ARTICLEIMAGE_URL = "ArticleImageURL";
        public static final String COLUMN_ARTICLEIMAGE = "ArticleImage";
        public static final String COLUMN_TRAILTEXT = "TrailText";
        public static final String COLUMN_HTML_BODY = "HTMLBody";
        public static final String COLUMN_BYLINE = "Byline";
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
                                                    + "/" + CONTENT_AUTHORITY
                                                    + "/" + PATH_NEWSARTICLE;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
                                                        + "/" + CONTENT_AUTHORITY
                                                        + "/" + PATH_NEWSARTICLE;

        public static Uri buildNewsArticleUri(long id){

            return ContentUris.withAppendedId(CONTENT_URI, id);

        }

        public static Uri buildNewsArticleUri(String articleID){

            return CONTENT_URI
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


        public static final Uri CONTENT_URI = BASE_CONTENT_URI
                                                .buildUpon()
                                                .appendPath(PATH_NEWSFEED)
                                                .build();
        public static final String TABLE_NAME = "NewsFeed";
        public static final String COLUMN_ARTICLEID = "ArticleID";
        public static final String COLUMN_SECTIONID = "SectionID";
        public static final String COLUMN_APIURL = "APIURL";
        public static final String COLUMN_WEBURL = "WebURL";
        public static final String COLUMN_WEBTITLE = "WebTitle";
        public static final String COLUMN_TRAILTEXT = "TrailText";
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
                                                    + "/" + CONTENT_AUTHORITY
                                                    + "/" + PATH_NEWSFEED ;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
                                                        + "/" + CONTENT_AUTHORITY
                                                        + "/" + PATH_NEWSFEED ;

        public static Uri buildNewsFeedUri(long id){

            return ContentUris.withAppendedId(CONTENT_URI, id);

        }

        public static Uri buildNewsFeedUri(String articleID){

            return CONTENT_URI
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
