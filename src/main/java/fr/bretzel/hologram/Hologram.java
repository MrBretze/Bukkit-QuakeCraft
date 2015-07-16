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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by mrbretzel on 13/07/15.
 */

public class Hologram {

    private HoloEntity[] holoEntities = {};
    private String[] lines = {};
    private Location[] locations = {};
    private Location location;

    public Hologram(World world, double x, double y, double z, String[] lines, HologramManager manager) {
        this(new Location(world, x, y, z), lines, manager);
    }

    public Hologram(Location location, String line, HologramManager manager) {
        this(location, new String[]{line}, manager);
    }

    public Hologram(World world, double x, double y, double z, String line, HologramManager manager) {
        this(new Location(world, x, y, z), line, manager);
    }

    public Hologram(Location location, String[] lines, HologramManager manager) {
        if(manager == null) {
            throw new NullPointerException("The hologram manager is not instanced !");
        }
        setLines(lines);
        setLocation(location);
        double yAdd = 0.0;
        List<Location> locs = new ArrayList<>();
        List<HoloEntity> holos = new ArrayList<>();
        for (String s : getLines()) {
            Location loc = location.clone().add(0.0, yAdd, 0.0);
            locs.add(loc);
            yAdd += 0.5;
            HoloEntity entity = new HoloEntity(loc, s, HologramManager.getPlugin());
            holos.add(entity);
        }
        setHoloEntities(holos.toArray(new HoloEntity[holos.size()]));
        setLocations(locs.toArray(new Location[locs.size()]));
        manager.getHoloList().add(this);
    }

    public void display(boolean b) {
        for(HoloEntity e : getHoloEntities()) {
            e.getStand().setCustomNameVisible(b);
        }
    }

    public HoloEntity[] getHoloEntities() {
        return holoEntities;
    }

    public Location[] getLocations() {
        return locations;
    }

    public String[] getLines() {
        return lines;
    }

    public Location getLocation() {
        return location;
    }

    public void setHoloEntities(HoloEntity[] holoEntities) {
        this.holoEntities = holoEntities;
    }

    public void setLines(String[] lines) {
        this.lines = lines;
    }

    public void setLocations(Location[] locations) {
        this.locations = locations;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public void remove() {
        for(HoloEntity entity : getHoloEntities()) {
            entity.getStand().remove();
        }
    }
}
