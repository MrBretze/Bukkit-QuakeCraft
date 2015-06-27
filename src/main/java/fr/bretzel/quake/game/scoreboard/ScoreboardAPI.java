package fr.bretzel.quake.game.scoreboard;

import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.*;

public class ScoreboardAPI {

    private Scoreboard scoreboard;

    private Map<String, Integer> scores;
    private LinkedList<UUID> players = new LinkedList<>();
    private List<Team> teams;
    private Objective objective;

    public ScoreboardAPI(String title) {
        this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        this.scores = Maps.newLinkedHashMap();
        this.teams = Lists.newArrayList();
        this.objective = scoreboard.registerNewObjective(title, "dummy");
        this.objective.setDisplaySlot(DisplaySlot.BELOW_NAME);
    }

    public void blankLine() {
        add(" ");
    }

    public void add(String text) {
        add(text, null);
    }

    public void add(String text, Integer score) {
        Preconditions.checkArgument(text.length() < 48, "text cannot be over 48 characters in length");
        text = fixDuplicates(text);
        scores.put(text, score);
    }

    private String fixDuplicates(String text) {
        while (scores.containsKey(text))
            text += "§r";
        if (text.length() > 48)
            text = text.substring(0, 47);
        return text;
    }

    private Map.Entry<Team, String> createTeam(String text) {
        String result = "";
        if (text.length() <= 16)
            return new AbstractMap.SimpleEntry<>(null, text);
        Team team = scoreboard.registerNewTeam("text-" + scoreboard.getTeams().size());
        Iterator<String> iterator = Splitter.fixedLength(16).split(text).iterator();
        team.setPrefix(iterator.next());
        result = iterator.next();
        if (text.length() > 32)
            team.setSuffix(iterator.next());
        teams.add(team);
        return new AbstractMap.SimpleEntry<>(team, result);
    }

    public LinkedList<UUID> getPlayers() {
        return players;
    }

    public Objective getObjective() {
        return objective;
    }

    public void build() {

        int index = scores.size();

        for (Map.Entry<String, Integer> text : scores.entrySet()) {
            Map.Entry<Team, String> team = createTeam(text.getKey());
            Integer score = text.getValue() != null ? text.getValue() : index;
            OfflinePlayer player = new FakeOfflinePlayer(team.getValue());
            if (team.getKey() != null)
                team.getKey().addPlayer(player);
            getObjective().getScore(player).setScore(score);
            index -= 1;
        }
    }

    public void update(String text, Integer score) {
        if (scores.containsKey(text)) {
            scores.put(text, score);
            for (Team t : teams) {
                if (t.getName() == text) {
                    t.unregister();
                }
            }

            Map.Entry<Team, String> team = createTeam(text);
            OfflinePlayer player = new FakeOfflinePlayer(team.getValue());
            if (team.getKey() != null)
                team.getKey().addPlayer(player);
            this.scoreboard.resetScores(player.getName());
            getObjective().getScore(player).setScore(score);
        }
    }

    public void reset() {
        scores.clear();
        for (Team t : teams)
            t.unregister();
        teams.clear();
    }

    public Scoreboard getScoreboard() {
        return scoreboard;
    }

    public void send(Player... players) {
        for (Player p : players) {
            p.setScoreboard(scoreboard);
            if (!this.players.contains(p.getUniqueId())) {
                this.players.add(p.getUniqueId());
            }
        }
    }


}
