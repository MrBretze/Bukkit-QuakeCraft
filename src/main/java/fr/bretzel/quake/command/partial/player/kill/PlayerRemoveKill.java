package fr.bretzel.quake.command.partial.player.kill;

import fr.bretzel.commands.PartialCommand;
import fr.bretzel.quake.PlayerInfo;
import fr.bretzel.quake.Quake;
import fr.bretzel.quake.command.partial.player.IPlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;

/**
 * Created by Loic on 31/07/2015.
 */
public class PlayerRemoveKill extends IPlayer
{

    private final int i;

    public PlayerRemoveKill(CommandSender sender, Command command, Permission permission, String[] args, Player player, int i)
    {
        super(sender, command, permission, args, player);
        this.i = i;
    }

    @Override
    public PartialCommand execute()
    {
        PlayerInfo info = Quake.getPlayerInfo(getPlayer());
        info.removePlayerKill(i);
        getPlayer().sendMessage(getI18("command.players.removekill.valid").replace("%kill%", "" + i));
        getPlayer().setScoreboard(info.getPlayerScoreboard());
        return this;
    }
}
