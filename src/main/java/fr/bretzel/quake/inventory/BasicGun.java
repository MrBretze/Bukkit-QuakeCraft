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
package fr.bretzel.quake.inventory;

import fr.bretzel.quake.Quake;
import fr.bretzel.quake.PlayerInfo;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * Created by MrBretzel on 21/06/2015.
 */

public class BasicGun extends Gun {

    private Material material;
    private ItemStack stack;
    private String name;
    private double reload;
    private Player player;

    public BasicGun(PlayerInfo info) {
        super(info);
        setPlayer(info.getPlayer());
        setMaterial(Material.WOOD_HOE);
        setName(ChatColor.GREEN + "Basic Railgun");
        setReload(info.getReloadTime());

        ItemStack stack = new ItemStack(getMaterial());
        stack.setAmount(1);
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(getName());
        for(ItemFlag flag : ItemFlag.values()) {
            meta.addItemFlags(flag);
        }
        stack.setItemMeta(meta);
        setStack(stack);
    }

    public BasicGun(Player player) {
        this(Quake.getPlayerInfo(player));
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public ItemStack getStack() {
        return stack;
    }

    public double getReload() {
        return reload;
    }

    public Material getMaterial() {
        return material;
    }

    public String getName() {
        return name;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setReload(double reload) {
        this.reload = reload;
    }

    public void setStack(ItemStack stack) {
        this.stack = stack;
    }
}
