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
package fr.bretzel.quake.game.task;

import fr.bretzel.quake.*;
import fr.bretzel.quake.game.Game;
import fr.bretzel.quake.game.State;
import fr.bretzel.quake.game.event.GameStartEvent;
import fr.bretzel.quake.game.scoreboard.ScoreboardAPI;
import fr.bretzel.quake.gun.BasicGun;
import fr.bretzel.quake.gun.Gun;
import fr.bretzel.quake.gun.raygun.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.DisplaySlot;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.UUID;

/**
 * Created by MrBretzel on 22/06/2015.
 */

public class GameStartTask extends GameTask
{

    private static final NumberFormat formatter = new DecimalFormat("00");
    String last = "Waiting...";
    private int minSecQuake = getGame().getSecLaunch();
    private final ScoreboardAPI scoreboardAPI = getGame().getScoreboardManager();

    public GameStartTask(JavaPlugin javaPlugin, long l, long l1, Game game)
    {
        super(javaPlugin, l, l1, game);
    }

    @Override
    public void run()
    {
        if (getGame().getPlayerList().size() < getGame().getMinPlayer())
        {
            getGame().getScoreboardManager().getScoreboard().resetScores(this.last);
            getGame().getScoreboardManager().getObjective().getScore("Waiting...").setScore(5);
            cancel();
            return;
        }
        if (minSecQuake <= 5 || minSecQuake == 10)
        {
            getGame().broadcastMessage(ChatColor.BLUE + "The game start in: " + Util.getChatColorByInt(minSecQuake) + minSecQuake);
            for (UUID id : getGame().getPlayerList())
            {
                Player p = Bukkit.getPlayer(id);
                if (p != null && p.isOnline())
                {
                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 2.0F, 2.0F);
                }
            }
        }

        if (minSecQuake > 0)
        {
            minSecQuake--;
        }

        String format = formatter.format(minSecQuake + 1) + "s";

        scoreboardAPI.getScoreboard().resetScores(this.last);

        scoreboardAPI.getObjective().getScore(format).setScore(5);

        this.last = format;

        if (minSecQuake <= 0 && getGame().getState() == State.WAITING)
        {
            GameStartEvent event = new GameStartEvent(getGame());
            Bukkit.getPluginManager().callEvent(event);
            if (event.isCancelled())
            {
                getGame().stop();
                cancel();
                return;
            }
            getGame().setState(State.STARTED);
            Quake.gameManager.signEvent.updateSign(getGame());
            scoreboardAPI.getObjective().unregister();
            scoreboardAPI.setObjective(scoreboardAPI.getScoreboard().registerNewObjective("quake", "dummy"));
            scoreboardAPI.getObjective().setDisplaySlot(DisplaySlot.SIDEBAR);
            for (UUID id : getGame().getPlayerList())
            {
                Player p = Bukkit.getPlayer(id);
                if ((p != null) && (p.isOnline()))
                {
                    getGame().respawn(p);
                    PlayerInfo info = Quake.getPlayerInfo(p);
                    info.give(getRandomGun(info));
                    Chrono chrono = new Chrono();
                    p.getInventory().setHeldItemSlot(0);
                    chrono.start();
                    Quake.gameManager.getGameChrono().put(getGame(), chrono);
                    getGame().setKill(p.getUniqueId(), 0);
                    scoreboardAPI.getObjective().getScore(p.getName()).setScore(1);
                    scoreboardAPI.getObjective().getScore(p.getName()).setScore(getGame().getKill(p));
                }
            }
            cancel();
        }
    }

    public Gun getRandomGun(PlayerInfo info)
    {
        int modeldata = info.getRandom().nextInt(6) + 1;
        Bukkit.broadcastMessage("Modeldata = " + modeldata);
        if (modeldata == 1)
        {
            return new BlasterDL44(info);
        } else if (modeldata == 2)
        {
            return new RayGun(info);
        } else if (modeldata == 3)
        {
            return new RayGunAlpha(info);
        } else if (modeldata == 4)
        {
            return new RayGunOmega(info);
        } else if (modeldata == 5)
        {
            return new RayGunNuclear(info);
        } else if (modeldata == 6)
        {
            return new RayGunHoney(info);
        }

        return new BasicGun(info);
    }
}
