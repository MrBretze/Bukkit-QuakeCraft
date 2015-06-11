package fr.bretzel.quake;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MrBretzel on 11/06/2015.
 */

public class Util {

    public static List<Location> getLocationByDirection(Player player, int range, double distance) {
        if(distance == 0.0D) {
            distance = 1.0D;
        }
        List<Location> list = new ArrayList<>();
        Location playerEyes = player.getEyeLocation();
        Vector direction = playerEyes.getDirection().normalize();
        Location f = playerEyes.clone();
        Vector progress = direction.clone().multiply(distance);
        int maxRange = 100 * range / 70;
        boolean wallHack = false;

        for(int loop = 0; loop < maxRange; loop++) {
            f.add(progress);
            if(!wallHack && f.getBlock().getType().isSolid()) {
                break;
            }
            list.add(f.clone());
        }

        return list;
    }

    public static List<Player> getPlayerListInDirection(List<Location> locations, Player shoot, double distance) {
        List<Player> players = new ArrayList<>();
        for(Entity e : getEntityListInDirection(locations, distance)) {
            if(e instanceof Player) {
                Player p = (Player) e;
                if(p != shoot && !players.contains(p) && !p.isDead()) {
                    players.add(p);
                }
            }
        }
        return players;
    }

    public static List<Entity> getEntityListInDirection(List<Location> locations, double distance) {
        List<Entity> entities = new ArrayList<>();
        for(Location l : locations) {
            entities.addAll(l.getWorld().getNearbyEntities(l, distance, distance, distance));
        }
        return entities;
    }
}
