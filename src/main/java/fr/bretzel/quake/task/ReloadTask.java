package fr.bretzel.quake.task;

import fr.bretzel.quake.PlayerInfo;
import fr.bretzel.quake.util.Title;
import org.bukkit.ChatColor;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

public class ReloadTask extends SchootTask {

    private int id = -1;
    private JavaPlugin javaPlugin;

    private double reload_time = 0;

    private String bare = "|||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||".trim();

    private boolean isDash = false;

    private double remplissage = 0;

    public ReloadTask(JavaPlugin javaPlugin, PlayerInfo info, double reload_time, boolean isDash) {
        super(info);
        this.reload_time = reload_time;
        this.isDash = isDash;

        Title.sendTitle(info.getPlayer(), 0, 20, 0);

        this.javaPlugin = javaPlugin;
        this.id = javaPlugin.getServer().getScheduler().scheduleSyncRepeatingTask(javaPlugin, this, 0, 0);
    }

    @Override
    public void run() {
        StringBuilder builder = new StringBuilder(bare);

        remplissage += 5 / reload_time;
        double position = Math.round(bare.length() * remplissage / 100);

        if (remplissage >= 100) {
            if(isDash) {
                getInfo().setDash(true);
            } else {
                getInfo().setShoot(true);
            }
            cancel();
        }

        if (position <= bare.length())
            builder.insert((int) position, ChatColor.GRAY + "" + ChatColor.BOLD);

        if (!isDash) {
            Title.sendTitle(getInfo().getPlayer(), Title.Type.ACTIONBAR,  ChatColor.GOLD + "" + "Reloading shoot: " + ChatColor.DARK_RED + "[" + ChatColor.GREEN + builder.toString() + ChatColor.DARK_RED + "]");
        } else {
            ItemMeta meta = getInfo().getPlayer().getInventory().getItemInMainHand().getItemMeta();
            meta.setDisplayName(ChatColor.GREEN + builder.toString());
            //getInfo().getPlayer().getInventory().getItemInMainHand().setItemMeta(meta);
        }
    }

    public int getId() {
        return id;
    }

    public JavaPlugin getJavaPlugin() {
        return javaPlugin;
    }

    public void cancel() {
        getJavaPlugin().getServer().getScheduler().cancelTask(getId());
    }
}
