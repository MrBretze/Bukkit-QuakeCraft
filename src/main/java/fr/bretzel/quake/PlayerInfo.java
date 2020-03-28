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
import fr.bretzel.quake.game.task.GameEndTask;
import fr.bretzel.quake.gun.Gun;
import fr.bretzel.quake.reader.PlayerInfoReader;
import fr.bretzel.raytrace.RayTrace;
import fr.bretzel.raytrace.RayTraceResult;
import net.minecraft.server.v1_15_R1.EntityPlayer;
import net.minecraft.server.v1_15_R1.PacketPlayOutWorldBorder;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.Vector;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * Created by MrBretzel on 14/06/2015.
 */

public class PlayerInfo
{

    private final Random random = new Random();
    private Player player;
    private double reload = 1.5;
    private Location firstLocation = null;
    private Location secondLocation = null;
    private Location deathLocation;
    private int playerkill = 0;
    private int coins = 0;
    private int won = 0;
    private int killstreak = 0;
    private File file;
    private int respawn = 0;
    private long lastShoot = System.currentTimeMillis();
    private long lastDash = System.currentTimeMillis();
    private Gun gun;

    private Vector direction = new Vector();

    protected PlayerInfo(Player player)
    {
        setPlayer(player);

        File mk = new File(Quake.quake.getDataFolder() + File.separator + "players" + File.separator);

        mk.mkdir();

        setFile(new File(mk, player.getUniqueId().toString() + ".dat"));

        try
        {
            if (!getFile().exists())
            {
                getFile().createNewFile();
                NBTTagCompound compound = new NBTTagCompound();
                compound.setDouble("reload", getReloadTime());
                NBTCompressedStreamTools.wrhite(compound, new FileOutputStream(getFile()));
            }
        } catch (Exception e)
        {
            e.fillInStackTrace();
        }
        PlayerInfoReader.read(this);
    }

    public Vector getDirection()
    {
        return direction;
    }

    public void setDirection(Vector direction)
    {
        this.direction = direction;
    }

    public Random getRandom()
    {
        return random;
    }

    public File getFile()
    {
        return file;
    }

    public void setFile(File file)
    {
        this.file = file;
    }

    public double getReloadTime()
    {
        return reload;
    }

    public void setReloadTime(double reload)
    {
        this.reload = reload;
    }

    public Player getPlayer()
    {
        return player;
    }

    public void setPlayer(Player player)
    {
        this.player = player;
    }

    public Location getFirstLocation()
    {
        return firstLocation;
    }

    public void setFirstLocation(Location firstLocation)
    {
        this.firstLocation = firstLocation;
    }

    public Location getSecondLocation()
    {
        return secondLocation;
    }

    public void setSecondLocation(Location secondLocation)
    {
        this.secondLocation = secondLocation;
    }

    public void setReload(long reload)
    {
        this.reload = reload;
    }

    public boolean isInGame()
    {
        return Quake.gameManager.getGameByPlayer(getPlayer()) != null;
    }

    public Location getDeathLocation()
    {
        return deathLocation;
    }

    public void setDeathLocation(Location deathLocation)
    {
        this.deathLocation = deathLocation;
    }

    public void give(Gun gun)
    {
        this.gun = gun;
        getPlayer().getInventory().clear();
        getPlayer().getInventory().setItem(0, gun.getStack());
    }

    public Gun getGun()
    {
        return gun;
    }

    public boolean canShoot()
    {
        return ((System.currentTimeMillis() - lastShoot) / 1000.0) >= 1.5;
    }

    public void setLastShoot(long lastShoot)
    {
        this.lastShoot = lastShoot;
    }

    public boolean canDash()
    {
        return ((System.currentTimeMillis() - lastDash) / 1000.0) >= 2.5;
    }

    public void setLastDash(long lastDash)
    {
        this.lastDash = lastDash;
    }

    public void dash()
    {
        if (canDash())
        {
            setLastDash(System.currentTimeMillis());
            Game game = Quake.gameManager.getGameByPlayer(getPlayer());
            Quake.logDebug("Dash for player " + getPlayer().getDisplayName() + " in game " + game.getDisplayName());
            if (game.getState() == State.STARTED)
            {
                PlayerDashEvent event = new PlayerDashEvent(getPlayer(), game);
                Bukkit.getPluginManager().callEvent(event);
                //Bukkit.getServer().getScheduler().runTaskLater(Quake.quake, new DashTask(this), (long) (getReloadTime() * 35));
                Vector pVector = player.getEyeLocation().getDirection();
                Vector vector = new Vector(pVector.getX(), 0.4D, pVector.getZ()).multiply(1.2D);
                Quake.logDebug("The vector for the dash is: " + vector);
                getPlayer().setVelocity(vector);
                getPlayer().getWorld().playSound(getPlayer().getLocation(), Sound.ENTITY_ENDER_DRAGON_FLAP, 1, 1);
            } else
            {
                Quake.logDebug("The game is not started, the dash is annulled");
            }
        } else
        {
            sendReloadingMsg(ChatColor.RED + "Dash available in " + ChatColor.DARK_RED + (((lastDash + 2.5 * 1000.0) - System.currentTimeMillis()) / 1000.0) + ChatColor.RED + "s");
        }
    }

    public void shoot()
    {
        if (canShoot())
        {
            setLastShoot(System.currentTimeMillis());
            Game game = Quake.gameManager.getGameByPlayer(getPlayer());

            if (game != null && game.getState() == State.STARTED)
            {
                Vector direction = getDirection();
                RayTraceResult traceResult = RayTrace.rayTrace(player.getEyeLocation(), direction,
                        0.5, 100, 0.09, location -> location.getBlock().getType().isAir());

                List<Player> playerList = traceResult.getEntities().stream().filter(entity -> entity instanceof Player && !entity.getUniqueId()
                        .equals(player.getUniqueId()) && game.getPlayerList().contains(entity.getUniqueId())).map(entity -> (Player) entity).collect(Collectors.toList());

                int kills = playerList.size();

                game.getPlayerList().forEach(uuid ->
                {
                    Player bukkitPlayer = Bukkit.getPlayer(uuid);
                    if (bukkitPlayer != null && bukkitPlayer.isOnline() && !bukkitPlayer.getUniqueId().equals(player.getUniqueId()))
                        bukkitPlayer.playSound(player.getLocation(), "quake.raygun.shot", SoundCategory.MASTER, 2, 1);
                });

                player.playSound(player.getLocation(), "quake.raygun.reload", SoundCategory.MASTER, 1F, 1F);
                player.playSound(player.getLocation(), "quake.death.stereo", 2F, 1.3F);


                //Player Effect at the end
                AtomicReference<Location> last = new AtomicReference<>();
                for (Location location : traceResult.getLocation().stream().filter(location ->
                {
                    if (last.get() == null)
                        last.set(location);
                    if (last.get().distance(location) > 0.35)
                    {
                        last.set(location);
                        return true;
                    } else return false;
                }).collect(Collectors.toList()))
                    if (location.distance(traceResult.getStartLocation()) > 0.45 && getGun() != null)
                        getGun().applyEffect(location);

                if (traceResult.getHitLocation() != null && getGun() != null)
                    getGun().endEffect(traceResult.getHitLocation());

                if (kills > 0)
                {
                    for (Player p : playerList)
                    {
                        game.setKillSteak(p, 0);
                        game.getPlayerList().forEach(uuid ->
                        {
                            Player player = Bukkit.getPlayer(uuid);
                            if (player != null && player != getPlayer())
                                player.playSound(p.getLocation(), "quake.death.mono", 2F, 1.3F);
                        });

                        if (getGun() != null)
                            getGun().playerDeadEffect(p);

                        PlayerInfo deathInfo = Quake.getPlayerInfo(p);
                        deathInfo.setLastDash(System.currentTimeMillis() + 2500);
                        deathInfo.setLastShoot(System.currentTimeMillis() - 1400);
                        deathInfo.setDeathLocation(p.getLocation());
                        //Respawn
                        game.respawn(p);
                    }

                    shootEvent(game, playerList);

                    int kill;

                    if (game.getKill(getPlayer()) == 0)
                        kill = kills;
                    else
                        kill = game.getKill(getPlayer()) + kills;

                    game.addKillStreak(getPlayer(), kills);

                    int killStreak = game.getKillStreak(getPlayer());

                    String soundQuake = "quake" + (random.nextBoolean() ? ".female." : ".male.");

                    if (killStreak == 5)
                    {
                        game.broadcastMessage(ChatColor.RED.toString() + ChatColor.BOLD + getPlayer().getDisplayName() + " IS KILLING SPREE !");
                        game.playSound(soundQuake + "killingspree", SoundCategory.MASTER, 0.5F, 1);
                        addKillStreak(1);
                    } else if (killStreak == 10)
                    {
                        game.broadcastMessage(ChatColor.RED.toString() + ChatColor.BOLD + getPlayer().getDisplayName() + " IS A RAMPAGE !");
                        game.playSound(soundQuake + "rampage", SoundCategory.MASTER, 0.5F, 1);
                        addKillStreak(1);
                    } else if (killStreak == 15)
                    {
                        game.broadcastMessage(ChatColor.RED.toString() + ChatColor.BOLD + getPlayer().getDisplayName() + " IS A UNSTOPPABLE !");
                        game.playSound(soundQuake + "unstoppable", SoundCategory.MASTER, 0.5F, 1);
                        addKillStreak(1);
                    } else if (killStreak == 20)
                    {
                        game.broadcastMessage(ChatColor.RED.toString() + ChatColor.BOLD + getPlayer().getDisplayName() + " GODLIKE !");
                        game.playSound(soundQuake + "godlike", SoundCategory.MASTER, 0.5F, 1);
                        addKillStreak(1);
                    } else if (killStreak == 25)
                    {
                        game.broadcastMessage(ChatColor.RED.toString() + ChatColor.BOLD + getPlayer().getDisplayName() + " MONSTER KILL !");
                        game.playSound(soundQuake + "monsterkill", SoundCategory.MASTER, 0.5F, 1);
                        addKillStreak(1);
                    }

                    addPlayerKill(kills);
                    addCoins(5 * kills);
                    game.addKill(getPlayer(), kills);
                    game.getScoreboardManager().getObjective().getScore(getPlayer().getName()).setScore(kill);

                    if (game.getKill(getPlayer()) >= game.getMaxKill())
                    {
                        GameEndEvent event = new GameEndEvent(getPlayer(), game);
                        Bukkit.getScheduler().runTask(Quake.quake, () ->
                        {
                            Bukkit.getPluginManager().callEvent(event);
                            if (event.isCancelled())
                            {
                                return;
                            }

                            for (UUID id : game.getPlayerList())
                            {
                                Player player = Bukkit.getPlayer(id);
                                if (player != null && player.isOnline())
                                {
                                    PlayerInfo playerInfo = Quake.getPlayerInfo(player);
                                    playerInfo.setLastDash(0L);
                                    playerInfo.setLastDash(0L);
                                    player.getInventory().clear();
                                }
                            }

                            Bukkit.getScheduler().runTask(Quake.quake, () -> new GameEndTask(Quake.quake, 10L, 10L, game, getPlayer()));
                            addWoon(1);
                            game.getTeam().setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
                            game.broadcastMessage(ChatColor.BLUE + ChatColor.BOLD.toString() + player.getName() + " Has won the game !");
                        });
                    }
                }
            }
        } else
        {
            sendReloadingMsg(ChatColor.RED + "Shoot available in " + ChatColor.DARK_RED + (((lastShoot + 1.5 * 1000.0) - System.currentTimeMillis()) / 1000.0) + ChatColor.RED + "s");
        }
    }

    private long lastSendMsg = System.currentTimeMillis();

    private void sendReloadingMsg(String msg)
    {
        if ((System.currentTimeMillis() - lastSendMsg) > 500)
        {
            player.sendMessage(msg);
            lastSendMsg = System.currentTimeMillis();
        }
    }

    public void shootEvent(Game game, List<Player> playerList)
    {
        int kill = playerList.size();

        String soundQuake = "quake" + (random.nextBoolean() ? ".female." : ".male.");

        for (Player p : playerList)
        {
            p.sendTitle("", ChatColor.RED + "Killed by " + ChatColor.RESET + player.getName(), 4, 15, 4);

            new BukkitRunnable()
            {
                final WorldBorder border = p.getWorld().getWorldBorder();
                final Location location = border.getCenter().clone();
                final int y = (int) (border.getSize() / 2);
                final int x = (int) (y - p.getLocation().distance(location));
                final int maxTick = 25;
                final int w = ((y - x) / (maxTick / 2));
                int tick = 0;
                int i = w;

                boolean fadeIn = true;

                {
                    location.setY(p.getLocation().getBlockY());
                }

                @Override
                public void run()
                {
                    if (tick == maxTick)
                    {
                        sendWorldBorderPacket(p, 0);
                        cancel();
                    }

                    tick++;

                    if (tick >= (maxTick / 2) - 1)
                    {
                        fadeIn = false;
                    }

                    sendWorldBorderPacket(p, i);

                    if (fadeIn)
                        i += x;
                    else i -= x;
                }
            }.runTaskTimer(Quake.quake, 0, 0);

            game.broadcastMessage(p.getDisplayName() + ChatColor.BLUE + " has been sprayed by " + ChatColor.RESET + player.getName());
        }

        if (!game.firstKill && kill > 0)
        {
            game.firstKill = true;
            game.playSound("quake.male.firstblood", SoundCategory.MASTER, 0.7F, 1);
            game.broadcastMessage(ChatColor.RED.toString() + ChatColor.BOLD + player.getDisplayName() + " FIRST BLOOD !");
        }

        if (kill == 2)
        {
            game.broadcastMessage(ChatColor.RED.toString() + ChatColor.BOLD + "Double kill !");
            game.playSound("quake.male.doublekill", SoundCategory.MASTER, 0.7F, 1);
        } else if (kill == 3)
        {
            game.broadcastMessage(ChatColor.RED.toString() + ChatColor.BOLD + "Triple kill !");
            game.playSound("quake.male.triplekill", SoundCategory.MASTER, 0.7F, 1);
        } else if (kill >= 4)
        {
            game.broadcastMessage(ChatColor.RED.toString() + ChatColor.BOLD + "Multi kill !");
            game.playSound(soundQuake + "multikill", SoundCategory.MASTER, 0.7F, 1);
        }

        if (game.getKill(player) >= 26)
        {
            game.broadcastMessage(ChatColor.RED.toString() + ChatColor.BOLD + player.getDisplayName() + " MONSTER KILL !");
            game.playSound("quake.female.whickedsick", SoundCategory.MASTER, 0.7F, 1);
        }
    }

    private void sendWorldBorderPacket(Player player, int warningBlocks)
    {
        EntityPlayer nmsPlayer = ((CraftPlayer) player).getHandle();
        net.minecraft.server.v1_15_R1.WorldBorder playerWorldBorder = nmsPlayer.world.getWorldBorder();
        PacketPlayOutWorldBorder worldBorder = new PacketPlayOutWorldBorder(playerWorldBorder, PacketPlayOutWorldBorder.EnumWorldBorderAction.SET_WARNING_BLOCKS);
        try
        {
            Field field = worldBorder.getClass().getDeclaredField("i");
            field.setAccessible(true);
            field.setInt(worldBorder, warningBlocks);
            field.setAccessible(!field.isAccessible());
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        nmsPlayer.playerConnection.sendPacket(worldBorder);
    }

    public int getPlayerKill()
    {
        return playerkill;
    }

    public void setPlayerKill(int playerkill)
    {
        this.playerkill = playerkill;
    }

    public void addPlayerKill(int kill)
    {
        setPlayerKill(getPlayerKill() + kill);
    }

    public void removePlayerKill(int kill)
    {
        setPlayerKill(getPlayerKill() - kill);
    }

    public int getCoins()
    {
        return coins;
    }

    public void setCoins(int coins)
    {
        this.coins = coins;
    }

    public void addCoins(int coins)
    {
        setCoins(getCoins() + coins);
    }

    public void removeCoins(int coins)
    {
        setCoins(getCoins() - coins);
    }

    public int getKillStreak()
    {
        return killstreak;
    }

    public void setKillStreak(int killstreak)
    {
        this.killstreak = killstreak;
    }

    public void addKillStreak(int killstreak)
    {
        setKillStreak(getKillStreak() + killstreak);
    }

    public void removeKillStreak(int killstreak)
    {
        setKillStreak(getKillStreak() - killstreak);
    }

    public Scoreboard getPlayerScoreboard()
    {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective objective = scoreboard.registerNewObjective(getPlayer().getName(), "dummy");
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
        return scoreboard;
    }

    public int getRespawn()
    {
        return respawn;
    }

    public void setRespawn(int respawn)
    {
        this.respawn = respawn;
    }

    public void addRespawn(int respawn)
    {
        setRespawn(getRespawn() + respawn);
    }

    public int getWon()
    {
        return won;
    }

    public void setWon(int won)
    {
        this.won = won;
    }

    public void addWoon(int woon)
    {
        setWon(getWon() + woon);
    }

    public void removeWoon(int woon)
    {
        setWon(getWon() - woon);
    }

    public void save()
    {
        try
        {
            NBTCompressedStreamTools.wrhite(PlayerInfoReader.write(this), new FileOutputStream(getFile()));
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
