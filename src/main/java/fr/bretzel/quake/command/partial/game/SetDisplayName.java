package fr.bretzel.quake.command.partial.game;

import fr.bretzel.commands.PartialCommand;
import fr.bretzel.quake.Quake;
import fr.bretzel.quake.game.Game;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;

/**
 * Created by MrBretzel on 18/10/2015.
 */
public class SetDisplayName extends IGame
{

    public SetDisplayName(CommandSender sender, Command command, Permission permission, String[] args, Game game)
    {
        super(sender, command, permission, args, game);
    }

    @Override
    public PartialCommand execute()
    {
        if (getArgs().length > 3)
        {
            if (getArgs()[3].length() > 16)
            {
                getSender().sendMessage(getI18("command.game.setdisplayname.error"));
                setValue(false);
                return this;
            }
            String name = ChatColor.translateAlternateColorCodes('&', getArgs()[3]);
            getGame().setDisplayName(name);
            getSender().sendMessage(getI18("command.game.setdisplayname.valid").replace("%value%", name));
            Quake.gameManager.signEvent.updateSign(getGame());
            return this;
        } else
        {
            getSender().sendMessage(getI18("command.game.setdisplayname.usage"));
            return this;
        }
    }
}
