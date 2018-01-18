package fr.bretzel.quake.hologram;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.plugin.Plugin;

public class HoloEntity {

    private ArmorStand stand;
    private String line;
    private Location location;
    private Plugin plugin;

    public HoloEntity(Location location,  String line, Plugin plugin) {
        setLocation(location);
        setPlugin(plugin);
        setLine(line);
        setStand(location.getWorld().spawn(location, ArmorStand.class));
        getStand().setGravity(false);
        getStand().setBasePlate(false);
        getStand().setArms(false);
        getStand().setCustomNameVisible(false);
        getStand().setMarker(true);
        getStand().setVisible(false);
        getStand().setCustomName(getLine());
    }

    public HoloEntity(World world, int x, int y, int z, String line, Plugin plugin) {
        this(new Location(world, x, y, z), line, plugin);
    }

    public ArmorStand getStand() {
        return stand;
    }

    public Location getLocation() {
        return location;
    }

    public Plugin getPlugin() {
        return plugin;
    }

    public String getLine() {
        return line;
    }

    public void setLine(String line) {
        this.line = line;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public void setPlugin(Plugin plugin) {
        this.plugin = plugin;
    }

    public void setStand(ArmorStand stand) {
        this.stand = stand;
    }
}
