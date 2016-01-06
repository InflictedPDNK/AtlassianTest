package org.pdnk.atlassiantagfinder;

import android.test.ActivityTestCase;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class TagFinderUnitTests extends ActivityTestCase
{
    public TagFinderUnitTests()
    {
        super();
    }

    TaggedStringFactory factory;

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        factory = new AtlassianTaggedStringFactory();
    }

    public void testSingleTagSearch() throws Exception
    {
        factory.registerTag("singletag", "(", ")", 0, 0);

        TaggedString result = factory.createFromString("(test)");
        assertTrue(result.toString().equals("singletag : test\n"));

        factory.registerTag("singletag", "[", "]", 0, 0);

        result = factory.createFromString("[test]");
        assertTrue(result.toString().equals("singletag : test\n"));
    }

    public void testMultipleTagSearch() throws Exception
    {
        factory.registerTag("tag1", "(", ")", 0, 0);
        factory.registerTag("tag2", "[", "]", 0, 0);

        TaggedString result = factory.createFromString("(test1)[test2]");
        assertTrue(result.toString().equals("tag1 : test1\ntag2 : test2\n"));

        factory.registerTag("tag1", "{", "}", 0, 0);

        result = factory.createFromString("{test1}[test2]");
        assertTrue(result.toString().equals("tag1 : test1\ntag2 : test2\n"));
    }

    public void testMultipleDisplacedTagSearch() throws Exception
    {
        factory.registerTag("tag1", "(", ")", 0, 0);
        factory.registerTag("tag2", "[", "]", 0, 0);

        TaggedString result = factory.createFromString("asfg (test1) ggsg [test2]");
        assertTrue(result.toString().equals("tag1 : test1\ntag2 : test2\n"));
    }

    public void testIncompleteTagSearch() throws Exception
    {
        factory.registerTag("tag1", "(", ")", 0, 0);

        TaggedString result = factory.createFromString("(test1");
        assertTrue(result.toString().equals(""));

        result = factory.createFromString("(test1) asf (test2");
        assertTrue(result.toString().equals("tag1 : test1\n"));
    }

    public void testJSONoutput() throws Exception
    {
        factory.registerTag("tag1", "(", ")", 0, 0);
        factory.registerTag("tag2", "[", "]", 0, 0);



        TaggedString result = factory.createFromString("abc (test1) def [test2][test3]");
        JSONObject json = result.toJSON();

        JSONObject jsonRoot = new JSONObject();
        JSONArray jsonArray1 =  new JSONArray();
        jsonRoot.put("tag1", jsonArray1);
        jsonArray1.put("test1");

        JSONArray jsonArray2 =  new JSONArray();
        jsonRoot.put("tag2", jsonArray2);
        jsonArray2.put("test2");
        jsonArray2.put("test3");

        assertTrue(jsonRoot.toString().equals(json.toString()));

    }

    public void testURLtag() throws Exception
    {
        factory.registerURLTag("tag1", "http://", TagFlags.DEL_END_WHITESPACE, TagFlags.FLAG_START_INCLUSIVE);

        TaggedString result = factory.createFromString("http://google.com");


        JSONObject json = result.toJSON();
        JSONArray linksArray = json.optJSONArray("tag1");
        assertNotNull(linksArray);

        JSONObject link = linksArray.optJSONObject(0);
        assertNotNull(link);

        String url = link.getString("url");
        assertNotNull(url);
        assertTrue(url.equals("http://google.com"));


        String title = link.getString("title");
        assertNotNull(title);
        assertTrue(title.equals("Google"));

    }

    public void testFlags() throws Exception
    {
        factory.registerTag("tag7", "/nested", TagFlags.DEL_END_ENDLESS, 0, TagFlags.FLAG_STRICTLY_FROM_START | TagFlags.FLAG_ALLOW_NESTED);
        factory.registerTag("tag6", "/nonested", TagFlags.DEL_END_ENDLESS, 0, TagFlags.FLAG_STRICTLY_FROM_START);

        factory.registerTag("tag1", "1", "1", 10, 0);

        assertTrue(factory.createFromString("1aabbccddeeffgg1").toString().equals(""));
        assertTrue(factory.createFromString("1aabb1").toString().equals("tag1 : aabb\n"));

        factory.registerTag("tag2", "/me", TagFlags.DEL_END_ENDLESS, 0, TagFlags.FLAG_STRICTLY_FROM_START);

        assertTrue(factory.createFromString("/me test").toString().equals("tag2 :  test\n"));
        assertTrue(factory.createFromString("test /me not").toString().equals(""));

        factory.registerTag("tag3", "/CASE", "/nocase", 0, TagFlags.FLAG_IGNORE_CASE);
        assertTrue(factory.createFromString("/case TeSt /NOCASE").toString().equals("tag3 :  TeSt \n"));

        factory.registerTag("tag4", "/trim", TagFlags.DEL_END_ENDLESS, 0, TagFlags.FLAG_TRIM_SPACES);
        assertTrue(factory.createFromString("/trim bla bla").toString().equals("tag4 : blabla\n"));

        factory.registerTag("tag5", "(", ")", 0, TagFlags.FLAG_START_INCLUSIVE | TagFlags.FLAG_END_INCLUSIVE);
        assertTrue(factory.createFromString("(test)").toString().equals("tag5 : (test)\n"));

        assertTrue(factory.createFromString("/nonested test (test)").toString().equals("tag6 :  test (test)\n"));
        assertTrue(factory.createFromString("/nested test (test)").toString().equals("tag7 :  test (test)\ntag5 : (test)\n"));
    }

    public void testRegexTagSearch() throws Exception
    {
        factory.registerTag("tag1", "@(\\w+)", 1);

        assertTrue(factory.createFromString("@test").toString().equals("tag1 : test\n"));
        assertTrue(factory.createFromString("abc @test def").toString().equals("tag1 : test\n"));
        assertTrue(factory.createFromString("abc @test1 def @test2").toString().equals("tag1 : test1\ntag1 : test2\n"));

        factory.registerTag("tag2", "%(\\w+)", 0);
        assertTrue(factory.createFromString("%test").toString().equals("tag2 : %test\n"));

    }
}