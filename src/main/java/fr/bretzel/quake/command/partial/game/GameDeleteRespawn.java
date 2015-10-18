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
public class GameDeleteRespawn extends IGame {

    public GameDeleteRespawn(CommandSender sender, Command command, Permission permission, String[] args, Game game) {
        super(sender, command, permission, args, game);
    }

    @Override
    public PartialCommand execute() {
        if (!getGame().hasRespawn(getPlayer().getLocation())) {
            getSender().sendMessage(getI18("command.game.deleterespawn.error"));
            return this;
        }
        if (getGame().isView())
            Quake.holoManager.removeHologram(getGame().getRespawn(getPlayer().getLocation()));
        getGame().removeRespawn(getPlayer().getLocation());
        getSender().sendMessage(getI18("command.game.deleterespawn.valid"));
        return this;
    }
}
