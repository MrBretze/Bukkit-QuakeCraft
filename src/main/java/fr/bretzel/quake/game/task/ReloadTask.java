/**
 * Copyright 2015 Lo?c Nussbaumer
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
package fr.bretzel.quake.game.task;

import fr.bretzel.quake.SchootTask;
import fr.bretzel.quake.PlayerInfo;
import fr.bretzel.quake.Title;
import org.bukkit.ChatColor;

/**
 * Created by MrBretzel on 22/06/2015.
 */
public class ReloadTask extends SchootTask {

    double reload_time = getInfo().getReloadTime();
    double current_reload = 0;
    String lengt = ChatColor.GREEN + "|||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||".trim();

    double potion = 3;

    long start;

    public ReloadTask(PlayerInfo info) {
        super(info);
        start = System.nanoTime();
        Title.sendTimings(info.getPlayer(), 0, 5, 0);
    }

    @Override
    public void run() {
        StringBuilder builder = new StringBuilder(lengt);

        double adding = ((reload_time * lengt.length()) / 2000);

        potion += adding;

        if ((int)potion < 71) {
            builder.insert((int) potion, ChatColor.GRAY);
            Title.sendTitle(getInfo().getPlayer(), adding + "", builder.toString());
        }

        if (potion >= 71 && potion <= 72) {
            Title.sendTitle(getInfo().getPlayer(), adding + "", "Time reload = " + (start - System.nanoTime()) / 1000000);
        }
        getInfo().setShoot(true);
    }
}
