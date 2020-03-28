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
package fr.bretzel.quake.game.task;

import fr.bretzel.quake.PlayerInfo;
import fr.bretzel.quake.Quake;
import fr.bretzel.quake.SchootTask;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

/**
 * Created by MrBretzel on 22/06/2015.
 */
public class ReloadTaskOld extends SchootTask
{

    private int id = -1;

    private double reload_time = 0;

    private final String bare = "|||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||".trim();

    private final boolean isDash;

    private double remplissage = 0;

    public ReloadTaskOld(PlayerInfo info, boolean isDash)
    {
        super(info);

        if (isDash)
            this.reload_time = 2.5;
        else
            this.reload_time = info.getReloadTime();

        this.isDash = isDash;

        getInfo().getPlayer().sendTitle("", "", 0, 20, 0);

        this.id = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(Quake.quake, this, 0, 0);
    }

    @Override
    public void run()
    {
        StringBuilder builder = new StringBuilder(bare);

        remplissage += 5 / reload_time;
        double position = Math.round(bare.length() * remplissage / 100);

        //if (isDash && getInfo().isDash())
        remplissage = 100;

        //if (!isDash && getInfo().isShoot())
        remplissage = 100;

        if (remplissage >= 100)
        {
            if (isDash)
            {
                //getInfo().setDash(true);
            } else
            {
                //getInfo().setShoot(true);
            }
            cancel();
        }

        if (position <= bare.length())
            builder.insert((int) position, ChatColor.GRAY + "" + ChatColor.BOLD);

        if (!isDash)
        {
            getInfo().getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.BLUE + "" + "Shoot: " +
                    ChatColor.DARK_GRAY + "[" + ChatColor.AQUA + "" + ChatColor.BOLD + builder.toString() + ChatColor.DARK_GRAY + "]"));
        }
    }

    public int getId()
    {
        return id;
    }

    public void cancel()
    {
        Bukkit.getServer().getScheduler().cancelTask(getId());
    }
}
