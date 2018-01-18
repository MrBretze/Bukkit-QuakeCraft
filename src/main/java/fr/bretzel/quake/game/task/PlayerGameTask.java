package fr.bretzel.quake.game.task;

import fr.bretzel.quake.PlayerInfo;

import fr.bretzel.quake.Title;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

public class PlayerGameTask implements Runnable{

    private int id = -1;
    private JavaPlugin javaPlugin;

    private String bare = "|||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||".trim();

    private PlayerInfo info;

    public PlayerGameTask(JavaPlugin javaPlugin, long l, long l1, PlayerInfo info) {
        this.javaPlugin = javaPlugin;
        this.id = javaPlugin.getServer().getScheduler().scheduleSyncRepeatingTask(javaPlugin, this, l, l1);

        this.info = info;

    }

    int dshoot = 0;
    int ddash = 0;

    @Override
    public void run() {
        if (info.isShoot()) {
            dshoot++;
            Title.sendTitle(info.getPlayer(), Title.Type.ACTIONBAR,  ChatColor.GOLD + "Reloading shoot: " + ChatColor.DARK_RED + "[" + getChatColor(dshoot) + bare + ChatColor.DARK_RED + "]");
            if (dshoot >= 50) {
                dshoot = 0;
            }
        } else {
            dshoot = 0;
        }

        /*if (info.isDash()) {
            ddash++;
            ItemMeta meta = info.getPlayer().getInventory().getItemInMainHand().getItemMeta();
            meta.setDisplayName(getChatColor(ddash) + bare);
            info.getPlayer().getInventory().getItemInMainHand().setItemMeta(meta);
            if (ddash >= 50) {
                ddash = 0;
            }
        } else {
            ddash = 0;
        }*/
    }

    public void cancel() {
        javaPlugin.getServer().getScheduler().cancelTask(id);
    }

    public ChatColor getChatColor(int value) {
        return value >= 25 ? ChatColor.DARK_GREEN : ChatColor.GREEN;
    }
}
