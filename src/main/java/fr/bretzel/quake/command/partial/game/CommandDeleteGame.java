package fr.bretzel.quake.command.partial.game;

import fr.bretzel.quake.util.PartialCommand;
import fr.bretzel.quake.Quake;
import fr.bretzel.quake.language.JsonBuilder;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;

public class CommandDeleteGame extends PartialCommand {

    private String games = "null";

    public CommandDeleteGame(CommandSender sender, Command command, Permission permission, String[] args, String games) {
        super(sender, command, permission, args);
        this.games = games;
    }

    @Override
    public PartialCommand execute() {
        Player player = (Player) getSender();
        if (games.equals("null")) {
            JsonBuilder.sendJson(getPlayer(), getI18n("util.quake.game.isnull"));
            return this;
        }

        Quake.gameManager.deleteGame(Quake.gameManager.getGameByName(games), player);
        return this;
    }
}
