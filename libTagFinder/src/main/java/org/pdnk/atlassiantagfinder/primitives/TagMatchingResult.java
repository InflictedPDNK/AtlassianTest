package org.pdnk.atlassiantagfinder.primitives;

/**
 * Created by Inflicted on 5/01/2016.
 */

/**
 * Primitive data type describing a result of Tag Signature matching
 * Contains TagData with tag information and some offsets for additional string processing
 */
public class TagMatchingResult
{
    /**
     *
     * @param tagData Tag information which is parsed and created by a Tag Signature
     * @param firstTagIndexRelative Index of first character in tag which includes the delimiter relative
     *                              to the beginning of incoming original string
     * @param lastTagIndexRelative Index of last character in tag which includes the delimiter relative
     *                              to the beginning of incoming original string
     */
    public TagMatchingResult(TagData tagData, int firstTagIndexRelative, int lastTagIndexRelative)
    {
        this.tagData = tagData;
        this.firstTagIndexRelative = firstTagIndexRelative;
        this.lastTagIndexRelative = lastTagIndexRelative;
    }

    /**
     * Index of first character in tag which includes the delimiter relative
     *                              to the beginning of incoming original string
     */
    public final int firstTagIndexRelative;

    /**
     * Index of last character in tag which includes the delimiter relative
     *                              to the beginning of incoming original string
     */
    public final int lastTagIndexRelative;

    /**
     * Tag information which is parsed and created by a Tag Signature
     */
    final public TagData tagData;
}
