package org.pdnk.atlassiantagfinder.primitives;

import java.util.HashMap;

/**
 * Created by Inflicted on 5/01/2016.
 */

/**
 * Primitive data type describing tag data
 * Contains tag name, actual tag and possible extra parameters created by a Tag Signature
 */
public class TagData
{
    /**
     *
     * @param name Tag name
     * @param data Tag data (actual tag)
     */
    public TagData(String name, String data)
    {
        this.name = name;
        this.data = data;
    }
    public final String name;
    public final String data;
    public HashMap<String, String> extraParams;
}
