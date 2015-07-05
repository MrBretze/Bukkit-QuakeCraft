/**
 * Copyright 2015 Lo�c Nussbaumer
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

import fr.bretzel.nbt.NBTCompressedStreamTools;
import fr.bretzel.nbt.NBTTagCompound;
import fr.bretzel.quake.Quake;
import fr.bretzel.quake.Util;
import fr.bretzel.quake.game.scoreboard.ScoreboardAPI;
import fr.bretzel.quake.reader.GameReader;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

/**
 * Created by MrBretzel on 12/06/2015.
 */

public class Game {

    private LinkedList<Location> respawn = new LinkedList<>();
    private Location firstLocation;
    private Location secondLocation;
    private Location spawn;
    private LinkedList<Block> blocks = new LinkedList<>();
    private String name;
    private File file;
    private List<UUID> playerList = new ArrayList<>();
    private LinkedList<Sign> signList = new LinkedList<>();
    private Random random = new Random();
    private boolean respawnview = false;
    private int maxPlayer = 16;
    private int minPlayer = 2;
    private int maxKill = 25;
    private int secLaunch = 15;
    private State state = State.WAITING;
    private ScoreboardAPI scoreboardManager = null;
    private String displayName;
    private LinkedHashMap<Player, Integer> playerKills = new LinkedHashMap<>();

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
        Block b = null;
        for(Block block : getBlocks()) {
            if(block.getWorld() == location.getWorld() && block.getX() == location.getBlockX() && block.getY() == location.getBlockY() && block.getZ() == location.getBlockZ()) {
                b = block;
                break;
            }
        }
        return b;
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
        if(view == true) {
            this.respawnview = true;
            for(Location location : getRespawn()) {
                location.getWorld().getBlockAt(location).setType(Material.BEACON);
                for (int xPoint = location.getBlockX() - 1; xPoint <= location.getBlockX() + 1 ; xPoint++) {
                    for (int zPoint = location.getBlockZ() - 1 ; zPoint <= location.getBlockZ() + 1; zPoint++) {
                        Location l = location.getWorld().getBlockAt(xPoint, location.getBlockY() - 1, zPoint).getLocation().clone();
                        for(UUID id : getPlayerList()) {
                            Player player = Bukkit.getPlayer(id);
                            if(player != null && player.isOnline()) {
                                player.sendBlockChange(l, Material.IRON_BLOCK, (byte) 0);
                            }
                        }
                    }
                }
            }
        } else {
            this.respawnview = false;
            for(Location location : getRespawn()) {
                for (int xPoint = location.getBlockX() - 1; xPoint <= location.getBlockX() + 1 ; xPoint++) {
                    for (int zPoint = location.getBlockZ() - 1 ; zPoint <= location.getBlockZ() + 1; zPoint++) {
                        Location l = location.getWorld().getBlockAt(xPoint, location.getBlockY() - 1, zPoint).getLocation().clone();
                        for(UUID id : getPlayerList()) {
                            Player player = Bukkit.getPlayer(id);
                            if(player != null && player.isOnline()) {
                                player.sendBlockChange(l, getBlockByLocation(l).getType(), getBlockByLocation(l).getData());
                            }
                        }
                    }
                }
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

    public int getKill(Player player) {
        return playerKills.get(player);
    }

    public void addKill(Player player, int kill) {
        int i = playerKills.get(player);
        i += kill;
        playerKills.remove(player);
        playerKills.put(player, i);
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

    public void stop() {
        for(UUID uuid : getPlayerList()) {
            Player p = Bukkit.getPlayer(uuid);
            if(p != null && p.isOnline()) {
                p.sendMessage(ChatColor.RED + "The game has been stopped !");
                p.teleport(Quake.gameManager.getLobby());
                p.getInventory().clear();
                p.setWalkSpeed(0.2F);
                p.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
                if (p.hasMetadata("killer")) {
                    p.removeMetadata("killer", Quake.quake);
                }
            }
        }

        getScoreboardManager().getObjective().unregister();
        getScoreboardManager().setObjective(getScoreboardManager().getScoreboard().registerNewObjective("quake", "dummy"));
        getScoreboardManager().getObjective().getScore("�r").setScore(10);
        getScoreboardManager().getObjective().getScore(getDisplayName()).setScore(9);
        getScoreboardManager().getObjective().getScore("�r�r").setScore(8);
        getScoreboardManager().getObjective().getScore(Quake.gameManager.signEvent.getInfoPlayer(this)).setScore(7);
        getScoreboardManager().getObjective().getScore("�r�r�r").setScore(6);
        getScoreboardManager().getObjective().setDisplaySlot(DisplaySlot.SIDEBAR);

        getPlayerList().clear();
        setState(State.WAITING);
        Quake.gameManager.signEvent.actualiseJoinSignForGame(this);
    }

    public void respawn(Player p) {
        Location location = getRespawn().get(random.nextInt(getRespawn().size()));
        int pSize = 0;
        int tentative;
        if (Quake.gameManager.getRespawnTentative().get(p) == null) {
            tentative = 0;
            Quake.gameManager.getRespawnTentative().put(p, 0);
        } else {
            tentative = Quake.gameManager.getRespawnTentative().get(p);
        }
        if (tentative <= 5) {
            p.teleport(location);
            Quake.gameManager.getRespawnTentative().put(p, 0);
            Quake.gameManager.getRespawnTentative().remove(p);
        } else {
            for (Entity e : p.getWorld().getNearbyEntities(location, 10, 10, 10)) {
                if (e instanceof Player) {
                    pSize++;
                }
            }
            if (pSize == 0) {
                p.teleport(location);
                Quake.gameManager.getRespawnTentative().remove(p);
                Quake.gameManager.getRespawnTentative().put(p, 0);
            } else {
                respawn(p);
                Quake.gameManager.getRespawnTentative().remove(p);
                Quake.gameManager.getRespawnTentative().put(p, tentative + 1);
            }
        }
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