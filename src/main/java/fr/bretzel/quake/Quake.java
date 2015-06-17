package fr.bretzel.quake;

import fr.bretzel.quake.arena.Arena;
import fr.bretzel.quake.arena.ArenaManager;
import fr.bretzel.quake.player.PlayerInfo;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

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
            PlayerInfo playerInfo = getPlayerInfo(((Player)sender));

            sender.sendMessage(playerInfo.getFirstLocation() + " ");

            sender.sendMessage(playerInfo.getSecondLocation() + " ");
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
}
