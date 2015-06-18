package fr.bretzel.quake;

import com.evilco.mc.nbt.TagCompound;
import com.evilco.mc.nbt.stream.NbtInputStream;

import fr.bretzel.quake.arena.Arena;
import fr.bretzel.quake.arena.ArenaManager;
import fr.bretzel.quake.player.PlayerInfo;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

/**
 * Created by MrBretzel on 09/06/2015.
 */

public class Quake extends JavaPlugin implements Listener {

    public static PluginManager manager;

    public static ArenaManager arenaManager;

    public static Quake quake;

    private static LinkedList<PlayerInfo> playerInfos = new LinkedList<>();

    @Override
    public void onEnable() {
        quake = this;

        getDataFolder().mkdir();

        manager = getServer().getPluginManager();

        arenaManager = new ArenaManager(this);

        getCommand("quake").setExecutor(new fr.bretzel.quake.command.Command());
        getCommand("quake").setTabCompleter(new fr.bretzel.quake.command.Command());

        File file = new File(getDataFolder(), File.separator + "arena" + File.separator);

        if(file.exists() && file.isDirectory()) {
            if(file.listFiles().length > 0) {
                Arena arena = new Arena(this);

                for (File f : file.listFiles()) {
                    try {
                        String name = f.getName().replace(".dat", "");
                        arena.setName(name);

                        NbtInputStream stream = new NbtInputStream(new FileInputStream(f));

                        TagCompound compound = (TagCompound)stream.readTag();

                        arena.setCompound(compound);

                        arena.setFirstLocation(toLocationString(compound.getString("location1")));

                        arena.setSecondLocation(toLocationString(compound.getString("location2")));

                        if(compound.getTag("respawn") != null) {
                            TagCompound respawn = compound.getCompound("respawn");
                            int u = respawn.getInteger("size");

                            for(int i = 0; i < u; i++) {
                                Location location = toLocationString(respawn.getString(String.valueOf(i))); //TODO: A tester !!
                                arena.addRespawn(location);
                            }
                        }

                        for(Block b : Util.blocksFromTwoPoints(arena.getFirstLocation(), arena.getSecondLocation())) {
                            arena.addBlock(b);
                        }

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    public void onDisable() {
        for(PlayerInfo playerInfo : playerInfos) {
            playerInfo.save();
        }

        for(Arena arena : arenaManager.getArenaLinkedList()) {
            arena.save();
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(label.equals("test")) {
            Player player = (Player) sender;

            for(Arena arena : arenaManager.getArenaLinkedList()) {
                for(Location location : arena.getRespawn()) {
                    player.sendMessage(location.toString());
                }
            }

            return true;
        }
        return true;
    }

    public static PlayerInfo getPlayerInfo(Player player) {
        PlayerInfo playerInfo = null;
        for(PlayerInfo pi : playerInfos) {
            if(pi.getPlayer() == player) {
                playerInfo = pi;
            }
        }
        if(playerInfo == null) {
            playerInfo = new PlayerInfo(player);
            playerInfos.add(playerInfo);
        }
        return playerInfo;
    }

    private String toStringLocation(Location location) {
        StringBuilder builder = new StringBuilder();
        builder.append(location.getWorld().getName() + ";")
                .append(location.getBlockX() + ";")
                .append(location.getBlockY() + ";")
                .append(location.getBlockZ() + ";");
        return builder.toString();
    }

    private Location toLocationString(String string) {
        String[] strings = string.split(";");
        return new Location(Bukkit.getWorld(strings[0]), Double.valueOf(strings[1]), Double.valueOf(strings[2]), Double.valueOf(strings[3]));
    }
}
