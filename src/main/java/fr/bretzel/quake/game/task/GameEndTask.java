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
package fr.bretzel.quake.game.task;

import fr.bretzel.quake.GameTask;
import fr.bretzel.quake.game.Game;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by Loic on 05/07/2015.
 */

public class GameEndTask extends GameTask {

    private int firewokSpawnable = 5;

    public GameEndTask(JavaPlugin javaPlugin, long l, long l1, Game game) {
        super(javaPlugin, l, l1, game);
    }

    @Override
    public void run() {

    }
}
