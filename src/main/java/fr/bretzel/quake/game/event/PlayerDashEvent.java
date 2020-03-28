package fr.bretzel.quake.game.event;

import fr.bretzel.quake.game.Game;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Created by MrBretzel on 11/07/2015.
 */
public class PlayerDashEvent extends Event implements Cancellable
{

    private static final HandlerList handlers = new HandlerList();
    private Player player;
    private Game game;
    private boolean cancelled;


    public PlayerDashEvent(Player player, Game game)
    {
        this.game = game;
        this.player = player;
    }

    public static HandlerList getHandlerList()
    {
        return handlers;
    }

    public Game getGame()
    {
        return game;
    }

    public void setGame(Game game)
    {
        this.game = game;
    }

    public Player getPlayer()
    {
        return player;
    }

    public void setPlayer(Player player)
    {
        this.player = player;
    }

    @Override
    public boolean isCancelled()
    {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled)
    {
        this.cancelled = cancelled;
    }

    public HandlerList getHandlers()
    {
        return handlers;
    }
}
