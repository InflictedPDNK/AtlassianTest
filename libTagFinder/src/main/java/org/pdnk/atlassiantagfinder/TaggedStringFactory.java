package org.pdnk.atlassiantagfinder;

/**
 * Created by Inflicted on 20/12/2015.
 */

/**
 * Factory object responsible for producing TaggedString objects.<p>
 * Allows registrations of Tag Signatures which are used as a parsing kernels<p>
 * The factory creates a final TaggedString object with the results encapsulated<p>
  * You can register multiple signatures with different combinations of start and end delimiters
 * for one and the same tagName. Consider that the flags will be combined. maxLength setting is
 * ignored for subsequent registrations.<p>
 * When using Regex-based signatures it is allowed to have only one signature per a tag name. If
 * other signatures are present (either Regex-based or other types) an exception will be thrown<p>
 */
public interface TaggedStringFactory
{

    /**
     * Register Tag Signature with Regular expression based delimiters
     * @param tagName Name of the tag or tag group
     * @param regex Regular expression used for matching the tag
     * @param groupNumber Number of matching group to return. Use 0 for whole regex matching
     * @throws IllegalArgumentException if any String parameters is null or empty or other tag signature
     * is present with the same tagName<p>
     * <p>
     * NOTE: When registering a tag, insertion order is preserved. Mind the flags you are using to
     * understand the expected priority of parsing<p>
     * NOTE: There can be only one Regex-based Tag signature per tagName registered in the Factory
     */
    void registerTag(String tagName, String regex, int groupNumber) throws IllegalArgumentException;

    /**
     * Register Tag Signature with explicit start and end delimiters
     * @param tagName Name of the tag or tag group
     * @param startDelimiter Substring used as a start delimiter. This substring is used as a whole. To register
     *                       a set of different characters, call the method with each character separately.
     *                       See NOTEs for details
     * @param endDelimiter Substring used as an end delimiter or one of TagFlags.DEL_END_* flags. See {@link TagFlags}
     * @param maxLength Maximum length of tag or 0 for unlimited. If length of a tag exceeds this value,
     *                  the tag will be ignored
     * @param flags Any combination of TagFlags.FLAG_* flags. See {@link TagFlags}
     * @throws IllegalArgumentException if any String parameters is null or empty or other tag signature
     * is present with the same tagName with Regex-based delimiters<p>
     * <p>
     * NOTE: You can register multiple signatures with different combinations of start and end delimiters
     * for one and the same tagName. Consider that the flags will be combined. maxLength setting is
     * ignored for subsequent registrations.<p>
     * NOTE: When registering a tag, insertion order is preserved. Mind the flags you are using to
     * understand the expected priority of parsing<p>
     * NOTE: There can be only one Regex-based Tag signature per tagName registered in the Factory
     */
    void registerTag(String tagName, String startDelimiter, String endDelimiter, int maxLength, int flags) throws IllegalArgumentException;

    /**
     * Register URL-based Tag Signature with explicit start and end delimiters
     * @param tagName Name of the tag or tag group
     * @param startDelimiter Substring used as a start delimiter. This substring is used as a whole. To register
     *                       a set of different characters, call the method with each character separately.
     *                       See NOTEs for details
     * @param endDelimiter Substring used as an end delimiter or one of TagFlags.DEL_END_* flags. See {@link TagFlags}
     * @param flags Any combination of TagFlags.FLAG_* flags. See {@link TagFlags}
     * @throws IllegalArgumentException if any String parameters is null or empty or other tag signature
     * is present with the same tagName with Regex-based delimiters<p>
     * <p>
     * NOTE: For URL-based tags, you might want to add "http://" and similar start delimiters. It will
     * make sense to use DEL_END_WHITESPACE as an end delimiter. Make sure to set FLAG_IGNORE_CASE<p>
     * NOTE: You can register multiple signatures with different combinations of start and end delimiters
     * for one and the same tagName. Consider that the flags will be combined. maxLength setting is
     * ignored for subsequent registrations.<p>
     * NOTE: When registering a tag, insertion order is preserved. Mind the flags you are using to
     * understand the expected priority of parsing<p>
     * NOTE: There can be only one Regex-based Tag signature per tagName registered in the Factory
     */
    void registerURLTag(String tagName, String startDelimiter, String endDelimiter, int flags) throws IllegalArgumentException;


    /**
     * Produce a {@link TaggedString} from the plain String message, possibly containing tags
     * @param inputString Plain text message with not yet detected tags
     * @return TaggedString
     */
    TaggedString createFromString(String inputString);
}
