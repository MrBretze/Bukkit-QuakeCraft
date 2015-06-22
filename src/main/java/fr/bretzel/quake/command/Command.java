package fr.bretzel.quake.command;

import com.google.common.collect.ImmutableList;

import fr.bretzel.quake.Quake;
import fr.bretzel.quake.Util;
import fr.bretzel.quake.game.Game;
import fr.bretzel.quake.game.GameManager;
import fr.bretzel.quake.PlayerInfo;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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

    private GameManager manager = Quake.gameManager;

    private List<String> MAIN = ImmutableList.of("create", "edit", "player", "delete", "setlobby");

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
                                manager.registerGame(player, args[1], info.getFirstLocation(), info.getSecondLocation());
                                return true;
                            } else {
                                player.sendMessage(ChatColor.RED + "Usage: /quake create <name>");
                                return true;
                            }
                        } else {
                            player.sendMessage(ChatColor.RED + "You don't have the permission for this command.");
                            return true;
                        }
                    } else if (args[0].equalsIgnoreCase("edit")) {
                        if (args.length > 1) {
                            if (manager.getGameByName(args[1]) != null) {
                                Game game = manager.getGameByName(args[1]);
                                if (args.length > 2) {
                                    if(args[2].equalsIgnoreCase("setspawn")) {
                                        game.setSpawn(player.getLocation());
                                        player.sendMessage(ChatColor.GREEN + "The new spawn for " + game.getName() + " has been set tou your position !");
                                        return true;
                                    } else if (args[2].equalsIgnoreCase("addrespawn")) {
                                        game.addRespawn(player.getLocation().add(0.0, 1, 0.0).clone());
                                        player.sendMessage(ChatColor.GREEN + "The respawn point has been set tout your position");
                                        return true;
                                    } else if(args[2].equalsIgnoreCase("view")) {
                                        if(game.getRespawn().isEmpty()) {
                                            player.sendMessage(ChatColor.GREEN + "The respawn has been not set for the game !");
                                            return true;
                                        } else {
                                            game.view();
                                            if(game.isView()) {
                                                player.sendMessage(ChatColor.GREEN + "The respawn location is visible !");
                                                return true;
                                            } else {
                                                player.sendMessage(ChatColor.GREEN + "The respawn location is not visible !");
                                                return true;
                                            }
                                        }
                                    } else {
                                        player.sendMessage(ChatColor.RED + "Usage: /quake edit " + game.getName() + " <setspawn | addrespawn | view>");
                                        return true;
                                    }
                                } else {
                                    player.sendMessage(ChatColor.RED + "Usage: /quake edit " + game.getName() + " <setspawn | addrespawn | view>");
                                    return true;
                                }
                            } else {
                                player.sendMessage(ChatColor.RED + "The game is not found !");
                                return true;
                            }
                        } else {
                            player.sendMessage(ChatColor.RED + "Usage: /quake edit <game>");
                            return true;
                        }
                    } else if (args[0].equalsIgnoreCase("delete")) {
                        /*
                         *TODO:
                        */
                        return true;
                    } else if(args[0].equalsIgnoreCase("player")) {
                        if(args.length > 1) {
                            if(Bukkit.getPlayer(args[1]) != null) {
                                Player target = Bukkit.getPlayer(args[1]);
                                if(args.length > 2) {
                                    if(args[2].equalsIgnoreCase("join")) {
                                        Game b = manager.getGameByPlayer(target);
                                        if(args.length > 3) {
                                            if(manager.getGameByName(args[3]) != null && b == null) {
                                                manager.getGameByName(args[3]).addPlayer(target);
                                                player.sendMessage(ChatColor.GREEN + "Player has join the game !");
                                            } else if(b != null) {
                                                player.sendMessage(ChatColor.RED + "The player is already in a a game !");
                                                return true;
                                            } else if(manager.getGameByName(args[3]) == null) {
                                                player.sendMessage(ChatColor.RED + "Game not found !");
                                                return true;
                                            } else {
                                                player.sendMessage(ChatColor.RED + "Usage: /quake player " + target.getDisplayName() + " join <game>");
                                                return true;
                                            }
                                        } else {
                                            player.sendMessage(ChatColor.RED + "Usage: /quake player " + target.getDisplayName() + " join <game>");
                                            return true;
                                        }
                                    } else if(args[2].equalsIgnoreCase("quit")) {
                                        if(manager.getGameByPlayer(target) != null) {
                                            manager.signEvent.actualiseJoinSignForGame(manager.getGameByPlayer(target));
                                            manager.getGameByPlayer(target).getPlayerList().remove(target.getUniqueId());
                                            player.sendMessage(ChatColor.GREEN + "Player has left the game !");
                                            return true;
                                        } else {
                                            player.sendMessage(ChatColor.RED + "Game not found !");
                                            return true;
                                        }
                                    } else {
                                        player.sendMessage(ChatColor.RED + "Usage: /quake player <player> <join | quit>");
                                        return true;
                                    }
                                } else {
                                    player.sendMessage(ChatColor.RED + "Usage: /quake player <player> <join | quit>");
                                    return true;
                                }
                            } else {
                                player.sendMessage(ChatColor.RED + "Player is not online !");
                                return true;
                            }
                        } else {
                            player.sendMessage(ChatColor.RED + "Usage: /quake player <player> <join | quit>");
                            return true;
                        }
                    } else if (args[0].equalsIgnoreCase("setlobby")) {
                        manager.setLobby(player.getLocation());
                        Quake.quake.getConfig().set("lobby", Util.toStringLocation(player.getLocation()));
                        Quake.quake.saveConfig();
                        player.sendMessage(ChatColor.GREEN + "The lobby has been set to your location !");
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
                sender.sendMessage(ChatColor.RED + "You don't have the permission for this command.");
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
