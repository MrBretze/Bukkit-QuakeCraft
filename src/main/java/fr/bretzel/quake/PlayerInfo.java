package fr.bretzel.quake;

import com.google.common.collect.Lists;

import fr.bretzel.quake.config.Config;
import fr.bretzel.quake.util.ParticleEffect;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public class PlayerInfo {

    private Player player;
    private ParticleEffect effect = ParticleEffect.FIREWORKS_SPARK;
    private double reload = 1.5;
    private Location firstLocation = null;
    private Location secondLocation = null;
    private boolean shoot = true;
    private boolean dash = true;
    private String name;
    private Date lastConnection;
    private int kill = 0;
    private int coins = 0;
    private int win = 0;
    private int killstreak = 0;
    private int death = 0;
    private int respawn = 0;

    private static List<PlayerInfo> playerInfos = Lists.newLinkedList();

    /**
     * Start of Static member
     */

    public static PlayerInfo getPlayerInfo(Player player) {
        for (PlayerInfo pi : playerInfos) {
            if (pi.getUUID().toString().equalsIgnoreCase(player.getUniqueId().toString())) {
                return pi;
            }
        }
        PlayerInfo info = new PlayerInfo(player);
        playerInfos.add(info);
        return info;
    }

    public static List<PlayerInfo> getAllPlayerInfo() {
        return playerInfos;
    }

    /**
     * End of Static member
     */

    public PlayerInfo(Player player) {
        setPlayer(player);
        setName(getPlayer().getName());
        load();
    }

    public void setLastConnection(Date date) {
        this.lastConnection = date;
    }

    public Date getLastConnection() {
        return lastConnection;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public double getReloadTime() {
        return reload;
    }

    public void setReloadTime(double reload) {
        this.reload = reload;
    }

    public ParticleEffect getEffect() {
        return effect;
    }

    public void setEffect(ParticleEffect effect) {
        this.effect = effect;
    }

    public UUID getUUID() {
        return getPlayer().getUniqueId();
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Location getFirstLocation() {
        return firstLocation;
    }

    public void setFirstLocation(Location firstLocation) {
        this.firstLocation = firstLocation;
    }

    public Location getSecondLocation() {
        return secondLocation;
    }

    public void setSecondLocation(Location secondLocation) {
        this.secondLocation = secondLocation;
    }

    public void setReload(double reload) {
        this.reload = reload;
    }

    public boolean isInGame() {
        return Quake.gameManager.getGameByPlayer(getPlayer()) != null;
    }

    public void giveGun() {
        getPlayer().getInventory().clear();
        getPlayer().getInventory().setItem(0, new ItemStack(Material.WOOD_HOE));
    }

    public boolean isShoot() {
        return shoot;
    }

    public void setShoot(boolean shoot) {
        this.shoot = shoot;
    }

    public boolean isDash() {
        return dash;
    }

    public void setDash(boolean dash) {
        this.dash = dash;
    }

    public int getKill() {
        return kill;
    }

    public void setKill(int playerkill) {
        this.kill = playerkill;
    }

    public void addKill(int kill) {
        setKill(getKill() + kill);
    }

    public void removePlayerKill(int kill) {
        setKill(getKill() - kill);
    }

    public int getCoins() {
        return coins;
    }

    public void setCoins(int coins) {
        this.coins = coins;
    }

    public void addCoins(int coins) {
        setCoins(getCoins() + coins);
    }

    public void removeCoins(int coins) {
        setCoins(getCoins() - coins);
    }

    public int getKillStreak() {
        return killstreak;
    }

    public void setKillStreak(int killstreak) {
        this.killstreak = killstreak;
    }

    public void addKillStreak(int killstreak) {
        setKillStreak(getKillStreak() + killstreak);
    }

    public void removeKillStreak(int killstreak) {
        setKillStreak(getKillStreak() - killstreak);
    }

    public int getRespawn() {
        return respawn;
    }

    public void setRespawn(int respawn) {
        this.respawn = respawn;
    }

    public void addRespawn(int respawn) {
        setRespawn(getRespawn() + respawn);
    }

    public int getWin() {
        return win;
    }

    public void setWin(int win) {
        this.win = win;
    }

    public void addWin(int win) {
        setWin(getWin() + win);
    }

    public void removeWin(int win) {
        setWin(getWin() - win);
    }

    public int getDeath() {
        return death;
    }

    public void setDeath(int death) {
        this.death = death;
    }

    public void addDeath(int death) {
        setDeath(getDeath() + death);
    }

    public void syncDB() {
        save();
        if (!isInGame() && getPlayer().isOnline()) {
            getPlayer().setScoreboard(getPlayerScoreboard());
        }
    }

    public Scoreboard getPlayerScoreboard() {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective objective = scoreboard.registerNewObjective(getPlayer().getDisplayName(), "dummy");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setDisplayName(ChatColor.RED.toString() + ChatColor.BOLD + "    Info    " + ChatColor.RESET.toString());
        objective.getScore(ChatColor.RESET.toString()).setScore(10);
        objective.getScore("Coins: " + ChatColor.BLUE + getCoins()).setScore(9);
        objective.getScore(ChatColor.RESET + ChatColor.RESET.toString()).setScore(8);
        objective.getScore("Kills: " + ChatColor.BLUE + getKill()).setScore(7);
        objective.getScore(ChatColor.RESET + "                            " + ChatColor.RESET).setScore(6);
        objective.getScore("Win: " + ChatColor.BLUE + getWin()).setScore(5);
        objective.getScore(ChatColor.RESET.toString() + ChatColor.RESET.toString() + ChatColor.RESET.toString() + ChatColor.RESET.toString()).setScore(4);
        objective.getScore("KillStreak: " + ChatColor.BLUE + getKillStreak()).setScore(3);
        objective.getScore(ChatColor.RESET.toString() + ChatColor.RESET.toString() + ChatColor.RESET.toString() + ChatColor.RESET.toString() + ChatColor.RESET.toString()).setScore(2);
        objective.getScore("Death: " + ChatColor.BLUE + getDeath()).setScore(1);
        return scoreboard;
    }

    public void save() {
        if (Quake.database.ifStringExist(getUUID().toString(), "UUID", Config.Table.PLAYERS)) {
            try {
                PreparedStatement statement = Quake.database.openConnection().prepareStatement("UPDATE " + Config.Table.PLAYERS.getTable() + " SET Effect = ?, Reload = ?, PlayerKill = ?, Coins = ?, Win = ?," +
                        " KillStreak = ?, Death = ?, Name = ?," + " LastConnection = ? WHERE UUID = ?");
                statement.setString(1, getEffect().getName());
                statement.setDouble(2, getReloadTime());
                statement.setInt(3, getKill());
                statement.setInt(4, getCoins());
                statement.setInt(5, getWin());
                statement.setInt(6, getKillStreak());
                statement.setInt(7, getDeath());
                statement.setString(8, getName());
                Date date = new java.sql.Date(getLastConnection().getTime());
                Object param = new java.sql.Timestamp(date.getTime());
                statement.setObject(9, param);
                statement.setString(10, getUUID().toString());
                Quake.database.executePreparedStatement(statement);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            try {
                PreparedStatement statement = Quake.database.openConnection().prepareStatement("INSERT INTO " + Config.Table.PLAYERS.getTable() + "(UUID, Effect, Reload, PlayerKill, Coins, Win, KillStreak, Death, Name, LastConnection) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
                statement.setString(1, getUUID().toString());
                statement.setString(2, getEffect().getName());
                statement.setDouble(3, getReloadTime());
                statement.setInt(4, getKill());
                statement.setInt(5, getCoins());
                statement.setInt(6, getWin());
                statement.setInt(7, getKillStreak());
                statement.setInt(8, getDeath());
                statement.setString(9, getName());
                if (getLastConnection() == null) {
                    setLastConnection(new Date());
                }
                Date date = new java.sql.Date(getLastConnection().getTime());
                Object param = new java.sql.Timestamp(date.getTime());
                statement.setObject(10, param);
                Quake.database.executePreparedStatement(statement);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void load() {
        try {
            Statement statement = Quake.database.openConnection().createStatement();
            ResultSet set = statement.executeQuery("SELECT * FROM " + Config.Table.PLAYERS.getTable() + " WHERE UUID = '" + getUUID().toString() + "'");
            if (set.next()) {
                setEffect(ParticleEffect.fromName(set.getString("Effect")));
                setReload(set.getDouble("Reload"));
                setKill(set.getInt("PlayerKill"));
                setCoins(set.getInt("Coins"));
                setWin(set.getInt("Win"));
                setKillStreak(set.getInt("KillStreak"));
                setDeath(set.getInt("Death"));
                setLastConnection(set.getDate("LastConnection"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        getPlayer().setScoreboard(getPlayerScoreboard());
    }
}
