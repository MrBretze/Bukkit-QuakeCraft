package fr.bretzel.quake.command;

import fr.bretzel.quake.PlayerInfo;
import fr.bretzel.quake.Quake;
import fr.bretzel.quake.game.Game;
import fr.bretzel.quake.game.event.PlayerLeaveGameEvent;
import fr.bretzel.quake.language.JsonBuilder;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LobbyCommand extends CommandExe {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            PlayerInfo info = Quake.getPlayerInfo(player);
            if (info.isInGame()) {
                Game game = Quake.gameManager.getGameByPlayer(info);
                PlayerLeaveGameEvent e = new PlayerLeaveGameEvent(player, game);
                Bukkit.getPluginManager().callEvent(e);
                if (e.isCancelled()) {
                    return false;
                }
                game.getPlayerList().remove(info);
                Quake.gameManager.signEvent.actualiseJoinSignForGame(game);
            }
            player.teleport(Quake.gameManager.getLobby());
            JsonBuilder.sendJson(player, getI18n("command.lobby"));
        }
        return false;
    }
}
