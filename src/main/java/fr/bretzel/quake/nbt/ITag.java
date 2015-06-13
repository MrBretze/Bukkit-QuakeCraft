package fr.bretzel.quake.nbt;

import fr.bretzel.quake.nbt.stream.NbtOutputStream;

import java.io.IOException;
import java.nio.charset.Charset;

/**
 * @auhtor Johannes Donath <johannesd@evil-co.com>
 * @copyright Copyright (C) 2014 Evil-Co <http://www.evil-co.org>
 */

public interface ITag {

    /**
     * Defines the string charset.
     */
    Charset STRING_CHARSET = Charset.forName("UTF-8");

    /**
     * Returns the tag name.
     *
     * @return The tag identifier.
     */
    String getName();

    /**
     * Sets a new name.
     *
     * @param name The new name.
     */
    void setName(String name);

    /**
     * Returns the name bytes.
     *
     * @return Name string in UTF-8 bytes.
     */
    byte[] getNameBytes();

    /**
     * Returns the parent tag.
     *
     * @return The parent tag container (if any).
     */
    ITagContainer getParent();

    /**
     * Sets a new parent.
     *
     * @param parent The new parent tag container.
     */
    void setParent(ITagContainer parent);

    /**
     * Returns the tag ID.
     *
     * @return The tag numerical identifier.
     */
    byte getTagID();

    /**
     * Writes a tag into a byte buffer.
     *
     * @param outputStream
     * @param anonymous
     */
    void write(NbtOutputStream outputStream, boolean anonymous) throws IOException;
}