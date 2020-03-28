package fr.bretzel.quake.gun.raygun;

import fr.bretzel.quake.PlayerInfo;
import fr.bretzel.quake.gun.Gun;
import org.bukkit.*;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class RayGunHoney extends Gun
{
    Location location = null;

    public RayGunHoney(PlayerInfo info)
    {
        super(info);
        ItemStack stack = new ItemStack(Material.WOODEN_HOE);
        stack.setAmount(1);
        ItemMeta meta = stack.getItemMeta();
        if (meta != null)
        {
            setName(ChatColor.GOLD + "Ray Gun Honey");

            meta.setCustomModelData(6);

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
            world.spawnParticle(Particle.BLOCK_CRACK, location, 0, 0, 0, 0, 0, Material.HONEY_BLOCK.createBlockData(), true);

            if (this.location != null)
                world.spawnParticle(Particle.FALLING_HONEY, location, 0, 0, 0, 0, 0, null, true);

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
