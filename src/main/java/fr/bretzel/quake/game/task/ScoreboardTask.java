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

import fr.bretzel.quake.ColorScroller;
import fr.bretzel.quake.GameTask;
import fr.bretzel.quake.Quake;
import fr.bretzel.quake.game.Game;
import fr.bretzel.quake.game.State;

import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by MrBretzel on 29/06/2015.
 */

public class ScoreboardTask extends GameTask {

    int w = 2;
    int wainting = 40;
    private ColorScroller scroller = new ColorScroller(ChatColor.RED, "QuakeCraft", "§c", "§4", "§c", true, false, ColorScroller.ScrollType.FORWARD);
    private String[] wait = {"Waitng...", "Waitng..", "Waitng."};

    public ScoreboardTask(JavaPlugin javaPlugin, long l, long l1, Game game) {
        super(javaPlugin, l, l1, game);
    }

    @Override
    public void run() {
        if (scroller.getScrollType() == ColorScroller.ScrollType.FORWARD) {
            if (scroller.getPosition() >= scroller.getString().length()) {
                scroller.setScrollType(ColorScroller.ScrollType.BACKWARD);
            }
        } else {
            if (scroller.getPosition() <= -1) {
                scroller.setScrollType(ColorScroller.ScrollType.FORWARD);
            }
        }

        if (getGame().getScoreboardManager().getObjective() == null) {
            getGame().getScoreboardManager().setObjective(getGame().getScoreboardManager().getScoreboard().registerNewObjective("quake", "dummy"));
        }

        getGame().getScoreboardManager().getObjective().setDisplayName(scroller.next());

        if (getGame().getState() == State.WAITING && wainting == 0 && !Quake.gameManager.getQuakeTaskHashMap().containsKey(getGame())) {
            this.wainting = 40;
            String news;
            String last;

            if(w != -1) {
                w = 2;
                last = wait[0];
            } else {
                last = wait[w];
            }

            news = wait[w];

            w--;

            getGame().getScoreboardManager().getScoreboard().resetScores(last);
            getGame().getScoreboardManager().getObjective().getScore(news).setScore(5);
        }
        this.wainting--;
    }
}
