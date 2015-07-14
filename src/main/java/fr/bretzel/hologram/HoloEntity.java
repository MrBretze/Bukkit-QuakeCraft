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
import org.bukkit.plugin.Plugin;

/**
 * Created by mrbretzel on 13/07/15.
 */

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
