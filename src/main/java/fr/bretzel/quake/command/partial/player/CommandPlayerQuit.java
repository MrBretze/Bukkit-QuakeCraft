package fr.bretzel.quake.command.partial.player;

import fr.bretzel.quake.PlayerInfo;
import fr.bretzel.quake.util.PartialCommand;
import fr.bretzel.quake.Quake;
import fr.bretzel.quake.game.Game;
import fr.bretzel.quake.game.event.PlayerLeaveGameEvent;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;

public class CommandPlayerQuit extends ICommandPlayer {

    public CommandPlayerQuit(CommandSender sender, Command command, Permission permission, String[] args, Player player) {
        super(sender, command, permission, args, player);
    }

    @Override
    public PartialCommand execute() {
        Game game = Quake.gameManager.getGameByPlayer(getPlayer());
        PlayerLeaveGameEvent event = new PlayerLeaveGameEvent(getPlayer(), game);
        Bukkit.getPluginManager().callEvent(event);
        if(event.isCancelled()) {
            return this;
        }
        getPlayer().teleport(Quake.gameManager.getLobby());
        game.getPlayerList().remove(PlayerInfo.getPlayerInfo(getPlayer()));
        Quake.gameManager.signEvent.actualiseJoinSignForGame(game);
        return this;
    }
}
