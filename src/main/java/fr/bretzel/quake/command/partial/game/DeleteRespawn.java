package fr.bretzel.quake.command.partial.game;

import fr.bretzel.commands.PartialCommand;
import fr.bretzel.quake.Quake;
import fr.bretzel.quake.game.Game;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;

/**
 * Created by Axelo on 18/10/2015.
 */
public class DeleteRespawn extends IGame
{

    public DeleteRespawn(CommandSender sender, Command command, Permission permission, String[] args, Game game)
    {
        super(sender, command, permission, args, game);
    }

    @Override
    public PartialCommand execute()
    {
        if (!getGame().hasRespawn(getPlayer().getEyeLocation()))
        {
            getSender().sendMessage(getI18("command.game.deleterespawn.error"));
            return this;
        }

        getGame().removeRespawn(getPlayer().getEyeLocation());

        if (getGame().isView())
        {
            Quake.holoManager.removeHologram(getPlayer().getEyeLocation(), 1.2);
            getGame().view(true);
        }

        getSender().sendMessage(getI18("command.game.deleterespawn.valid"));
        return this;
    }
}
