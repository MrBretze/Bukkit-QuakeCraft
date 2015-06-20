package fr.bretzel.quake.game.event;

import fr.bretzel.quake.game.Game;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Created by MrBretzel on 20/06/2015.
 */

public class GameCreate extends Event {

    private Game game;
    private Player creator;

    public GameCreate(Game game, Player creator) {
        setGame(game);
        setCreator(creator);
    }

    public Game getGame() {
        return game;
    }

    public Player getCreator() {
        return creator;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public void setCreator(Player creator) {
        this.creator = creator;
    }

    private static final HandlerList handlers = new HandlerList();

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
