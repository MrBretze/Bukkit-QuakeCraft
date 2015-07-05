/**
 * Copyright 2015 Loïc Nussbaumer
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you
 * may not use this file except in compliance with the License. You
 * may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License. See accompanying
 * LICENSE file.
 */
package fr.bretzel.quake.game.event;

import fr.bretzel.quake.game.Game;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.List;

/**
 * Created by MrBretzel on 20/06/2015.
 */

public class PlayerShootEvent extends Event implements Cancellable {

    private Player player;
    private Game game;
    private int kill = 0;
    private boolean cancelled;
    private List<Location> locations;
    private List<Player> players;

    public PlayerShootEvent(Player player, Game game, List<Location> locations, List<Player> players) {
        this.game = game;
        this.player = player;
        this.locations = locations;
        this.players = players;
        this.kill = players.size();
    }

    public List<Location> getLocations() {
        return locations;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void setLocations(List<Location> locations) {
        this.locations = locations;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
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
