package org.pdnk.atlassiantagfinder;

import org.json.JSONObject;

/**
 * Created by Inflicted on 5/01/2016.
 */

/**
 * Object which contains an original message with a collection of detected tags and respective data
 * Can be used for conversion to other formats, such as JSON
 */
public interface TaggedString
{
    /** Convert string to a JSONObject */
    JSONObject toJSON();

    /** Get the original message */
    String getOriginalString();
}
