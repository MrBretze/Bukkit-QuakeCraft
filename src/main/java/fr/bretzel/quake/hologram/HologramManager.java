package fr.bretzel.quake.hologram;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

public class HologramManager implements Listener {

    private List<Hologram> holoList = new ArrayList<>();
    private Plugin plugin;

    public HologramManager(Plugin plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public Hologram getHologram(World world, double x, double y, double z) {
        return this.getHologram(new Location(world, x, y, z));
    }

    public Hologram getHologram(World world, int x, int y, int z) {
        return this.getHologram(new Location(world, x, y, z));
    }

    public Hologram getHologram(Location location) {
        return this.getHologram(location, 0.5);
    }

    public Hologram getHologram(World world, int x, int y, int z, double range) {
        return getHologram(new Location(world, x, y, z), range);
    }

    public Hologram getHologram(World world, double x, double y, double z, double range) {
        return getHologram(new Location(world, x, y, z), range);
    }

    public Hologram getHologram(Location location, double range) {
        for (Hologram holo : getHologramList())
            if (holo.getLocation().getWorld() == location.getWorld() && holo.getLocation().distance(location) <= range)
                return holo;
        return null;
    }

    public void removeHologram(World world, int x, int y, int z, double range) {
        this.removeHologram(new Location(world, x, y, z), range);
    }

    public void removeHologram(World world, double x, double y, double z) {
        this.removeHologram(new Location(world, x, y, z), 0.5);
    }

    public void removeHologram(World world, int x, int y, int z) {
        this.removeHologram(new Location(world, x, y, z), 0.5);
    }

    public void removeHologram(World world, double x, double y, double z, double range) {
        this.removeHologram(new Location(world, x, y, z), range);
    }

    public void removeHologram(Location location) {
        this.removeHologram(location, 0.5);
    }

    public void removeHologram(Location location, double range) {
        if (!exist(location, range))
            return;
        Hologram hologram = getHologram(location, range);
        if (hologram == null) {
            throw new NullPointerException();
        }
        for (HoloEntity e : hologram.getHoloEntities())
            if (e.getStand() != null)
                e.getStand().remove();
    }

    public boolean exist(World world, double x, double y, double z, double range) {
        return exist(new Location(world, x, y, z), range);
    }

    public boolean exist(World world, double x, double y, double z) {
        return exist(new Location(world, x, y, z), 0.5);
    }

    public boolean exist(World world, int x, int y, int z, double range) {
        return exist(new Location(world, x, y, z), range);
    }

    public boolean exist(World world, int x, int y, int z) {
        return exist(new Location(world, x, y, z), 0.5);
    }

    public boolean exist(Location location) {
        return getHologram(location, 0.5) != null;
    }

    public boolean exist(Location location, double range) {
        return getHologram(location, range) != null;
    }

    public List<Hologram> getHologramList() {
        return holoList;
    }

    public Plugin getPlugin() {
        return plugin;
    }

    @EventHandler
    private void onPluginDisable(PluginDisableEvent event) {
        if (event.getPlugin().getName().equals(getPlugin().getName()))
            for (Hologram hologram : getHologramList())
                hologram.remove();
    }
}
