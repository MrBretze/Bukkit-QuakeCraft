package fr.bretzel.hologram;

import org.bukkit.Location;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.List;

public class Hologram
{

    private HoloEntity[] holoEntities = {};
    private String[] lines = {};
    private Location[] locations = {};
    private Location location;
    private final HologramManager holomanager;
    private boolean visible = false;

    public Hologram(World world, double x, double y, double z, String[] lines, HologramManager manager)
    {
        this(new Location(world, x, y, z), lines, manager);
    }

    public Hologram(Location location, String line, HologramManager manager)
    {
        this(location, new String[]{line}, manager);
    }

    public Hologram(World world, double x, double y, double z, String line, HologramManager manager)
    {
        this(new Location(world, x, y, z), line, manager);
    }

    public Hologram(Location location, String[] lines, HologramManager manager)
    {
        if (manager == null)
        {
            throw new NullPointerException("The hologram manager is not instanced !");
        }

        this.holomanager = manager;
        setLines(lines);
        setLocation(location);
        double yAdd = 0.0;
        List<Location> locs = new ArrayList<>();
        List<HoloEntity> holos = new ArrayList<>();
        for (String s : getLines())
        {
            Location loc = location.clone().add(0.0, yAdd, 0.0);
            locs.add(loc);
            yAdd += 0.5;
            HoloEntity entity = new HoloEntity(loc, s, getHolomanager().getPlugin());
            holos.add(entity);
        }
        setHoloEntities(holos.toArray(new HoloEntity[holos.size()]));
        setLocations(locs.toArray(new Location[locs.size()]));
        manager.getHologramList().add(this);
    }

    public boolean isVisible()
    {
        return this.visible;
    }

    public void setVisible(boolean b)
    {
        this.visible = b;
        for (HoloEntity e : getHoloEntities())
        {
            e.getStand().setCustomNameVisible(b);
        }
    }

    public HoloEntity[] getHoloEntities()
    {
        return holoEntities;
    }

    public void setHoloEntities(HoloEntity[] holoEntities)
    {
        this.holoEntities = holoEntities;
    }

    public Location[] getLocations()
    {
        return locations;
    }

    public void setLocations(Location[] locations)
    {
        this.locations = locations;
    }

    public String[] getLines()
    {
        return lines;
    }

    public void setLines(String[] lines)
    {
        this.lines = lines;
    }

    public Location getLocation()
    {
        return location;
    }

    public void setLocation(Location location)
    {
        this.location = location;
    }

    public HologramManager getHolomanager()
    {
        return holomanager;
    }

    public void remove()
    {
        for (HoloEntity entity : getHoloEntities())
        {
            entity.getStand().remove();
        }
    }
}
