package fr.bretzel.quake.command.partial.player.win;

import fr.bretzel.quake.command.PartialCommand;
import fr.bretzel.quake.PlayerInfo;
import fr.bretzel.quake.Quake;
import fr.bretzel.quake.command.partial.player.ICommandPlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;

/**
 * Created by Axelo on 08/08/2015.
 */
public class CommandPlayerAddWin extends ICommandPlayer {

    private int i;

    public CommandPlayerAddWin(CommandSender sender, Command command, Permission permission, String[] args, Player player, int i) {
        super(sender, command, permission, args, player);
        this.i = i;
    }

    @Override
    public PartialCommand execute() {
        PlayerInfo info = Quake.getPlayerInfo(getPlayer());
        info.addWin(i);
        getPlayer().sendMessage(getI18n("command.players.addwon.valid").replace("%win%", "" + i));
        getPlayer().setScoreboard(info.getPlayerScoreboard());
        return this;
    }
}
