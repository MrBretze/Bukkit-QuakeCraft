package fr.bretzel.quake.gun.raygun;

import fr.bretzel.quake.PlayerInfo;
import fr.bretzel.quake.gun.Gun;
import org.bukkit.*;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class BlasterDL44 extends Gun
{
    Location location = null;

    public BlasterDL44(PlayerInfo info)
    {
        super(info);

        ItemStack stack = new ItemStack(Material.WOODEN_HOE);
        stack.setAmount(1);
        ItemMeta meta = stack.getItemMeta();
        if (meta != null)
        {
            setName(ChatColor.DARK_GRAY + "Blaster DL-44");

            meta.setCustomModelData(1);

            meta.setDisplayName(getName());

            for (ItemFlag flag : ItemFlag.values())
            {
                meta.addItemFlags(flag);
            }
            stack.setItemMeta(meta);
        }
        setStack(stack);
    }

    @Override
    public void applyEffect(Location location)
    {
        World world = location.getWorld();
        if (world != null)
        {
            world.spawnParticle(Particle.REDSTONE, location, 1, 0, 0, 0, 0,
                    new Particle.DustOptions(Color.fromBGR(0, 0, 255), 0.8f), true);

            if (this.location != null)
                world.spawnParticle(Particle.TOWN_AURA, location, 5, 0, 0, 0, 0, null, true);

            this.location = location;
        }
    }

    @Override
    public void endEffect(Location location)
    {
        super.endEffect(location);
        this.location = null;
    }
}
