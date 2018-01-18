package fr.bretzel.quake.command.partial.game;

import fr.bretzel.quake.util.PartialCommand;
import fr.bretzel.quake.game.Game;
import fr.bretzel.quake.language.JsonBuilder;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;

public class SetMinPlayer extends ICommandGame {

    public SetMinPlayer(CommandSender sender, Command command, Permission permission, String[] args, Game game) {
        super(sender, command, permission, args, game);
    }

    @Override
    public PartialCommand execute() {
        if (getArgs().length > 3) {
            int i;
            try {
                i = Integer.valueOf(getArgs()[3]);
            } catch (Exception e) {
                JsonBuilder.sendJson(getPlayer(), getI18n("command.game.setminplayer.error.1"));
                return this;
            }
            if (i < 2) {
                JsonBuilder.sendJson(getPlayer(), getI18n("command.game.setminplayer.error.2"));
                return this;
            }
            getGame().setMinPlayer(i);
            JsonBuilder.sendJson(getPlayer(), getI18n("command.game.setminplayer.valid").replace("%game%", getGame().getName()).replace("%value%", "" + i));
            return this;
        } else {
            JsonBuilder.sendJson(getPlayer(), getI18n("command.game.setminplayer.usage").replace("%game%", getGame().getName()));
            return this;
        }
    }
}
