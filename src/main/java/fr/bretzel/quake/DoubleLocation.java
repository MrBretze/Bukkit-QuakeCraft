package fr.bretzel.quake;

import fr.bretzel.quake.api.IDoubleLocation;
import fr.bretzel.quake.api.IQuakePlayer;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created by MrBretzel on 30/10/2015.
 */
public class DoubleLocation implements IDoubleLocation {

    private Location locationOne;
    private Location locationTwo;

    public DoubleLocation(Location one, Location two) {
        this.locationOne = one;
        this.locationTwo = two;
    }

    @Override
    public World getWorld() {
        return getLocationOne().getWorld();
    }

    @Override
    public Location getLocationOne() {
        return this.locationOne;
    }

    @Override
    public Location getLocationTwo() {
        return this.locationTwo;
    }

    @Override
    public Location getMidle() {
        return new Location(getWorld(), getMaxX() / getMinX(), getMaxY() / getMinY(), getMaxZ() / getMinZ());
    }

    @Override
    public double distance() {
        return Math.sqrt((getLocationOne().getX() - getLocationTwo().getX()) + (getLocationOne().getY() - getLocationTwo().getY()) + (getLocationOne().getZ() - getLocationTwo().getZ()));
    }

    @Override
    public int getMaxX() {
        return getLocationOne().getBlockX() > getLocationTwo().getBlockX() ? getLocationOne().getBlockX() : getLocationTwo().getBlockX();
    }

    @Override
    public int getMinX() {
        return getLocationOne().getBlockX() < getLocationTwo().getBlockX() ? getLocationOne().getBlockX() : getLocationTwo().getBlockX();
    }

    @Override
    public int getMaxY() {
        return getLocationOne().getBlockY() > getLocationTwo().getBlockY() ? getLocationOne().getBlockY() : getLocationTwo().getBlockY();
    }

    @Override
    public int getMinY() {
        return getLocationOne().getBlockY() < getLocationTwo().getBlockY() ? getLocationOne().getBlockY() : getLocationTwo().getBlockY();
    }

    @Override
    public int getMaxZ() {
        return getLocationOne().getBlockZ() > getLocationTwo().getBlockZ() ? getLocationOne().getBlockZ() : getLocationTwo().getBlockZ();
    }

    @Override
    public int getMinZ() {
        return getLocationOne().getBlockZ() < getLocationTwo().getBlockZ() ? getLocationOne().getBlockZ() : getLocationTwo().getBlockZ();
    }

    @Override
    public boolean inArea(Location location) {
        return location.getBlockX() >= getMinX() && location.getBlockX() <= getMaxX() && location.getBlockX() >= getMinY() && location.getBlockY() <= getMaxY() && location.getBlockZ() >= getMinZ() && location.getBlockZ()<= getMaxZ();
    }

    @Override
    public boolean hasEntity() {
        return getEntitys().isEmpty();
    }

    @Override
    public boolean hasQuakePlayer() {
        return getPlayers().isEmpty();
    }

    @Override
    public List<Entity> getEntitys() {
        return getWorld().getEntities();
    }

    @Override
    public List<IQuakePlayer> getPlayers() {
        Iterator<Entity> e = getEntitys().iterator();
        List<IQuakePlayer> pl = new ArrayList<>();
        while (e.hasNext()) {
            Entity entity = e.next();
            if (entity instanceof Player)
                pl.add(QuakePlayer.getIQuakePlayer(entity.getUniqueId()));
        }
        return pl;
    }
}
