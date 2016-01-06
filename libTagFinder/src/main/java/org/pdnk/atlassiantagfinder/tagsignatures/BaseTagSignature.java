package org.pdnk.atlassiantagfinder.tagsignatures;

import java.util.ArrayList;

/**
 * Created by Inflicted on 5/01/2016.
 */

/**
 * Base class with some convenient method implementations. Should be subclassed by other Tag Signature
 * implementations
 */
public abstract class BaseTagSignature implements TagSignature
{
    BaseTagSignature(String name, String startDelimiter, String endDelimiter, int flags)
    {
        startDelimiters = new ArrayList<>();
        endDelimiters = new ArrayList<>();

        if(startDelimiter != null && !startDelimiter.isEmpty())
            startDelimiters.add(startDelimiter);

        if(endDelimiter != null && !endDelimiter.isEmpty())
            endDelimiters.add(endDelimiter);

        this.name = name;
        this.flags = flags;
    }

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public int getFlags()
    {
        return flags;
    }

    @Override
    public boolean hasStartDelimiter(String delimiter)
    {
        return startDelimiters.contains(delimiter);
    }

    @Override
    public boolean hasEndDelimiter(String delimiter)
    {
        return endDelimiters.contains(delimiter);
    }

    @Override
    public void addStartDelimiter(String delimiter)
    {
        if(!hasStartDelimiter(delimiter))
            startDelimiters.add(delimiter);
    }

    @Override
    public void addEndDelimiter(String delimiter)
    {
        if(!hasEndDelimiter(delimiter))
            endDelimiters.add(delimiter);
    }

    @Override
    public void addFlags(int flags)
    {
        this.flags |= flags;
    }

    @Override
    public boolean isFlagSet(int flag)
    {
        return (flags & flag) == flag;
    }

    final String name;
    final protected ArrayList<String> startDelimiters;
    final protected ArrayList<String> endDelimiters;
    protected int flags;
}
