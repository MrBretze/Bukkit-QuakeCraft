/**
 * Copyright 2015 Loïc Nussbaumer
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you
 * may not use this file except in compliance with the License. You
 * may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License. See accompanying
 * LICENSE file.
 */
package fr.bretzel.quake.hologram;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mrbretzel on 13/07/15.
 */

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
