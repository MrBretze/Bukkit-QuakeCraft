package fr.bretzel.quake.api;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;

import java.util.List;

/**
 * Created by MrBretzel on 13/11/15.
 */
public interface IDoubleLocation {

    World getWorld();

    Location getLocationOne();

    Location getLocationTwo();

    Location getMidle();

    double distance();

    int getMaxX();

    int getMinX();

    int getMaxY();

    int getMinY();

    int getMaxZ();

    int getMinZ();

    boolean inArea(Location location);

    boolean hasEntity();

    boolean hasQuakePlayer();

    List<Entity> getEntitys();

    List<IQuakePlayer> getPlayers();
}
