package fr.bretzel.quake.command.partial.player;

import fr.bretzel.commands.PartialCommand;
import fr.bretzel.quake.PlayerInfo;
import fr.bretzel.quake.Quake;

import fr.bretzel.quake.game.Game;
import fr.bretzel.quake.game.event.PlayerLeaveGameEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;

/**
 * Created by mrbretzel on 20/07/15.
 */
public class PartialPlayer extends IPlayer {

    public PartialPlayer(CommandSender sender, Command command, Permission permission, String[] args, Player player) {
        super(sender, command, permission, args, player);
    }

    @Override
    public PartialCommand execute() {
        if(getArgs().length > 2) {
            PlayerInfo info = Quake.getPlayerInfo(getPlayer());
            if(getArgs()[2].equalsIgnoreCase("quit")) {
                if(info.isInGame()) {
                    Game game = Quake.gameManager.getGameByPlayer(getPlayer());
                    PlayerLeaveGameEvent event = new PlayerLeaveGameEvent(getPlayer(), game);
                    Bukkit.getPluginManager().callEvent(event);
                    if(event.isCancelled()) {
                        return this;
                    }
                    getPlayer().teleport(Quake.gameManager.getLobby());
                    game.getPlayerList().remove(getPlayer().getUniqueId());
                    Quake.gameManager.signEvent.actualiseJoinSignForGame(game);
                } else {
                    getSender().sendMessage(ChatColor.RED + "The player is not in a game !");
                    setValue(true);
                    return this;
                }
            }
        } else {
            getSender().sendMessage(ChatColor.RED + "/quake players <player> <quit | join | setcoins | setkill | setkillsteak | setwon>");
            setValue(true);
            return this;
        }
        return this;
    }
}
