package org.pdnk.atlassiantagfinder.tagsignatures;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.pdnk.atlassiantagfinder.primitives.TagMatchingResult;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Inflicted on 5/01/2016.
 */

/**
 * Tag Signature for URL-based tags.<p>
 * Subclasses {@link SimpleTagSignature} and uses its matching logic. In addition to that, extract a
 * web page title upon successful download of a page from parsed URL. The title and url are then added
 * as extra parameters to {@link org.pdnk.atlassiantagfinder.TaggedString}<p>
 * All operations are synchronous. It is a top caller's responsibility to provide asynchronous calls
 * to {@link org.pdnk.atlassiantagfinder.TaggedStringFactory} methods.
 *
 */
public class URLTagSignature extends SimpleTagSignature
{
    /**Limit the page load size to prevent excessive download*/
    private final int MAX_PAGE_LOAD_SIZE = 10000; //in bytes

    public URLTagSignature(String name, String startDelimiter, String endDelimiter, int flags)
    {
        super(name, startDelimiter, endDelimiter, 0, flags);
    }

    @Override
    public ArrayList <TagMatchingResult> match(String inputString)
    {
        //for URL tags allow original parsing, but do some extra work with the results
        ArrayList <TagMatchingResult> results = super.match(inputString);


        if(results != null)
        {
            //create extra parameters based on the results
            for (TagMatchingResult result : results)
            {
                result.tagData.extraParams = new HashMap<>(2);
                result.tagData.extraParams.put("url", result.tagData.data);

                //if internal loading of titles is enabled, fetch the page and extract title from it
                try
                {
                    String prefix = "";
                    if(!result.tagData.data.toLowerCase().startsWith("http://") &&
                       !result.tagData.data.toLowerCase().startsWith("https://"))
                    {
                        prefix += "http://";
                    }

                    Document incomingDoc = Jsoup.connect(prefix + result.tagData.data)
                                                   .maxBodySize(MAX_PAGE_LOAD_SIZE)
                                                   .followRedirects(true)
                                                   .get();

                    result.tagData.extraParams.put("title", incomingDoc.title());

                } catch (Exception e)
                {
                    //if failed, simply ignore the title param
                    e.printStackTrace();
                }


            }
        }

        return results;
    }
}
