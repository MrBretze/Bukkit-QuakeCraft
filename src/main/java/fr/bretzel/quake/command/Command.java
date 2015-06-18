package fr.bretzel.quake.command;

import com.google.common.collect.ImmutableList;

import fr.bretzel.quake.Quake;
import fr.bretzel.quake.arena.Arena;
import fr.bretzel.quake.arena.ArenaManager;
import fr.bretzel.quake.player.PlayerInfo;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MrBretzel on 14/06/2015.
 */
public class Command implements CommandExecutor, TabCompleter {

    private ArenaManager manager = Quake.arenaManager;

    private List<String> MAIN = ImmutableList.of("create", "edit", "link", "delete");

    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            PlayerInfo info = Quake.getPlayerInfo(player);
            if (player.hasPermission("quake.command")) {
                if (args.length > 0) {
                    if (args[0].equalsIgnoreCase("create")) {
                        if (player.hasPermission("quake.command.create")) {
                            if (args.length > 1) {
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
                    } else if (args[0].equalsIgnoreCase("edit")) {
                        if (args.length > 1) {
                            if (manager.getArenaByName(args[1]) != null) {
                                Arena arena = manager.getArenaByName(args[1]);
                                if (args.length > 2) {
                                    if(args[2].equalsIgnoreCase("setspawn")) {
                                        arena.setSpawn(player.getLocation());
                                        player.sendMessage(ChatColor.GREEN + "The new spawn for " + arena.getName() + " has bin set tou your position !");

                                    } else if (args[2].equalsIgnoreCase("addrespawn")) {
                                        arena.addRespawn(player.getLocation().add(0.0, 1, 0.0).clone());
                                        player.sendMessage(ChatColor.GREEN + "The respawn point has bin set tout your position");
                                    } else if(args[2].equalsIgnoreCase("view")) {
                                        if(arena.getRespawn().isEmpty()) {
                                            player.sendMessage(ChatColor.GREEN + "The respawn has bin not set for the arena !");
                                            return true;
                                        } else {
                                            arena.view();
                                            if(arena.isView()) {
                                                player.sendMessage(ChatColor.GREEN + "The respawn location is visible !");
                                                return true;
                                            } else {
                                                player.sendMessage(ChatColor.GREEN + "The respawn location is not visible !");
                                                return true;
                                            }
                                        }
                                    } else {
                                        player.sendMessage(ChatColor.RED + "Usage: /quake edit " + arena.getName() + " <setspawn | addrespawn | view>");
                                        return true;
                                    }
                                } else {
                                    player.sendMessage(ChatColor.RED + "Usage: /quake edit " + arena.getName() + " <setspawn | addrespawn | view>");
                                    return true;
                                }
                            } else {
                                player.sendMessage(ChatColor.RED + "The arena is not found !");
                                return true;
                            }
                        } else {
                            player.sendMessage(ChatColor.RED + "Usage: /quake edit <arena>");
                            return true;
                        }
                    } else if (args[0].equalsIgnoreCase("delete")) {
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
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, org.bukkit.command.Command command, String label, String[] args) {
        if(args.length == 1) {
            return (List) StringUtil.copyPartialMatches(args[0], MAIN, new ArrayList<>());
        } else {
            return ImmutableList.of();
        }
    }
}
