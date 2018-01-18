package fr.bretzel.quake.command.partial.player;

import fr.bretzel.quake.command.PartialCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;

/**
 * Created by mrbretzel on 20/07/15.
 */
public abstract class ICommandPlayer extends PartialCommand {

    private Player player;

    public ICommandPlayer(CommandSender sender, Command command, Permission permission, String[] args, Player player) {
        super(sender, command, permission, args);
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }
}