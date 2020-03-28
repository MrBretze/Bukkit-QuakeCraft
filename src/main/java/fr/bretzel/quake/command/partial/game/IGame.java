package fr.bretzel.quake.command.partial.game;

import fr.bretzel.commands.PartialCommand;
import fr.bretzel.quake.game.Game;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;

/**
 * Created by Axelo on 09/08/2015.
 */
public abstract class IGame extends PartialCommand
{

    private final Game game;

    public IGame(CommandSender sender, Command command, Permission permission, String[] args, Game game)
    {
        super(sender, command, permission, args);
        this.game = game;
    }

    public Game getGame()
    {
        return game;
    }
}
