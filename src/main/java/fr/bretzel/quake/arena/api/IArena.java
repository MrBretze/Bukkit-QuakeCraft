package fr.bretzel.quake.arena.api;

import org.bukkit.Location;

import java.util.LinkedList;

/**
 * Created by MrBretzel on 12/06/2015.
 */

public interface IArena {

    void setFirstLocation(Location location);

    void setSecondLocation(Location location);

    Location getFirstLocation();

    Location getSecondLocation();

    LinkedList<? super IRule> getRules();

}
