package com.example.android.breakinuse.NewsProvider;

import android.provider.BaseColumns;

public class NewsContract {

    public static final class NewsArticle implements BaseColumns{

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

    }

    public static final class NewsFeed implements BaseColumns{

        public static final String TABLE_NAME = "NewsFeed";
        public static final String COLUMN_ARTICLEID = "ArticleID";
        public static final String COLUMN_SECTIONID = "SectionID";
        public static final String COLUMN_APIURL = "APIURL";
        public static final String COLUMN_WEBURL = "WebURL";
        public static final String COLUMN_WEBTITLE = "WebTitle";
        public static final String COLUMN_TRAILTEXT = "TrailText";

    }

}
