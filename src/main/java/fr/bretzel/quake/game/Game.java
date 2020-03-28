/**
 * Copyright 2015 Lo?c Nussbaumer
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
package fr.bretzel.quake.game;

import fr.bretzel.hologram.Hologram;
import fr.bretzel.nbt.NBTCompressedStreamTools;
import fr.bretzel.nbt.NBTTagCompound;
import fr.bretzel.quake.PlayerInfo;
import fr.bretzel.quake.Quake;
import fr.bretzel.quake.game.scoreboard.ScoreboardAPI;
import fr.bretzel.quake.reader.GameReader;
import org.bukkit.*;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.NameTagVisibility;
import org.bukkit.scoreboard.Team;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

/**
 * Created by MrBretzel on 12/06/2015.
 */

public class Game
{

    public boolean firstKill = false;
    private LinkedList<Location> respawn = new LinkedList<>();
    private Location firstLocation;
    private Location secondLocation;
    private Location spawn;
    private String name = "";
    private File file;
    private List<UUID> playerList = new ArrayList<>();
    private LinkedList<Sign> signList = new LinkedList<>();
    private final Random random = new Random();
    private Team team;
    private boolean respawnview = false;
    private int maxPlayer = 16;
    private int minPlayer = 2;
    private int maxKill = 25;
    private int secLaunch = 20;
    private State state = State.WAITING;
    private ScoreboardAPI scoreboardManager = null;
    private String displayName = "";
    private final HashMap<UUID, Integer> playerKills = new HashMap<>();
    private final HashMap<UUID, Integer> killStreak = new HashMap<>();

    public Game(Location firstLocation, Location secondLocation, String name)
    {
        setFirstLocation(firstLocation);
        setSecondLocation(secondLocation);
        setName(name);
        setDisplayName(name);

        calculeSpawnBase();

        File mk = new File(Quake.quake.getDataFolder(), File.separator + "game" + File.separator);
        mk.mkdir();
        setFile(new File(mk, getName() + ".dat"));
        try
        {
            if (!getFile().exists())
            {
                getFile().createNewFile();
                NBTTagCompound compound = new NBTTagCompound();
                NBTCompressedStreamTools.wrhite(compound, new FileOutputStream(getFile()));
            }
        } catch (IOException e)
        {
            e.fillInStackTrace();
        }
        setScoreboardManager(new ScoreboardAPI(this));
        Team team = getScoreboardManager().getScoreboard().registerNewTeam(getName());
        team.setNameTagVisibility(NameTagVisibility.NEVER);
        setTeam(team);
    }

    public Game()
    {
    }

    public int getSecLaunch()
    {
        return secLaunch;
    }

    public void setSecLaunch(int secLaunch)
    {
        this.secLaunch = secLaunch;
    }

    public int getMaxKill()
    {
        return maxKill;
    }

    public void setMaxKill(int maxKill)
    {
        this.maxKill = maxKill;
    }

    public State getState()
    {
        return state;
    }

    public void setState(State state)
    {
        this.state = state;
        Quake.gameManager.signEvent.updateSign(this);
    }

    public File getFile()
    {
        return file;
    }

    public void setFile(File file)
    {
        this.file = file;
    }

    public int getMaxPlayer()
    {
        return maxPlayer;
    }

    public void setMaxPlayer(int maxPlayer)
    {
        this.maxPlayer = maxPlayer;
    }

    public int getMinPlayer()
    {
        return minPlayer;
    }

    public void setMinPlayer(int minPlayer)
    {
        this.minPlayer = minPlayer;
    }

    public List<UUID> getPlayerList()
    {
        return playerList;
    }

    public void setPlayerList(List<UUID> playerList)
    {
        this.playerList = playerList;
    }

    public void addPlayer(Player player)
    {
        if (!getPlayerList().contains(player.getUniqueId()))
        {
            getPlayerList().add(player.getUniqueId());
        }
    }

    public void addPlayer(UUID uuid)
    {
        if (!getPlayerList().contains(uuid))
        {
            getPlayerList().add(uuid);
        }
    }

    public String getDisplayName()
    {
        return displayName;
    }

    public void setDisplayName(String dysplayName)
    {
        if (dysplayName.length() > 16)
            return;

        this.displayName = dysplayName;

        if (getScoreboardManager() != null)
        {
            getScoreboardManager().getScoreboard().getEntries().forEach(s -> getScoreboardManager().getScoreboard().getScores(s).forEach(score ->
            {
                if (score.getScore() == 9)
                    getScoreboardManager().getScoreboard().resetScores(s);
            }));

            getScoreboardManager().getObjective().getScore("Map: " + getDisplayName()).setScore(9);
        }
    }

    public ScoreboardAPI getScoreboardManager()
    {
        return scoreboardManager;
    }

    public void setScoreboardManager(ScoreboardAPI scoreboardManager)
    {
        this.scoreboardManager = scoreboardManager;
    }

    public LinkedList<Location> getRespawns()
    {
        return respawn;
    }

    public boolean hasRespawn(Location location)
    {
        return getRespawn(location, 1.5) != null;
    }

    public Location getRespawn(Location location, double distance)
    {
        for (Location l : getRespawns())
            if (location.distance(l) < distance)
                return l;
        return null;
    }

    public Location getRespawn(Location location)
    {
        return getRespawn(location, 1.5);
    }

    public void removeRespawn(Location location)
    {
        getRespawns().remove(getRespawn(location));
    }

    public void setRespawn(LinkedList<Location> respawn)
    {
        this.respawn = respawn;
    }

    public void addRespawn(Location location)
    {
        getRespawns().add(location.add(0.0, 1.0, 0.0));
    }

    public String getName()
    {
        return this.name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public Location getFirstLocation()
    {
        return this.firstLocation;
    }

    public void setFirstLocation(Location location)
    {
        this.firstLocation = location;
    }

    public Location getSecondLocation()
    {
        return this.secondLocation;
    }

    public void setSecondLocation(Location location)
    {
        this.secondLocation = location;
    }

    public LinkedList<Sign> getSignList()
    {
        return signList;
    }

    public void setSignList(LinkedList<Sign> signList)
    {
        this.signList = signList;
    }

    public void addSign(Sign sign)
    {
        this.signList.add(sign);
    }

    public void removeSign(Sign sign)
    {
        this.signList.remove(sign);
    }

    public void broadcastMessage(String msg)
    {
        for (UUID id : getPlayerList())
        {
            Player p = Bukkit.getPlayer(id);
            if (p != null && p.isOnline())
            {
                p.sendMessage(msg);
            }
        }
    }

    public void view(boolean view)
    {
        this.respawnview = view;
        if (view)
        {
            for (Location location : getRespawns())
            {
                Quake.holoManager.removeHologram(location, 1);
                new Hologram(location, "Respawn: " + (getRespawns().indexOf(location) + 1), Quake.holoManager).setVisible(true);
            }
        } else
        {
            for (Location location : getRespawns())
            {
                Hologram hologram = Quake.holoManager.getHologram(location, 1);
                if (hologram != null)
                    Quake.holoManager.removeHologram(location, 1);
            }
        }
    }

    public void view()
    {
        view(!this.respawnview);
    }

    public boolean isView()
    {
        return respawnview;
    }

    public int getKill(UUID player)
    {
        return playerKills.getOrDefault(player, 0);
    }

    public int getKill(Player player)
    {
        return getKill(player.getUniqueId());
    }

    public void addKill(Player player, int kill)
    {
        addKill(player.getUniqueId(), kill);
    }

    public void addKill(UUID player, int kill)
    {
        int i = 0;
        if (playerKills.containsKey(player))
        {
            i = playerKills.get(player);
        }
        i += kill;
        playerKills.remove(player);
        playerKills.put(player, i);
    }

    public void setKill(UUID uuid, int kill)
    {
        playerKills.remove(uuid);
        playerKills.put(uuid, kill);
    }

    public Location getSpawn()
    {
        return spawn;
    }

    public void setSpawn(Location spawn)
    {
        this.spawn = spawn;
    }

    private void calculeSpawnBase()
    {
        int x1 = getFirstLocation().getBlockX();
        int y1 = getFirstLocation().getBlockY();
        int z1 = getFirstLocation().getBlockZ();

        int x2 = getSecondLocation().getBlockX();
        int y2 = getSecondLocation().getBlockY();
        int z2 = getSecondLocation().getBlockZ();

        int x = (x1 + x2) / 2;
        int y = (y1 + y2) / 2;
        int z = (z1 + z2) / 2;

        setSpawn(new Location(getFirstLocation().getWorld(), Double.parseDouble(String.valueOf(x)), Double.parseDouble(String.valueOf(y)), Double.parseDouble(String.valueOf(z))));
    }

    public boolean isInArea(int x, int y, int z)
    {
        return isInArea(new Location(getFirstLocation().getWorld(), x, y, z));
    }

    public boolean isInArea(Player player)
    {
        return isInArea(player.getLocation());
    }

    public boolean isInArea(Location location)
    {
        int maxX = (Math.max(getFirstLocation().getBlockX(), getSecondLocation().getBlockX()));
        int minX = (Math.min(getFirstLocation().getBlockX(), getSecondLocation().getBlockX()));

        int maxY = (Math.max(getFirstLocation().getBlockY(), getSecondLocation().getBlockY()));
        int minY = (Math.min(getFirstLocation().getBlockY(), getSecondLocation().getBlockY()));

        int maxZ = (Math.max(getFirstLocation().getBlockZ(), getSecondLocation().getBlockZ()));
        int minZ = (Math.min(getFirstLocation().getBlockZ(), getSecondLocation().getBlockZ()));

        return location.getBlockX() >= minX && location.getBlockX() <= maxX && location.getBlockY() >= minY && location.getBlockY() <= maxY && location.getBlockZ() >= minZ && location.getBlockZ() <= maxZ;
    }

    public void addKillStreak(Player player, int kill)
    {
        addKillStreak(player.getUniqueId(), kill);
    }

    public void addKillStreak(UUID uuid, int kill)
    {
        if (killStreak.containsKey(uuid))
        {
            killStreak.put(uuid, getKillStreak(uuid) + kill);
        } else
        {
            killStreak.put(uuid, kill);
        }
    }

    public int getKillStreak(Player player)
    {
        return getKillStreak(player.getUniqueId());
    }

    public int getKillStreak(UUID uuid)
    {
        return killStreak.get(uuid);
    }

    public void setKillSteak(Player player, int kill)
    {
        setKillSteak(player.getUniqueId(), kill);
    }

    public void setKillSteak(UUID uuid, int kill)
    {
        killStreak.put(uuid, kill);
    }

    public Team getTeam()
    {
        return team;
    }

    public void setTeam(Team team)
    {
        this.team = team;
    }

    public void stop()
    {
        setState(State.WAITING);

        for (UUID uuid : getPlayerList())
        {
            Player p = Bukkit.getPlayer(uuid);
            if (p != null && p.isOnline())
            {
                PlayerInfo info = Quake.getPlayerInfo(p);
                if (!Quake.gameManager.getLobby().getChunk().isLoaded())
                {
                    Quake.gameManager.getLobby().getChunk().load(false);
                }
                p.teleport(Quake.gameManager.getLobby());
                p.getInventory().clear();
                p.setWalkSpeed(0.2F);
                p.setScoreboard(info.getPlayerScoreboard());
                info.setLastDash(0);
                info.setLastShoot(0);
            }
        }

        getScoreboardManager().getObjective().unregister();
        getScoreboardManager().setObjective(getScoreboardManager().getScoreboard().registerNewObjective("quake", "dummy"));
        getScoreboardManager().getObjective().getScore(ChatColor.RESET.toString()).setScore(10);
        getScoreboardManager().getObjective().getScore("Map: " + getDisplayName()).setScore(9);
        getScoreboardManager().getObjective().getScore(ChatColor.RESET.toString() + ChatColor.RESET.toString()).setScore(8);
        getScoreboardManager().getObjective().getScore(ChatColor.RESET.toString() + "                            " + ChatColor.RESET.toString()).setScore(6);
        getScoreboardManager().getObjective().getScore("Waiting...").setScore(5);
        getScoreboardManager().getObjective().setDisplaySlot(DisplaySlot.SIDEBAR);

        getTeam().unregister();
        Team t = getScoreboardManager().getScoreboard().registerNewTeam(getDisplayName());
        t.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
        setTeam(t);

        getPlayerList().clear();
        playerKills.clear();
        killStreak.clear();
        firstKill = false;
        Quake.gameManager.signEvent.updateSign(this);
    }

    public void respawn(Player p)
    {
        if (getState() == State.STARTED)
        {
            PlayerInfo info = Quake.getPlayerInfo(p);
            List<Location> sorterRespawn = new ArrayList<>(getRespawns());

            if (info.getDeathLocation() != null && getRespawn(info.getDeathLocation()) != null)
                sorterRespawn.remove(getRespawn(info.getDeathLocation()));

            Collections.shuffle(sorterRespawn);

            double checkRange = 30D;

            if (info.getRespawn() > 0)
                checkRange = checkRange - info.getRespawn();

            boolean noEntity = true;

            for (Location location : sorterRespawn)
            {
                location = new Location(location.getWorld(), location.getBlockX() + 0.5, location.getBlockY(), location.getBlockZ() + 0.5);

                for (UUID uuid : getPlayerList())
                {
                    Player player = Bukkit.getPlayer(uuid);
                    if (player != null && player != p)
                        if (location.distance(player.getLocation()) <= checkRange)
                            noEntity = false;
                }

                if (noEntity)
                {
                    info.setRespawn(0);
                    info.setDeathLocation(null);
                    p.teleport(location);
                    return;
                }
            }

            if (!noEntity)
            {
                info.addRespawn(1);
                respawn(p);
            }
        }
    }

    public void playSound(String sound, SoundCategory soundCategory, float volume, float pitch)
    {
        getPlayerList().forEach(uuid ->
        {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null && player.isOnline())
            {
                Bukkit.getScheduler().runTaskLater(Quake.quake, () ->
                {
                    player.playSound(player.getLocation(), sound, soundCategory, volume, pitch);
                }, 3L);
            }
        });
    }

    public void playSound(Sound sound, SoundCategory soundCategory, float volume, float pitch)
    {
        getPlayerList().forEach(uuid ->
        {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null && player.isOnline())
                player.playSound(player.getLocation(), sound, soundCategory, volume, pitch);
        });
    }

    public void playSound(Location location, Sound sound, SoundCategory soundCategory, float volume, float pitch)
    {
        getPlayerList().forEach(uuid ->
        {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null && player.isOnline())
                player.playSound(location, sound, soundCategory, volume, pitch);
        });
    }

    public void playSound(Location location, Sound sound, float f1, float f2)
    {
        playSound(location, sound, SoundCategory.AMBIENT, f1, f2);
    }

    public void playSound(Location location, Sound sound)
    {
        playSound(location, sound, 1F, 1F);
    }

    public void playSound(Location location, String sound, SoundCategory soundCategory, float volume, float pitch)
    {
        getPlayerList().forEach(uuid ->
        {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null && player.isOnline())
                player.playSound(location, sound, soundCategory, volume, pitch);
        });
    }

    public void playSound(Location location, String sound, float f1, float f2)
    {
        playSound(location, Sound.valueOf(sound), f1, f2);
    }

    public void playSound(World world, double x, double y, double z, Sound sound)
    {
        playSound(new Location(world, x, y, z), sound, 1F, 1F);
    }

    public void playSound(World world, double x, double y, double z, Sound sound, float f1, float f2)
    {
        playSound(new Location(world, x, y, z), sound, f1, f2);
    }

    public void playSound(World world, double x, double y, double z, String sound)
    {
        playSound(new Location(world, x, y, z), sound, 1F, 1F);
    }

    public void playSound(World world, double x, double y, double z, String sound, float f1, float f2)
    {
        playSound(new Location(world, x, y, z), sound, f1, f2);
    }

    public void delete()
    {
        getFile().delete();
        Quake.gameManager.getGameLinkedList().remove(this);
    }

    public void save()
    {
        try
        {
            NBTCompressedStreamTools.wrhite(GameReader.write(this), new FileOutputStream(getFile()));
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public String toString()
    {
        return "{" + getFirstLocation().toString() + ", " + getSecondLocation().toString() + ", " + "ArenaName: " + getName() + "}";
    }
}
