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

import fr.bretzel.quake.Permission;
import fr.bretzel.quake.Quake;
import fr.bretzel.quake.command.partial.Create;
import fr.bretzel.quake.command.partial.Delete;
import fr.bretzel.quake.command.partial.game.PartialGame;
import fr.bretzel.quake.command.partial.player.PartialPlayer;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MrBretzel on 14/06/2015.
 */
public class Command extends CommandExe {

    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        if(sender instanceof Player) {
            if(args.length > 0) {
                if (args[0].equalsIgnoreCase("game")) {
                    if(args.length > 1) {
                        if (!sender.hasPermission("quake.game")) {
                            sender.sendMessage("You dont have the permission !");
                            return false;
                        }
                        if (Quake.gameManager.containsGame(args[1])) {
                            // /quake args0 args1  args2         args3...
                            // /quake game  <game> setmineplayer 87
                            return new PartialGame(sender, command, Permission.COMMAND_GAME, args, Quake.gameManager.getGameByName(args[1])).execute().value();
                        } else {
                            sender.sendMessage(getI18n("util.gameNotFound"));
                            return true;
                        }
                    } else {
                        sender.sendMessage(getI18n("command.game.usage"));
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
                } else if (args[0].equalsIgnoreCase("create")) {
                    if (!sender.hasPermission("quake.create")) {
                        sender.sendMessage("You dont have the permission !");
                        return false;
                    }
                    if (args.length > 1) {
                        if (Quake.gameManager.getGameByName(args[1]) == null) {
                            return new Create(sender, command, null, args, args[1]).execute().value();
                        } else {
                            sender.sendMessage(getI18n("util.gameAlreadyCreate"));
                            return true;
                        }
                    } else {
                        sender.sendMessage(getI18n("command.game.create.usage"));
                        return true;
                    }
                } else if (args[0].equalsIgnoreCase("delete")) {
                    if (!sender.hasPermission("quake.game")) {
                        sender.sendMessage("You dont have the permission !");
                        return false;
                    }
                    if (args.length > 1) {
                        if (Quake.gameManager.getGameByName(args[1]) != null) {
                            return new Delete(sender, command, null, args, args[1]).execute().value();
                        } else {
                            sender.sendMessage(getI18n("util.gameNotFound"));
                            return true;
                        }
                    } else {
                        sender.sendMessage(getI18n("command.game.delete.usage"));
                        return true;
                    }
                } else if (args[0].equalsIgnoreCase("help")) {
                    for(String s : getHelps()) {
                        sender.sendMessage(s);
                    }
                    return true;
                } else {
                    //Other command !
                    sender.sendMessage("Not a valid command");
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

    public List<String> getHelps() {
        List list = new ArrayList();
        for(int i = 1; i <= 100; i++) {
            String value = getI18n("command.help." + i);
            if (StringUtils.isNotEmpty(value) && !value.equalsIgnoreCase("command.help." + i))
                list.add(value);
            else
                return list;

        }
        return list;
    }
}