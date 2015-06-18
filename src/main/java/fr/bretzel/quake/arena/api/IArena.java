package fr.bretzel.quake.arena.api;

import fr.bretzel.quake.arena.Rule;
import org.bukkit.Location;

import java.util.LinkedList;

/**
 * Created by MrBretzel on 12/06/2015.
 */

public interface IArena {

    void setFirstLocation(Location location);

    void setSecondLocation(Location location);

    void setName(String name);

    String getName();

    Location getFirstLocation();

    Location getSecondLocation();

}
