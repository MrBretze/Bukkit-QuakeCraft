package fr.bretzel.raytrace;

import org.apache.commons.lang.Validate;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class RayTrace
{
    public static RayTraceResult rayTrace(Location origin, Vector direction, double entityRange, double blocksAway, double accuracy, Predicate<Location> predicate)
    {
        Vector progress = direction.normalize().multiply(new Vector(accuracy, accuracy, accuracy));
        Location orp = origin.clone();
        World world = origin.getWorld();
        Validate.notNull(world, "World cannot be null");

        ArrayList<Location> locationList = new ArrayList<>();
        ArrayList<Entity> entitiesList = new ArrayList<>();

        while (origin.distance(orp) <= blocksAway)
        {
            orp.add(progress);

            if (!predicate.test(orp))
            {
                BoundingBox boundingBox = new BoundingBox(orp.getBlock());
                if (boundingBox.isInside(orp.toVector()))
                    return new RayTraceResult(origin, orp.clone(), orp.getBlock(), entitiesList, locationList);
            }

            locationList.add(orp.clone());

            final Location entityScan = orp.clone();

            if (entityRange > 0 && entityScan.distance(origin) > 0.5)
                world.getEntities().stream().filter(entity ->
                {
                    if (entitiesList.contains(entity))
                        return false;

                    entityScan.subtract(progress);

                    while (entityScan.clone().subtract(progress).distance(orp) <= entityRange)
                        entityScan.subtract(progress);

                    return entity.getBoundingBox().expand(entityRange, 0, entityRange).contains(entityScan.getX(), entityScan.getY(), entityScan.getZ());
                }).forEach(entitiesList::add);
        }
        return new RayTraceResult(origin, orp.clone(), orp.getBlock(), entitiesList, locationList);
    }
}
