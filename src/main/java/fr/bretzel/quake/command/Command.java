package fr.bretzel.quake.command;

import com.google.common.collect.ImmutableList;

import fr.bretzel.quake.Quake;
import fr.bretzel.quake.arena.ArenaManager;
import fr.bretzel.quake.player.PlayerInfo;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Created by MrBretzel on 14/06/2015.
 */
public class Command implements CommandExecutor, TabCompleter {

    private ArenaManager manager = Quake.arenaManager;

    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        if(sender instanceof Player) {
            Player player = (Player) sender;
            PlayerInfo info = Quake.getPlayerInfo(player);
            if(player.hasPermission("quake.command")) {
                if (args.length > 0) {
                    if(args[0].equalsIgnoreCase("create")) {
                        if(player.hasPermission("quake.command.create")) {
                            if(args.length > 1) {
                                manager.registerArena(player, args[1], info.getFirstLocation(), info.getSecondLocation());
                                return true;
                            } else {
                                player.sendMessage(ChatColor.RED + "Usage: /quake create <name>");
                                return true;
                            }
                        } else {
                            player.sendMessage(ChatColor.RED + "You dont have the permission for this command.");
                            return true;
                        }
                    } else if(args[0].equalsIgnoreCase("edit")) {
                        /*
                         TODO:
                        */
                        return true;
                    } else if(args[0].equalsIgnoreCase("delete")) {
                        /*
                         TODO:
                        */
                        return true;
                    } else {
                        player.sendMessage(ChatColor.RED + "Usage: /quake <create | edit | delete>");
                        return true;
                    }
                } else {
                    player.sendMessage(ChatColor.RED + "Usage: /quake <create | edit | delete>");
                    return true;
                }
            } else {
                sender.sendMessage(ChatColor.RED + "You dont have the permission for this command.");
                return true;
            }
        } else {
            sender.sendMessage(ChatColor.RED + "Player ?");
            return true;
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, org.bukkit.command.Command command, String s, String[] strings) {
        return ImmutableList.of();
    }
}
