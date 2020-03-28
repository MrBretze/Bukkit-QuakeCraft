package fr.bretzel.quake.command.partial.player.killstreak;

import fr.bretzel.commands.PartialCommand;
import fr.bretzel.quake.PlayerInfo;
import fr.bretzel.quake.Quake;
import fr.bretzel.quake.command.partial.player.IPlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;

/**
 * Created by Axelo on 08/08/2015.
 */
public class PlayerSetKillStreak extends IPlayer
{

    private final int i;

    public PlayerSetKillStreak(CommandSender sender, Command command, Permission permission, String[] args, Player player, int i)
    {
        super(sender, command, permission, args, player);
        this.i = i;
    }

    @Override
    public PartialCommand execute()
    {
        PlayerInfo info = Quake.getPlayerInfo(getPlayer());
        info.setKillStreak(i);
        getPlayer().sendMessage(getI18("command.players.setkillsteak.valid").replace("%killstreak%", "" + i));
        getPlayer().setScoreboard(info.getPlayerScoreboard());
        return this;
    }
}
