package fr.bretzel.quake.game.event;

import fr.bretzel.quake.game.Game;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Created by MrBretzel on 20/06/2015.
 */

public class PlayerJoinGame extends Event {

    private Player player;
    private Game game;

    public PlayerJoinGame(Player player, Game game) {
        setGame(game);
        setPlayer(player);
    }

    public Game getGame() {
        return game;
    }

    public Player getPlayer() {
        return player;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    private static final HandlerList handlers = new HandlerList();

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
