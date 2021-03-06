/**
 * Copyright 2015 Lo�c Nussbaumer
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you
 * may not use this file except in compliance with the License. You
 * may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License. See accompanying
 * LICENSE file.
 */
package fr.bretzel.quake;

import fr.bretzel.quake.game.Game;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by MrBretzel on 21/06/2015.
 */

public abstract class GameTask implements Runnable
{

    private int id = -1;
    private final JavaPlugin javaPlugin;
    private final Game game;

    public GameTask(JavaPlugin javaPlugin, long l, long l1, Game game)
    {
        this.javaPlugin = javaPlugin;
        this.id = javaPlugin.getServer().getScheduler().runTaskTimer(javaPlugin, this, l, l1).getTaskId();
        this.game = game;
    }

    public Game getGame()
    {
        return game;
    }

    public int getId()
    {
        return id;
    }

    public JavaPlugin getJavaPlugin()
    {
        return javaPlugin;
    }

    public void cancel()
    {
        getJavaPlugin().getServer().getScheduler().cancelTask(getId());
    }
}
