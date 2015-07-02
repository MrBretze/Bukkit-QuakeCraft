package fr.bretzel.quake;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

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
                    boolean dX = Math.abs(l.getX() - px) < 0.9D * distance;
                    boolean dY = Math.abs(l.getY() - py) < 1.6D * distance;
                    boolean dZ = Math.abs(l.getZ() - pz) < 0.9D * distance;

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
        List<Block> blocks = new ArrayList<Block>();

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

    public static String toBinary(String s) {
        StringBuilder builder = new StringBuilder();
        byte[] bytes = s.getBytes();

        for (byte b : bytes) {
            int val = b;
            for (int i = 0; i < 8; i++)
            {
                builder.append((val & 128) == 0 ? 0 : 1);
                val <<= 1;
            }
            builder.append(' ');
        }
        return builder.toString();
    }

    public static String toText(String s){
        int charCode = Integer.parseInt(s, 2);
        return Character.toString((char) charCode);
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

    public static void respawn(Player player) throws InvocationTargetException {
        try {
            Object nmsPlayer = player.getClass().getMethod("getHandle").invoke(player);
            Object con = nmsPlayer.getClass().getDeclaredField("playerConnection").get(nmsPlayer);

            Class<?> EntityPlayer = Class.forName(nmsPlayer.getClass().getPackage().getName() + ".EntityPlayer");

            Field minecraftServer = con.getClass().getDeclaredField("minecraftServer");
            minecraftServer.setAccessible(true);
            Object mcserver = minecraftServer.get(con);

            Object playerlist = mcserver.getClass().getDeclaredMethod("getPlayerList").invoke(mcserver);
            Method moveToWorld = playerlist.getClass().getMethod("moveToWorld", EntityPlayer, int.class, boolean.class);
            moveToWorld.invoke(playerlist, nmsPlayer, 0, false);
        } catch (NoSuchMethodException | IllegalAccessException | ClassNotFoundException | NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    public static void shootFirework(Location l) {
        FireworkEffectPlayer pEffect = new FireworkEffectPlayer();
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

        Color c1 = getColor(r.nextInt(255), r.nextInt(255), r.nextInt(255));
        Color c2 = getColor(r.nextInt(255), r.nextInt(255), r.nextInt(255));
        FireworkEffect effect = FireworkEffect.builder().withColor(c1).withFade(c2).flicker(r.nextBoolean()).trail(r.nextBoolean()).with(type).build();
        pEffect.playFirework(l.getWorld(), l, effect);
    }

    private static Color getColor(int r, int v, int b) {
        return Color.fromRGB(r, v, b);
    }

    public static void download(URL address, File localFileName) {
        OutputStream out = null;
        URLConnection conn;
        InputStream in = null;

        if (!localFileName.exists()) {
            try {
                localFileName.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            URL url = address;
            out = new BufferedOutputStream(new FileOutputStream(localFileName));
            conn = url.openConnection();
            in = conn.getInputStream();
            byte[] buffer = new byte[1024];

            int numRead;
            while ((numRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, numRead);
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
                if (out != null) {
                    out.close();
                }
            } catch (IOException ioe) {
            }
        }
    }
}
