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
package fr.bretzel.quake.gun;

import fr.bretzel.quake.PlayerInfo;
import fr.bretzel.quake.Util;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Created by MrBretzel on 22/06/2015.
 */

public abstract class Gun implements IGun
{

    private PlayerInfo playerInfo;
    private String name = "Basic Gun";
    private ItemStack stack = new ItemStack(Material.WOODEN_HOE);

    public Gun(PlayerInfo info)
    {
        setPlayerInfo(info);
    }

    public PlayerInfo getPlayerInfo()
    {
        return playerInfo;
    }

    public void setPlayerInfo(PlayerInfo playerInfo)
    {
        this.playerInfo = playerInfo;
    }

    public void applyEffect(Location location)
    {
        World world = location.getWorld();
        if (world != null)
            world.spawnParticle(Particle.REDSTONE, location, 1, 0, 0, 0, 0, new Particle.DustOptions(Color.WHITE, 1), true);
    }

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public void setName(String name)
    {
        this.name = name;
    }

    @Override
    public ItemStack getStack()
    {
        return stack;
    }

    @Override
    public void setStack(ItemStack stack)
    {
        this.stack = stack;
    }

    @Override
    public Player getPlayer()
    {
        return getPlayerInfo().getPlayer();
    }

    @Override
    public void setPlayer(Player player)
    {
        this.playerInfo.setPlayer(player);
    }

    public void endEffect(Location location)
    {
        location.getWorld().spawnParticle(Particle.FLASH, location, 1, 0, 0, 0, 0, null, true);
    }

    public void playerDeadEffect(Player player)
    {
        for (double d = 0; d <= 2; d += 0.1)
            Util.getCircle(player.getLocation().add(0, d, 0), 0.5, 10)
                    .forEach(location ->
                            location.getWorld().spawnParticle(Particle.REDSTONE, location, 10, 0.2, 0.2, 0.2, 0,
                                    new Particle.DustOptions(Color.RED, 0.8F), true));

        for (double d = 0; d <= 2; d += 0.1)
            player.getWorld().spawnParticle(Particle.BLOCK_CRACK, player.getLocation().add(0, 1, 0), 10, 0, 1, 0, 0,
                    Material.REDSTONE_BLOCK.createBlockData(), true);
    }
}
