package fr.bretzel.quake.command.partial.player;

import fr.bretzel.commands.PartialCommand;
import fr.bretzel.quake.PlayerInfo;
import fr.bretzel.quake.Quake;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;

/**
 * Created by mrbretzel on 21/07/15.
 */

public class PlayerSetCoins extends IPlayer {

    private int i = 0;

    public PlayerSetCoins(CommandSender sender, Command command, Permission permission, String[] args, Player player, int i) {
        super(sender, command, permission, args, player);
        this.i = i;
    }

    @Override
    public PartialCommand execute() {
        PlayerInfo info = Quake.getPlayerInfo(getPlayer());
        info.setCoins(i);
        info.getPlayer().sendMessage(ChatColor.GREEN + "Your coins has been set to: " + i);
        getPlayer().setScoreboard(info.getPlayerScoreboard());
        return this;
    }
}
