package org.pdnk.atlassiantagfinder;

/**
 * Created by Inflicted on 5/01/2016.
 */
public class TagFlags
{
    /** Use a white-space character as a delimiter*/
    public static final String DEL_END_WHITESPACE = "whitespace";

    /** Use a non-word character as a delimiter*/
    public static final String DEL_END_NONWORD = "nonword";

    /** Read a string to the end like there is no end delimiter at all*/
    public static final String DEL_END_ENDLESS = "endless";

    /** Requires a tag to be located right in the beginning of a string. Otherwise it is ignored*/
    public static final int FLAG_STRICTLY_FROM_START = 2;

    /** Allows nested tags in the tag. Mind the order of tag registration*/
    public static final int FLAG_ALLOW_NESTED = 4;

    /** Include start delimiter as part of the tag*/
    public static final int FLAG_START_INCLUSIVE = 8;

    /** Include end delimiter as part of the tag*/
    public static final int FLAG_END_INCLUSIVE = 16;

    /** Ignore case sensitivity for both a string and the delimiters when matching. Does not affect
     * the resulting tag(s)*/
    public static final int FLAG_IGNORE_CASE = 32;

    /** Remove empty spaces from resulting tag(s)*/
    public static final int FLAG_TRIM_SPACES = 64;
}
