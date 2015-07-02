package fr.bretzel.quake.game.task;

import fr.bretzel.quake.ColorScroller;
import fr.bretzel.quake.GameTask;
import fr.bretzel.quake.game.Game;
import fr.bretzel.quake.game.State;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by MrBretzel on 29/06/2015.
 */

public class ScoreboardTask extends GameTask {

    int w = 2;
    private ColorScroller scroller = new ColorScroller(ChatColor.RED, "QuakeCraft", "§c", "§4", "§c", true, false, ColorScroller.ScrollType.FORWARD);
    private String[] wait = {"Waitng...", "Waitng..", "Waitng."};

    public ScoreboardTask(JavaPlugin javaPlugin, long l, long l1, Game game) {
        super(javaPlugin, l, l1, game);
    }

    @Override
    public void run() {
        if (scroller.getScrollType() == ColorScroller.ScrollType.FORWARD) {
            if (scroller.getPosition() >= scroller.getString().length()) {
                scroller.setScrollType(ColorScroller.ScrollType.BACKWARD);
            }
        } else {
            if (scroller.getPosition() <= -1) {
                scroller.setScrollType(ColorScroller.ScrollType.FORWARD);
            }
        }

        if (getGame().getScoreboardManager().getObjective() == null) {
            getGame().getScoreboardManager().setObjective(getGame().getScoreboardManager().getScoreboard().registerNewObjective("quake", "dummy"));
        }

        getGame().getScoreboardManager().getObjective().setDisplayName(scroller.next());

        if (getGame().getState() == State.WAITING) {
            String n;
            String l;

            l = wait[w];
            if (w < 1) {
                w = 2;
            }
            w--;
            n = wait[w];

            getGame().getScoreboardManager().getScoreboard().resetScores(l);
            getGame().getScoreboardManager().getObjective().getScore(n).setScore(5);
        }
    }
}
