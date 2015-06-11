package fr.bretzel.quake;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;

import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.meta.FireworkMeta;
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

            List<Location> locs = Util.getLocationByDirection(player, 100, 1.0D);

            for(Location l : locs) {
                ParticleEffect.FIREWORKS_SPARK.display(0, 0, 0, 0, 1, l, 100);
            }

            List<Player> pList = Util.getPlayerListInDirection(locs, player, 1.0D);

            for(Player p : pList) {
                p.setHealth(0);
                Location l = p.getLocation();
                shootFirework(l);
            }
        }
    }

    public void shootFirework(Location l) {
        Firework fw = l.getWorld().spawn(l.clone(),Firework.class);
        FireworkMeta fm = fw.getFireworkMeta();
        Random r = new Random();
        int fType = r.nextInt(5) + 1;
        FireworkEffect.Type type = FireworkEffect.Type.BALL;
        switch (fType) {
            case 1:
                type = FireworkEffect.Type.BALL;
                break;
            case 2:
                type = FireworkEffect.Type.BALL_LARGE;
                break;
            case 3:
                type = FireworkEffect.Type.BURST;
                break;
            case 4:
                type = FireworkEffect.Type.CREEPER;
                break;
            case 5:
                type = FireworkEffect.Type.STAR;
        }

        int c1i = r.nextInt(17) + 1;
        int c2i = r.nextInt(17) + 1;
        Color c1 = getColor (c1i);
        Color c2 = getColor (c2i);
        FireworkEffect effect = FireworkEffect.builder()
                .flicker(r.nextBoolean()).withColor(c1).withFade(c2)
                .with(type).trail(r.nextBoolean()).build();
        fm.addEffect(effect);
        fm.setPower(0);
        fw.setFireworkMeta(fm);
    }

    public Color getColor(int c){
        switch (c){
            default:
            case 1:
                return Color.AQUA;
            case 2:
                return Color.BLACK;
            case 3:
                return Color.BLUE;
            case 4:
                return Color.FUCHSIA;
            case 5:
                return Color.GRAY;
            case 6:
                return Color.GREEN;
            case 7:
                return Color.LIME;
            case 8:
                return Color.MAROON;
            case 9:
                return Color.NAVY;
            case 10:
                return Color.OLIVE;
            case 11:
                return Color.ORANGE;
            case 12:
                return Color.PURPLE;
            case 13:
                return Color.RED;
            case 14:
                return Color.SILVER;
            case 15:
                return Color.TEAL;
            case 16:
                return Color.WHITE;
            case 17:
                return Color.YELLOW;
        }
    }
}
