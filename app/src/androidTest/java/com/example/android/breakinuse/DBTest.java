package com.example.android.breakinuse;
import com.example.android.breakinuse.NewsProvider.NewsContract;
import com.example.android.breakinuse.NewsProvider.NewsDBHelper;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;
import android.util.Log;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public class DBTest extends AndroidTestCase {

    private static final String TAG = DBTest.class.getName();

    private void deleteTheDatabase() {
        mContext.deleteDatabase(NewsDBHelper.DATABASE_NAME);
    }

    protected void setUp(){
        deleteTheDatabase();
    }

    public void testCreateDB() throws Throwable{

        final HashSet<String> tableNameHashSet = new HashSet<>();
        tableNameHashSet.add(NewsContract.NewsArticle.TABLE_NAME);
        tableNameHashSet.add(NewsContract.NewsFeed.TABLE_NAME);

        deleteTheDatabase();
        SQLiteDatabase db = (new NewsDBHelper(mContext)).getWritableDatabase();
        assertEquals(true, db.isOpen());

        Cursor cursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type = 'table'",null);
        assertTrue("Error: The DB has not been created correctly.",cursor.moveToFirst());

        do {

            tableNameHashSet.remove(cursor.getString(0));

        } while (cursor.moveToNext());
        assertTrue("Error: The DB was created without the news article and news feed tables.",
                tableNameHashSet.isEmpty());

        cursor = db.rawQuery("PRAGMA table_info (" + NewsContract.NewsFeed.TABLE_NAME + ")", null);
        assertTrue("Error: Unable to query the NewsFeedTable for table info.", cursor.moveToFirst());

        final HashSet<String> newsFeedColumnSet = new HashSet<>();
        newsFeedColumnSet.add(NewsContract.NewsFeed.COLUMN_ARTICLEID);
        newsFeedColumnSet.add(NewsContract.NewsFeed.COLUMN_SECTIONID);
        newsFeedColumnSet.add(NewsContract.NewsFeed.COLUMN_WEBURL);
        newsFeedColumnSet.add(NewsContract.NewsFeed.COLUMN_APIURL);
        newsFeedColumnSet.add(NewsContract.NewsFeed.COLUMN_WEBTITLE);
        newsFeedColumnSet.add(NewsContract.NewsFeed.COLUMN_TRAILTEXT);

        int columnNameIndex = cursor.getColumnIndex("name");
        String columnName;
        do {

            columnName = cursor.getString(columnNameIndex);
            newsFeedColumnSet.remove(columnName);


        } while (cursor.moveToNext());
        assertTrue("Error: The relevant columns haven't been inserted in the NewsFeedTable.",newsFeedColumnSet.isEmpty());

        cursor = db.rawQuery( "PRAGMA table_info (" + NewsContract.NewsArticle.TABLE_NAME +")" , null);
        assertTrue("Error: Unable to query the NewsArticleTable for table info.", cursor.moveToFirst());

        final HashSet<String> newsArticleColumnSet = new HashSet<>();
        newsArticleColumnSet.add(NewsContract.NewsArticle.COLUMN_NEWSFEED_KEY);
        newsArticleColumnSet.add(NewsContract.NewsArticle.COLUMN_WEBURL);
        newsArticleColumnSet.add(NewsContract.NewsArticle.COLUMN_ARTICLEID);
        newsArticleColumnSet.add(NewsContract.NewsArticle.COLUMN_SECTIONID);
        newsArticleColumnSet.add(NewsContract.NewsArticle.COLUMN_HEADLINE);
        newsArticleColumnSet.add(NewsContract.NewsArticle.COLUMN_IMAGESOURCE_HTML);
        newsArticleColumnSet.add(NewsContract.NewsArticle.COLUMN_ARTICLEIMAGE);
        newsArticleColumnSet.add(NewsContract.NewsArticle.COLUMN_ARTICLEIMAGE_URL);
        newsArticleColumnSet.add(NewsContract.NewsArticle.COLUMN_TRAILTEXT);
        newsArticleColumnSet.add(NewsContract.NewsArticle.COLUMN_HTML_BODY);
        newsArticleColumnSet.add(NewsContract.NewsArticle.COLUMN_BYLINE);

        columnNameIndex = cursor.getColumnIndex("name");
        do {

            columnName = cursor.getString(columnNameIndex);
            newsArticleColumnSet.remove(columnName);

        } while (cursor.moveToNext());
        assertTrue("Error: The relevant columns haven't been inserted in the NewsFeedTable.",newsFeedColumnSet.isEmpty());

        db.close();
        cursor.close();
    }

    public void testQueriesDB(){

        SQLiteDatabase db = (new NewsDBHelper(mContext)).getWritableDatabase();
        assertTrue("Error: Unable to open the DB", db.isOpen());

        ContentValues newsFeedTestValues = new ContentValues();
        newsFeedTestValues.put(NewsContract.NewsFeed.COLUMN_ARTICLEID,
                "football/2015/aug/23/arsene-wenger-arsenal-liverpool");
        newsFeedTestValues.put(NewsContract.NewsFeed.COLUMN_SECTIONID, "football");
        newsFeedTestValues.put(NewsContract.NewsFeed.COLUMN_APIURL,
                "http://content.guardianapis.com/football/2015/aug/23/arsene-wenger-arsenal-liverpool");
        newsFeedTestValues.put(NewsContract.NewsFeed.COLUMN_WEBURL,
                "http://www.theguardian.com/football/2015/aug/23/arsene-wenger-arsenal-liverpool");
        newsFeedTestValues.put(NewsContract.NewsFeed.COLUMN_WEBTITLE,
                "Arsène Wenger: Arsenal must ‘play with good pace’ to beat Liverpool");
        newsFeedTestValues.put(NewsContract.NewsFeed.COLUMN_TRAILTEXT,
                "Liverpool have shown greater resilience this season but on their last visit to Arsenal they were whacked 4-1");

        long newsFeedRowID = db.insert(NewsContract.NewsFeed.TABLE_NAME, null, newsFeedTestValues);
        assertTrue(newsFeedRowID != -1);

        Cursor cursor = db.query(NewsContract.NewsFeed.TABLE_NAME, null, null, null, null, null, null);
        assertTrue("Error: Unable to retrieve data from NewsFeedTable", cursor.moveToFirst());

        int columnIndex;
        String columnName, columnValue;

        // Intialising the columnIndex from 1 instead of 0 as the 0th colum is _ID
        // which is not present in the content value set.

        for (columnIndex = 1; columnIndex < cursor.getColumnCount(); ++columnIndex ){

            columnName = cursor.getColumnName(columnIndex);
            columnValue = cursor.getString(columnIndex);
            assertTrue("Error: Unable to retrieve the column from contentValueSet", newsFeedTestValues.containsKey(columnName));
            assertTrue("Error: Unable to match the key (" + columnName + ") with value (" + newsFeedTestValues.get(columnName) + ") pair ",
                    ((String) newsFeedTestValues.get(columnName)).equals(columnValue));

        }
        assertFalse("Error: Multiple rows returned", cursor.moveToNext());

        cursor.moveToFirst();
        columnIndex = cursor.getColumnIndex("_id");
        ContentValues newsArticleTestValues = new ContentValues();
        newsArticleTestValues.put(NewsContract.NewsArticle.COLUMN_NEWSFEED_KEY, cursor.getInt(columnIndex));
        newsArticleTestValues.put(NewsContract.NewsArticle.COLUMN_WEBURL,
                "http://www.theguardian.com/football/2015/aug/23/arsene-wenger-arsenal-liverpool");
        newsArticleTestValues.put(NewsContract.NewsArticle.COLUMN_SECTIONID,
                "football");
        newsArticleTestValues.put(NewsContract.NewsArticle.COLUMN_ARTICLEID,
                "football/2015/aug/23/arsene-wenger-arsenal-liverpool");
        newsArticleTestValues.put(NewsContract.NewsArticle.COLUMN_HEADLINE,
                "Arsène Wenger: Arsenal must ‘play with good pace’ to beat Liverpool");
        newsArticleTestValues.put(NewsContract.NewsArticle.COLUMN_IMAGESOURCE_HTML,
                "<figure class=\\\"element element-image\\\" data-media-id=\\\"e52f4a05ed8b91febd6bc8e4c8a12cf12138888b\\\"> <img src=\\\"http://media.guim.co.uk/e52f4a05ed8b91febd6bc8e4c8a12cf12138888b/0_47_3000_1800/1000.jpg\\\" alt=\\\"Arsène Wenger knows that Liverpool are unlikely to be as obliging as they were last April against Arsenal.\\\" width=\\\"1000\\\" height=\\\"600\\\" class=\\\"gu-image\\\" /> <figcaption> <span class=\\\"element-image__caption\\\">Arsène Wenger knows that Liverpool are unlikely to be as obliging as they were last April against Arsenal.</span> <span class=\\\"element-image__credit\\\">Photograph: David Price/Arsenal FC via Getty Images</span> </figcaption> </figure>");
        newsArticleTestValues.put(NewsContract.NewsArticle.COLUMN_ARTICLEIMAGE_URL,
                "http://media.guim.co.uk/e52f4a05ed8b91febd6bc8e4c8a12cf12138888b/0_47_3000_1800/1000.jpg\\");
        newsArticleTestValues.put(NewsContract.NewsArticle.COLUMN_ARTICLEIMAGE, 101010);
        newsArticleTestValues.put(NewsContract.NewsArticle.COLUMN_TRAILTEXT,
                "Liverpool have shown greater resilience this season but on their last visit to Arsenal they were whacked 4-1");
        newsArticleTestValues.put(NewsContract.NewsArticle.COLUMN_HTML_BODY,
                "<p>There is nothing quite like the cauldron of a match day to expose the gossamer fragility of the football manager’s lot. When Arsenal last hosted Liverpool back in April, the scene played out in the theatre that hosts the post-match soliloquies epitomised the extremes that come with the territory.</p> <p>Enter Brendan Rodgers, accompanied by some notably discordant music. On the field <a href=\\\"http://www.theguardian.com/football/2015/apr/04/arsenal-liverpool-premier-league-match-report\\\" title=\\\"\\\">his team had been whacked 4-1</a>, lurching painfully away from the Champions League reckoning. Off it the club was reeling from the opening salvo of Raheem Sterling’s exit strategy. That <a href=\\\"http://www.theguardian.com/football/2015/apr/01/raheem-sterling-not-money-grabbing-contract-liverpool\\\" title=\\\"\\\">bizarrely timed BBC interview</a>, which bought the player’s future into sharp focus, was the talk of Merseyside and beyond. All in all it was a tough old afternoon for Rodgers.</p> <p>Arsène Wenger could empathise. He, too, has had his moments over a long career in which problems pile up to the point where it is difficult to breathe calmly and think with clarity. Those are the points when the imperative is to somehow hold your nerve while those around you tug at every loose fray. It had been only a few months previously that <a href=\\\"http://www.theguardian.com/football/2014/dec/07/arsenal-arsene-wenger-booed-train-stoke\\\" title=\\\"\\\">Wenger had been heckled by his own supporters at the railway station after a defeat at Stoke</a>. But here he was, basking in the glow of one of Arsenal’s most convincing performances, a classic example of Wenger’s vision of joyful, fast, fluid football. It was, as Wenger put it proudly afterwards, “the game we love”.</p> <p>Aesthetics apart, it was also important in that it crowned a long winning run at the Emirates Stadium. That attacking masterclass against Liverpool was an 11th successive victory in domestic football for Arsenal – their best home form in aeons.</p> <p>Since then? It has not escaped Wenger’s attention in the embryonic stages of this new season that Arsenal’s home potency has been diluted. Results since that Liverpool game, spanning the end of the last campaign and start of this, do not make pretty reading. Last season finished up with goalless draws against <a href=\\\"http://www.theguardian.com/football/2015/apr/26/arsenal-chelsea-premier-league-match-report\\\" title=\\\"\\\">Chelsea</a> and <a href=\\\"http://www.theguardian.com/football/2015/may/20/arsenal-sunderland-premier-league-match-report\\\" title=\\\"\\\">Sunderland</a>, a smash and grab three points for <a href=\\\"http://www.theguardian.com/football/2015/may/11/arsenal-swansea-premier-league-match-report\\\" title=\\\"\\\">Swansea</a>, and the only bright spot was a handsome win against a <a href=\\\"http://www.theguardian.com/football/2015/may/24/arsenal-west-bromwich-albion-premier-league-match-report\\\" title=\\\"\\\">West Brom</a> team that were halfway to the beach. This term began with a subdued and error-strewn <a href=\\\"http://www.theguardian.com/football/2015/aug/09/arsenal-west-ham-premier-league-match-report\\\" title=\\\"\\\">loss against West Ham</a>.</p> <p>Wenger wants the pattern to change back – and quickly. “You need to be strong at home that is for sure,” he says. “If you want to win the championship you need to win your home games. It can happen that you lose the odd game but overall you need home strength.”</p> <p>All those games that frustrated Arsenal stemmed from a familiar model of Wenger’s men passing their way into cul-de-sacs formed by a well organised and diligent opposition rearguard. Chelsea, Swansea, West Ham and to an extent desperate Sunderland pulled off the same trick. Swamp the area in front of the final third Arsenal are trying to pick holes through, sit tight, and take it from there.</p> <p>“Against teams who came only to defend, we didn’t find the goal,” Wenger said. “But it happens to all the other teams as well. Normally you would think over 19 games you can sometimes be unlucky once or twice. Most of the time if you really dominate the games you will win.” The key to avoid falling into a similar trap when Liverpool visit on Monday night comes down to one critical aspect. “Play with good pace,” Wenger said.</p> <p>Liverpool are unlikely to be as obliging as they were last April, when Rodgers had a final stab at playing three at the back to see his defence comprehensively dismantled. There is a definite sense that they have started this campaign with more emphasis on resilience. With the fixture list putting up two assignments with bad memories – <a href=\\\"http://www.theguardian.com/football/2015/may/24/stoke-city-liverpool-premier-league-match-report\\\" title=\\\"\\\">Stoke</a> and Arsenal away where they conceded a combined 10 goals – before the international break gives Rodgers good reason to play more safely than he might normally want to. <a href=\\\"http://www.theguardian.com/football/2015/aug/21/jordan-henderson-liverpool-arsenal\\\" title=\\\"\\\">Jordan Henderson</a> has been impressive in the anchoring midfield role, and his fitness will be a big factor in Liverpool’s approach.</p> <p>Rodgers’ team are a work in progress as he tries to integrate the new signings, and build sparks and connections within the group. Although Wenger has set great store on the “cohesion” that comes with his squad being stable and largely unchanged, he is still tinkering to find the ideal balance. He seems undecided about whether it is best to play Santi Cazorla or Aaron Ramsey in central midfield (with the other positioned wider). “It is tricky,” Wenger said.</p> <aside class=\\\"element element-rich-link\\\"> <p> <span>Related: </span><a href=\\\"http://www.theguardian.com/football/2015/aug/22/arsene-wenger-arsenal-capitalise-talent-turnover\\\">Arsène Wenger says competition at ‘big, big clubs’ is good for Arsenal</a> </p> </aside>  <p>“Santi is an important player in the buildup of our game. He is naturally a guy who brings fluidity, and gets you out of tight situations.” Sometimes, though Wenger wants more “physical power in the centre of the park” which is where Ramsey gets the nod.</p> <p>Although he says it is an “impossible job” to keep all the options who like to play centrally happy, Wenger is glad he is not having to deal with too much flux in his team. He does not Rodgers the job of rebuilding with Luis Suárez and Raheem Sterling prised away in successive summers.</p> <p>“Let’s remember two years ago with Suárez, Sturridge and Sterling they scored over a hundred goals. To score more than 100 goals in the Premier League, you need special quality to do that. Sterling was part of that. I personally rate Raheem Sterling. We will see that in the longer term. He’s a quality player. But they bought Firmino. We will see what he will produce. He is a similar player.”</p> <p>Does he have some empathy, having himself endured the picking off of key players in the past? “They refused to sell us Suárez so I cannot feel too much sympathy for them,” Wenger said with a wry smile.</p>");
        newsArticleTestValues.put(NewsContract.NewsArticle.COLUMN_BYLINE,
                "Amy Lawrence");

        long newsArticleRowID = db.insert(NewsContract.NewsArticle.TABLE_NAME, null, newsArticleTestValues);
        assertTrue(newsArticleRowID != -1);

        cursor = db.query(NewsContract.NewsArticle.TABLE_NAME,null,null,null,null,null,null);
        assertTrue("Error: Unable to retrieve data from NewsArticleTable", cursor.moveToFirst());

        String error = "testInsertReadDb weatherEntry failed to validate";
        Set<Map.Entry<String, Object>> valueSet = newsArticleTestValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {

            columnName = entry.getKey();
            int idx = cursor.getColumnIndex(columnName);
            assertFalse("Column '" + columnName + "' not found. " + error, idx == -1);
            String expectedValue = entry.getValue().toString();
            assertEquals("Value '" + entry.getValue().toString() +
                    "' did not match the expected value '" +
                    expectedValue + "'. " + error, expectedValue, cursor.getString(idx));

        }
        assertFalse("Error: Multiple rows returned", cursor.moveToNext());

        db.close();
        cursor.close();

    }

}
