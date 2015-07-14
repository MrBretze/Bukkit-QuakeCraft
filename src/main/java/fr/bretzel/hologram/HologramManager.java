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
package fr.bretzel.hologram;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
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
    private static Plugin plugin;

    public HologramManager(Plugin plugin) {
        setPlugin(plugin);
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
        for (Hologram holo : getHoloList()) {
            if (holo.getLocation().distance(location) <= range) {
                return holo;
            }
        }
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

    public void removeHologram(Location location) {
        this.removeHologram(location, 0.5);
    }

    public void removeHologram(Location location, double range) {
        Hologram hologram = getHologram(location, range);
        if (hologram == null) {
            return;
        }
        for (HoloEntity e : hologram.getHoloEntities()) {
            ArmorStand stand = e.getStand();
            if (stand != null)
                stand.remove();
        }
    }

    public List<Hologram> getHoloList() {
        return holoList;
    }

    public void setHoloList(List<Hologram> holoList) {
        this.holoList = holoList;
    }

    public static Plugin getPlugin() {
        return plugin;
    }

    public void setPlugin(Plugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPluginDisable(PluginDisableEvent event) {
        Plugin plugin = event.getPlugin();
        if(plugin == getPlugin()) {
            for(Hologram hologram : getHoloList()) {
                for(HoloEntity e : hologram.getHoloEntities()) {
                    e.getStand().remove();
                }
            }
        }
    }
}
