package fr.bretzel.quake.task.game;

import fr.bretzel.quake.PlayerInfo;
import fr.bretzel.quake.Quake;
import fr.bretzel.quake.util.Util;
import fr.bretzel.quake.game.Game;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class GameEndTask extends GameTask {

    private Player player;
    private int firewokSpawnable = 15;

    public GameEndTask(JavaPlugin javaPlugin, long l, long l1, Game game, Player player) {
        super(javaPlugin, l, l1, game);
        setPlayer(player);
    }

    @Override
    public void run() {
        if (firewokSpawnable >= 0) {
            firewokSpawnable--;
            if (firewokSpawnable == 5) {
                for (PlayerInfo player : getGame().getPlayerList()) {
                    Player p = player.getPlayer();
                    if (p != null && p.isOnline()) {
                        sendGameInfo(p);
                    }
                }
            }
            Util.spawnFirework(Util.getCircle(player.getLocation().clone(), 1, 8));
        } else {
            Bukkit.getScheduler().runTaskLater(Quake.quake, new Runnable() {
                @Override
                public void run() {
                    getGame().stop();
                }
            }, 50L);
            cancel();
        }
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    private void sendGameInfo(Player player) {
        NumberFormat formatter = new DecimalFormat("00");
        player.sendMessage(ChatColor.AQUA + "####################");
        player.sendMessage(ChatColor.AQUA + "#    Kills: " + ChatColor.BLUE.toString() + formatter.format(getGame().getKill(player)) + ChatColor.AQUA + "              #");
        player.sendMessage(ChatColor.AQUA + "#    Coins: " + ChatColor.BLUE.toString() + formatter.format(getGame().getKill(player) * 5) + ChatColor.AQUA + "             #");
        player.sendMessage(ChatColor.AQUA + "####################");
    }
}
