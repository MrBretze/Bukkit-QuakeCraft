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

import fr.bretzel.commands.PartialCommand;
import fr.bretzel.quake.game.Game;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;

/**
 * Created by MrBretzel on 09/08/2015.
 */
public class PartialGame extends IGame
{


    public PartialGame(CommandSender sender, Command command, Permission permission, String[] args, Game game)
    {
        super(sender, command, permission, args, game);
    }

    @Override
    public PartialCommand execute()
    {
        if (getArgs().length > 2)
        {
            if (getArgs()[2].equalsIgnoreCase("setdisplayname"))
            {
                if (getArgs().length > 3)
                {
                    return new SetDisplayName(getSender(), getCommand(), getPermission(), getArgs(), getGame()).execute();
                } else
                {
                    getSender().sendMessage(getI18("command.game.setdisplayname.usage"));
                    return this;
                }
            } else if (getArgs()[2].equalsIgnoreCase("setmaxplayer"))
            {
                if (getArgs().length > 3)
                {
                    return new SetMaxPlayer(getSender(), getCommand(), getPermission(), getArgs(), getGame()).execute();
                } else
                {
                    getSender().sendMessage(getI18("command.game.setmaxplayer.usage"));
                    return this;
                }
            } else if (getArgs()[2].equalsIgnoreCase("setminplayer"))
            {
                if (getArgs().length > 3)
                {
                    return new SetMinPlayer(getSender(), getCommand(), getPermission(), getArgs(), getGame()).execute();
                } else
                {
                    getSender().sendMessage(getI18("command.game.setminplayer.usage"));
                    return this;
                }
            } else if (getArgs()[2].equalsIgnoreCase("view"))
            {
                if (getArgs().length > 3)
                {
                    return new View(getSender(), getCommand(), getPermission(), getArgs(), getGame()).execute();
                } else
                {
                    getSender().sendMessage(getI18("command.game.view.usage"));
                    return this;
                }
            } else if (getArgs()[2].equalsIgnoreCase("addrespawn"))
            {
                return new AddRespawn(getSender(), getCommand(), getPermission(), getArgs(), getGame()).execute();
            } else if (getArgs()[2].equalsIgnoreCase("deleterespawn"))
            {
                return new DeleteRespawn(getSender(), getCommand(), getPermission(), getArgs(), getGame()).execute();
            } else if (getArgs()[2].equalsIgnoreCase("setspawn"))
            {
                return new SetSpawn(getSender(), getCommand(), getPermission(), getArgs(), getGame()).execute();
            } else
            {
                getSender().sendMessage(getI18("command.game.usage"));
                return this;
            }
        } else
        {
            getSender().sendMessage(getI18("command.game.usage"));
            return this;
        }
    }
}
