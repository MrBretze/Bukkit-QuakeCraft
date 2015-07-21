package fr.bretzel.quake.command.partial.player;

import fr.bretzel.commands.PartialCommand;
import fr.bretzel.quake.Quake;
import fr.bretzel.quake.game.Game;
import fr.bretzel.quake.game.SignEvent;
import fr.bretzel.quake.game.event.PlayerJoinGameEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;

/**
 * Created by mrbretzel on 20/07/15.
 */
public class PlayerJoin extends IPlayer {

    private Game game;
    private SignEvent e = Quake.gameManager.signEvent;

    public PlayerJoin(CommandSender sender, Command command, Permission permission, String[] args, Player player, Game game) {
        super(sender, command, permission, args, player);
        this.game = game;
    }

    @Override
    public PartialCommand execute() {
        PlayerJoinGameEvent event = new PlayerJoinGameEvent(getPlayer(), game);
        Bukkit.getPluginManager().callEvent(event);
        if(event.isCancelled()) {
            return this;
        }
        getPlayer().teleport(game.getSpawn());
        game.addPlayer(getPlayer());
        getSender().sendMessage(ChatColor.GREEN + "The player has been added to the game !");
        e.actualiseJoinSignForGame(game);
        if (e.lastPlayerInGame.equalsIgnoreCase("lastInGame")) {
            e.lastPlayerInGame = e.getInfoPlayer(game);
            game.getScoreboardManager().getObjective().getScore(e.lastPlayerInGame).setScore(7);
            return this;
        } else {
            game.getScoreboardManager().getScoreboard().resetScores(e.lastPlayerInGame);
            e.lastPlayerInGame = e.getInfoPlayer(game);
            game.getScoreboardManager().getObjective().getScore(e.lastPlayerInGame).setScore(7);
            return this;
        }
    }
}
