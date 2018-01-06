/**
 * Copyright 2015 Loïc Nussbaumer
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you
 * may not use this file except in compliance with the License. You
 * may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License. See accompanying
 * LICENSE file.
 */
package fr.bretzel.quake;

import fr.bretzel.quake.config.Config;
import fr.bretzel.quake.game.Game;
import fr.bretzel.quake.game.State;
import fr.bretzel.quake.game.event.GameEndEvent;
import fr.bretzel.quake.game.event.PlayerDashEvent;
import fr.bretzel.quake.game.event.PlayerShootEvent;
import fr.bretzel.quake.game.task.DashTask;
import fr.bretzel.quake.game.task.GameEndTask;
import fr.bretzel.quake.game.task.ReloadTask;
import fr.bretzel.quake.inventory.Gun;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.NameTagVisibility;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.util.Vector;

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Random;
import java.util.UUID;

/**
 * Created by MrBretzel on 14/06/2015.
 */

public class PlayerInfo {

    private Player player;
    private ParticleEffect effect = ParticleEffect.FIREWORKS_SPARK;
    private double reload = 1.5;
    private Location firstLocation = null;
    private Location secondLocation = null;
    private boolean shoot = true;
    private boolean dash = true;
    private int playerkill = 0;
    private int coins = 0;
    private int won = 0;
    private int killstreak = 0;
    private int death = 0;
    private int respawn = 0;
    private Random random = new Random();

    public PlayerInfo(Player player) {
        setPlayer(player);

        Bukkit.getScheduler().runTask(Quake.quake, new LoadTask(this));
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

    public void give(Gun gun) {
        getPlayer().getInventory().clear();
        getPlayer().getInventory().setItem(0, gun.getStack());
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

    public void dash() {
        if (isDash()) {
            Game game = Quake.gameManager.getGameByPlayer(getPlayer());
            Quake.logDebug("Dash for player " + getPlayer().getDisplayName() + " in game " + game.getDisplayName());
            if (game.getState() == State.STARTED) {
                PlayerDashEvent event = new PlayerDashEvent(getPlayer(), game);
                Bukkit.getPluginManager().callEvent(event);
                setDash(false);
                Bukkit.getServer().getScheduler().runTaskLater(Quake.quake, new DashTask(this), (long) (getReloadTime() * 35));
                Vector pVector = player.getEyeLocation().getDirection();
                Vector vector = new Vector(pVector.getX(), 0.4D, pVector.getZ()).multiply(1.2D);
                Quake.logDebug("The vector for the dash is: " + vector);
                getPlayer().setVelocity(vector);
                getPlayer().getWorld().playSound(getPlayer().getLocation(), Sound.ENTITY_ENDERDRAGON_FLAP, random.nextFloat(), random.nextFloat());
            } else {
                Quake.logDebug("The game is not started, the dash is annulled");
            }
        } else {
            Quake.logDebug("The dash is annulled because the cooldown is not finish");
        }
    }

    public void shoot() {
        if (isShoot()) {
            Game game = Quake.gameManager.getGameByPlayer(getPlayer());
            List<Location> locs = Util.getLocationByDirection(getPlayer(), 200, 0.59D);
            PlayerShootEvent shoot = new PlayerShootEvent(getPlayer(), game, locs, Util.getPlayerListInDirection(locs, getPlayer(), 0.59D));
            Bukkit.getPluginManager().callEvent(shoot);
            if (shoot.isCancelled()) {
                return;
            }
            if (game.getState() == State.STARTED) {
                setShoot(false);
                Bukkit.getServer().getScheduler().runTaskLater(Quake.quake, new ReloadTask(this), (long) (this.getReloadTime() * 20));
                Util.playSound(getPlayer().getEyeLocation(), Sound.ENTITY_FIREWORK_LARGE_BLAST, 2F, 1F);
                for (Location location : locs) {
                    new ParticleEffect.ParticlePacket(getEffect(), 0, 0, 0, 0, 1, true, null).sendTo(location, 200D);
                }
                if (shoot.getKill() > 0) {
                    for (Player p : shoot.getPlayers()) {
                        shoot.getGame().setKillSteak(p, 0);
                        Util.playSound(p.getLocation(), Sound.ENTITY_BLAZE_DEATH, 2F, 2F);
                        shoot.getGame().respawn(p);
                        Quake.getPlayerInfo(p).addDeath(1);
                    }
                    int kill;
                    if (Integer.valueOf(game.getKill(getPlayer())) == null) {
                        kill = shoot.getKill();
                    } else {
                        kill = game.getKill(getPlayer()) + shoot.getKill();
                    }

                    shoot.getGame().addKillStreak(getPlayer(), shoot.getKill());

                    int killStreak = shoot.getGame().getKillStreak(getPlayer());

                    if (killStreak == 5) {
                        game.broadcastMessage(ChatColor.RED.toString() + ChatColor.BOLD + getPlayer().getDisplayName() + " IS KILLING SPREE !");
                        addKillStreak(1);
                    } else if (killStreak == 10) {
                        game.broadcastMessage(ChatColor.RED.toString() + ChatColor.BOLD + getPlayer().getDisplayName() + " IS A RAMPAGE !");
                        addKillStreak(1);
                    } else if (killStreak == 15) {
                        game.broadcastMessage(ChatColor.RED.toString() + ChatColor.BOLD + getPlayer().getDisplayName() + " IS A DEMON !");
                        addKillStreak(1);
                    } else if (killStreak == 20) {
                        game.broadcastMessage(ChatColor.RED.toString() + ChatColor.BOLD + getPlayer().getDisplayName() + " MONSTER KIL !!");
                        addKillStreak(1);
                    }

                    addPlayerKill(shoot.getKill());
                    addCoins(5 * shoot.getKill());
                    game.addKill(getPlayer(), shoot.getKill());
                    game.getScoreboardManager().getObjective().getScore(getPlayer().getName()).setScore(kill);

                    if (game.getKill(getPlayer()) >= game.getMaxKill()) {
                        GameEndEvent event = new GameEndEvent(getPlayer(), game);
                        Bukkit.getPluginManager().callEvent(event);
                        if (event.isCancelled()) {
                            return;
                        }
                        for (UUID id : game.getPlayerList()) {
                            Player player = Bukkit.getPlayer(id);
                            if (player != null && player.isOnline()) {
                                PlayerInfo info = Quake.getPlayerInfo(player);
                                player.getInventory().clear();
                                info.setShoot(false);
                                info.setDash(false);
                            }
                        }

                        new GameEndTask(Quake.quake, 10L, 10L, game, getPlayer());

                        addWoon(1);
                        game.getTeam().setNameTagVisibility(NameTagVisibility.ALWAYS);
                        game.broadcastMessage(ChatColor.BLUE + ChatColor.BOLD.toString() + player.getName() + " Has won the game !");
                    }
                }
            }
        }
    }

    public int getPlayerKill() {
        return playerkill;
    }

    public void setPlayerKill(int playerkill) {
        this.playerkill = playerkill;
    }

    public void addPlayerKill(int kill) {
        setPlayerKill(getPlayerKill() + kill);
    }

    public void removePlayerKill(int kill) {
        setPlayerKill(getPlayerKill() - kill);
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

    public Scoreboard getPlayerScoreboard() {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective objective = scoreboard.registerNewObjective(getPlayer().getDisplayName(), "dummy");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setDisplayName(ChatColor.RED.toString() + ChatColor.BOLD + "    Info    " + ChatColor.RESET.toString());
        objective.getScore(ChatColor.RESET.toString()).setScore(10);
        objective.getScore("Coins: " + ChatColor.BLUE + getCoins()).setScore(9);
        objective.getScore(ChatColor.RESET + ChatColor.RESET.toString()).setScore(8);
        objective.getScore("Kills: " + ChatColor.BLUE + getPlayerKill()).setScore(7);
        objective.getScore(ChatColor.RESET + "                            " + ChatColor.RESET).setScore(6);
        objective.getScore("Win: " + ChatColor.BLUE + getWon()).setScore(5);
        objective.getScore(ChatColor.RESET.toString() + ChatColor.RESET.toString() + ChatColor.RESET.toString() + ChatColor.RESET.toString()).setScore(4);
        objective.getScore("KillStreak: " + ChatColor.BLUE + getKillStreak()).setScore(3);
        objective.getScore(ChatColor.RESET.toString() + ChatColor.RESET.toString() + ChatColor.RESET.toString() + ChatColor.RESET.toString() + ChatColor.RESET.toString()).setScore(2);
        objective.getScore("Death: " + ChatColor.BLUE + getDeath()).setScore(1);
        return scoreboard;
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

    public int getWon() {
        return won;
    }

    public void setWon(int won) {
        this.won = won;
    }

    public void addWoon(int woon) {
        setWon(getWon() + woon);
    }

    public void removeWoon(int woon) {
        setWon(getWon() - woon);
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

    public void save() {
        if (Quake.config.ifStringExist(getUUID().toString(), "UUID", Config.Table.PLAYERS)) {
            try {
                PreparedStatement statement = Quake.config.openConnection().prepareStatement("UPDATE " + Config.Table.PLAYERS.getTable() + " SET Effect = ?, Reload = ?, PlayerKill = ?, Coins = ?, Won = ?, KillStreak = ?, Death = ? WHERE UUID = ?");
                statement.setString(1, getEffect().getName());
                statement.setDouble(2, getReloadTime());
                statement.setInt(3, getPlayerKill());
                statement.setInt(4, getCoins());
                statement.setInt(5, getWon());
                statement.setInt(6, getKillStreak());
                statement.setInt(7, getDeath());
                statement.setString(8, getUUID().toString());
                Quake.config.executePreparedStatement(statement);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            try {
                PreparedStatement statement = Quake.config.openConnection().prepareStatement("INSERT INTO " + Config.Table.PLAYERS.getTable() + "(UUID, Effect, Reload, PlayerKill, Coins, Won, KillStreak, Death) VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
                statement.setString(1, getUUID().toString());
                statement.setString(2, getEffect().getName());
                statement.setDouble(3, getReloadTime());
                statement.setInt(4, getPlayerKill());
                statement.setInt(5, getCoins());
                statement.setInt(6, getWon());
                statement.setInt(7, getKillStreak());
                statement.setInt(8, getDeath());
                Quake.config.executePreparedStatement(statement);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private class LoadTask implements Runnable {

        private PlayerInfo info;

        public LoadTask(PlayerInfo info) {
            this.info = info;
        }

        @Override
        public void run() {
            if (Quake.config.ifStringExist(info.getUUID().toString(), "UUID", Config.Table.PLAYERS)) {
                load();
            }
        }

        public void load() {
            try {
                Statement statement = Quake.config.openConnection().createStatement();
                ResultSet set = statement.executeQuery("SELECT * FROM " + Config.Table.PLAYERS.getTable() + " WHERE UUID = '" + info.getUUID().toString() +"'");
                if (set.next()) {
                    setEffect(ParticleEffect.fromName(set.getString("Effect")));
                    setReload(set.getDouble("Reload"));
                    setPlayerKill(set.getInt("PlayerKill"));
                    setCoins(set.getInt("Coins"));
                    setWon(set.getInt("Won"));
                    setKillStreak(set.getInt("KillStreak"));
                    setDeath(set.getInt("Death"));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            info.getPlayer().setScoreboard(info.getPlayerScoreboard());
        }
    }
}
