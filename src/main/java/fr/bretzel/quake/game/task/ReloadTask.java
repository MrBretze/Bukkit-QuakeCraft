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
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by MrBretzel on 22/06/2015.
 */
public class ReloadTask extends SchootTask {

    private int id = -1;
    private JavaPlugin javaPlugin;

    private double reload_time = 0;

    private String bare = "|||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||".trim();

    private boolean isDash = false;

    private double remplissage = 0;

    public ReloadTask(JavaPlugin javaPlugin, PlayerInfo info, double reload_time, boolean isDash) {
        super(info);
        this.reload_time = reload_time;
        this.isDash = isDash;

        Title.sendTitle(info.getPlayer(), 0, 20, 0);

        this.javaPlugin = javaPlugin;
        this.id = javaPlugin.getServer().getScheduler().scheduleSyncRepeatingTask(javaPlugin, this, 0, 0);
    }

    @Override
    public void run() {
        StringBuilder builder = new StringBuilder(bare);

        remplissage += 5 / reload_time;
        double position = Math.round(bare.length() * remplissage / 100);

        if (remplissage >= 100) {
            if(isDash) {
                getInfo().setDash(true);
            } else {
                getInfo().setShoot(true);
            }
            cancel();
        }

        if (position <= bare.length())
            builder.insert((int) position, ChatColor.GRAY + "" + ChatColor.BOLD);

        if (!isDash) {
            Title.sendTitle(getInfo().getPlayer(), Title.Type.ACTIONBAR,  ChatColor.GOLD + "" + "Reloading shoot: " + ChatColor.DARK_RED + "[" + ChatColor.GREEN + builder.toString() + ChatColor.DARK_RED + "]");
        } else {
            ItemMeta meta = getInfo().getPlayer().getInventory().getItemInMainHand().getItemMeta();
            meta.setDisplayName(ChatColor.GREEN + builder.toString());
            //getInfo().getPlayer().getInventory().getItemInMainHand().setItemMeta(meta);
        }
    }

    public int getId() {
        return id;
    }

    public JavaPlugin getJavaPlugin() {
        return javaPlugin;
    }

    public void cancel() {
        getJavaPlugin().getServer().getScheduler().cancelTask(getId());
    }
}
