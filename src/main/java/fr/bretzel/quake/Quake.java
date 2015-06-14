package fr.bretzel.quake;

import fr.bretzel.quake.arena.ArenaManager;
import fr.bretzel.quake.player.PlayerInfo;
import org.bukkit.*;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.util.*;

/**
 * Created by MrBretzel on 09/06/2015.
 */

public class Quake extends JavaPlugin implements Listener {

    public static PluginManager manager;

    public static ArenaManager arenaManager;

    public static Quake quake;

    private static LinkedList<PlayerInfo> playerInfos = new LinkedList<>();

    @Override
    public void onEnable() {
        quake = this;

        manager = getServer().getPluginManager();

        //manager.registerEvents(this, this);

        arenaManager = new ArenaManager(this);

        getCommand("quake").setExecutor(new fr.bretzel.quake.command.Command());
        getCommand("quake").setTabCompleter(new fr.bretzel.quake.command.Command());
    }

    @Override
    public void onDisable() {

    }

    @EventHandler
    public void onPlayerClick(PlayerInteractEvent event) {
        if(event.getPlayer().getItemInHand() != null) {
            Player player = event.getPlayer();

            List<Location> locs = Util.getLocationByDirection(player, 100, 0.8D);

            for(Location l : locs) {
                ParticleEffect.FIREWORKS_SPARK.display(0, 0, 0, 0, 1, l, 100);
            }

            List<Player> pList = Util.getPlayerListInDirection(locs, player, 0.59D);

            for(Player p : pList) {
                p.setMetadata("killedby", new FixedMetadataValue(this, player));
                p.setHealth(0);
                shootFirework(p.getLocation(), -3);
            }
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player p = event.getEntity();

        Player killer = (Player) p.getMetadata("killedby").get(0).value();

        Bukkit.broadcastMessage(ChatColor.AQUA + p.getDisplayName() + ChatColor.BLUE + " was slain by " + ChatColor.AQUA + killer.getDisplayName());

        event.setDeathMessage(null);
    }

    public void shootFirework(Location l, int power) {
        Firework fw = l.getWorld().spawn(l.clone().add(0.0D, 0.7D, 0.0D),Firework.class);
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

        try {
            Field f = fm.getClass().getDeclaredField("power");
            f.setAccessible(true);
            f.set(fm, power);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

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

    public static PlayerInfo getPlayerInfo(Player player) {
        PlayerInfo playerInfo = null;
        for(PlayerInfo pi : playerInfos) {
            if(pi.getPlayer() == player) {
                playerInfo = pi;
            }
        }
        if(playerInfo == null) {
            playerInfo = new PlayerInfo(player);
            playerInfos.add(playerInfo);
        }
        return playerInfo;
    }
}
