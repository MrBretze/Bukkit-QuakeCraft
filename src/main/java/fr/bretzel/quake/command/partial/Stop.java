package fr.bretzel.quake.command.partial;

import fr.bretzel.quake.command.PartialCommand;
import fr.bretzel.quake.Quake;
import fr.bretzel.quake.game.Game;
import fr.bretzel.quake.game.State;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;

/**
 * Created by mrbretzel on 20/07/15.
 */
public class Stop extends PartialCommand {

    private String arena = "null";

    public Stop(CommandSender sender, Command command, Permission permission, String[] args, String arena) {
        super(sender, command, permission, args);
        this.arena = arena;
    }

    @Override
    public PartialCommand execute() {
        Player player = (Player) getSender();
        if("null".equals(arena)) {
            setValue(true);
            return this;
        }
        Game game = Quake.gameManager.getGameByName(arena);
        if(game.getState() == State.STARTED) {
            setValue(true);
            game.broadcastMessage(ChatColor.RED + "The game has been break by a admin !");
            game.stop();
            return this;
        } else {
            setValue(true);
            player.sendMessage(ChatColor.RED + "The game is not started !");
            return this;
        }
    }
}
