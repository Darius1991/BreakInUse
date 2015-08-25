package com.example.android.breakinuse;

import android.net.Uri;
import android.test.AndroidTestCase;
import android.util.Log;

import com.example.android.breakinuse.NewsProvider.NewsContract;

public class ContentProviderTest extends AndroidTestCase {

    private static final String TAG = ContentProviderTest.class.getName();

    public void testNewsContract(){

        final String articleIDTest = "commentisfree/2015/aug/25/silence-in-the-catholic-church-may-be-its-weapon-of-self-destruction";
        final long idTest = 1123581321;
        Uri newsArticleURI_articleID = NewsContract.NewsArticle.buildNewsArticleUri(articleIDTest);
        Uri newsArticleURI_ID = NewsContract.NewsArticle.buildNewsArticleUri(idTest);

        assertNotNull("Error: Unable to read the newsArticleURI_articleID correctly", newsArticleURI_articleID);
        assertEquals("Error: Unable to match the newsArtileURIs",
                newsArticleURI_articleID.toString(),
                "content://com.example.android.breakinuse/newsArticle?ArticleID=commentisfree%2F2015%2Faug%2F25%2Fsilence-in-the-catholic-church-may-be-its-weapon-of-self-destruction");
        assertTrue("Error: Unable to match the articleIDs",
                NewsContract.NewsArticle.getArticleIDFromURI(newsArticleURI_articleID).equals(articleIDTest));
        assertNotNull("Error: Unable to read the newsArticleURI_ID correctly", newsArticleURI_ID);
        assertEquals("Error: Unable to match the newsArtileURIs",
                newsArticleURI_ID.toString(),
                "content://com.example.android.breakinuse/newsArticle/1123581321");

        Uri newsFeedURI_articleID = NewsContract.NewsFeed.buildNewsFeedUri(articleIDTest);
        Uri newsFeedURI_ID = NewsContract.NewsFeed.buildNewsFeedUri(idTest);

        assertNotNull("Error: Unable to read the newsFeedURI_articleID correctly", newsFeedURI_articleID);
        assertEquals("Error: Unable to match the newsFeedURIs",
                newsFeedURI_articleID.toString(),
                "content://com.example.android.breakinuse/newsFeed?ArticleID=commentisfree%2F2015%2Faug%2F25%2Fsilence-in-the-catholic-church-may-be-its-weapon-of-self-destruction");
        assertTrue("Error: Unable to match the articleIDs",
                NewsContract.NewsFeed.getArticleIDFromURI(newsFeedURI_articleID).equals(articleIDTest));
        assertNotNull("Error: Unable to read the newsFeedURI_ID correctly", newsFeedURI_ID);
        assertEquals("Error: Unable to match the newsArtileURIs",
                newsFeedURI_ID.toString(),
                "content://com.example.android.breakinuse/newsFeed/1123581321");

    }

}
