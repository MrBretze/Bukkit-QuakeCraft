/**
 * Copyright 2015 Loïc Nussbaumer
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
package fr.bretzel.quake.game.scoreboard;


import fr.bretzel.quake.Quake;
import fr.bretzel.quake.game.Game;
import fr.bretzel.quake.game.task.ScoreboardTask;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

/**
 * Created by MrBretzel on 29/06/2015
 */

public class ScoreboardAPI
{

    private Scoreboard scoreboard;

    private Objective objective;

    private final ScoreboardTask task;

    private Game game;

    public ScoreboardAPI(Game game)
    {
        setGame(game);

        setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());

        setObjective(getScoreboard().registerNewObjective("quake", "dummy", "quake"));

        getObjective().getScore(ChatColor.RESET.toString()).setScore(10);
        getObjective().getScore("Map: " + game.getDisplayName()).setScore(9);
        getObjective().getScore(ChatColor.RESET.toString() + ChatColor.RESET.toString()).setScore(8);
        getObjective().getScore(ChatColor.RESET.toString() + "                            " + ChatColor.RESET.toString()).setScore(6);
        getObjective().getScore("Waiting...").setScore(5);
        getObjective().setDisplaySlot(DisplaySlot.SIDEBAR);

        task = new ScoreboardTask(Quake.quake, 2, 2, getGame());
    }

    public Game getGame()
    {
        return game;
    }

    public void setGame(Game game)
    {
        this.game = game;
    }

    public Objective getObjective()
    {
        return objective;
    }

    public void setObjective(Objective objective)
    {
        this.objective = objective;
    }

    public Scoreboard getScoreboard()
    {
        return scoreboard;
    }

    public void setScoreboard(Scoreboard scoreboard)
    {
        this.scoreboard = scoreboard;
    }

    public ScoreboardTask getTask()
    {
        return task;
    }
}
