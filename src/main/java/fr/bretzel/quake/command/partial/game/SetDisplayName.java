package fr.bretzel.quake.command.partial.game;

import fr.bretzel.quake.util.PartialCommand;
import fr.bretzel.quake.Quake;
import fr.bretzel.quake.game.Game;
import fr.bretzel.quake.language.JsonBuilder;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;

public class SetDisplayName extends ICommandGame {

    public SetDisplayName(CommandSender sender, Command command, Permission permission, String[] args, Game game) {
        super(sender, command, permission, args, game);
    }

    @Override
    public PartialCommand execute() {
        if (getArgs().length > 3) {
            StringBuilder buildName = new StringBuilder();
            for (int i = 3; i < getArgs().length; i++) {
                buildName.append(" ").append(getArgs()[i]);
            }

            String name = ChatColor.translateAlternateColorCodes('&', buildName.toString());
            getGame().setDisplayName(name);
            JsonBuilder.sendJson(getPlayer(), getI18n("command.game.setdisplayname.valid").replace("%game%", getGame().getName()));
            Quake.gameManager.signEvent.actualiseJoinSignForGame(getGame());
            return this;
        } else {
            getSender().sendMessage(getI18n("command.game.setdisplayname.usage"));
            return this;
        }
    }
}
