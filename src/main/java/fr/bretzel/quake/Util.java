/**
 * Copyright 2015 Loïc Nussbaumer
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you
 * may not use this file except in compliance with the License. You
 * may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Created by MrBretzel on 11/06/2015.
 */

public class Util {

    public static LinkedList<Location> getLocationByDirection(Player player, int range, double distance) {
        if(distance == 0.0D) {
            distance = 1.0D;
        }
        LinkedList<Location> list = new LinkedList<>();
        Location playerEyes = player.getEyeLocation();
        Vector direction = playerEyes.getDirection().normalize();
        Location f = playerEyes.clone();
        Vector progress = direction.clone().multiply(distance);
        int maxRange = 100 * range / 70;
        boolean wallHack = false;

        for(int loop = maxRange; loop > 0; loop--) {
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
        for(Entity e : getEntityListInDirection(locations, shoot, distance)) {
            if(e instanceof Player) {
                Player p = (Player) e;
                if(p != shoot && !players.contains(p) && !p.isDead()) {
                    players.add(p);
                }
            }
        }
        return players;
    }

    public static List<Entity> getEntityListInDirection(List<Location> locations, Player shoot, double distance) {
        List<Entity> entities = new ArrayList<>();
        for(Location l : locations) {
            for(Entity e : l.getWorld().getEntities()) {
                if (e instanceof LivingEntity) {
                    LivingEntity j = (LivingEntity) e;
                    Location h = j.getLocation().add(0.0, 1, 0.0);

                    double px = h.getX();
                    double py = h.getY();
                    double pz = h.getZ();
                    boolean dX = Math.abs(l.getX() - px) < 1.0D * distance;
                    boolean dY = Math.abs(l.getY() - py) < 1.6D * distance;
                    boolean dZ = Math.abs(l.getZ() - pz) < 1.0D * distance;

                    if(dX && dY && dZ && !entities.contains(e) && e.getUniqueId() != shoot.getUniqueId() && !e.isDead()) {
                        entities.add(e);
                    }
                }
            }
        }
        return entities;
    }

    public static List<Block> blocksFromTwoPoints(Location loc1, Location loc2)
    {
        List<Block> blocks = new ArrayList<>();

        int topBlockX = (loc1.getBlockX() < loc2.getBlockX() ? loc2.getBlockX() : loc1.getBlockX());
        int bottomBlockX = (loc1.getBlockX() > loc2.getBlockX() ? loc2.getBlockX() : loc1.getBlockX());

        int topBlockY = (loc1.getBlockY() < loc2.getBlockY() ? loc2.getBlockY() : loc1.getBlockY());
        int bottomBlockY = (loc1.getBlockY() > loc2.getBlockY() ? loc2.getBlockY() : loc1.getBlockY());

        int topBlockZ = (loc1.getBlockZ() < loc2.getBlockZ() ? loc2.getBlockZ() : loc1.getBlockZ());
        int bottomBlockZ = (loc1.getBlockZ() > loc2.getBlockZ() ? loc2.getBlockZ() : loc1.getBlockZ());

        for(int x = bottomBlockX; x <= topBlockX; x++)
        {
            for(int z = bottomBlockZ; z <= topBlockZ; z++)
            {
                for(int y = bottomBlockY; y <= topBlockY; y++)
                {
                    Block block = loc1.getWorld().getBlockAt(x, y, z);
                    blocks.add(block);
                }
            }
        }

        return blocks;
    }

    public static String toStringLocation(Location location) {
        return (location.getWorld().getName() + ";") + location.getBlockX() + ";" + location.getBlockY() + ";" + location.getBlockZ() + ";" + location.getYaw() + ";" + location.getPitch() + "";
    }

    public static ChatColor getChatColorByInt(int i) {
        if(i >= 10) {
            return ChatColor.GREEN;
        } else if(i > 5 && i < 10) {
            return ChatColor.GOLD;
        } else {
            return ChatColor.RED;
        }
    }

    public static Location toLocationString(String string) {
        String[] strings = string.split(";");
        return new Location(Bukkit.getWorld(strings[0]), Double.valueOf(strings[1]), Double.valueOf(strings[2]), Double.valueOf(strings[3]), Float.valueOf(strings[4]), Float.valueOf(strings[5]));
    }

    public static void shootFirework(Location l) {
        Random r = new Random();
        int fType = r.nextInt(5) + 1;
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
        FireworkEffect effect = FireworkEffect.builder().withColor(Arrays.asList(getColor(), getColor(), getColor(), getColor())).withFade(Arrays.asList(getColor(), getColor(), getColor(), getColor())).flicker(r.nextBoolean()).trail(r.nextBoolean()).with(type).build();

        Firework firework = l.getWorld().spawn(l, Firework.class);
        firework.getFireworkMeta().addEffect(effect);

        Object fwHandle = getHandle(firework);

        FireworkMeta data = firework.getFireworkMeta();
        data.clearEffects();
        data.setPower(1);
        data.addEffect(effect);
        firework.setFireworkMeta(data);

        try {
            Object packet = getMinecraftServerClass("PacketPlayOutEntityStatus").getConstructor(fwHandle.getClass().getSuperclass(), Byte.TYPE).newInstance(fwHandle, (byte) 17);
            for(Player p : l.getWorld().getPlayers()) {
                Object handle = getHandle(p);
                Object connection = handle.getClass().getDeclaredField("playerConnection").get(handle);
                Method sendPacket = getMethod(connection.getClass(), "sendPacket");
                sendPacket.invoke(connection, packet);
                firework.detonate();
                firework.remove();
            }
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | NoSuchFieldException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }


    private static Color getColor(int r, int v, int b) {
        return Color.fromRGB(r, v, b);
    }

    private static Color getColor() {
        Random random = new Random();
        return Color.fromRGB(random.nextInt(255), random.nextInt(255), random.nextInt(255));
    }

    private static Object getHandle(Object obj) {
        try {
            return getMethod(obj.getClass(), "getHandle").invoke(obj);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static Method getMethod(Class<?> clazz, String name, Class<?>... args) {
        for (Method m : clazz.getMethods())
            if (m.getName().equals(name)
                    && (args.length == 0 || ClassListEqual(args,
                    m.getParameterTypes()))) {
                m.setAccessible(true);
                return m;
            }
        return null;
    }

    private static boolean ClassListEqual(Class<?>[] l1, Class<?>[] l2) {
        boolean equal = true;
        if (l1.length != l2.length)
            return false;
        for (int i = 0; i < l1.length; i++)
            if (l1[i] != l2[i]) {
                equal = false;
                break;
            }
        return equal;
    }

    public static Class<?> getMinecraftServerClass(String name) {
        try {
            return Class.forName("net.minecraft.server." + getBukkitVersion() + "." + name);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getBukkitVersion() {
        String packageName = Bukkit.getServer().getClass().getPackage().getName();
        return packageName.substring(packageName.lastIndexOf('.') + 1);
    }
}
