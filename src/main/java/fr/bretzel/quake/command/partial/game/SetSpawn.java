package fr.bretzel.quake.command.partial.game;

import fr.bretzel.quake.command.PartialCommand;
import fr.bretzel.quake.game.Game;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;

/**
 * Created by MrBretzel on 19/10/2015.
 */
public class SetSpawn extends IGame {

    public SetSpawn(CommandSender sender, Command command, Permission permission, String[] args, Game game) {
        super(sender, command, permission, args, game);
    }

    @Override
    public PartialCommand execute() {
        getGame().setSpawn(getPlayer().getLocation());
        getSender().sendMessage(getI18("command.game.setspawn.valid"));
        return this;
    }
}
