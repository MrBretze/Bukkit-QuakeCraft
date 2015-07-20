package fr.bretzel.quake.command.partial;

import fr.bretzel.commands.PartialCommand;
import fr.bretzel.quake.PlayerInfo;
import fr.bretzel.quake.Quake;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;

/**
 * Created by mrbretzel on 20/07/15.
 */
public class Create extends PartialCommand {

    private String arena = "null";

    public Create(CommandSender sender, Command command, Permission permission, String[] args, String arena) {
        super(sender, command, permission, args);
        this.arena = arena;
    }

    @Override
    public PartialCommand execute() {
        Player player = (Player) getSender();
        PlayerInfo info = Quake.getPlayerInfo(player);
        if("null".equals(arena)) {
            setValue(true);
            return this;
        }
        Quake.gameManager.registerGame(player, arena, info.getFirstLocation(), info.getSecondLocation());
        setValue(true);
        return this;
    }
}
