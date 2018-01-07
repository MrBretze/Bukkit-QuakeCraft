package fr.bretzel.quake.command.partial.game;

import fr.bretzel.quake.command.PartialCommand;
import fr.bretzel.quake.Quake;
import fr.bretzel.quake.game.Game;
import fr.bretzel.quake.language.JsonBuilder;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;

/**
 * Created by MrBretzel on 18/10/2015.
 */
public class SetMaxPlayer extends ICommandGame {

    public SetMaxPlayer(CommandSender sender, Command command, Permission permission, String[] args, Game game) {
        super(sender, command, permission, args, game);
    }

    @Override
    public PartialCommand execute() {
        if (getArgs().length > 3) {
            int i;
            try {
                i = Integer.valueOf(getArgs()[3]);
            } catch (Exception e) {
                JsonBuilder.sendJson(getPlayer(), getI18n("command.game.setmaxplayer.error.1"));
                return this;
            }
            if (i < 2) {
                JsonBuilder.sendJson(getPlayer(), getI18n("command.game.setmaxplayer.error.2"));
                return this;
            }
            getGame().setMaxPlayer(i);
            Quake.gameManager.signEvent.actualiseJoinSignForGame(getGame());
            JsonBuilder.sendJson(getPlayer(), getI18n("command.game.setmaxplayer.valid").replace("%game%", getGame().getName()).replace("%value%", "" + i));
            return this;
        } else {
            JsonBuilder.sendJson(getPlayer(), getI18n("command.game.setmaxplayer.usage").replace("%game%", getGame().getName()));
            return this;
        }
    }
}
