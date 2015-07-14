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

import fr.bretzel.nbt.NBTCompressedStreamTools;
import fr.bretzel.nbt.NBTTagCompound;
import fr.bretzel.quake.game.Game;
import fr.bretzel.quake.game.State;
import fr.bretzel.quake.game.event.GameEndEvent;
import fr.bretzel.quake.game.event.PlayerDashEvent;
import fr.bretzel.quake.game.event.PlayerShootEvent;
import fr.bretzel.quake.game.task.DashTask;
import fr.bretzel.quake.game.task.GameEndTask;
import fr.bretzel.quake.game.task.ReloadTask;
import fr.bretzel.quake.inventory.Gun;
import fr.bretzel.quake.reader.PlayerInfoReader;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.util.Vector;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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
    private File file;
    private int respawn = 0;
    private Random random = new Random();

    public PlayerInfo(Player player) {
        setPlayer(player);

        File mk = new File(Quake.quake.getDataFolder() + File.separator + "players" + File.separator);

        mk.mkdir();

        setFile(new File(mk, player.getUniqueId().toString() + ".dat"));

        try {
            if (!getFile().exists()) {
                getFile().createNewFile();
                NBTTagCompound compound = new NBTTagCompound();
                compound.setDouble("reload", getReloadTime());
                compound.setString("effect", getEffect().getName());
                NBTCompressedStreamTools.wrhite(compound, new FileOutputStream(getFile()));
            }
        } catch (Exception e) {
            e.fillInStackTrace();
        }
        PlayerInfoReader.read(this);
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
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

    public void setReload(long reload) {
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
            if (game.getState() == State.STARTED) {
                PlayerDashEvent event = new PlayerDashEvent(getPlayer(), game);
                Bukkit.getPluginManager().callEvent(event);
                setDash(false);
                Bukkit.getServer().getScheduler().runTaskLater(Quake.quake, new DashTask(this), (long) (getReloadTime() * 35));
                Vector pVector = player.getEyeLocation().getDirection();
                Vector vector = new Vector(pVector.getX(), 0.4D, pVector.getZ()).multiply(1.2D);
                getPlayer().setVelocity(vector);
                getPlayer().getWorld().playSound(getPlayer().getLocation(), Sound.ENDERDRAGON_WINGS, random.nextFloat(), random.nextFloat());
            }
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
            if (shoot.getKill() > 0 && game.getState() == State.STARTED) {
                setShoot(false);
                Bukkit.getServer().getScheduler().runTaskLater(Quake.quake, new ReloadTask(this), (long) (this.getReloadTime() * 20));
                for (Location location : locs) {
                    new ParticleEffect.ParticlePacket(getEffect(), 0.0F, 0.0F, 0.0F, 0.0F, 1, true, null).sendTo(location.clone(), 200D);
                }
                for (Player p : shoot.getPlayers()) {
                    shoot.getGame().setKillSteak(p, 0);
                    p.getWorld().playSound(p.getLocation(), Sound.BLAZE_DEATH, 2F, 2F);
                    shoot.getGame().respawn(p);
                }
                getPlayer().getWorld().playSound(getPlayer().getLocation(), Sound.FIREWORK_BLAST, 2F, 1F);
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

                addKill(shoot.getKill());
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
                            player.getInventory().clear();
                            setDash(false);
                            setShoot(false);
                        }
                    }
                    GameEndTask endTask = new GameEndTask(Quake.quake, 15L, 15L, game, getPlayer());
                    addWoon(1);
                    game.broadcastMessage(ChatColor.BLUE + ChatColor.BOLD.toString() + player.getName() + " Has won the game !");
                }
            }
        }
    }

    public int getPlayerkill() {
        return playerkill;
    }

    public void setPlayerkill(int playerkill) {
        this.playerkill = playerkill;
    }

    public void addKill(int kill) {
        setPlayerkill(getPlayerkill() + kill);
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

    public int getKillStreak() {
        return killstreak;
    }

    public void setKillStreak(int killstreak) {
        this.killstreak = killstreak;
    }

    public void addKillStreak(int kill) {
        setKillStreak(getKillStreak() + kill);
    }

    public Scoreboard getPlayerScoreboard() {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective objective = scoreboard.registerNewObjective(getPlayer().getDisplayName(), "dummy");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setDisplayName(ChatColor.RED.toString() + ChatColor.BOLD + "    Info    §r");
        objective.getScore("§r").setScore(10);
        objective.getScore("Coins: " + ChatColor.BLUE + getCoins()).setScore(9);
        objective.getScore("§r§r").setScore(8);
        objective.getScore("Kills: " + ChatColor.BLUE + getPlayerkill()).setScore(7);
        objective.getScore("§r§r§r").setScore(6);
        objective.getScore("Win: " + ChatColor.BLUE + getWon()).setScore(5);
        objective.getScore("§r§r§r§r").setScore(4);
        objective.getScore("KillStreak: " + ChatColor.BLUE + getKillStreak()).setScore(3);
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

    public void save() {
        try {
            NBTCompressedStreamTools.wrhite(PlayerInfoReader.write(this), new FileOutputStream(getFile()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
