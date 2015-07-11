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
package fr.bretzel.quake;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Firework;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.util.Vector;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Created by MrBretzel on 11/06/2015.
 */

public class Util {

    public static LinkedList<Location> getLocationByDirection(Player player, int range, double distance) {
        if (distance == 0.0D) {
            distance = 1.0D;
        }
        LinkedList<Location> list = new LinkedList<>();
        Location playerEyes = player.getEyeLocation();
        Vector direction = playerEyes.getDirection().normalize();
        Location f = playerEyes.clone();
        Vector progress = direction.clone().multiply(distance);
        int maxRange = 100 * range / 70;
        boolean wallHack = false;

        for (int loop = maxRange; loop > 0; loop--) {
            f.add(progress);
            if (!wallHack && f.getBlock().getType().isSolid()) {
                break;
            }
            list.add(f.clone());
        }

        return list;
    }

    public static List<Player> getPlayerListInDirection(List<Location> locations, Player shoot, double distance) {
        List<Player> players = new ArrayList<>();
        for (Entity e : getEntityListInDirection(locations, shoot, distance)) {
            if (e instanceof Player) {
                Player p = (Player) e;
                if (p != shoot && !players.contains(p) && !p.isDead()) {
                    players.add(p);
                }
            }
        }
        return players;
    }

    public static List<Entity> getEntityListInDirection(List<Location> locations, Player shoot, double distance) {
        List<Entity> entities = new ArrayList<>();
        for (Location l : locations) {
            for (Entity e : l.getWorld().getEntities()) {
                if (e instanceof LivingEntity) {
                    LivingEntity j = (LivingEntity) e;
                    Location h = j.getLocation().add(0.0, 1, 0.0);

                    double px = h.getX();
                    double py = h.getY();
                    double pz = h.getZ();
                    boolean dX = Math.abs(l.getX() - px) < 1.0D * distance;
                    boolean dY = Math.abs(l.getY() - py) < 1.6D * distance;
                    boolean dZ = Math.abs(l.getZ() - pz) < 1.0D * distance;

                    if (dX && dY && dZ && !entities.contains(e) && e.getUniqueId() != shoot.getUniqueId() && !e.isDead()) {
                        entities.add(e);
                    }
                }
            }
        }
        return entities;
    }

    public static List<Block> blocksFromTwoPoints(Location loc1, Location loc2) {
        List<Block> blocks = new ArrayList<>();

        int topBlockX = (loc1.getBlockX() < loc2.getBlockX() ? loc2.getBlockX() : loc1.getBlockX());
        int bottomBlockX = (loc1.getBlockX() > loc2.getBlockX() ? loc2.getBlockX() : loc1.getBlockX());

        int topBlockY = (loc1.getBlockY() < loc2.getBlockY() ? loc2.getBlockY() : loc1.getBlockY());
        int bottomBlockY = (loc1.getBlockY() > loc2.getBlockY() ? loc2.getBlockY() : loc1.getBlockY());

        int topBlockZ = (loc1.getBlockZ() < loc2.getBlockZ() ? loc2.getBlockZ() : loc1.getBlockZ());
        int bottomBlockZ = (loc1.getBlockZ() > loc2.getBlockZ() ? loc2.getBlockZ() : loc1.getBlockZ());

        for (int x = bottomBlockX; x <= topBlockX; x++) {
            for (int z = bottomBlockZ; z <= topBlockZ; z++) {
                for (int y = bottomBlockY; y <= topBlockY; y++) {
                    Block block = loc1.getWorld().getBlockAt(x, y, z);
                    blocks.add(block);
                }
            }
        }

        return blocks;
    }

    public static String toStringLocation(Location location) {
        return (location.getWorld().getName() + ";") +
                location.getBlockX() + ";" +
                location.getBlockY() + ";" +
                location.getBlockZ() + ";" +
                location.getYaw() + ";" +
                location.getPitch() + "";
    }

    public static ChatColor getChatColorByInt(int i) {
        if (i >= 10) {
            return ChatColor.GREEN;
        } else if (i > 5 && i < 10) {
            return ChatColor.GOLD;
        } else {
            return ChatColor.RED;
        }
    }

    public static Location toLocationString(String string) {
        String[] strings = string.split(";");
        return new Location(Bukkit.getWorld(strings[0]), Double.valueOf(strings[1]),
                Double.valueOf(strings[2]),
                Double.valueOf(strings[3]),
                Float.valueOf(strings[4]),
                Float.valueOf(strings[5]));
    }


    /**
     * InstantFirework class made by TehHypnoz.
     * <p>
     * Credits to:
     * <p>
     * - fromgate, for explaining that setting the ticksFlown field to the expectedLifespan field will create instant fireworks.
     * - Skionz, for the getNMSClass() method.
     * <p>
     * Example usage:
     * FireworkEffect fireworkEffect = FireworkEffect.builder().flicker(false).trail(true).with(Type.BALL).withColor(Color.ORANGE).withFade(Color.RED).build();
     * Location location = p.getLocation();
     * new InstantFirework(fireworkEffect, location);
     */
    public static void shootFirework(Location location) {
        Random random = new Random();
        int fType = random.nextInt(5) + 1;
        FireworkEffect.Type type = null;
        switch (fType) {
            case 1:
                type = FireworkEffect.Type.BALL;
                break;
            case 2:
                type = FireworkEffect.Type.BALL_LARGE;
                break;
            case 3:
                type = FireworkEffect.Type.BURST;
                break;
            case 4:
                type = FireworkEffect.Type.CREEPER;
                break;
            case 5:
                type = FireworkEffect.Type.STAR;
        }
        FireworkEffect effect = FireworkEffect.builder().withFade(Arrays.asList(getColor(), getColor(), getColor()))
                .with(type)
                .withColor(Arrays.asList(getColor(), getColor(), getColor()))
                .flicker(random.nextBoolean()).trail(random.nextBoolean()).build();
        Firework f = location.getWorld().spawn(location, Firework.class);
        FireworkMeta fm = f.getFireworkMeta();
        fm.addEffect(effect);
        f.setFireworkMeta(fm);
        try {
            Class<?> entityFireworkClass = getClass("net.minecraft.server.", "EntityFireworks");
            Class<?> craftFireworkClass = getClass("org.bukkit.craftbukkit.", "entity.CraftFirework");
            Object firework = craftFireworkClass.cast(f);
            Method handle = firework.getClass().getMethod("getHandle");
            Object entityFirework = handle.invoke(firework);
            Field expectedLifespan = entityFireworkClass.getDeclaredField("expectedLifespan");
            Field ticksFlown = entityFireworkClass.getDeclaredField("ticksFlown");
            ticksFlown.setAccessible(true);
            ticksFlown.setInt(entityFirework, expectedLifespan.getInt(entityFirework) - 1);
            ticksFlown.setAccessible(false);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static String getCardinalDirection(Player player) {
        double rotation = (player.getLocation().getYaw() - 90) % 360;
        if (rotation < 0) {
            rotation += 360.0;
        }
        if (0 <= rotation && rotation < 22.5) {
            return "N";
        } else if (22.5 <= rotation && rotation < 67.5) {
            return "NE";
        } else if (67.5 <= rotation && rotation < 112.5) {
            return "E";
        } else if (112.5 <= rotation && rotation < 157.5) {
            return "SE";
        } else if (157.5 <= rotation && rotation < 202.5) {
            return "S";
        } else if (202.5 <= rotation && rotation < 247.5) {
            return "SW";
        } else if (247.5 <= rotation && rotation < 292.5) {
            return "W";
        } else if (292.5 <= rotation && rotation < 337.5) {
            return "NW";
        } else if (337.5 <= rotation && rotation < 360.0) {
            return "N";
        } else {
            return null;
        }
    }

    public static Location getRollBackLocation(Player player) {
        Location location = player.getLocation();
        switch (getCardinalDirection(player)) {
            case "N":
                location.add(0, 0, -1);
                break;
            case "E":
                location.add(1, 0, 0);
                break;
            case "S":
                location.add(0, 0, 1);
                break;
            case "W":
                location.add(-1, 0, 0);
                break;
            case "NE":
                location.add(1, 0, -1);
                break;
            case "SE":
                location.add(1, 0, 1);
                break;
            case "NW":
                location.add(-1, 0, -1);
                break;
            case "SW":
                location.add(-1, 0, 1);
                break;
        }
        return location;
    }


    public static Color getColor(int r, int v, int b) {
        return Color.fromRGB(r, v, b);
    }

    private static Color getColor() {
        Random random = new Random();
        return getColor(random.nextInt(255), random.nextInt(255), random.nextInt(255));
    }

    private static Class<?> getClass(String prefix, String nmsClassString) throws ClassNotFoundException {
        String version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3] + ".";
        String name = prefix + version + nmsClassString;
        Class<?> nmsClass = Class.forName(name);
        return nmsClass;
    }
}
