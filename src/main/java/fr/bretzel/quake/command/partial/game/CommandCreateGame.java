package fr.bretzel.quake.command.partial.game;

import fr.bretzel.quake.util.PartialCommand;
import fr.bretzel.quake.PlayerInfo;
import fr.bretzel.quake.Quake;
import fr.bretzel.quake.language.JsonBuilder;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;

public class CommandCreateGame extends PartialCommand {

    private String games = "null";

    public CommandCreateGame(CommandSender sender, Command command, Permission permission, String[] args, String games) {
        super(sender, command, permission, args);
        this.games = games;
    }

    @Override
    public PartialCommand execute() {
        Player player = (Player) getSender();
        PlayerInfo info = PlayerInfo.getPlayerInfo(player);
        if (games.equals("null")) {
            setValue(true);
            JsonBuilder.sendJson(getPlayer(), getI18n("util.quake.game.isnull"));
            return this;
        }

        Quake.gameManager.registerGame(player, games, info.getFirstLocation(), info.getSecondLocation());
        setValue(true);
        return this;
    }
}
