package org.pdnk.atlassiantagfinder;

import org.pdnk.atlassiantagfinder.primitives.TagData;
import org.pdnk.atlassiantagfinder.primitives.TagMatchingResult;
import org.pdnk.atlassiantagfinder.tagsignatures.RegexTagSignature;
import org.pdnk.atlassiantagfinder.tagsignatures.SimpleTagSignature;
import org.pdnk.atlassiantagfinder.tagsignatures.TagSignature;
import org.pdnk.atlassiantagfinder.tagsignatures.URLTagSignature;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;

/**
 * Created by Inflicted on 5/01/2016.
 */
public class AtlassianTaggedStringFactory implements TaggedStringFactory
{
    public AtlassianTaggedStringFactory()
    {
        registeredTags = new LinkedHashMap<>();
    }

    @Override
    public void registerTag(String tagName, String regex, int groupNumber) throws IllegalArgumentException
    {
        //validate params
        if(regex == null || regex.isEmpty() || tagName == null || tagName.isEmpty())
        {
            throw new IllegalArgumentException("Regex string and tag name must not be null or empty");
        }

        //check for existing tag. Regex signatures can be complex, thus there can be only one tag
        //with regex and no other types of signatures for that tag
        if(registeredTags.containsKey(tagName))
            throw new IllegalArgumentException("There can be only one regex signature per tag");

        //register new signature
        registeredTags.put(tagName, new RegexTagSignature(tagName, regex, groupNumber));
    }

    @Override
    public void registerTag(String tagName, String startDelimiter, String endDelimiter, int maxLength, int flags)
    {
        //validate params
        if(startDelimiter == null || startDelimiter.isEmpty() || endDelimiter == null ||
           endDelimiter.isEmpty() || tagName == null || tagName.isEmpty())
        {
            throw new IllegalArgumentException("Delimiters and tag name must not be null or empty");
        }

        try
        {
            //see if this tag already has a signature. if not, register new one
            if(!doesSignatureExist(tagName, startDelimiter, endDelimiter, flags))
            {
                registeredTags.put(tagName, new SimpleTagSignature(tagName, startDelimiter, endDelimiter, maxLength, flags));
            }
        }
        catch (IllegalArgumentException e)
        {
            //propagate the exception
            throw e;
        }
    }

    @Override
    public void registerURLTag(String tagName, String startDelimiter, String endDelimiter, int flags)
    {
        //validate params
        if(startDelimiter == null || startDelimiter.isEmpty() || endDelimiter == null ||
                   endDelimiter.isEmpty() || tagName == null || tagName.isEmpty())
        {
            throw new IllegalArgumentException("Delimiters and tag name must not be null or empty");
        }

        try
        {
            //see if this tag already has a signature. if not, register new one
            if(!doesSignatureExist(tagName, startDelimiter, endDelimiter, flags))
            {
                registeredTags.put(tagName, new URLTagSignature(tagName, startDelimiter, endDelimiter, flags));
            }
        }
        catch (IllegalArgumentException e)
        {
            //propagate the exception
            throw e;
        }

    }

    @Override
    public TaggedString createFromString(String inputString)
    {
        return new AtlassianTaggedString(inputString, findTags(inputString, null));
    }

    private Collection<TagData> findTags(String inputString, TagSignature ignoreSignature)
    {
        ArrayList<TagData> parsedTags = null;

        //duplicate the original string to allow safe modifications
        StringBuilder dupString = new StringBuilder(inputString);

        //iterate through all registered signatures and feed the string to each of them for parsing
        for (TagSignature signature : registeredTags.values())
        {
            //if a specific ignore signature is set, skip it
            if(signature == ignoreSignature)
                continue;

            ArrayList<TagMatchingResult> results;

            //when FLAG_STRICTLY_FROM_START a signature expects a start delimiter from 0 index. in
            //such case only the original string must be parsed
            if(signature.isFlagSet (TagFlags.FLAG_STRICTLY_FROM_START))
                results = signature.match(inputString);
            else
                results = signature.match(dupString.toString());


            //if something has been parsed, handle the results
            if(results != null)
            {
                for (TagMatchingResult result : results)
                {
                    //if something is found, let's handle it
                    if(result.tagData != null)
                    {
                        if(parsedTags == null)
                            parsedTags = new ArrayList<>();

                        //add parsed tag to the list
                        parsedTags.add(result.tagData);

                        //cut out the parsed tag to speed up further processing and prevent any
                        //possible intersection between tags
                        dupString.replace(result.firstTagIndexRelative, result.lastTagIndexRelative, "");

                        //if FLAG_ALLOW_NESTED is set, recursively process the parsed tag, but ignore
                        //current signature so no infinite recursions occur in case FLAG_START_INCLUSIVE
                        //is set
                        if(signature.isFlagSet(TagFlags.FLAG_ALLOW_NESTED))
                        {
                            Collection<TagData> nestedTags = findTags(result.tagData.data, signature);
                            if(nestedTags != null)
                                parsedTags.addAll(nestedTags);
                        }
                    }
                }
            }
        }

        return parsedTags;
    }

    private boolean doesSignatureExist(String tagName, String startDelimiter, String endDelimiter, int flags) throws IllegalArgumentException
    {
        //try to find an existing signature
        TagSignature existingTag = registeredTags.get(tagName);

        if(existingTag == null)
        {
            //if no signatures with the same name exist, search for duplicating start delimiters as they must be unique
            for (TagSignature signature: registeredTags.values())
            {
                if(signature.hasStartDelimiter(startDelimiter))
                    throw new IllegalArgumentException("Start delimiter \"" + startDelimiter + "\" is already registered for tag " + signature.getName());
            }

            //if all good, return appropriately
            return false;
        }

        //check for existing Regex tag signature. Regex signatures can be complex, thus there can be only one tag
        //with regex and no other types of signatures for that tag
        if(existingTag instanceof RegexTagSignature)
            throw new IllegalArgumentException("There can be only one regex signature per tag");


        //for existing signatures simply add new start and end delimiters. there is no need to check
        //for duplicates as it is done internally
        existingTag.addStartDelimiter(startDelimiter);
        existingTag.addEndDelimiter(endDelimiter);

        //flags are always combined with the previous
        existingTag.addFlags(flags);

        return true;
    }

    private final LinkedHashMap<String, TagSignature> registeredTags;

}
