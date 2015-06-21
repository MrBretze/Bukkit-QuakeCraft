package fr.bretzel.quake.game.event;

import fr.bretzel.quake.game.Game;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Created by MrBretzel on 20/06/2015.
 */

public class GameCreate extends Event implements Cancellable {

    private Game game;
    private Player creator;
    private boolean cancel;

    public GameCreate(Game game, Player creator) {
        setGame(game);
        setCreator(creator);
    }

    @Override
    public void setCancelled(boolean b) {
        this.cancel = b;
    }

    @Override
    public boolean isCancelled() {
        return cancel;
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
