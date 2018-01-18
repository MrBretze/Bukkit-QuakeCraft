package fr.bretzel.quake.game.scoreboard;


import fr.bretzel.quake.Quake;
import fr.bretzel.quake.game.Game;
import fr.bretzel.quake.task.game.GameScoreboardTask;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

public class ScoreboardAPI {

    private Scoreboard scoreboard;

    private Objective objective;

    private GameScoreboardTask task;

    private Game game;

    public ScoreboardAPI(Game game) {
        setGame(game);

        setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());

        setObjective(getScoreboard().registerNewObjective("quake", "dummy"));

        getObjective().getScore(ChatColor.RESET.toString()).setScore(10);
        getObjective().getScore("Map: " + game.getDisplayName()).setScore(9);
        getObjective().getScore(ChatColor.RESET.toString() + ChatColor.RESET.toString()).setScore(8);
        getObjective().getScore(ChatColor.RESET.toString() + "                            " + ChatColor.RESET.toString()).setScore(6);
        getObjective().getScore("Waiting...").setScore(5);
        getObjective().setDisplaySlot(DisplaySlot.SIDEBAR);

        task = new GameScoreboardTask(Quake.quake, 2, 2, getGame());
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

    public GameScoreboardTask getTask() {
        return task;
    }
}
