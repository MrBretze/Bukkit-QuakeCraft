package fr.bretzel.quake.game.task;

import fr.bretzel.quake.ColorScroller;
import fr.bretzel.quake.Quake;
import fr.bretzel.quake.game.Game;
import fr.bretzel.quake.game.GameManager;
import fr.bretzel.quake.game.State;
import fr.bretzel.quake.game.scoreboard.ScoreboardAPI;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Created by MrBretzel on 27/06/2015.
 */

public class ScoreboardTask extends BukkitRunnable {

    private Game game;
    private GameManager manager = Quake.gameManager;
    private ScoreboardAPI scoreboardAPI = game.getScoreboardManager();
    private ColorScroller scroller = new ColorScroller(ChatColor.RED, "QuakeCraft", "§c", "§4", "§c", true, false, ColorScroller.ScrollType.FORWARD);

    public ScoreboardTask(Game game) {
        this.game = game;
    }

    @Override
    public void run() {
        if (game.getState() == State.WAITING) {
            if (scroller.getScrollType() == ColorScroller.ScrollType.FORWARD) {
                if (scroller.getPosition() >= scroller.getString().length()) {
                    scroller.setScrollType(ColorScroller.ScrollType.BACKWARD);
                }
            } else {
                if (scroller.getPosition() <= -1) {
                    scroller.setScrollType(ColorScroller.ScrollType.FORWARD);
                }
            }

            scoreboardAPI.getObjective().setDisplayName(scroller.next());
        }
    }
}
