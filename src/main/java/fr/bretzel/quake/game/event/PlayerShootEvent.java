package fr.bretzel.quake.game.event;

import fr.bretzel.quake.Util;
import fr.bretzel.quake.game.Game;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Created by MrBretzel on 20/06/2015.
 */

public class PlayerShootEvent extends Event implements Cancellable {

    private Player player;
    private Game game;
    private int kill = 0;
    private boolean cancelled;

    public PlayerShootEvent(Player player, Game game) {
        setGame(game);
        setPlayer(player);
        setKill(Util.getPlayerListInDirection(Util.getLocationByDirection(player, 200, 0.5), player, 0.5).size());
    }

    public int getKill() {
        return kill;
    }

    public void setKill(int kill) {
        this.kill = kill;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public Game getGame() {
        return game;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    private static final HandlerList handlers = new HandlerList();

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
