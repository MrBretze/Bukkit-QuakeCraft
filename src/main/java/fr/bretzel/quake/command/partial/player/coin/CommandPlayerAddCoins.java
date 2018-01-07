package fr.bretzel.quake.command.partial.player.coin;

import fr.bretzel.quake.command.PartialCommand;
import fr.bretzel.quake.PlayerInfo;
import fr.bretzel.quake.Quake;
import fr.bretzel.quake.command.partial.player.ICommandPlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;

/**
 * Created by Loic on 25/07/2015.
 */

public class CommandPlayerAddCoins extends ICommandPlayer {
    private int i = 0;

    public CommandPlayerAddCoins(CommandSender sender, Command command, Permission permission, String[] args, Player player, int i) {
        super(sender, command, permission, args, player);
        this.i = i;
    }

    @Override
    public PartialCommand execute() {
        PlayerInfo info = Quake.getPlayerInfo(getPlayer());
        info.addCoins(i);
        getPlayer().sendMessage(getI18n("command.players.addcoins.valid").replace("%coins%", "" + i));
        getPlayer().setScoreboard(info.getPlayerScoreboard());
        return this;
    }
}
