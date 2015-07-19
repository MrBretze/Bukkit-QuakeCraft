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
package fr.bretzel.quake;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * Created by mrbretzel on 19/07/15.
 */

public class EndTaskUtil {

    private static Random random = new Random();

    public static void spawnFirework(List<Location> location) {
        Iterator<Location> iterator = location.iterator();
        while (iterator.hasNext()) {
            Location l = iterator.next();
            Firework fw = (Firework) l.getWorld().spawnEntity(l, EntityType.FIREWORK);
            FireworkMeta fwm = fw.getFireworkMeta();
            int rt = random.nextInt(5) + 1;
            FireworkEffect.Type type = FireworkEffect.Type.BALL;
            if (rt == 1) type = FireworkEffect.Type.BALL;
            if (rt == 2) type = FireworkEffect.Type.BALL_LARGE;
            if (rt == 3) type = FireworkEffect.Type.BURST;
            if (rt == 4) type = FireworkEffect.Type.CREEPER;
            if (rt == 5) type = FireworkEffect.Type.STAR;
            int u = random.nextInt(256);
            int b = random.nextInt(256);
            int g = random.nextInt(256);
            Color c1 = Color.fromRGB(u, g, b);
            u = random.nextInt(256);
            b = random.nextInt(256);
            g = random.nextInt(256);
            Color c2 = Color.fromRGB(u, g, b);
            FireworkEffect effect = FireworkEffect.builder().flicker(random.nextBoolean()).withColor(c1).withFade(c2).with(type).trail(random.nextBoolean()).build();
            fwm.addEffect(effect);
            int rp = random.nextInt(2) + 1;
            fwm.setPower(rp);
            fw.setFireworkMeta(fwm);
        }
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

}
