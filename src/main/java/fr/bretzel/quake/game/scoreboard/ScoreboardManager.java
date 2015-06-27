package fr.bretzel.quake.game.scoreboard;

import fr.bretzel.quake.game.Game;

import org.bukkit.Bukkit;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;

/**
 * Created by MrBretzel on 24/06/2015.
 */

public class ScoreboardManager {

    private LinkedList<OfflinePlayer> offlinePlayers = new LinkedList<>();
    private Game game;
    private Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
    private String objname = "info";
    private Objective objective;

    public ScoreboardManager(Game game) {
        setGame(game);

        if(scoreboard.getObjective(objname) == null) {
            objective = scoreboard.registerNewObjective(objname, "dummy");
        } else {
            objective = scoreboard.getObjective(objname);
        }
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public LinkedList<OfflinePlayer> getOfflinePlayers() {
        return offlinePlayers;
    }

    public void setOfflinePlayers(LinkedList<OfflinePlayer> offlinePlayers) {
        this.offlinePlayers = offlinePlayers;
    }

    public OfflinePlayer getOfflinePlayer(String name) {
        for (OfflinePlayer off : getOfflinePlayers()) {
            if(off.getName().equals(name)) {
                return off;
            }
        }
        return null;
    }

    public void addLine(String line) {
        addLine(line, line);
    }

    public void addLine(String line, String displayName) {
        try {
            OfflinePlayer offPlayer = OfflinePlayer.class.getDeclaredConstructor(String.class).newInstance(line);
            if(getOfflinePlayer(line) == null) {
                offPlayer.setDisplayName(displayName);
                getOfflinePlayers().add(offPlayer);
                setLine(offPlayer);
            }
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    public void setLine(int line, String name) {
        objective.getScore(name).setScore(line);
    }

    public void setLine(int line, OfflinePlayer offlinePlayer) {
        setLine(line, offlinePlayer.getDisplayName());
    }

    public void setLine(String name) {
        setLine(getOfflinePlayers().size(), name);
    }

    public void setLine(OfflinePlayer offlinePlayer) {
        setLine(getOfflinePlayers().size(), offlinePlayer.getDisplayName());
    }

    public String getLine(String offPlayer) {
        return getOfflinePlayer(offPlayer).getDisplayName();
    }

    public String getLine(OfflinePlayer offlinePlayer) {
        return offlinePlayer.getDisplayName();
    }

    public void resetLine(String line) {
        this.scoreboard.resetScores(line);
    }

    public void resetLine(OfflinePlayer offlinePlayer) {
        this.resetLine(offlinePlayer.getDisplayName());
    }

    public void resetLine(int index) {
        resetLine(getOfflinePlayers().get(index));
    }

    public Objective getObjective() {
        return objective;
    }

    public void setObjective(Objective objective) {
        this.objective = objective;
    }

    public void setObjectiveName(String objname) {
        this.objname = objname;
    }

    public String getObjectiveName() {
        return objname;
    }

    public void setScoreboard(Scoreboard scoreboard) {
        this.scoreboard = scoreboard;
    }

    public Scoreboard getScoreboard() {
        return scoreboard;
    }
}
