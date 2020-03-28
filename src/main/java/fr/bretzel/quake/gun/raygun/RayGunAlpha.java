package fr.bretzel.quake.gun.raygun;

import fr.bretzel.quake.PlayerInfo;
import fr.bretzel.quake.gun.Gun;
import org.bukkit.*;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class RayGunAlpha extends Gun
{
    public RayGunAlpha(PlayerInfo info)
    {

        super(info);
        ItemStack stack = new ItemStack(Material.WOODEN_HOE);
        stack.setAmount(1);
        ItemMeta meta = stack.getItemMeta();
        if (meta != null)
        {
            setName(ChatColor.BLUE + "Ray Gun Element 83");

            meta.setCustomModelData(3);

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
            world.spawnParticle(Particle.FLAME, location, 1, 0, 0, 0, 0, null, true);
            world.spawnParticle(Particle.FLAME, location, 1, 0, 0, 0, 0.02, null, true);
        }
    }
}
