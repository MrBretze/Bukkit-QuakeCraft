package fr.bretzel.quake.game;

import fr.bretzel.quake.Chrono;
import fr.bretzel.quake.GameTask;
import fr.bretzel.quake.Quake;
import fr.bretzel.quake.Util;
import fr.bretzel.quake.game.event.GameCreate;
import fr.bretzel.quake.game.event.PlayerJoinGame;
import fr.bretzel.quake.game.event.PlayerLeaveGame;
import fr.bretzel.quake.inventory.BasicGun;
import fr.bretzel.quake.player.PlayerInfo;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.UUID;

/**
 * Created by MrBretzel on 12/06/2015.
 */

public class GameManager implements Listener {

    private LinkedList<Game> gameLinkedList = new LinkedList<>();
    private HashMap<Game, GameTask> gameQuakeTaskHashMap = new HashMap<>();
    private LinkedHashMap<UUID, Chrono> uuidToChrono = new LinkedHashMap<>();
    private Quake quake;
    public SignEvent signEvent;
    private Location lobby;

    public GameManager(Quake quake) {
        this.quake = quake;

        quake.manager.registerEvents(this, quake);

        this.signEvent = new SignEvent(this);

        if(!quake.getConfig().isSet("lobby")) {
            quake.getConfig().set("lobby", Util.toStringLocation(Bukkit.getWorlds().get(0).getSpawnLocation()));
        } else {
            lobby = Util.toLocationString(quake.getConfig().getString("lobby"));
        }
    }

    public void registerGame(Player creator, String name, Location loc1, Location loc2) {
        if(loc1 == null) {
            creator.sendMessage(ChatColor.RED + "The first is not set !");
            return;
        }
        if (loc2 == null) {
            creator.sendMessage(ChatColor.RED + "The second is not set !");
            return;
        }
        if(containsGame(name)) {
            creator.sendMessage(ChatColor.RED + "The game is already exist !");
            return;
        } else {
            Game game = new Game(loc1, loc2, name);
            gameLinkedList.add(game);
            GameCreate event = new GameCreate(game, creator);
            Bukkit.getPluginManager().callEvent(event);
            if(event.isCancelled()) {
                return;
            }
            creator.sendMessage(ChatColor.GREEN + "The game " + name + " has been create !");
        }
    }

    public Game getGameByName(String name) {
        Game game = null;
        for(Game a : gameLinkedList) {
            if(a.getName().equals(name)) {
                game = a;
            }
        }
        return game;
    }

    public boolean containsGame(Game game) {
        return gameLinkedList.contains(game);
    }

    public boolean containsGame(String arena) {
        return gameLinkedList.contains(getGameByName(arena));
    }

    public Location getLobby() {
        if(lobby == null) {
            return Bukkit.getWorlds().get(0).getSpawnLocation();
        }
        return lobby;
    }

    public HashMap<Game, GameTask> getQuakeTaskHashMap() {
        return gameQuakeTaskHashMap;
    }

    public GameTask getTaskByGame(Game game) {
        return getQuakeTaskHashMap().get(game);
    }

    public void setLobby(Location lobby) {
        this.lobby = lobby;
    }

    public SignEvent getSignEvent() {
        return signEvent;
    }

    public void setSignEvent(SignEvent signEvent) {
        this.signEvent = signEvent;
    }

    public Quake getQuake() {
        return quake;
    }

    public LinkedList<Game> getGameLinkedList() {
        return gameLinkedList;
    }

    public LinkedHashMap<UUID, Chrono> getUuidToChrono() {
        return uuidToChrono;
    }

    public void setGameLinkedList(LinkedList<Game> gameLinkedList) {
        this.gameLinkedList = gameLinkedList;
    }

    public Chrono getChronoByUUID(UUID id) {
        return getUuidToChrono().get(id);
    }

    public Game getGameByPlayer(Player player) {
        for(Game a : getGameLinkedList()) {
            if(a.getPlayerList().contains(player.getUniqueId())) {
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

        if(player.hasPermission("quake.event.select") && player.getItemInHand() != null && player.getItemInHand().getType() == Material.GOLD_HOE && !pi.isInGame()) {
            switch (action) {
                case LEFT_CLICK_BLOCK:
                    leftClick(player, event);
                    break;
                case RIGHT_CLICK_BLOCK:
                    rightClick(player, event);
                    break;
            }
        }

        if(player.hasPermission("quake.event.shoot") && player.getItemInHand() != null && pi.isInGame()) {
            switch (action) {
                case LEFT_CLICK_BLOCK:
                    player.sendMessage(ChatColor.RED + "NOOOOOOOP");
                    break;
                case RIGHT_CLICK_BLOCK:
                    pi.shoot();
                    break;
            }
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if(getGameByPlayer(player) != null) {
            Game game = getGameByPlayer(player);
            if(!game.getBlocks().contains(event.getTo().getBlock())) {
                Vector vector = player.getEyeLocation().getDirection().normalize().multiply(-0.3);
                vector.setY(0);
                player.teleport(event.getFrom().add(vector));
            }
        }
    }

    @EventHandler
    public void PlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Game game = getGameByPlayer(player);
        if (game == null) {
            player.teleport(getLobby());
        } else {
            Chrono c = getChronoByUUID(player.getUniqueId());
            if(c != null) {
                c.stop();
                int minute = c.getMinute();
                int heure = c.getHeure();
                if(heure > 0) {
                    game.getPlayerList().remove(player.getUniqueId());
                    player.teleport(getLobby());
                }
                if(minute > 2) {
                    game.getPlayerList().remove(player.getUniqueId());
                    player.teleport(getLobby());
                }
            } else {
                game.getPlayerList().remove(player.getUniqueId());
                player.teleport(getLobby());
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        Game game = getGameByPlayer(player);
        if (game != null) {
            if(game.getState() == State.WAITING) {
                game.getPlayerList().remove(player.getUniqueId());
            } else {
                Chrono chrono = new Chrono();
                chrono.start();
                getUuidToChrono().put(player.getUniqueId(), chrono);
            }
            signEvent.actualiseJoinSignForGame(game);
        }
    }

    @EventHandler
    public void onPlayerJoinGame(PlayerJoinGame event) {
        Player player = event.getPlayer();
        Game game = event.getGame();

        if (game.getState() == State.WAITING) {
            if(game.getPlayerList().size() + 1 > game.getMaxPlayer()) {
                player.sendMessage(ChatColor.RED + "The game is full !");
                event.setCancelled(true);
                return;
            } else if (game.getPlayerList().size() + 1 == game.getMinPlayer()) {
                GameTask gameTaks = new GameTask(Quake.quake, 20L, 20L, game) {
                    int minSecQuake = getGame().getSecLaunch();
                    @Override
                    public void run() {
                        if (minSecQuake > 0) {
                            for (UUID id : getGame().getPlayerList()) {
                                Player p = Bukkit.getPlayer(id);
                                if (p.isOnline()) {
                                    p.sendMessage(ChatColor.AQUA + "The game start in: " + Util.getChatColorByInt(minSecQuake) + String.valueOf(minSecQuake));
                                }
                            }
                            minSecQuake--;
                        }
                        if (minSecQuake <= 0) {
                            getGame().setState(State.STARTED);
                            Quake.gameManager.signEvent.actualiseJoinSignForGame(getGame());
                            for(UUID id : getGame().getPlayerList()) {
                                Player p = Bukkit.getPlayer(id);
                                if(p.isOnline()) {
                                    PlayerInfo info = Quake.getPlayerInfo(p);
                                    info.give(new BasicGun(info));
                                }
                            }
                            cancel();
                        }
                    }
                };
                getQuakeTaskHashMap().put(game, gameTaks);
                return;
            } else if(game.getPlayerList().size() + 1 <= game.getMaxPlayer()) {
                return;
            } else {
                player.sendMessage(ChatColor.DARK_RED + "Une erreur ses produite !");
                player.sendMessage(ChatColor.DARK_RED + "L'évènement PlayerJoinGame et annuler !");
                player.sendMessage(ChatColor.DARK_RED + "Report: ");
                player.sendMessage(ChatColor.RED + game.getName() + ", " + game.getMaxPlayer() + ", " + game.getPlayerList() + ", " + game.getState().name() + ", " + game.getRespawn() + ", " + game.getFirstLocation()
                        + ", " + game.getSecondLocation());
                event.setCancelled(true);
                return;
            }
        } else if(game.getState() == State.STARTED) {
            player.sendMessage(ChatColor.RED + "The game has bin started !");
            event.setCancelled(true);
            return;
        }
    }

    @EventHandler
    public void onPlayerQuitGameEvent(PlayerLeaveGame event) {
        Game game = event.getGame();
        Player player = event.getPlayer();
        game.getPlayerList().remove(player.getUniqueId());
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
