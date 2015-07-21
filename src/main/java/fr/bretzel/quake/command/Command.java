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
package fr.bretzel.quake.command;

import fr.bretzel.commands.CommandExe;
import fr.bretzel.quake.command.partial.Create;
import fr.bretzel.quake.command.partial.Delete;
import fr.bretzel.quake.command.partial.Stop;
import fr.bretzel.quake.command.partial.player.PartialPlayer;
import fr.bretzel.quake.permission.Permission;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by MrBretzel on 14/06/2015.
 */
public class Command extends CommandExe {

    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        if(sender instanceof Player) {
            if(args.length > 0) {
                if (args[0].equalsIgnoreCase("create")) {
                    if(args.length > 1) {
                        return new Create(sender, command, Permission.COMMAND_CREATE, args, args[1]).execute().value();
                    } else {
                        sender.sendMessage(ChatColor.RED + "/quake create <name>");
                        return true;
                    }
                } else if(args[0].equalsIgnoreCase("delete")) {
                    if(args.length > 1) {
                        return new Delete(sender, command, Permission.COMMAND_DELETE, args, args[1]).execute().value();
                    } else {
                        sender.sendMessage(ChatColor.RED + "/quake delete <name>");
                        return true;
                    }
                } else if(args[0].equalsIgnoreCase("stop")) {
                    if(args.length > 1) {
                        return new Stop(sender, command, Permission.COMMAND_STOP, args, args[1]).execute().value();
                    } else {
                        sender.sendMessage(ChatColor.RED + "/quake stop <game>");
                        return true;
                    }
                } else if(args[0].equalsIgnoreCase("players")) {
                    if(args.length > 1) {
                        if (Bukkit.getPlayer(args[1]) != null) {
                            return new PartialPlayer(sender, command, Permission.COMMAND_PLAYER, args, Bukkit.getPlayer(args[1])).execute().value();
                        } else {
                            sender.sendMessage(ChatColor.RED + "Player can not bee found !");
                            return true;
                        }
                    } else {
                        sender.sendMessage(ChatColor.RED + "Usage: /quake players <player> <quit | join | setcoins | addcoins | removecoins | setkill | setkillsteak | setwon>");
                        return true;
                    }
                } else {
                    //Other command !
                    return true;
                }
            } else {
                sender.sendMessage(ChatColor.RED + "/quake help");
                return true;
            }
        } else {
            sender.sendMessage(ChatColor.RED + "It must be a player !");
            return true;
        }
    }
}