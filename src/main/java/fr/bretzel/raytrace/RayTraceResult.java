package fr.bretzel.raytrace;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;

import java.util.List;

public class RayTraceResult
{
    private final List<Entity> entities;
    private final List<Location> locations;
    private final Location endLocation;
    private final Location startLocation;

    public RayTraceResult(Location startLocation, Location hitLocation, Block hitBlock, List<Entity> entities, List<Location> locations)
    {
        this.startLocation = startLocation;
        this.endLocation = hitLocation;
        this.locations = locations;
        this.entities = entities;
    }

    public List<Entity> getEntities()
    {
        return entities;
    }

    public Location getHitLocation()
    {
        return endLocation;
    }

    public Location getStartLocation()
    {
        return startLocation;
    }

    public List<Location> getLocation()
    {
        return locations;
    }
}
