package fr.bretzel.quake.command.partial.game;

import fr.bretzel.commands.PartialCommand;
import fr.bretzel.quake.game.Game;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;

/**
 * Created by MrBretzel on 18/10/2015.
 */
public class GameAddRespawn extends IGame {

    public GameAddRespawn(CommandSender sender, Command command, Permission permission, String[] args, Game game) {
        super(sender, command, permission, args, game);
    }

    @Override
    public PartialCommand execute() {
        if (getGame().hasRespawn(getPlayer().getLocation())) {
            getSender().sendMessage(getI18("command.game.addrespawn.error"));
            return this;
        }
        getGame().addRespawn(getPlayer().getLocation());
        if (getGame().isView()) {
            getGame().view(true);
        }
        getSender().sendMessage("command.game.addrespawn.valid");
        return this;
    }
}
