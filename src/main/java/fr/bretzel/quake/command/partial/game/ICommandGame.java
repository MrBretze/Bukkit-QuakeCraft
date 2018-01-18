package fr.bretzel.quake.command.partial.game;

import fr.bretzel.quake.util.PartialCommand;
import fr.bretzel.quake.game.Game;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;

public abstract class ICommandGame extends PartialCommand {

    private Game game;

    public ICommandGame(CommandSender sender, Command command, Permission permission, String[] args, Game game) {
        super(sender, command, permission, args);
        this.game = game;
    }

    public Game getGame() {
        return game;
    }
}
