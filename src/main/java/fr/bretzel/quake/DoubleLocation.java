package fr.bretzel.quake;

import com.google.common.collect.Maps;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.HashMap;
import java.util.Objects;

/**
 * Created by MrBretzel on 30/10/2015.
 */
public class DoubleLocation implements Savable {

    private Location loc1;
    private Location loc2;

    public DoubleLocation(Location one, Location two) {
        this.loc1 = one;
        this.loc2 = two;
    }

    public static DoubleLocation load(HashMap<String, Object> data) {
        if (!data.containsKey("loc1") || !data.containsKey("loc2"))
            throw new NullPointerException("The HahMap could not contains the key loc1 or loc2");
        return new DoubleLocation(Location.deserialize((java.util.Map<String, Object>) data.get("loc1")), Location.deserialize((java.util.Map<String, Object>) data.get("loc2")));
    }

    public Location getLocationOne() {
        return loc1;
    }

    public Location getLocationTwo() {
        return loc2;
    }

    public double distance() {
        return getLocationOne().distance(getLocationTwo());
    }

    public int getMaxX() {
        return (getLocationOne().getBlockX() < getLocationTwo().getBlockX() ? getLocationTwo().getBlockX() : getLocationOne().getBlockX());
    }

    public int getMineX() {
        return (getLocationOne().getBlockX() > getLocationTwo().getBlockX() ? getLocationTwo().getBlockX() : getLocationOne().getBlockX());
    }

    public int getMaxY() {
        return (getLocationOne().getBlockY() < getLocationTwo().getBlockY() ? getLocationTwo().getBlockY() : getLocationOne().getBlockY());
    }

    public int getMinY() {
        return (getLocationOne().getBlockY() > getLocationTwo().getBlockY() ? getLocationTwo().getBlockY() : getLocationOne().getBlockY());
    }

    public int getMaxZ() {
        return (getLocationOne().getBlockZ() < getLocationTwo().getBlockZ() ? getLocationTwo().getBlockZ() : getLocationOne().getBlockZ());
    }

    public int getMinZ() {
        return (getLocationOne().getBlockZ() > getLocationTwo().getBlockZ() ? getLocationTwo().getBlockZ() : getLocationOne().getBlockZ());
    }

    public World getWorld() {
        return Objects.equals(getLocationOne().getWorld().getName(), getLocationTwo().getWorld().getName()) ? getLocationOne().getWorld() : null;
    }

    @Override
    public HashMap<String, Object> save() {
        HashMap<String, Object> data = Maps.newHashMap();
        data.put("loc1", loc1.serialize());
        data.put("loc2", loc2.serialize());
        return data;
    }
}
