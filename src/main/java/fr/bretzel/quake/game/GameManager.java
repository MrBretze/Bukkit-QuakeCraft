/**
 * Copyright 2015 Lo�c Nussbaumer
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

import fr.bretzel.quake.*;
import fr.bretzel.quake.game.event.GameCreateEvent;
import fr.bretzel.quake.game.event.PlayerJoinGameEvent;
import fr.bretzel.quake.game.event.PlayerLeaveGameEvent;
import fr.bretzel.quake.game.event.PlayerShootEvent;
import fr.bretzel.quake.game.task.GameStartTask;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.*;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.UUID;

/**
 * Created by MrBretzel on 12/06/2015.
 */

public class GameManager implements Listener {

    public SignEvent signEvent;
    public int maxMinute = 10;
    private LinkedList<Game> gameLinkedList = new LinkedList<>();
    private HashMap<Game, GameStartTask> gameQuakeTaskHashMap = new HashMap<>();
    private LinkedHashMap<UUID, Chrono> uuidToChrono = new LinkedHashMap<>();
    private LinkedHashMap<Game, Chrono> gameChrono = new LinkedHashMap<>();
    private Location lobby;

    public GameManager() {

        Quake.manager.registerEvents(this, Quake.quake);

        this.signEvent = new SignEvent(this);

        if (!Quake.quake.getConfig().isSet("lobby")) {
            Quake.quake.getConfig().set("lobby", Util.toStringLocation(Bukkit.getWorlds().get(0).getSpawnLocation()));
        } else {
            lobby = Util.toLocationString(Quake.quake.getConfig().getString("lobby"));
        }
    }

    public void registerGame(Player creator, String name, Location loc1, Location loc2) {
        if (loc1 == null) {
            creator.sendMessage(ChatColor.RED + "The first is not set !");
            return;
        }
        if (loc2 == null) {
            creator.sendMessage(ChatColor.RED + "The second is not set !");
            return;
        }
        if (containsGame(name)) {
            creator.sendMessage(ChatColor.RED + "The game is already exist !");
            return;
        }
        Game game = new Game(loc1, loc2, name);
        gameLinkedList.add(game);
        GameCreateEvent event = new GameCreateEvent(game, creator);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return;
        }
        creator.sendMessage(ChatColor.GREEN + "The game " + name + " has been create !");
    }

    public void deleteGame(Game game, Player deleter) {
        if(game != null) {
            game.delete();
            deleter.sendMessage(ChatColor.GREEN + "The game has been deleted !");
        } else {
            deleter.sendMessage(ChatColor.RED + "The game has been not found !");
        }
    }

    public Game getGameByName(String name) {
        Game game = null;
        for (Game a : gameLinkedList) {
            if (a.getName().equals(name)) {
                game = a;
            }
        }
        return game;
    }

    public boolean containsGame(Game game) {
        return gameLinkedList.contains(game);
    }

    public boolean containsGame(String name) {
        return gameLinkedList.contains(getGameByName(name));
    }

    public Location getLobby() {
        if (lobby == null) {
            return Bukkit.getWorlds().get(0).getSpawnLocation();
        }
        return lobby;
    }

    public void setLobby(Location lobby) {
        this.lobby = lobby;
    }


    public HashMap<Game, GameStartTask> getQuakeTaskHashMap() {
        return gameQuakeTaskHashMap;
    }

    public LinkedHashMap<Game, Chrono> getGameChrono() {
        return gameChrono;
    }

    public Chrono getChronoGame(Game game) {
        return getGameChrono().get(game);
    }

    public GameTask getTaskByGame(Game game) {
        return getQuakeTaskHashMap().get(game);
    }

    public SignEvent getSignEvent() {
        return signEvent;
    }

    public void setSignEvent(SignEvent signEvent) {
        this.signEvent = signEvent;
    }

    public LinkedList<Game> getGameLinkedList() {
        return gameLinkedList;
    }

    public void setGameLinkedList(LinkedList<Game> gameLinkedList) {
        this.gameLinkedList = gameLinkedList;
    }

    public LinkedHashMap<UUID, Chrono> getUuidToChrono() {
        return uuidToChrono;
    }

    public Chrono getChronoByUUID(UUID id) {
        return getUuidToChrono().get(id);
    }

    public Game getGameByPlayer(Player player) {
        for (Game a : getGameLinkedList()) {
            if (a.getPlayerList().contains(player.getUniqueId())) {
                return a;
            }
        }
        return null;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Action action = event.getAction();
        Player player = event.getPlayer();
        PlayerInfo pi = Quake.getPlayerInfo(player);

        if (player.hasPermission("quake.event.select") && player.getItemInHand() != null && player.getItemInHand().getType() == Material.GOLD_HOE && !pi.isInGame()) {
            switch (action) {
                case LEFT_CLICK_BLOCK:
                    leftClick(player, event);
                    break;
                case RIGHT_CLICK_BLOCK:
                    rightClick(player, event);
                    break;
            }
        }

        if (player.hasPermission("quake.event.shoot") && player.getItemInHand() != null && pi.isInGame()) {
            Game game = getGameByPlayer(player);
            if (game.getState() == State.STARTED) {
                if (action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK) {
                    pi.dash();
                    event.setCancelled(true);
                } else if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
                    pi.shoot();
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        PlayerInfo info = Quake.getPlayerInfo(player);
        if (info.isInGame()) {
            Game game = getGameByPlayer(player);
            if (!game.isInArea(event.getTo())) {
                player.teleport(event.getFrom().setDirection(player.getEyeLocation().getDirection()));
            }
        }
    }

    @EventHandler
    public void PlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        PlayerInfo info = Quake.getPlayerInfo(player);

        if (info.isInGame()) {
            Game game = getGameByPlayer(player);
            Chrono c = getChronoByUUID(player.getUniqueId());
            if (c != null) {
                c.stop();
                int minute = c.getMinutes();
                int heure = c.getHours();
                if (heure > 0 || minute >= 5) {
                    game.getPlayerList().remove(player.getUniqueId());
                    player.teleport(getLobby());
                    player.setScoreboard(info.getPlayerScoreboard());
                    getUuidToChrono().remove(player.getUniqueId());
                } else {
                    player.setScoreboard(game.getScoreboardManager().getScoreboard());
                }
            } else {
                player.teleport(getLobby());
                player.setScoreboard(info.getPlayerScoreboard());
            }
        } else {
            player.teleport(getLobby());
            player.setScoreboard(info.getPlayerScoreboard());
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        PlayerInfo info = Quake.getPlayerInfo(player);
        info.save();
        Quake.getPlayerInfos().remove(info);
    }

    @EventHandler
    public void onPlayerJoinGame(PlayerJoinGameEvent event) {
        Player player = event.getPlayer();
        Game game = event.getGame();

        if (game.getState() == State.WAITING) {
            int players = game.getPlayerList().size() + 1;
            if (players == game.getMinPlayer()) {
                GameStartTask gameStartTask = new GameStartTask(Quake.quake, 20L, 20L, game);
                getQuakeTaskHashMap().put(game, gameStartTask);
                game.broadcastMessage(player.getDisplayName() + ChatColor.BLUE + " has joined (" + ChatColor.AQUA + players + ChatColor.DARK_GRAY + "/" + ChatColor.AQUA + game.getMaxPlayer()
                        + ChatColor.BLUE + ")");
                player.sendMessage(player.getDisplayName() + ChatColor.BLUE + " has joined (" + ChatColor.AQUA + players + ChatColor.DARK_GRAY + "/" + ChatColor.AQUA + game.getMaxPlayer()
                        + ChatColor.BLUE + ")");
            } else {
                game.broadcastMessage(player.getDisplayName() + ChatColor.BLUE + " has joined (" + ChatColor.AQUA + players + ChatColor.DARK_GRAY + "/" + ChatColor.AQUA + game.getMaxPlayer()
                        + ChatColor.BLUE + ")");
                player.sendMessage(player.getDisplayName() + ChatColor.BLUE + " has joined (" + ChatColor.AQUA + players + ChatColor.DARK_GRAY + "/" + ChatColor.AQUA + game.getMaxPlayer()
                        + ChatColor.BLUE + ")");
            }
            if (player.getGameMode() != GameMode.ADVENTURE) {
                player.setGameMode(GameMode.ADVENTURE);
            }
            player.setScoreboard(game.getScoreboardManager().getScoreboard());
            game.getTeam().addPlayer(player);
            player.setWalkSpeed(0.3F);
        } else if (game.getState() == State.STARTED) {
            player.sendMessage(ChatColor.RED + "The game has bin started !");
            event.setCancelled(true);
            return;
        }
    }

    @EventHandler
    public void onPlayerQuitGameEvent(PlayerLeaveGameEvent event) {
        Game game = event.getGame();
        final Player player = event.getPlayer();
        final PlayerInfo info = Quake.getPlayerInfo(player);
        if (game.getState() == State.STARTED) {
            if (game.getPlayerList().size() - 1 == 0) {
                game.stop();
            }
        }
        int players = game.getPlayerList().size() - 1;
        game.broadcastMessage(player.getDisplayName() + ChatColor.BLUE + " has left (" + ChatColor.AQUA + players + ChatColor.DARK_GRAY + "/" + ChatColor.AQUA + game.getMaxPlayer()
                + ChatColor.BLUE + ")");
        player.setWalkSpeed(0.2F);
        new BukkitRunnable() {

            @Override
            public void run() {
                player.setScoreboard(info.getPlayerScoreboard());
            }
        }.runTaskLater(Quake.quake, 10L);
    }

    @EventHandler
    public void onPlayerHasDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (getGameByPlayer(player) != null) {
                if (event.getCause() != EntityDamageEvent.DamageCause.CUSTOM) {
                    event.setCancelled(true);
                }
            }
        }
    }

    public int getMaxMinute() {
        return maxMinute;
    }

    @EventHandler
    public void onPlayerShoot(PlayerShootEvent event) {
        Player player = event.getPlayer();
        Game game = event.getGame();
        PlayerInfo info = Quake.getPlayerInfo(player);

        if (info.isShoot()) {
            if (event.getKill() == 2) {
                game.broadcastMessage(ChatColor.RED.toString() + ChatColor.BOLD + "Double kill !");
            } else if (event.getKill() == 3) {
                game.broadcastMessage(ChatColor.RED.toString() + ChatColor.BOLD + "Triple kill !");
            } else if (event.getKill() >= 4) {
                game.broadcastMessage(ChatColor.RED.toString() + ChatColor.BOLD + "Multiple kill !");
            }

            for (Player p : event.getPlayers()) {
                Util.shootFirework(p.getEyeLocation());
                game.broadcastMessage(p.getDisplayName() + ChatColor.BLUE + " has been sprayed by " + ChatColor.RESET + player.getName());
            }
        }
    }

    @EventHandler
    public void onPlayerChangeFoodLevel(FoodLevelChangeEvent event) {
        Player player = (Player) event.getEntity();
        PlayerInfo info = Quake.getPlayerInfo(player);
        if (info.isInGame()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerChangeSlotBar(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        PlayerInfo info = Quake.getPlayerInfo(player);
        if (info.isInGame()) {
            Game game = getGameByPlayer(player);
            if (game.getState() == State.STARTED) {
                player.getInventory().setHeldItemSlot(0);
                event.setCancelled(true);
            }
        }
    }

    private void rightClick(Player player, PlayerInteractEvent event) {
        PlayerInfo info = Quake.getPlayerInfo(player);
        Location location = event.getClickedBlock().getLocation();
        info.setSecondLocation(location.clone());
        player.sendMessage(ChatColor.GREEN + "The second point has bin set to: " + location.getBlockX() + " " + location.getBlockY() + " " + location.getBlockZ());
        event.setCancelled(true);
    }

    private void leftClick(Player player, PlayerInteractEvent event) {
        PlayerInfo info = Quake.getPlayerInfo(player);
        Location location = event.getClickedBlock().getLocation();
        info.setFirstLocation(location.clone());
        player.sendMessage(ChatColor.GREEN + "The first point has bin set to: " + location.getBlockX() + " " + location.getBlockY() + " " + location.getBlockZ());
        event.setCancelled(true);
    }
}
