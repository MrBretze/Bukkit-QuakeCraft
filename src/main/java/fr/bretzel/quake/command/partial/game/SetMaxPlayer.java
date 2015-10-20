package fr.bretzel.quake.command.partial.game;

import fr.bretzel.commands.PartialCommand;
import fr.bretzel.quake.game.Game;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;

/**
 * Created by MrBretzel on 18/10/2015.
 */
public class SetMaxPlayer extends IGame {

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
                getSender().sendMessage(getI18("command.game.setmaxplayer.error"));
                return this;
            }
            if (i < 2) {
                getSender().sendMessage(getI18("command.game.setmaxplayer.error2"));
                return this;
            }
            getGame().setMaxPlayer(i);
            getSender().sendMessage(getI18("command.game.setmaxplayer.valid").replace("%value%", String.valueOf(i)));
            return this;
        } else {
            getSender().sendMessage(getI18("command.game.setmaxplayer.usage"));
            return this;
        }
    }
}
