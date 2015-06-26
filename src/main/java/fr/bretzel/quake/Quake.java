package fr.bretzel.quake;


import fr.bretzel.nbt.NBTCompressedStreamTools;
import fr.bretzel.quake.game.Game;
import fr.bretzel.quake.game.GameManager;
import fr.bretzel.quake.reader.GameReader;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

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
    public static GameManager gameManager;
    public static Quake quake;
    private static LinkedList<PlayerInfo> playerInfos = new LinkedList<>();

    @Override
    public void onEnable() {
        quake = this;

        getDataFolder().mkdir();

        manager = getServer().getPluginManager();

        gameManager = new GameManager(this);

        getCommand("quake").setExecutor(new fr.bretzel.quake.command.Command());
        getCommand("quake").setTabCompleter(new fr.bretzel.quake.command.Command());

        File file = new File(getDataFolder(), File.separator + "game" + File.separator);

        if(file.exists() && file.isDirectory()) {
            if(file.listFiles().length > 0) {
                initGame(file);
            }
        }

        saveResource("config.yml", false);

        reloadConfig();
    }

    @Override
    public void onDisable() {
        saveConfig();

        for(PlayerInfo playerInfo : playerInfos) {
            playerInfo.save();
        }

        for(Game game : gameManager.getGameLinkedList()) {
            game.save();
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(label.equals("test")) {
            final Player player = (Player) sender;
            player.setHealth(0);
            BukkitRunnable runnable = new BukkitRunnable() {
                @Override
                public void run() {
                    Util.respawn(player);
                }
            };
            runnable.runTaskLater(Quake.quake, 20L);
            return true;
        }
        return true;
    }

    public static PlayerInfo getPlayerInfo(Player player) {
        for(PlayerInfo pi : playerInfos) {
            if(pi.getPlayer() == player) {
                return pi;
            }
        }
        PlayerInfo info = new PlayerInfo(player);
        playerInfos.add(info);
        return info;
    }

    private void initGame(File file) {
        for (File f : file.listFiles()) {
            try {
                GameReader.read(NBTCompressedStreamTools.read(new FileInputStream(f)), f);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
