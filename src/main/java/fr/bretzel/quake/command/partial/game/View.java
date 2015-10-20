package fr.bretzel.quake.command.partial.game;

import fr.bretzel.commands.PartialCommand;
import fr.bretzel.quake.game.Game;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;

/**
 * Created by MrBretzel on 20/10/2015.
 */
public class View extends IGame {

    public View(CommandSender sender, Command command, Permission permission, String[] args, Game game) {
        super(sender, command, permission, args, game);
    }

    @Override
    public PartialCommand execute() {
        if (getArgs().length > 3) {
            boolean b;
            try {
                b = Boolean.valueOf(getArgs()[3]);
            } catch (Exception e) {
                getSender().sendMessage("command.game.view.error");
                return this;
            }
            getGame().view(b);
            if (b) getSender().sendMessage(getI18("command.game.view.validTrue"));
            else getSender().sendMessage(getI18("command.game.view.validFalse"));
        } else {
            getSender().sendMessage(getI18("command.game.view.usage"));
            return this;
        }
        return this;
    }
}
