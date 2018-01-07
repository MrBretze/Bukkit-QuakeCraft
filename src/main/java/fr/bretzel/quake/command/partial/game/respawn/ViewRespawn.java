package fr.bretzel.quake.command.partial.game.respawn;

import fr.bretzel.quake.command.PartialCommand;
import fr.bretzel.quake.command.partial.game.ICommandGame;
import fr.bretzel.quake.game.Game;
import fr.bretzel.quake.language.JsonBuilder;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;

/**
 * Created by MrBretzel on 20/10/2015.
 */
public class ViewRespawn extends ICommandGame {

    public ViewRespawn(CommandSender sender, Command command, Permission permission, String[] args, Game game) {
        super(sender, command, permission, args, game);
    }

    @Override
    public PartialCommand execute() {
        getGame().view();
        JsonBuilder.sendJson(getPlayer(), getI18n("command.game.viewrespawn.valid." + getGame().isView()).replace("%game%", getGame().getName()));
        return this;
    }
}
