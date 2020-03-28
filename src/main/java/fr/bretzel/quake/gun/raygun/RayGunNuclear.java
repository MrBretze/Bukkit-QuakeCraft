package fr.bretzel.quake.gun.raygun;

import fr.bretzel.quake.PlayerInfo;
import fr.bretzel.quake.gun.Gun;
import org.bukkit.*;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class RayGunNuclear extends Gun
{
    public RayGunNuclear(PlayerInfo info)
    {
        super(info);
        ItemStack stack = new ItemStack(Material.WOODEN_HOE);
        stack.setAmount(1);
        ItemMeta meta = stack.getItemMeta();
        if (meta != null)
        {
            setName(ChatColor.GREEN + "Ray Gun Element 93");

            meta.setCustomModelData(5);

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
            world.spawnParticle(Particle.SNEEZE, location, 1, 0, 0, 0, 0.01, null, true);
            world.spawnParticle(Particle.REDSTONE, location, 2, 0.15, 0.15, 0.15, 0,
                    new Particle.DustOptions(Color.fromBGR(127, 255, 0), 1), true);
        }
    }
}
