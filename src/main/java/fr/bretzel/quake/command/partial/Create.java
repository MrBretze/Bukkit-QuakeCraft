package fr.bretzel.quake.command.partial;

import fr.bretzel.commands.PartialCommand;
import fr.bretzel.quake.PlayerInfo;
import fr.bretzel.quake.Quake;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;

/**
 * Created by MrBretzel on 20/07/15.
 */
public class Create extends PartialCommand {

    private String games = "null";

    public Create(CommandSender sender, Command command, Permission permission, String[] args, String games) {
        super(sender, command, permission, args);
        this.games = games;
    }

    @Override
    public PartialCommand execute() {
        Player player = (Player) getSender();
        PlayerInfo info = Quake.getPlayerInfo(player);
        if ("null".equals(games)) {
            setValue(true);
            getSender().sendMessage(getI18("command.game.create.nameIsNull"));
            return this;
        }
        Quake.gameManager.registerGame(player, games, info.getFirstLocation(), info.getSecondLocation());
        setValue(true);
        return this;
    }
}
