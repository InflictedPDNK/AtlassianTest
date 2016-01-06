package org.pdnk.atlassiantagfinder.tagsignatures;

import org.pdnk.atlassiantagfinder.primitives.TagData;
import org.pdnk.atlassiantagfinder.primitives.TagMatchingResult;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Inflicted on 5/01/2016.
 */

/**
 * Regex-based Tag Signature.<p>
 * Provides ability to use a regular expression as matching pattern
 */
public class RegexTagSignature extends BaseTagSignature
{
    public RegexTagSignature(String name, String regex, int groupNumber)
    {
        super(name, null, null, 0);

        this.groupNumber = groupNumber;
        pattern = Pattern.compile(regex);
    }

    @Override
    public ArrayList<TagMatchingResult> match(String inputString)
    {
         return parseInternally(inputString);
    }

    protected ArrayList<TagMatchingResult> parseInternally(String inputString)
    {
        ArrayList<TagMatchingResult> results = null;

        Matcher m = pattern.matcher(inputString);

        //in order to preserve relative indexing of each tag offset each subsequent found indices by
        //the sum of previously found tags' lengths
        int offset = 0;

        while(m.find())
        {
            if(results == null)
                results = new ArrayList<>(m.groupCount());

            results.add(new TagMatchingResult(new TagData(name, m.group(groupNumber)), m.start() - offset, m.end() - offset));

            //use a whole match's length
            offset += m.group().length();
        }

        return results;
    }

    private final Pattern pattern;
    private final int groupNumber;
}
