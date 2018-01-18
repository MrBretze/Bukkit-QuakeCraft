/**
 * Copyright 2015 Lo�c Nussbaumer
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
package fr.bretzel.quake.util;

import org.bukkit.*;
import org.bukkit.entity.*;
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

    public static void playSound(Location location, Sound sound, float volume, float pitch) {
        for(Player player : location.getWorld().getPlayers()) {
            player.playSound(location, sound, volume, pitch);
        }
    }

    public static void playSound(Location location, Sound sound) {
        playSound(location, sound, 1F, 1F);
    }

    public static void playSound(Location location, String sound) {
        playSound(location, Sound.valueOf(sound), 1F, 1F);
    }

    public static void playSound(Location location, String sound, float f1, float f2) {
        playSound(location, Sound.valueOf(sound), f1, f2);
    }

    public static void playSound(World world, double x, double y, double z, Sound sound) {
        playSound(new Location(world, x, y, z), sound, 1F, 1F);
    }

    public static void playSound(World world, double x, double y, double z, Sound sound, float f1, float f2) {
        playSound(new Location(world, x, y, z), sound, f1, f2);
    }

    public static void playSound(World world, double x, double y, double z, String sound) {
        playSound(new Location(world, x, y, z), sound, 1F, 1F);
    }

    public static void playSound(World world, double x, double y, double z, String sound, float f1, float f2) {
        playSound(new Location(world, x, y, z), sound, f1, f2);
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
        FireworkEffect.Type type = getFireworkType(fType);
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

    public static FireworkEffect.Type getFireworkType(int i) {
        switch (i) {
            case 1:
                return FireworkEffect.Type.BALL;
            case 2:
                return FireworkEffect.Type.BALL_LARGE;
            case 3:
                return FireworkEffect.Type.BURST;
            case 4:
                return FireworkEffect.Type.CREEPER;
            case 5:
                return FireworkEffect.Type.STAR;
            default:
                return FireworkEffect.Type.BALL;
        }
    }

    public static void spawnFirework(List<Location> location) {
        Iterator<Location> iterator = location.iterator();
        while (iterator.hasNext()) {
            Location l = iterator.next();
            Random random = new Random();
            Firework fw = (Firework) l.getWorld().spawnEntity(l, EntityType.FIREWORK);
            FireworkMeta fwm = fw.getFireworkMeta();
            int rt = random.nextInt(5) + 1;
            FireworkEffect.Type type = getFireworkType(rt);
            int u = random.nextInt(256);
            int b = random.nextInt(256);
            int g = random.nextInt(256);
            Color c1 = Color.fromRGB(u, g, b);
            u = random.nextInt(256);
            b = random.nextInt(256);
            g = random.nextInt(256);
            Color c2 = Color.fromRGB(u, g, b);
            FireworkEffect effect = FireworkEffect.builder().flicker(random.nextBoolean()).withColor(c1).withFade(c2).with(type).trail(random.nextBoolean()).build();
            fwm.addEffect(effect);
            int rp = random.nextInt(2) + 1;
            fwm.setPower(rp);
            fw.setFireworkMeta(fwm);
        }
    }

    public static List<Location> getCircle(Location center, double radius, int amount){
        World world = center.getWorld();
        double increment = (2 * Math.PI) / amount;
        ArrayList<Location> locations = new ArrayList<>();
        for (int i = 0;i < amount; i++) {
            double angle = i * increment;
            double x = center.getX() + (radius * Math.cos(angle));
            double z = center.getZ() + (radius * Math.sin(angle));
            locations.add(new Location(world, x, center.getY(), z));
        }
        return locations;
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
