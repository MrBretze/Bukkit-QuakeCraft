package fr.bretzel.quake.command.partial.game;

import fr.bretzel.quake.util.PartialCommand;
import fr.bretzel.quake.game.Game;
import fr.bretzel.quake.language.JsonBuilder;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;

public class SetSpawn extends ICommandGame {

    public SetSpawn(CommandSender sender, Command command, Permission permission, String[] args, Game game) {
        super(sender, command, permission, args, game);
    }

    @Override
    public PartialCommand execute() {
        getGame().setSpawn(getPlayer().getLocation());
        JsonBuilder.sendJson(getPlayer(), "");
        return this;
    }
}
