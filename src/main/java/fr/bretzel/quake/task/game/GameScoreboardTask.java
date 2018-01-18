package fr.bretzel.quake.task.game;

import fr.bretzel.quake.util.ColorScroller;
import fr.bretzel.quake.game.Game;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

public class GameScoreboardTask extends GameTask {

    private ColorScroller scroller = new ColorScroller(ChatColor.RED, "QuakeCraft", ChatColor.DARK_RED.toString(), ChatColor.RED.toString(), ChatColor.RED.toString(), true, false, ColorScroller.ScrollType.FORWARD);

    public GameScoreboardTask(JavaPlugin javaPlugin, long l, long l1, Game game) {
        super(javaPlugin, l, l1, game);
    }

    @Override
    public void run() {

        if(scroller.getScrollType() == ColorScroller.ScrollType.FORWARD) {
            if(scroller.getPosition() >= scroller.getString().length()) {
                scroller.setScrollType(ColorScroller.ScrollType.BACKWARD);
            }
        } else {
            if(scroller.getPosition() <= -1) {
                scroller.setScrollType(ColorScroller.ScrollType.FORWARD);
            }
        }

        if (getGame().getScoreboardManager().getObjective() == null) {
            getGame().getScoreboardManager().setObjective(getGame().getScoreboardManager().getScoreboard().registerNewObjective("quake", "dummy"));
        }

        getGame().getScoreboardManager().getObjective().setDisplayName(scroller.next());
    }
}
