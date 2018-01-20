package fr.bretzel.quake.game;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import fr.bretzel.quake.*;
import fr.bretzel.quake.config.Config;
import fr.bretzel.quake.game.event.GameEndEvent;
import fr.bretzel.quake.game.event.PlayerDashEvent;
import fr.bretzel.quake.task.game.GameEndTask;
import fr.bretzel.quake.task.game.GamePlayerTask;
import fr.bretzel.quake.task.ReloadTask;
import fr.bretzel.quake.hologram.Hologram;
import fr.bretzel.quake.game.scoreboard.ScoreboardAPI;

import fr.bretzel.quake.util.ParticleEffect;
import fr.bretzel.quake.util.Util;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.NameTagVisibility;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.Vector;

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class Game {

    private List<Location> respawn = Lists.newArrayList();
    private Location firstLocation;
    private Location secondLocation;
    private Location spawn;
    private String name;
    private File file;
    private List<PlayerInfo> playerList = Lists.newArrayList();
    private List<Sign> signList = Lists.newArrayList();
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
    private Map<PlayerInfo, Integer> playerKills = Maps.newHashMap();
    private Map<PlayerInfo, Integer> playerDeath = Maps.newHashMap();
    private Map<PlayerInfo, Integer> killStreak = Maps.newHashMap();
    private Map<PlayerInfo, Integer> currentKillStreak = Maps.newHashMap();

    public List<GamePlayerTask> gamePlayerTasks = Lists.newArrayList();

    public Game(Location firstLocation, Location secondLocation, String name) {
        setFirstLocation(firstLocation);
        setSecondLocation(secondLocation);
        setName(name);
        setDisplayName(name);

        setSpawn(getDefaultSpawn());


        setScoreboardManager(new ScoreboardAPI(this));
        Team team = getScoreboardManager().getScoreboard().registerNewTeam(getName());
        team.setNameTagVisibility(NameTagVisibility.NEVER);
        setTeam(team);
    }

    public Game(ResultSet set) {
        load(set);
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

    public List<PlayerInfo> getPlayerList() {
        return playerList;
    }

    public void setPlayerList(List<PlayerInfo> playerList) {
        this.playerList = playerList;
    }

    public void addPlayer(PlayerInfo player) {
        if (!getPlayerList().contains(player)) {
            getPlayerList().add(player);
        }
    }

    public void addPlayer(Player player) {
        addPlayer(PlayerInfo.getPlayerInfo(player));
    }

    public void addPlayer(UUID uuid) {
        addPlayer(Bukkit.getPlayer(uuid));
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

    public List<Location> getRespawns() {
        return respawn;
    }

    public boolean hasRespawn(Location location) {
        for (Location l : getRespawns()) {
            if (location.distance(l) < 1.5)
                return true;
        }
        return false;
    }

    public Location getRespawn(Location location) {
        for (Location l : getRespawns()) {
            if (location.distance(l) < 1.5)
                return l;
        }
        return null;
    }

    public void removeRespawn(Location location) {
        getRespawns().remove(getRespawn(location));
    }

    public void setRespawn(LinkedList<Location> respawn) {
        this.respawn = respawn;
    }

    public void addRespawn(Location location) {
        getRespawns().add(location.add(0.0, 1.0, 0.0));
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

    public List<Sign> getSignList() {
        return signList;
    }

    public void setSignList(LinkedList<Sign> signList) {
        this.signList = signList;
    }

    public void addSign(Location location, Game game) {
        this.signList.add(new Sign(location, game));
    }

    public void removeSign(Sign sign) {
        this.signList.remove(sign);
    }

    public void broadcastMessage(String msg) {
        for (PlayerInfo id : getPlayerList()) {
            Player p = id.getPlayer();
            if (p != null && p.isOnline()) {
                p.sendMessage(msg);
            }
        }
    }

    public void broadcastTitle(String msg) {
        for (PlayerInfo id : getPlayerList()) {
            Player p = id.getPlayer();
            if (p != null && p.isOnline()) {
                p.sendMessage(msg);
            }
        }
    }

    public void view(boolean view) {
        this.respawnview = view;
        if (view) {
            int s = 0;
            for (Location location : getRespawns()) {
                s++;
                Hologram hologram = Quake.holoManager.getHologram(location, 0.5);
                if (hologram == null) {
                    hologram = new Hologram(location, "Respawn: " + s, Quake.holoManager);
                }
                if (!hologram.isVisible()) {
                    hologram.display(true);
                }
            }
        } else {
            for (Location location : getRespawns()) {
                Hologram hologram = Quake.holoManager.getHologram(location, 0.5);
                if (hologram != null)
                    hologram.display(false);
            }
        }
    }

    public void view() {
        view(!isView());
    }

    public boolean isView() {
        return respawnview;
    }

    public int getKill(UUID player) {
        return getKill(Bukkit.getPlayer(player));
    }

    public int getKill(Player player) {
        return getKill(PlayerInfo.getPlayerInfo(player));
    }

    public int getKill(PlayerInfo player) {
        if (!playerKills.containsKey(player)) {
            playerKills.put(player, 0);
        }
        return playerKills.get(player);
    }

    public void addKill(Player player, int kill) {
        addKill(PlayerInfo.getPlayerInfo(player), kill);
    }

    public void addKill(PlayerInfo player, int kill) {
        int i = 0;
        if (playerKills.containsKey(player)) {
            i = playerKills.get(player);
        }
        i += kill;
        playerKills.remove(player);
        playerKills.put(player, i);
    }

    public void setKill(PlayerInfo player, int kill) {
        if (playerKills.containsKey(player)) {
            playerKills.remove(player);
        }
        playerKills.put(player, kill);
    }

    public int getDeath(UUID player) {
        return getDeath(Bukkit.getPlayer(player));
    }

    public int getDeath(Player player) {
        return getDeath(PlayerInfo.getPlayerInfo(player));
    }

    public int getDeath(PlayerInfo player) {
        if (!playerDeath.containsKey(player)) {
            playerDeath.put(player, 0);
        }
        return playerDeath.get(player);
    }

    public void addDeath(Player player, int kill) {
        addDeath(PlayerInfo.getPlayerInfo(player), kill);
    }

    public void addDeath(PlayerInfo player, int kill) {
        int i = 0;
        if (playerDeath.containsKey(player)) {
            i = playerDeath.get(player);
        }
        i += kill;
        playerDeath.remove(player);
        playerDeath.put(player, i);
    }

    public void setDeath(PlayerInfo player, int kill) {
        if (playerDeath.containsKey(player)) {
            playerDeath.remove(player);
        }
        playerDeath.put(player, kill);
    }

    public Location getSpawn() {
        return spawn;
    }

    public void setSpawn(Location spawn) {
        this.spawn = spawn;
    }

    private Location getDefaultSpawn() {
        int x1 = getFirstLocation().getBlockX();
        int y1 = getFirstLocation().getBlockY();
        int z1 = getFirstLocation().getBlockZ();

        int x2 = getSecondLocation().getBlockX();
        int y2 = getSecondLocation().getBlockY();
        int z2 = getSecondLocation().getBlockZ();

        int x = (x1 + x2) / 2;
        int y = (y1 + y2) / 2;
        int z = (z1 + z2) / 2;

        return new Location(getFirstLocation().getWorld(), Double.valueOf(String.valueOf(x)), Double.valueOf(String.valueOf(y)), Double.valueOf(String.valueOf(z)));
    }

    public boolean isInArea(int x, int y, int z) {
        return isInArea(new Location(getFirstLocation().getWorld(), x, y, z));
    }

    public boolean isInArea(Player player) {
        return isInArea(player.getLocation());
    }

    public boolean isInArea(Location location) {
        int maxX = (getFirstLocation().getBlockX() < getSecondLocation().getBlockX() ? getSecondLocation().getBlockX() : getFirstLocation().getBlockX());
        int minX = (getFirstLocation().getBlockX() > getSecondLocation().getBlockX() ? getSecondLocation().getBlockX() : getFirstLocation().getBlockX());

        int maxY = (getFirstLocation().getBlockY() < getSecondLocation().getBlockY() ? getSecondLocation().getBlockY() : getFirstLocation().getBlockY());
        int minY = (getFirstLocation().getBlockY() > getSecondLocation().getBlockY() ? getSecondLocation().getBlockY() : getFirstLocation().getBlockY());

        int maxZ = (getFirstLocation().getBlockZ() < getSecondLocation().getBlockZ() ? getSecondLocation().getBlockZ() : getFirstLocation().getBlockZ());
        int minZ = (getFirstLocation().getBlockZ() > getSecondLocation().getBlockZ() ? getSecondLocation().getBlockZ() : getFirstLocation().getBlockZ());

        return location.getBlockX() >= minX && location.getBlockX() <= maxX && location.getBlockY() >= minY && location.getBlockY() <= maxY && location.getBlockZ() >= minZ && location.getBlockZ() <= maxZ;
    }

    public void addKillStreak(Player player, int kill) {
        addKillStreak(PlayerInfo.getPlayerInfo(player), kill);
    }

    public void addKillStreak(PlayerInfo player, int kill) {
        if (killStreak.containsKey(player)) {
            killStreak.put(player, getKillStreak(player) + kill);
        } else {
            killStreak.put(player, kill);
        }
    }

    public int getKillStreak(Player player) {
        return getKillStreak(PlayerInfo.getPlayerInfo(player));
    }

    public int getKillStreak(PlayerInfo player) {
        if (!killStreak.containsKey(player))
            return 0;
        return killStreak.get(player);
    }

    public void setKillSteak(Player player, int kill) {
        setKillSteak(PlayerInfo.getPlayerInfo(player), kill);
    }

    public void setKillSteak(PlayerInfo uuid, int kill) {
        killStreak.put(uuid, kill);
    }

    //

    public void addCurrentKillStreak(Player player, int kill) {
        addCurrentKillStreak(PlayerInfo.getPlayerInfo(player), kill);
    }

    public void addCurrentKillStreak(PlayerInfo player, int kill) {
        if (currentKillStreak.containsKey(player)) {
            currentKillStreak.put(player, getCurrentKillStreak(player) + kill);
        } else {
            currentKillStreak.put(player, kill);
        }
    }

    public int getCurrentKillStreak(Player player) {
        return getCurrentKillStreak(PlayerInfo.getPlayerInfo(player));
    }

    public int getCurrentKillStreak(PlayerInfo player) {
        return currentKillStreak.get(player);
    }

    public void setCurrentKillSteak(Player player, int kill) {
        setCurrentKillSteak(PlayerInfo.getPlayerInfo(player), kill);
    }

    public void setCurrentKillSteak(PlayerInfo uuid, int kill) {
        currentKillStreak.put(uuid, kill);
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public boolean isWallHackBlock(Location location) {
        return location.getBlock().getType().isSolid();
    }

    public void shootPlayer(PlayerInfo player) {
        if (player.isShoot() && getState() == State.STARTED) {
            player.setShoot(false);

            new ReloadTask(Quake.quake, player, player.getReloadTime(), false);

            Util.playSound(player.getPlayer().getLocation(), Sound.ENTITY_FIREWORK_LARGE_BLAST, 7.5F, 0.8F);

            int range = 200; //Quake.quake.getConfig().getInt("quake.shoot.range");

            Location playerEyes = player.getPlayer().getEyeLocation();
            org.bukkit.util.Vector direction = playerEyes.getDirection().normalize();
            Location f = playerEyes.clone();
            Vector progress = direction.clone().multiply(0.5);
            int maxRange = 100 * range / 70;

            List<UUID> killeds = Lists.newArrayList();

            for (int loop = maxRange; loop > 0; loop--) {
                new ParticleEffect.ParticlePacket(player.getEffect(), 0, 0, 0, 0, 1, true, null).sendTo(f, 200D);
                f.add(progress);

                if (isWallHackBlock(f)) {
                    break;
                }

                for (Entity e : f.getWorld().getNearbyEntities(f, 5, 5, 5)) {
                    if (e instanceof Player) {
                        Player player_killed = (Player) e;
                        Vector pDeplacement = player_killed.getVelocity();
                        Location h = player_killed.getLocation().add(pDeplacement.getX(), 1, pDeplacement.getZ());

                        double px = h.getX();
                        double py = h.getY();
                        double pz = h.getZ();
                        boolean dX = Math.abs(f.getX() - px) < 0.957D * 0.59D;
                        boolean dY = Math.abs(f.getY() - py) < 1.685D * 0.59D;
                        boolean dZ = Math.abs(f.getZ() - pz) < 0.957D * 0.59D;

                        if (dX && dY && dZ && player_killed != player.getPlayer() && !killeds.contains(player_killed.getUniqueId()) && !e.isDead()) {
                            PlayerInfo info_killed = PlayerInfo.getPlayerInfo(player_killed);
                            Util.shootFirework(info_killed.getPlayer().getEyeLocation());
                            respawn(info_killed.getPlayer());
                            addDeath(player_killed, 1);
                            killeds.add(player_killed.getUniqueId());
                            setCurrentKillSteak(info_killed, 0);
                        }
                    }
                }
            }

            int killed = killeds.size();

            if (killed > 0) {
                addKill(player, killed);
                addCurrentKillStreak(player, 1);

                int killStreak = getCurrentKillStreak(player);

                if (killStreak == 5) {
                    broadcastMessage(ChatColor.RED.toString() + ChatColor.BOLD + player.getPlayer().getDisplayName() + " IS KILLING SPREE !");
                    addKillStreak(player, 1);
                } else if (killStreak == 10) {
                    broadcastMessage(ChatColor.RED.toString() + ChatColor.BOLD + player.getPlayer().getDisplayName() + " IS A RAMPAGE !");
                    addKillStreak(player, 1);
                } else if (killStreak == 15) {
                    broadcastMessage(ChatColor.RED.toString() + ChatColor.BOLD + player.getPlayer().getDisplayName() + " IS A DEMON !");
                    addKillStreak(player, 1);
                } else if (killStreak == 20) {
                    broadcastMessage(ChatColor.RED.toString() + ChatColor.BOLD + player.getPlayer().getDisplayName() + " MONSTER KIL !!");
                    addKillStreak(player, 1);
                }

                getScoreboardManager().getObjective().getScore(player.getPlayer().getName()).setScore(getKill(player));

                if (getKill(player) >= getMaxKill()) {
                    GameEndEvent event = new GameEndEvent(player.getPlayer(), this);
                    Bukkit.getPluginManager().callEvent(event);
                    if (event.isCancelled()) {
                        return;
                    }
                    for (PlayerInfo id : getPlayerList()) {
                        Player p = id.getPlayer();
                        p.getInventory().clear();
                        id.setShoot(false);
                        id.setDash(false);
                    }

                    new GameEndTask(Quake.quake, 10L, 10L, this, player.getPlayer());

                    player.addWin(1);

                    getTeam().setNameTagVisibility(NameTagVisibility.ALWAYS);
                    broadcastMessage(ChatColor.BLUE + ChatColor.BOLD.toString() + player.getPlayer().getDisplayName() + " Has win the game !");
                }
            }
        }
    }

    public void dashPlayer(PlayerInfo player) {
        if (player.isDash()) {
            Game game = Quake.gameManager.getGameByPlayer(player);
            if (game.getState() == State.STARTED) {
                PlayerDashEvent event = new PlayerDashEvent(player.getPlayer(), game);
                Bukkit.getPluginManager().callEvent(event);
                player.setDash(false);
                new ReloadTask(Quake.quake, player, 3, true);
                Vector pVector = player.getPlayer().getEyeLocation().getDirection();
                Vector vector = new Vector(pVector.getX(), 0.4686D, pVector.getZ()).multiply(1.4D);
                player.getPlayer().setVelocity(vector);
                player.getPlayer().getWorld().playSound(player.getPlayer().getLocation(), Sound.ENTITY_ENDERDRAGON_FLAP, random.nextFloat(), random.nextFloat());
            }
        }
    }

    public void stop() {
        setState(State.WAITING);

        for (GamePlayerTask task : gamePlayerTasks) {
            task.cancel();
        }

        for (PlayerInfo info : getPlayerList()) {
            Player p = info.getPlayer();
            info.setDash(true);
            info.setShoot(true);

            info.addKill(getKill(info));
            info.addDeath(getDeath(info));
            info.addCoins(getKill(info) * 5);

            info.syncDB();

            if (p != null && p.isOnline()) {
                p.teleport(Quake.gameManager.getLobby());
                p.getInventory().clear();
                p.setWalkSpeed(0.2F);
                p.setScoreboard(info.getPlayerScoreboard());
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
        t.setNameTagVisibility(NameTagVisibility.NEVER);
        setTeam(t);

        getPlayerList().clear();
        playerKills.clear();
        killStreak.clear();
        Quake.gameManager.signEvent.actualiseJoinSignForGame(this);
    }

    public void respawn(Player p) {
        if (getState() == State.STARTED) {
            Location respawn = null;
            double distRespawn = 0;
            for (Location location : getRespawns()) {
                for (Player e : location.getWorld().getEntitiesByClass(Player.class)) {
                    if (PlayerInfo.getPlayerInfo(e).isInGame()) {
                        double distanceSquared = p.getLocation().distanceSquared(e.getLocation());
                        if (distanceSquared > distRespawn) {
                            respawn = location;
                        }
                    }
                }
            }

            p.teleport(respawn);
        }
    }

    public void delete() {
        getFile().delete();
        Quake.gameManager.getGameLinkedList().remove(this);
    }

    public void load(ResultSet set) {

    }

    public void save() {
        try {
            if (Quake.database.ifStringExist(getName(), "Name", Config.Table.GAMES)) {
                PreparedStatement statement = Quake.database.openConnection().prepareStatement("UPDATE " + Config.Table.PLAYERS.getTable() + " SET DisplayName = ?, FirstLocation = ?, SecondLocation = ?, SpawnLocation = ?, MaxPlayer = ?," +
                        " MinPlayer = ?, MaxKill = ?, Respawns = ?," + " Signs = ? WHERE Name = ?");
                statement.setString(1, getDisplayName());
                statement.setString(2, Util.toStringLocation(getFirstLocation()));
                statement.setString(2, Util.toStringLocation(getSecondLocation()));
                statement.setString(4, Util.toStringLocation(getSpawn()));
                statement.setInt(5, getMaxPlayer());
                statement.setInt(6, getMinPlayer());
                statement.setInt(7, getMaxKill());
                statement.setString(8, locsToStrings(getRespawns()));
                //TODO statement.setString(9, locsToStrings(getSignList()));
                statement.setString(10, getName());
            } else {
                PreparedStatement statement = Quake.database.openConnection().prepareStatement("INSERT INTO " + Config.Table.GAMES.getTable() + "(Name, DisplayName, FirstLocation, SecondLocation, SpawnLocation, MaxPlayer, MinPlayer, MaxKill, Respawns, Signs) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
                statement.setString(1, getName());
                statement.setString(2, getDisplayName());
                statement.setString(3, Util.toStringLocation(getFirstLocation()));
                statement.setString(4, Util.toStringLocation(getSecondLocation()));
                statement.setString(5, Util.toStringLocation(getSpawn()));
                statement.setInt(6, getMaxPlayer());
                statement.setInt(7, getMinPlayer());
                statement.setInt(8, getMaxKill());
                statement.setString(9, locsToStrings(getRespawns()));
                //TODO statement.setString(10, locsToStrings(getSignList()));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private String locsToStrings(List<? extends Location> locs) {
        StringBuilder builder = new StringBuilder();
        for (Location l : locs) {
            builder.append("|").append(Util.toStringLocation(l));
        }
        String r = builder.toString();

        if (r.length() > 65535)
            throw new StringIndexOutOfBoundsException("Please remove a respawn point, you are reach the max value.");

        return r;
    }

    private String saveSign(List<Location> locations) {
        return "";
    }
}
