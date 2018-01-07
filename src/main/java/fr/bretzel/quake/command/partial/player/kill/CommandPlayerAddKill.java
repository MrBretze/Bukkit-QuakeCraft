package fr.bretzel.quake.command.partial.player.kill;

import fr.bretzel.quake.command.PartialCommand;
import fr.bretzel.quake.PlayerInfo;
import fr.bretzel.quake.Quake;
import fr.bretzel.quake.command.partial.player.ICommandPlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;

/**
 * Created by Loic on 31/07/2015.
 */
public class CommandPlayerAddKill extends ICommandPlayer {

    private int i;

    public CommandPlayerAddKill(CommandSender sender, Command command, Permission permission, String[] args, Player player, int i) {
        super(sender, command, permission, args, player);
        this.i = i;
    }

    @Override
    public PartialCommand execute() {
        PlayerInfo info = Quake.getPlayerInfo(getPlayer());
        info.addKill(i);
        getPlayer().sendMessage(getI18n("command.players.addkill.valid").replace("%kill%", "" + i));
        getPlayer().setScoreboard(info.getPlayerScoreboard());
        return this;
    }
}
