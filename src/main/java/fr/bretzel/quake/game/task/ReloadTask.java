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
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by MrBretzel on 22/06/2015.
 */
public class ReloadTask extends SchootTask {

    private int id = -1;
    private JavaPlugin javaPlugin;

    private double reload_time = getInfo().getReloadTime();

    private int time = (int) (reload_time * 20);

    private String bare = ChatColor.GREEN + "|||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||".trim();

    private double position = 3;

    private double remplissage = 5 / reload_time;

    private long start;

    public ReloadTask(JavaPlugin javaPlugin, long l, long l1, PlayerInfo info) {
        super(info);
        start = System.nanoTime();
        Title.sendTimings(info.getPlayer(), 0, 50, 0);

        this.javaPlugin = javaPlugin;
        this.id = javaPlugin.getServer().getScheduler().scheduleSyncRepeatingTask(javaPlugin, this, l, l1);
    }


    //actualise tout les 0.05sec

    private int current_time = 0;

    @Override
    public void run() {
        StringBuilder builder = new StringBuilder(bare);

        position += Math.round(bare.length() * remplissage / 100);


        builder.insert((int) position, ChatColor.GRAY);

        Title.sendTitle(getInfo().getPlayer(), "", builder.toString());
        
        current_time++;

        if (current_time == time) {
            Title.sendTitle(getInfo().getPlayer(), "", "Time reload = " + (System.nanoTime() - start) / 1000000);
            getInfo().setShoot(true);
            cancel();
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
