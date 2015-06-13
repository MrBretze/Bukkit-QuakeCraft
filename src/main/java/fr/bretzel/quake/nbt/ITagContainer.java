package fr.bretzel.quake.nbt;


/**
 * @auhtor Johannes Donath <johannesd@evil-co.com>
 * @copyright Copyright (C) 2014 Evil-Co <http://www.evil-co.org>
 */
public interface ITagContainer extends ITag {

    /**
     * Removes a tag from the container.
     *
     * @param tag The tag.
     * @return
     */
    void removeTag( ITag tag);
}