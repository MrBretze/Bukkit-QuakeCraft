/**
 * Copyright 2015 Loïc Nussbaumer
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you
 * may not use this file except in compliance with the License. You
 * may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
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
import fr.bretzel.quake.game.event.PlayerShootEvent;
import fr.bretzel.quake.game.task.DashTask;
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
    private File file;
    private Random random = new Random();

    public PlayerInfo(Player player) {
        setPlayer(player);

        File mk = new File(Quake.quake.getDataFolder() + File.separator + "players" + File.separator);

        mk.mkdir();

        setFile(new File(mk, player.getUniqueId().toString() + ".dat"));

        try {
            if(!getFile().exists()) {
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
        if(isDash()) {
            setDash(false);
            Bukkit.getServer().getScheduler().runTaskLater(Quake.quake, new DashTask(this), (long) (getReloadTime() * 35));
            Vector pVector = player.getEyeLocation().getDirection();
            Vector vector = new Vector(pVector.getX(), 0.4D, pVector.getZ()).multiply(1.2D);
            getPlayer().setVelocity(vector);
            getPlayer().getWorld().playSound(getPlayer().getLocation(), Sound.ENDERDRAGON_WINGS, random.nextFloat(), random.nextFloat());
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
            setShoot(false);
            Bukkit.getServer().getScheduler().runTaskLater(Quake.quake, new ReloadTask(this), (long) (this.getReloadTime() * 20));
            for (Location location : locs) {
                new ParticleEffect.ParticlePacket(getEffect(), 0.0F, 0.0F, 0.0F, 0.0F, 1, true, null).sendTo(location.clone(), 200D);
            }
            if (shoot.getKill() > 0) {
                for (Player p : shoot.getPlayers()) {
                    shoot.getGame().respawn(p);
                    p.getWorld().playSound(p.getLocation(), Sound.ENDERDRAGON_WINGS, random.nextFloat(), random.nextFloat());
                }
                getPlayer().getWorld().playSound(getPlayer().getLocation(), Sound.FIREWORK_BLAST2, random.nextFloat(), random.nextFloat());
                int kill;
                if (Integer.valueOf(game.getKill(getPlayer())) == null) {
                    kill = shoot.getKill();
                } else {
                    kill = game.getKill(getPlayer()) + shoot.getKill();
                }
                addKill(shoot.getKill());
                addCoins(5 * shoot.getKill());
                game.addKill(getPlayer(), shoot.getKill());
                game.getScoreboardManager().getObjective().getScore(getPlayer().getName()).setScore(kill);
                setPlayerkill(getPlayerkill() + shoot.getKill());
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

    public Scoreboard getPlayerScoreboard() {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective objective = scoreboard.registerNewObjective(getPlayer().getDisplayName(), "dummy");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setDisplayName(ChatColor.RED.toString() + ChatColor.BOLD + "    Info    §r");
        objective.getScore("Coins: " + ChatColor.BLUE + getCoins()).setScore(10);
        objective.getScore("§r").setScore(9);
        objective.getScore("Kills: " + ChatColor.BLUE + getPlayerkill()).setScore(8);
        return scoreboard;
    }

    public void save() {
        try {
            NBTCompressedStreamTools.wrhite(PlayerInfoReader.write(this), new FileOutputStream(getFile()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
