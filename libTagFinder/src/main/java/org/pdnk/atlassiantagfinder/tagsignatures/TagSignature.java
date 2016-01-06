package org.pdnk.atlassiantagfinder.tagsignatures;

import org.pdnk.atlassiantagfinder.primitives.TagMatchingResult;

import java.util.ArrayList;

/**
 * Created by Inflicted on 5/01/2016.
 */

/**
 * Tag signature interface responsible for describing the tag matching behaviour and flags<p>
 * Implementations may vary in parsing, algorithms and logic involved
 * {@link org.pdnk.atlassiantagfinder.TaggedStringFactory} is responsible for containing the registered
 * objects of TagSignature and passing the original string to each of the signature for its internal analysis
 * The result is then accumulated in {@link org.pdnk.atlassiantagfinder.TaggedString} and produced for
 * client's further use.
 */
public interface TagSignature
{
    /**
     * Performs matching of signature with the incoming string according to the implemented logic,
     * registered delimiters and set flags
     * @param inputString Plain string possibly containing tags
     * @return Array of {@link TagMatchingResult} per each found tag, or null if nothing has been found
     */
    ArrayList<TagMatchingResult> match(String inputString);

    /**
     *
     * @return Name of the tag signature
     */
    String getName();

    /**
     *
     * @return Combination of set flags of the signature. See {@link org.pdnk.atlassiantagfinder.TagFlags}
     */
    int getFlags();

    /**
     * Convenient method to test against a particular flag
     * @return True if requested flag is set in the signature. See {@link org.pdnk.atlassiantagfinder.TagFlags}
     */
    boolean isFlagSet(int flag);

    /**
     * Convenient method to check if particular delimiter is already contained in the signature
     * @param delimiter Delimiter substring
     * @return True if the delimiter is available
     */
    boolean hasStartDelimiter(String delimiter);

    /**
     * Convenient method to check if particular delimiter is already contained in the signature
     * @param delimiter Delimiter substring
     * @return True if the delimiter is available
     */
    boolean hasEndDelimiter(String delimiter);

    /**
     * Add new delimiter to the signature. If duplicating, has no effect
     * @param delimiter Delimiter substring
     */
    void addStartDelimiter(String delimiter);

    /**
     * Add new delimiter to the signature. If duplicating, has no effect
     * @param delimiter Delimiter substring
     */

    void addEndDelimiter(String delimiter);

    /**
     * Add new combination of flags to the signature. Flags are combined with the existing ones
     * @param flags A combination of flags to be added
     */
    void addFlags(int flags);
}
