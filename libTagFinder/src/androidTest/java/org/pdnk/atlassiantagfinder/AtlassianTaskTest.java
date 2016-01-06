package org.pdnk.atlassiantagfinder;

import android.test.ActivityTestCase;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by Inflicted on 6/01/2016.
 */
public class AtlassianTaskTest extends ActivityTestCase
{
    public AtlassianTaskTest()
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

    public void testTaskCriteria() throws Exception
    {
        factory.registerTag("mentions", "@(\\w+)", 1);
        factory.registerTag("emoticons", "(", ")", 15, TagFlags.FLAG_TRIM_SPACES);

        factory.registerURLTag("links", "http://", TagFlags.DEL_END_WHITESPACE,
                                      TagFlags.FLAG_START_INCLUSIVE |
                                              TagFlags.FLAG_IGNORE_CASE |
                                              TagFlags.FLAG_TRIM_SPACES);


        TaggedString result = factory.createFromString("@bob @john (success) such a cool feature; http://google.com");

        JSONObject json = result.toJSON();

        JSONObject jsonRoot = new JSONObject();
        JSONArray jsonArray1 =  new JSONArray();
        jsonRoot.put("mentions", jsonArray1);
        jsonArray1.put("bob");
        jsonArray1.put("john");

        JSONArray jsonArray2 =  new JSONArray();
        jsonRoot.put("emoticons", jsonArray2);
        jsonArray2.put("success");

        JSONArray jsonArray3 =  new JSONArray();
        jsonRoot.put("links", jsonArray3);

        JSONObject linkObj = new JSONObject();
        jsonArray3.put(linkObj);
        linkObj.put("title", "Google");
        linkObj.put("url", "http://google.com");

        //order can be changed resulting in failed assertion. it'd be better to compare each object/array separately
        assertTrue(jsonRoot.toString().equals(json.toString()));

    }
}
