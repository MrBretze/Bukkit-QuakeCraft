/**
 * Copyright 2015 Lo�c Nussbaumer
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License"); you
 * may not use this file except in compliance with the License. You
 * may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License. See accompanying
 * LICENSE file.
 */
package fr.bretzel.quake.command.partial.game;

import fr.bretzel.quake.Quake;
import fr.bretzel.quake.command.PartialCommand;
import fr.bretzel.quake.command.partial.game.respawn.AddRespawn;
import fr.bretzel.quake.command.partial.game.respawn.RemoveRespawn;
import fr.bretzel.quake.command.partial.game.respawn.ViewRespawn;
import fr.bretzel.quake.game.Game;
import fr.bretzel.quake.language.JsonBuilder;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;

/**
 * Created by MrBretzel on 09/08/2015.
 */
public class PartialCommandGame extends PartialCommand {


    public PartialCommandGame(CommandSender sender, Command command, Permission permission, String[] args) {
        super(sender, command, permission, args);
    }

    @Override
    public PartialCommand execute() {
        if (Quake.gameManager.containsGame(getArgs()[1])) {
            Game game = Quake.gameManager.getGameByName(getArgs()[1]);
            if (getArgs().length > 2) {
                if (getArgs()[2].equalsIgnoreCase("setdisplayname")) {
                    if (getArgs().length > 3) {
                        return new SetDisplayName(getSender(), getCommand(), getPermission(), getArgs(), game).execute();
                    } else {
                        JsonBuilder.sendJson(getPlayer(), getI18n("command.game.setdisplayname.usage").replace("%game%", game.getName()));
                        return this;
                    }
                } else if (getArgs()[2].equalsIgnoreCase("setmaxplayer")) {
                    if (getArgs().length > 3) {
                        return new SetMaxPlayer(getSender(), getCommand(), getPermission(), getArgs(), game).execute();
                    } else {
                        JsonBuilder.sendJson(getPlayer(), getI18n("command.game.setmaxplayer.usage").replace("%game%", game.getName()));
                        return this;
                    }
                } else if (getArgs()[2].equalsIgnoreCase("setminplayer")) {
                    if (getArgs().length > 3) {
                        return new SetMinPlayer(getSender(), getCommand(), getPermission(), getArgs(), game).execute();
                    } else {
                        JsonBuilder.sendJson(getPlayer(), getI18n("command.game.setminplayer.usage").replace("%game%", game.getName()));
                        return this;
                    }
                } else if (getArgs()[2].equalsIgnoreCase("viewrespawn")) {
                    return new ViewRespawn(getSender(), getCommand(), getPermission(), getArgs(), game).execute();
                } else if (getArgs()[2].equalsIgnoreCase("addrespawn")) {
                    return new AddRespawn(getSender(), getCommand(), getPermission(), getArgs(), game).execute();
                } else if (getArgs()[2].equalsIgnoreCase("removerespawn")) {
                    return new RemoveRespawn(getSender(), getCommand(), getPermission(), getArgs(), game).execute();
                } else if (getArgs()[2].equalsIgnoreCase("setspawn")) {
                    return new SetSpawn(getSender(), getCommand(), getPermission(), getArgs(), game).execute();
                } else {
                    JsonBuilder.sendJson(getPlayer(), getI18n("command.game.usage").replace("%game%", game.getName()));
                    return this;
                }
            } else {
                JsonBuilder.sendJson(getPlayer(), getI18n("command.game.usage").replace("%game%", game.getName()));
                return this;
            }
        } else if (getArgs()[1].equalsIgnoreCase("create")) {
            if (getArgs().length > 2) {
                if (Quake.gameManager.containsGame(getArgs()[2])) {
                    JsonBuilder.sendJson(getPlayer(), getI18n("command.game.create.error"));
                    return this;
                } else {
                    JsonBuilder.sendJson(getPlayer(), getI18n("command.game.create.valid").replace("%game%", getArgs()[2]));
                    return new CommandCreateGame(getSender(), getCommand(), getPermission(), getArgs(), getArgs()[2]).execute();
                }
            } else {
                JsonBuilder.sendJson(getPlayer(), getI18n("command.game.create.usage"));
                return this;
            }
        } else if (getArgs()[1].equalsIgnoreCase("delete")) {
            if (getArgs().length > 2) {
                if (Quake.gameManager.containsGame(getArgs()[2])) {
                    JsonBuilder.sendJson(getPlayer(), getI18n("command.game.delete.valid").replace("%game%", getArgs()[2]));
                    return new CommandDeleteGame(getSender(), getCommand(), getPermission(), getArgs(), getArgs()[2]).execute();
                } else {
                    JsonBuilder.sendJson(getPlayer(), getI18n("command.game.delete.error"));
                    return this;
                }
            } else {
                JsonBuilder.sendJson(getPlayer(), getI18n("command.game.delete.usage"));
                return this;
            }
        } else {
            JsonBuilder.sendJson(getPlayer(), getI18n("command.game.usage").replace("%game%", "<game>"));
            return this;
        }
    }
}
