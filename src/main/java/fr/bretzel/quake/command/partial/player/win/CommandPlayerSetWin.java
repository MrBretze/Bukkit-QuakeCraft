package fr.bretzel.quake.command.partial.player.win;

import fr.bretzel.quake.command.PartialCommand;
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
public class PlayerSetWin extends IPlayer{

    private int i;

    public PlayerSetWin(CommandSender sender, Command command, Permission permission, String[] args, Player player, int i) {
        super(sender, command, permission, args, player);
        this.i = i;
    }

    @Override
    public PartialCommand execute() {
        PlayerInfo info = Quake.getPlayerInfo(getPlayer());
        info.setWin(i);
        getPlayer().sendMessage(getI18("command.players.setwon.valid").replace("%win%", "" + i));
        getPlayer().setScoreboard(info.getPlayerScoreboard());
        return this;
    }
}
