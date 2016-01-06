package org.pdnk.atlassiantagfinder;

import org.json.JSONArray;
import org.json.JSONObject;
import org.pdnk.atlassiantagfinder.primitives.TagData;

import java.util.Collection;
import java.util.Map;

/**
 * Created by Inflicted on 5/01/2016.
 */
public class AtlassianTaggedString implements TaggedString
{
    public AtlassianTaggedString(String originalString, Collection<TagData> tags)
    {
        this.originalString = originalString;
        this.tags = tags;
    }

    @Override
    public JSONObject toJSON()
    {
        JSONObject jsonRoot = new JSONObject();
        try
        {
            if(tags != null)
                for(TagData tag : tags)
                {
                    //simple construction of JSONObject with JSONArrays, as per the Atlassian reqs

                    JSONArray jsonArray = jsonRoot.optJSONArray(tag.name);
                    if(jsonArray == null)
                    {
                        jsonArray = new JSONArray();
                        jsonRoot.put(tag.name, jsonArray);
                    }

                    if(tag.extraParams == null || tag.extraParams.isEmpty())
                    {
                        jsonArray.put(tag.data);
                    }else
                    {
                        JSONObject jsonObject = new JSONObject(tag.extraParams);
                        jsonArray.put(jsonObject);
                    }
                }
        } catch (Exception e)
        {
        }

        return jsonRoot;
    }


    @Override
    public String getOriginalString()
    {
        return originalString;
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();

        if(tags != null)
            for (TagData tagData : tags)
            {
                sb.append(tagData.name);
                sb.append(" : ");
                sb.append(tagData.data);
                if(tagData.extraParams != null && !tagData.extraParams.isEmpty())
                {
                    sb.append("\n[\n");
                    for (Map.Entry<String, String> item : tagData.extraParams.entrySet())
                    {
                        sb.append(item.getKey());
                        sb.append(" : ");
                        sb.append(item.getValue());
                        sb.append("\n");
                    }

                    sb.append("]\n");
                }
                sb.append("\n");
            }

        return sb.toString();
    }

    private final String originalString;
    private final Collection<TagData> tags;
}
