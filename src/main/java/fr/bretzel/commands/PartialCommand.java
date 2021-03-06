package fr.bretzel.commands;

import fr.bretzel.quake.Quake;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;

/**
 * Created by mrbretzel on 19/07/15.
 */

public abstract class PartialCommand
{

    private final Command command;
    private final CommandSender sender;
    private final Permission permission;
    private final String[] args;
    private Player player;
    private boolean value = true;
    private boolean isplayer = false;

    public PartialCommand(CommandSender sender, Command command, Permission permission, String[] args)
    {
        this.sender = sender;
        this.command = command;
        this.permission = permission;
        this.args = args;
        if (sender instanceof Player)
        {
            isplayer = true;
            this.player = (Player) sender;
        }
    }

    public PartialCommand execute()
    {
        return this;
    }

    public Command getCommand()
    {
        return command;
    }

    public CommandSender getSender()
    {
        return sender;
    }

    public Permission getPermission()
    {
        return permission;
    }

    public String[] getArgs()
    {
        return args;
    }

    public boolean value()
    {
        return this.value;
    }

    public void setValue(boolean value)
    {
        this.value = value;
    }

    public String getI18(String key)
    {
        return Quake.getI18n(key);
    }

    public Player getPlayer()
    {
        return player;
    }

    public boolean isPlayer()
    {
        return isplayer;
    }
}
