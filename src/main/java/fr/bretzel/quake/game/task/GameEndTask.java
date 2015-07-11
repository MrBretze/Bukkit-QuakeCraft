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
import org.bukkit.*;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Random;
import java.util.UUID;

/**
 * Created by Loic on 05/07/2015.
 */

public class GameEndTask extends GameTask {

    private Player player;
    private int firewokSpawnable = 10;
    private Random random = new Random();

    public GameEndTask(JavaPlugin javaPlugin, long l, long l1, Game game, Player player) {
        super(javaPlugin, l, l1, game);
        setPlayer(player);
    }

    private static Color getColor(int c) {
        switch (c) {
            default:
            case 1:
                return Color.AQUA;
            case 2:
                return Color.BLACK;
            case 3:
                return Color.BLUE;
            case 4:
                return Color.FUCHSIA;
            case 5:
                return Color.GRAY;
            case 6:
                return Color.GREEN;
            case 7:
                return Color.LIME;
            case 8:
                return Color.MAROON;
            case 9:
                return Color.NAVY;
            case 10:
                return Color.OLIVE;
            case 11:
                return Color.ORANGE;
            case 12:
                return Color.PURPLE;
            case 13:
                return Color.RED;
            case 14:
                return Color.SILVER;
            case 15:
                return Color.TEAL;
            case 16:
                return Color.WHITE;
            case 17:
                return Color.YELLOW;
        }
    }

    @Override
    public void run() {
        if (firewokSpawnable >= 0) {
            firewokSpawnable--;
            if (firewokSpawnable == 5) {
                for (UUID uuid : getGame().getPlayerList()) {
                    Player p = Bukkit.getPlayer(uuid);
                    if (p != null && p.isOnline()) {
                        sendGameInfo(p);
                    }
                }
            }
            for (int i = 0; i <= 5; i++) {
                spawnFirework(player.getLocation());
            }
        } else {
            getGame().stop();
            cancel();
        }
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    private void spawnFirework(Location location) {
        Firework fw = location.getWorld().spawn(location.clone().add(0.0D, 0.7D, 0.0D), Firework.class);
        FireworkMeta fm = fw.getFireworkMeta();
        int fType = random.nextInt(5) + 1;
        FireworkEffect.Type type = FireworkEffect.Type.BALL;
        switch (fType) {
            case 1:
                type = FireworkEffect.Type.BALL;
                break;
            case 2:
                type = FireworkEffect.Type.BALL_LARGE;
                break;
            case 3:
                type = FireworkEffect.Type.BURST;
                break;
            case 4:
                type = FireworkEffect.Type.CREEPER;
                break;
            case 5:
                type = FireworkEffect.Type.STAR;
        }

        int c1i = random.nextInt(17) + 1;
        int c2i = random.nextInt(17) + 1;
        Color c1 = getColor(c1i);
        Color c2 = getColor(c2i);
        FireworkEffect effect = FireworkEffect.builder()
                .flicker(random.nextBoolean()).withColor(c1).withFade(c2)
                .with(type).trail(random.nextBoolean()).build();
        fm.addEffect(effect);
        fm.setPower(random.nextInt(2) + 1);
        fw.setFireworkMeta(fm);
    }

    public void sendGameInfo(Player player) {
        NumberFormat formatter = new DecimalFormat("00");
        player.sendMessage(ChatColor.AQUA + "####################");
        player.sendMessage(ChatColor.AQUA + "#    Kills: " + ChatColor.BLUE.toString() + formatter.format(getGame().getKill(player)) + ChatColor.AQUA + "            #");
        player.sendMessage(ChatColor.AQUA + "#    Coins: " + ChatColor.BLUE.toString() + formatter.format(getGame().getKill(player) * 5) + ChatColor.AQUA + "          #");
        player.sendMessage(ChatColor.AQUA + "####################");

    }
}
