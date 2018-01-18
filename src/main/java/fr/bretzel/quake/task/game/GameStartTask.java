package fr.bretzel.quake.task.game;

import fr.bretzel.quake.*;
import fr.bretzel.quake.game.Game;
import fr.bretzel.quake.game.State;
import fr.bretzel.quake.game.event.GameStartEvent;
import fr.bretzel.quake.game.scoreboard.ScoreboardAPI;
import fr.bretzel.quake.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.DisplaySlot;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class GameStartTask extends GameTask {

    private static NumberFormat formatter = new DecimalFormat("00");
    String last = "Waiting...";
    private int minSecQuake = getGame().getSecLaunch();
    private ScoreboardAPI scoreboardAPI = getGame().getScoreboardManager();

    public GameStartTask(JavaPlugin javaPlugin, long l, long l1, Game game) {
        super(javaPlugin, l, l1, game);
    }

    @Override
    public void run() {
        if (getGame().getPlayerList().size() < getGame().getMinPlayer()) {
            getGame().getScoreboardManager().getScoreboard().resetScores(this.last);
            getGame().getScoreboardManager().getObjective().getScore("Waiting...").setScore(5);
            cancel();
            return;
        }
        if (minSecQuake <= 5 || minSecQuake == 10) {
            getGame().broadcastMessage(ChatColor.BLUE + "The game start in: " + Util.getChatColorByInt(minSecQuake) + String.valueOf(minSecQuake));
            for (PlayerInfo id : getGame().getPlayerList()) {
                Player p = id.getPlayer();
                if (p != null && p.isOnline()) {
                    Util.playSound(p.getLocation(), Sound.BLOCK_NOTE_PLING, 2.0F, 2.0F);
                }
            }
        }

        if (minSecQuake > 0) {
            minSecQuake--;
        }

        String format = formatter.format(minSecQuake + 1) + "s";

        scoreboardAPI.getScoreboard().resetScores(this.last);

        scoreboardAPI.getObjective().getScore(format).setScore(5);

        this.last = format;

        if (minSecQuake <= 0 && getGame().getState() == State.WAITING) {
            GameStartEvent event = new GameStartEvent(getGame());
            Bukkit.getPluginManager().callEvent(event);
            if (event.isCancelled()) {
                getGame().stop();
                cancel();
                return;
            }
            getGame().setState(State.STARTED);
            Quake.gameManager.signEvent.actualiseJoinSignForGame(getGame());
            scoreboardAPI.getObjective().unregister();
            scoreboardAPI.setObjective(scoreboardAPI.getScoreboard().registerNewObjective("quake", "dummy"));
            scoreboardAPI.getObjective().setDisplaySlot(DisplaySlot.SIDEBAR);

            int locuse = 0;

            for (PlayerInfo id : getGame().getPlayerList()) {
                Player player = id.getPlayer();
                if ((player != null) && (player.isOnline())) {
                    player.teleport(getGame().getRespawns().get(locuse));
                    PlayerInfo info = PlayerInfo.getPlayerInfo(player);
                    info.giveGun();
                    player.getInventory().setHeldItemSlot(0);
                    getGame().setKill(id, 0);
                    scoreboardAPI.getObjective().getScore(player.getName()).setScore(1);
                    scoreboardAPI.getObjective().getScore(player.getName()).setScore(getGame().getKill(player));

                    locuse++;

                    getGame().gamePlayerTasks.add(new GamePlayerTask(Quake.quake, 0, 0, info));

                    if (locuse >= getGame().getPlayerList().size()) {
                        locuse = 0;
                    }
                }
            }
            cancel();
        }
    }
}
