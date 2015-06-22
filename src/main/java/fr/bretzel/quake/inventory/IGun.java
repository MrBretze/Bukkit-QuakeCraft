package fr.bretzel.quake.inventory;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Created by MrBretzel on 22/06/2015.
 */
public interface IGun {

    Player getPlayer();

    void setPlayer(Player player);

    ItemStack getStack();

    double getReload();

    Material getMaterial();

    String getName();

    void setMaterial(Material material);

    void setName(String name);

    void setReload(double reload);

    void setStack(ItemStack stack);
}
