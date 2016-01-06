package org.pdnk.atlassiantagfinder.tagsignatures;

import org.pdnk.atlassiantagfinder.primitives.TagData;
import org.pdnk.atlassiantagfinder.TagFlags;
import org.pdnk.atlassiantagfinder.primitives.TagMatchingResult;

import java.util.ArrayList;

/**
 * Created by Inflicted on 5/01/2016.
 */

/**
 * Simple Tag Signature with straightforward delimiter matching (also known as brute force)
 * No fancy algorithms are used as the nature of original strings is casual, thus they are too small
 * to impact the performance via brute force analysis.
 * Matching performed by detecting start and end indices of the tag based on the given delimiters
 * and flags, then the substring is extracted. The remaining string is parsed recursively, thus matching
 * can produce multiple results
 */
public class SimpleTagSignature extends BaseTagSignature
{
    public SimpleTagSignature(String name, String startDelimiter, String endDelimiter, int maxLength, int flags)
    {
        super(name, startDelimiter, endDelimiter, flags);
        this.maxLength = maxLength;
    }

    private final int maxLength;

    @Override
    public ArrayList<TagMatchingResult> match(String inputString)
    {
        return parseInternally(inputString, 0);
    }

    protected ArrayList<TagMatchingResult> parseInternally(String inputString, int initialOffset)
    {
        ArrayList<TagMatchingResult> results = null;

        int startOffset = -1;
        int startDelimiterLength = 0;
        int endOffset = -1;
        int endDelimiterLength = 0;

        String modifiedInputString = isFlagSet(TagFlags.FLAG_IGNORE_CASE) ?
                                             inputString.substring(initialOffset).toLowerCase() :
                                             inputString.substring(initialOffset);

        //try to find a start delimiter
        for (String startDelimiter : startDelimiters)
        {
            if(isFlagSet(TagFlags.FLAG_IGNORE_CASE))
            {
                startDelimiter = startDelimiter.toLowerCase();
            }

            startOffset = modifiedInputString.indexOf(startDelimiter);

            //bail if found
            if(startOffset != -1)
            {
                startDelimiterLength = startDelimiter.length();
                break;
            }

        }

        //if no start is found or there is not enough character after it, return null
        if(startOffset == -1 || (startOffset + startDelimiterLength) >= modifiedInputString.length())
            return null;

        //if FLAG_STRICTLY_FROM_START is set, but first index is not 0, ignore the string and exit
        if(isFlagSet(TagFlags.FLAG_STRICTLY_FROM_START) && startOffset > 0)
            return null;

        //now handle the end delimiter
        endDelimiterLoop:
        for (String endDelimiter : endDelimiters)
        {
            switch (endDelimiter)
            {
                case TagFlags.DEL_END_ENDLESS:
                    //if DEL_END_ENDLESS is set, the remaining characters are part of the tag.

                    endOffset = modifiedInputString.length();
                    break endDelimiterLoop;
                case TagFlags.DEL_END_WHITESPACE:
                case TagFlags.DEL_END_NONWORD:

                    boolean lookingForWhiteSpace = endDelimiter.equals(TagFlags.DEL_END_WHITESPACE);

                    //for DEL_END_WHITESPACE or DEL_END_NONWORD delimiter simply find a first index of
                    //any whitespace or non-word character.
                    for (int i = startOffset + startDelimiterLength; i < modifiedInputString.length(); ++i)
                    {
                        char charToTest = modifiedInputString.charAt(i);
                        boolean testPassed = lookingForWhiteSpace ? Character.isWhitespace(charToTest) :
                                                     !Character.isLetterOrDigit(charToTest);
                        if (testPassed)
                        {
                            endOffset = i;
                            break;
                        }
                    }
                    //if nothing is found, treat the remaining string as part of the tag
                    if (endOffset == -1)
                        endOffset = modifiedInputString.length();

                    break endDelimiterLoop;
                default:
                    //in all other cases simply look for the delimiter explicitly

                    if(isFlagSet(TagFlags.FLAG_IGNORE_CASE))
                    {
                        endDelimiter = endDelimiter.toLowerCase();
                    }

                    endOffset = modifiedInputString.indexOf(endDelimiter, startOffset + startDelimiterLength + 1);

                    //bail if found
                    if (endOffset != -1)
                    {
                        endDelimiterLength = endDelimiter.length();
                        break endDelimiterLoop;
                    }
                    break;
            }

        }

        if(endOffset == -1)
            return null;


        //indices (relative to the initial offset) which describe a tag with delimiters
        int firstTagIndexRelative = startOffset;
        int lastTagIndexRelative = endOffset + endDelimiterLength;

        int totalDelimiterLength = 0;

        if(isFlagSet(TagFlags.FLAG_START_INCLUSIVE))
            totalDelimiterLength += startDelimiterLength;
        else
            startOffset += startDelimiterLength;

        if(isFlagSet(TagFlags.FLAG_END_INCLUSIVE))
        {
            totalDelimiterLength += endDelimiterLength;
            endOffset += endDelimiterLength;
        }

        if(startOffset < endOffset)
        {
            //use the original string without any case modifications!
            String tagString = isFlagSet(TagFlags.FLAG_TRIM_SPACES) ?
                                       inputString.substring(startOffset + initialOffset , endOffset + initialOffset).replaceAll("\\s", "") :
                                       inputString.substring(startOffset + initialOffset , endOffset + initialOffset);

            if (tagString.length() - totalDelimiterLength > 0 &&
                (maxLength <= 0 || tagString.length() - totalDelimiterLength <= maxLength))
            {
                //create the collection
                results = new ArrayList<>();

                //add the result to the collection
                results.add(new TagMatchingResult(new TagData(name, tagString), firstTagIndexRelative, lastTagIndexRelative));
            }
        }

        //if something, remains parse it recursively respecting the offset
        if(lastTagIndexRelative < modifiedInputString.length())
        {
            ArrayList<TagMatchingResult> parsedList = parseInternally(inputString, lastTagIndexRelative + initialOffset);
            if(parsedList != null)
            {
                if(results != null)
                    results.addAll(parsedList);
                else
                    return parsedList;
            }
        }

        return results;
    }
}
