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

import fr.bretzel.quake.GameTask;
import fr.bretzel.quake.Quake;
import fr.bretzel.quake.Util;
import fr.bretzel.quake.game.Game;
import fr.bretzel.quake.game.State;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.UUID;

/**
 * Created by Loic on 05/07/2015.
 */

public class GameEndTask extends GameTask
{

    private Player player;
    private int firewokSpawnable = 15;

    public GameEndTask(JavaPlugin javaPlugin, long l, long l1, Game game, Player player)
    {
        super(javaPlugin, l, l1, game);
        setPlayer(player);
        game.setState(State.ENDING);
    }

    @Override
    public void run()
    {
        if (firewokSpawnable >= 0)
        {
            firewokSpawnable--;

            if (firewokSpawnable == 5)
            {
                for (UUID uuid : getGame().getPlayerList())
                {
                    Player p = Bukkit.getPlayer(uuid);
                    if (p != null && p.isOnline())
                    {
                        sendGameInfo(p);
                    }
                }
            }

            Util.spawnFirework(Util.getCircle(player.getLocation().clone(), 0.4, 1));
        } else
        {
            Bukkit.getScheduler().runTaskLater(Quake.quake, () -> getGame().stop(), 35L);
            cancel();
        }
    }

    public Player getPlayer()
    {
        return player;
    }

    public void setPlayer(Player player)
    {
        this.player = player;
    }

    private void sendGameInfo(Player player)
    {
        NumberFormat formatter = new DecimalFormat("00");
        player.sendMessage(ChatColor.AQUA + "####################");
        player.sendMessage(ChatColor.AQUA + "#    Kills: " + ChatColor.BLUE.toString() + formatter.format(getGame().getKill(player)) + ChatColor.AQUA + "             #");
        player.sendMessage(ChatColor.AQUA + "#    Coins: " + ChatColor.BLUE.toString() + formatter.format(getGame().getKill(player) * 5) + ChatColor.AQUA + "           #");
        player.sendMessage(ChatColor.AQUA + "####################");
    }
}
