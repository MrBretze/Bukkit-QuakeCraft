package fr.bretzel.quake.game.scoreboard;


import fr.bretzel.quake.Quake;
import fr.bretzel.quake.game.Game;
import fr.bretzel.quake.game.task.ScoreboardTask;
import org.bukkit.Bukkit;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

/**
 * Created by MrBretzel on 29/06/2015
 */

public class ScoreboardAPI {

    private Scoreboard scoreboard;

    private Objective objective;

    private ScoreboardTask task;

    private Game game;

    public ScoreboardAPI(Game game) {
        setGame(game);

        setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());

        setObjective(getScoreboard().registerNewObjective("quake", "dummy"));

        getObjective().getScore("§r").setScore(10);
        getObjective().getScore(game.getDisplayName()).setScore(9);
        getObjective().getScore("§r§r").setScore(8);
        getObjective().getScore(Quake.gameManager.signEvent.getInfoPlayer(game)).setScore(7);
        getObjective().getScore("§r§r§r").setScore(6);

        getObjective().setDisplaySlot(DisplaySlot.SIDEBAR);

        task = new ScoreboardTask(Quake.quake, 2, 2, getGame());
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public Objective getObjective() {
        return objective;
    }

    public void setObjective(Objective objective) {
        this.objective = objective;
    }

    public Scoreboard getScoreboard() {
        return scoreboard;
    }

    public void setScoreboard(Scoreboard scoreboard) {
        this.scoreboard = scoreboard;
    }

    public ScoreboardTask getTask() {
        return task;
    }
}
