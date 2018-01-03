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

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Created by MrBretzel on 20/06/2015.
 */

public class GameCreateEvent extends Event implements Cancellable {

    private Game game;
    private Player creator;
    private boolean cancel;

    public GameCreateEvent(Game game, Player creator) {
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
