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
package fr.bretzel.quake.game;

import fr.bretzel.hologram.Hologram;
import fr.bretzel.nbt.NBTCompressedStreamTools;
import fr.bretzel.nbt.NBTTagCompound;
import fr.bretzel.quake.PlayerInfo;
import fr.bretzel.quake.Quake;
import fr.bretzel.quake.Util;
import fr.bretzel.quake.game.scoreboard.ScoreboardAPI;
import fr.bretzel.quake.reader.GameReader;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
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

public class Game {

    private LinkedList<Location> respawn = new LinkedList<>();
    private List<Location> respawns = getRespawn();
    private Location firstLocation;
    private Location secondLocation;
    private Location spawn;
    private LinkedList<Block> blocks = new LinkedList<>();
    private String name;
    private File file;
    private List<UUID> playerList = new ArrayList<>();
    private LinkedList<Sign> signList = new LinkedList<>();
    private Random random = new Random();
    private Team team;
    private boolean respawnview = false;
    private int maxPlayer = 16;
    private int minPlayer = 2;
    private int maxKill = 25;
    private int secLaunch = 15;
    private State state = State.WAITING;
    private ScoreboardAPI scoreboardManager = null;
    private String displayName;
    private HashMap<UUID, Integer> playerKills = new HashMap<>();
    private HashMap<UUID, Integer> killStreak = new HashMap<>();

    public Game(Location firstLocation, Location secondLocation, String name) {
        setFirstLocation(firstLocation);
        setSecondLocation(secondLocation);
        setName(name);
        setDisplayName(name);

        for(Block block : Util.blocksFromTwoPoints(getFirstLocation(), getSecondLocation())) {
            addBlock(block);
        }

        calculeSpawnBase();

        File mk = new File(Quake.quake.getDataFolder(), File.separator + "game" + File.separator);
        mk.mkdir();
        setFile(new File(mk, getName() + ".dat"));
        try {
            if (!getFile().exists()) {
                getFile().createNewFile();
                NBTTagCompound compound = new NBTTagCompound();
                NBTCompressedStreamTools.wrhite(compound, new FileOutputStream(getFile()));
            }
        } catch (IOException e) {
            e.fillInStackTrace();
        }
        setScoreboardManager(new ScoreboardAPI(this));
        Team team = getScoreboardManager().getScoreboard().registerNewTeam(getName());
        team.setNameTagVisibility(NameTagVisibility.NEVER);
        setTeam(team);
    }

    public Game() {
    }

    public int getSecLaunch() {
        return secLaunch;
    }

    public void setSecLaunch(int secLaunch) {
        this.secLaunch = secLaunch;
    }

    public int getMaxKill() {
        return maxKill;
    }

    public void setMaxKill(int maxKill) {
        this.maxKill = maxKill;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public int getMaxPlayer() {
        return maxPlayer;
    }

    public void setMaxPlayer(int maxPlayer) {
        this.maxPlayer = maxPlayer;
    }

    public int getMinPlayer() {
        return minPlayer;
    }

    public void setMinPlayer(int minPlayer) {
        this.minPlayer = minPlayer;
    }

    public List<UUID> getPlayerList() {
        return playerList;
    }

    public void setPlayerList(List<UUID> playerList) {
        this.playerList = playerList;
    }

    public void addPlayer(Player player) {
        if(!getPlayerList().contains(player.getUniqueId())) {
            getPlayerList().add(player.getUniqueId());
        }
    }

    public void addPlayer(UUID uuid) {
        if(!getPlayerList().contains(uuid)) {
            getPlayerList().add(uuid);
        }
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String dysplayName) {
        this.displayName = dysplayName;
    }

    public ScoreboardAPI getScoreboardManager() {
        return scoreboardManager;
    }

    public void setScoreboardManager(ScoreboardAPI scoreboardManager) {
        this.scoreboardManager = scoreboardManager;
    }

    public LinkedList<Location> getRespawn() {
        return respawn;
    }

    public void setRespawn(LinkedList<Location> respawn) {
        this.respawn = respawn;
    }

    public void addRespawn(Location location) {
        getRespawn().add(location);
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Location getFirstLocation() {
        return this.firstLocation;
    }

    public void setFirstLocation(Location location) {
        this.firstLocation = location;
    }

    public Location getSecondLocation() {
        return this.secondLocation;
    }

    public void setSecondLocation(Location location) {
        this.secondLocation = location;
    }

    public LinkedList<Block> getBlocks() {
        return blocks;
    }

    public void setBlocks(LinkedList<Block> blocks) {
        this.blocks = blocks;
    }

    public Block getBlockByLocation(Location location) {
        for(Block block : getBlocks()) {
            if(block.getWorld() == location.getWorld() && block.getX() == location.getBlockX() && block.getY() == location.getBlockY() && block.getZ() == location.getBlockZ()) {
                return block;
            }
        }
        return null;
    }

    public void addBlock(Block block) {
        getBlocks().add(block);
    }

    public LinkedList<Sign> getSignList() {
        return signList;
    }

    public void setSignList(LinkedList<Sign> signList) {
        this.signList = signList;
    }

    public void addSign(Sign sign) {
        this.signList.add(sign);
    }

    public void removeSign(Sign sign) {
        this.signList.remove(sign);
    }

    public void broadcastMessage(String msg) {
        for (UUID id : getPlayerList()) {
            Player p = Bukkit.getPlayer(id);
            if(p != null && p.isOnline()) {
                p.sendMessage(msg);
            }
        }
    }

    public void view(boolean view) {
        if (view) {
            this.respawnview = true;
            int s = 0;
            for(Location location : getRespawn()) {
                s++;
                Hologram hologram = Quake.holoManager.getHologram(location, 0.5);
                if(hologram == null) {
                    hologram = new Hologram(location, "Respawn: " + s, Quake.holoManager);
                }
                hologram.display(true);
            }
        } else {
            this.respawnview = false;
            for(Location location : getRespawn()) {
                Hologram hologram = Quake.holoManager.getHologram(location, 0.5);
                if(hologram != null)
                    hologram.display(false);
            }
        }
    }

    public void view() {
        if(this.respawnview) {
            view(false);
        } else {
            view(true);
        }
    }

    public boolean isView() {
        return respawnview;
    }

    public int getKill(UUID player) {
        return playerKills.get(player);
    }

    public int getKill(Player player) {
        return getKill(player.getUniqueId());
    }

    public void addKill(Player player, int kill) {
        addKill(player.getUniqueId(), kill);
    }

    public void addKill(UUID player, int kill) {
        int i = 0;
        if(playerKills.containsKey(player)) {
            i = playerKills.get(player);
        }
        i += kill;
        playerKills.remove(player);
        playerKills.put(player, i);
    }

    public void setKill(UUID uuid, int kill) {
        if (playerKills.containsKey(uuid)) {
            playerKills.remove(uuid);
        }
        playerKills.put(uuid, kill);
    }

    public Location getSpawn() {
        return spawn;
    }

    public void setSpawn(Location spawn) {
        this.spawn = spawn;
    }

    private void calculeSpawnBase() {
        int x1 = getFirstLocation().getBlockX();
        int y1 = getFirstLocation().getBlockY();
        int z1 = getFirstLocation().getBlockZ();

        int x2 = getSecondLocation().getBlockX();
        int y2 = getSecondLocation().getBlockY();
        int z2 = getSecondLocation().getBlockZ();

        int x = (x1 + x2) / 2;
        int y = (y1 + y2) / 2;
        int z = (z1 + z2) / 2;

        setSpawn(new Location(getFirstLocation().getWorld(), Double.valueOf(String.valueOf(x)), Double.valueOf(String.valueOf(y)), Double.valueOf(String.valueOf(z))));
    }

    public void addKillStreak(Player player, int kill) {
        addKillStreak(player.getUniqueId(), kill);
    }

    public void addKillStreak(UUID uuid, int kill) {
        if (killStreak.containsKey(uuid)) {
            killStreak.put(uuid, getKillStreak(uuid) + kill);
        } else {
            killStreak.put(uuid, kill);
        }
    }

    public Integer getKillStreak(Player player) {
        return getKillStreak(player.getUniqueId());
    }

    public Integer getKillStreak(UUID uuid) {
        return killStreak.get(uuid);
    }

    public void setKillSteak(Player player, int kill) {
        setKillSteak(player.getUniqueId(), kill);
    }

    public void setKillSteak(UUID uuid, int kill) {
        killStreak.put(uuid, kill);
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public Team getTeam() {
        return team;
    }

    public void stop() {
        setState(State.WAITING);

        for(UUID uuid : getPlayerList()) {
            Player p = Bukkit.getPlayer(uuid);
            PlayerInfo info = Quake.getPlayerInfo(p);
            if(p != null && p.isOnline()) {
                if (!Quake.gameManager.getLobby().getChunk().isLoaded()) {
                    Quake.gameManager.getLobby().getChunk().load(false);
                }
                p.teleport(Quake.gameManager.getLobby());
                p.getInventory().clear();
                p.setWalkSpeed(0.2F);
                p.setScoreboard(info.getPlayerScoreboard());
                info.setDash(true);
                info.setShoot(true);
            }
        }

        getScoreboardManager().getObjective().unregister();
        getScoreboardManager().setObjective(getScoreboardManager().getScoreboard().registerNewObjective("quake", "dummy"));
        getScoreboardManager().getObjective().getScore("§r").setScore(10);
        getScoreboardManager().getObjective().getScore("Map: " + getDisplayName()).setScore(9);
        getScoreboardManager().getObjective().getScore("§r§r").setScore(8);
        getScoreboardManager().getObjective().getScore("§r§r§r").setScore(6);
        getScoreboardManager().getObjective().getScore("Waiting...").setScore(5);
        getScoreboardManager().getObjective().setDisplaySlot(DisplaySlot.SIDEBAR);

        getTeam().getPlayers().clear();
        getTeam().setNameTagVisibility(NameTagVisibility.NEVER);

        getPlayerList().clear();
        playerKills.clear();
        killStreak.clear();
        Quake.gameManager.signEvent.actualiseJoinSignForGame(this);
    }

    public void respawnAtStart(Player player) {
        Location location = respawns.get(random.nextInt(respawns.size()));
        respawns.remove(respawns.indexOf(location));
        player.teleport(location);
    }

    public void respawn(Player p) {
        if (getState() == State.STARTED) {
            PlayerInfo info = Quake.getPlayerInfo(p);
            Location location = getRespawn().get(random.nextInt(getRespawn().size()));
            int esize = location.getWorld().getNearbyEntities(location, 10.5D, 10.5D, 10.5D).size();
            if (esize == 0) {
                p.teleport(location);
            } else if (info.getRespawn() >= 5) {
                p.teleport(location);
            } else {
                info.addRespawn(1);
                respawn(p);
                return;
            }
            info.setRespawn(0);
        }
    }

    public void delete() {
        getFile().delete();
        Quake.gameManager.getGameLinkedList().remove(this);
    }

    public void save() {
        try {
            NBTCompressedStreamTools.wrhite(GameReader.write(this), new FileOutputStream(getFile()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return "{" + getFirstLocation().toString() + ", " + getSecondLocation().toString() + ", " + "ArenaName: " + getName() + "}";
    }
}
