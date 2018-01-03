package fr.bretzel.quake.command.partial;

import fr.bretzel.commands.PartialCommand;
import fr.bretzel.quake.Quake;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;

/**
 * Created by mrbretzel on 20/07/15.
 */

public class Delete extends PartialCommand {

    private String games = "null";

    public Delete(CommandSender sender, Command command, Permission permission, String[] args, String games) {
        super(sender, command, permission, args);
        this.games = games;
    }

    @Override
    public PartialCommand execute() {
        Player player = (Player) getSender();
        if ("null".equals(games)) {
            setValue(true);
            getSender().sendMessage(getI18("command.game.create.nameIsNull"));
            return this;
        }
        Quake.gameManager.deleteGame(Quake.gameManager.getGameByName(games), player);
        setValue(true);
        return this;
    }
}
