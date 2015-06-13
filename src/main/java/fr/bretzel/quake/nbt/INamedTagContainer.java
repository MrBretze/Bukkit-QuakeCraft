package fr.bretzel.quake.nbt;

import fr.bretzel.quake.nbt.error.TagNotFoundException;
import fr.bretzel.quake.nbt.error.UnexpectedTagTypeException;

import java.util.Map;

/**
 * @auhtor Johannes Donath <johannesd@evil-co.com>
 * @copyright Copyright (C) 2014 Evil-Co <http://www.evil-co.org>
 */
public interface INamedTagContainer extends ITagContainer {

    /**
     * Returns the tag associated with the given name.
     *
     * @param name The tag name.
     */
    ITag getTag( String name);

    /**
     * Returns the tag associated with the given name, ensuring its type is as expected
     *
     * @param name     The tag name
     * @param tagClass The expected tag type
     * @return the tag
     * @throws UnexpectedTagTypeException The tag is found, but of different type than expected
     * @throws TagNotFoundException       There is no tag with the given name in this container
     */
    <T extends ITag> T getTag(String name, Class<T> tagClass)
            throws UnexpectedTagTypeException, TagNotFoundException;

    /**
     * Returns a named map of all tags.
     *
     * @return
     */
    Map<String, ITag> getTags();

    /**
     * Removes a tag from the container.
     *
     * @param tag The tag name.
     */
    void removeTag( String tag);

    /**
     * Sets a new tag.
     *
     * @param tag The tag.
     */
    void setTag( ITag tag);
}