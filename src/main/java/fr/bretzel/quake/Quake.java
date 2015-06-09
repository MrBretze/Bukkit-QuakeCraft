package fr.bretzel.quake;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import java.util.*;

/**
 * Created by MrBretzel on 09/06/2015.
 */

public class Quake extends JavaPlugin implements Listener {

    public static PluginManager manager;
    public Set<Material> m = new HashSet<>();

    @Override
    public void onEnable() {
        manager = getServer().getPluginManager();

        manager.registerEvents(this, this);

        for(Material material : Material.values()) {
            if(material.isTransparent()) {
                m.add(material);
            }
        }
    }

    @Override
    public void onDisable() {

    }

    @EventHandler
    public void onPlayerClick(PlayerInteractEvent event) {
        if(event.getPlayer().getItemInHand() != null) {
            Player player = event.getPlayer();

            List<Player> victims = getTargetV3(player, 100, 1.2D, false);

            for(Player l : victims) {
                l.setFireTicks(20);
            }
        }
    }

    public List<Player> getTargetV3(Player player, int maxRange, double aiming, boolean wallHack) {
        ArrayList target = new ArrayList();
        Location playerEyes = player.getEyeLocation();
        Vector direction = playerEyes.getDirection().normalize();
        ArrayList targets = new ArrayList();
        List<Player> lx = new ArrayList<>(Bukkit.getOnlinePlayers());
        int testLoc = lx.size();

        for(int loc = 0; loc < testLoc; ++loc) {
            Player block = lx.get(loc);
            if(block != player && block.getLocation().distanceSquared(playerEyes) < (double)(maxRange * maxRange)) {
                targets.add(block);
            }
        }

        Location var35 = playerEyes.clone();
        Vector progress = direction.clone().multiply(0.7D);
        maxRange = 100 * maxRange / 70;
        int loop = 0;

        while(loop < maxRange) {
            ++loop;
            var35.add(progress);
            Block var34 = var35.getBlock();
            if(!wallHack && var34.getType().isSolid()) {
                break;
            }

            double var37 = var35.getX();
            double ly = var35.getY();
            double lz = var35.getZ();
            Iterator var29 = Bukkit.getOnlinePlayers().iterator();

            while(var29.hasNext()) {
                Player possibleTarget = (Player) var29.next();

                try {
                    ParticleEffect.FLAME.display(0.0F, 0.0F, 0.0F, 0.0F, 1, var35, 100);
                } catch (Exception var33) {
                    var33.printStackTrace();
                }
            }

            var29 = targets.iterator();

            while(var29.hasNext()) {
                Player var38 = (Player)var29.next();
                if(var38.getUniqueId() != player.getUniqueId()) {
                    Location var36 = var38.getLocation().add(0.0D, 0.85D, 0.0D);
                    double px = var36.getX();
                    double py = var36.getY();
                    double pz = var36.getZ();
                    boolean dX = Math.abs(var37 - px) < 0.7D * aiming;
                    boolean dY = Math.abs(ly - py) < 1.7D * aiming;
                    boolean dZ = Math.abs(lz - pz) < 0.7D * aiming;
                    if(dX && dY && dZ && !target.contains(var38)) {
                        target.add(var38);
                    }
                }
            }
        }

        return target;
    }
}
