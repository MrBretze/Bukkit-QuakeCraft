package fr.bretzel.quake;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

/**
 * Created by MrBretzel on 09/06/2015.
 */

public class Quake extends JavaPlugin implements Listener {

    public static PluginManager manager;

    @Override
    public void onEnable() {
        manager = getServer().getPluginManager();

        manager.registerEvents(this, this);

    }

    @Override
    public void onDisable() {

    }

    @EventHandler
    public void onPlayerClick(PlayerInteractEvent event) {
        if(event.getPlayer().getItemInHand() != null) {
            Player player = event.getPlayer();

            List<Location> locs = Util.getLocationByDirection(player, 100, 1.2D);

            for(Location l : locs) {
                ParticleEffect.FIREWORKS_SPARK.display(0, 0, 0, 0, 1, l, 100);
            }
        }
    }
}
