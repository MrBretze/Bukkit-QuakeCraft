package fr.bretzel.quake.gun.raygun;

import fr.bretzel.quake.PlayerInfo;
import fr.bretzel.quake.gun.Gun;
import org.bukkit.*;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class RayGunOmega extends Gun
{
    public RayGunOmega(PlayerInfo info)
    {
        super(info);

        ItemStack stack = new ItemStack(Material.WOODEN_HOE);
        stack.setAmount(1);
        ItemMeta meta = stack.getItemMeta();

        if (meta != null)
        {
            setName(ChatColor.DARK_BLUE + "Ray Gun Element 76");

            meta.setCustomModelData(4);

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
            world.spawnParticle(Particle.DOLPHIN, location, 10, 0.05, 0.05, 0.05, 1, null, true);
            world.spawnParticle(Particle.FALLING_WATER, location, 1, 0.1, 0.1, 0.1, 1, null, true);
        }
    }
}
