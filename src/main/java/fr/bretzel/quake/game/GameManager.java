package fr.bretzel.quake.game;

import fr.bretzel.quake.*;
import fr.bretzel.quake.game.event.GameCreateEvent;
import fr.bretzel.quake.game.event.PlayerJoinGameEvent;
import fr.bretzel.quake.game.event.PlayerLeaveGameEvent;
import fr.bretzel.quake.game.event.PlayerShootEvent;
import fr.bretzel.quake.game.task.GameStart;
import fr.bretzel.quake.game.task.MainTask;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.util.Vector;

import java.util.*;

/**
 * Created by MrBretzel on 12/06/2015.
 */

public class GameManager implements Listener {

    public SignEvent signEvent;
    public int maxMinute = 10;
    private LinkedList<Game> gameLinkedList = new LinkedList<>();
    private HashMap<Game, GameStart> gameQuakeTaskHashMap = new HashMap<>();
    private LinkedHashMap<UUID, Chrono> uuidToChrono = new LinkedHashMap<>();
    private LinkedHashMap<Game, Chrono> gameChrono = new LinkedHashMap<>();
    private HashMap<Player, Integer> respawnTentative = new HashMap<>();
    private Random random = new Random();
    private Quake quake;
    private Location lobby;
    private MainTask mainTask;

    public GameManager(Quake quake) {
        this.quake = quake;

        Quake.manager.registerEvents(this, quake);

        this.signEvent = new SignEvent(this);

        if(!quake.getConfig().isSet("lobby")) {
            quake.getConfig().set("lobby", Util.toStringLocation(Bukkit.getWorlds().get(0).getSpawnLocation()));
        } else {
            lobby = Util.toLocationString(quake.getConfig().getString("lobby"));
        }

        this.mainTask = new MainTask(this);

        mainTask.runTaskTimer(Quake.quake, 5L, 5L);
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
        }
        Game game = new Game(loc1, loc2, name);
        gameLinkedList.add(game);
        GameCreateEvent event = new GameCreateEvent(game, creator);
        Bukkit.getPluginManager().callEvent(event);
        if(event.isCancelled()) {
            return;
        }
        creator.sendMessage(ChatColor.GREEN + "The game " + name + " has been create !");
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

    public void setLobby(Location lobby) {
        this.lobby = lobby;
    }

    public HashMap<Player, Integer> getRespawnTentative() {
        return respawnTentative;
    }

    public HashMap<Game, GameStart> getQuakeTaskHashMap() {
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

    public Quake getQuake() {
        return quake;
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
            Game game = getGameByPlayer(player);
            if(game.getState() == State.STARTED) {
                if (action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK) {
                    pi.dash();
                } else if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
                    pi.shoot();
                }
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
                if(heure > 0 || minute > 2) {
                    game.getPlayerList().remove(player.getUniqueId());
                    player.teleport(getLobby());
                }
                getUuidToChrono().remove(player.getUniqueId());
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
                GameStart start = getQuakeTaskHashMap().get(game);
                if (start != null) {
                    if(game.getPlayerList().size() < game.getMinPlayer()) {
                        start.cancel();
                        getQuakeTaskHashMap().remove(game);
                    }
                }
            } else {
                Chrono chrono = new Chrono();
                chrono.start();
                getUuidToChrono().put(player.getUniqueId(), chrono);
            }
            signEvent.actualiseJoinSignForGame(game);
        }
    }

    @EventHandler
    public void onPlayerJoinGame(PlayerJoinGameEvent event) {
        Player player = event.getPlayer();
        Game game = event.getGame();

        if (game.getState() == State.WAITING) {
            int players = game.getPlayerList().size() + 1;
            if(players == game.getMinPlayer()) {
                GameStart gameStart = new GameStart(Quake.quake, 20L, 20L, game);
                getQuakeTaskHashMap().put(game, gameStart);
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
            player.setScoreboard(game.getScoreboardManager().getScoreboard());
            game.getScoreboardManager().getScoreboard().resetScores(signEvent.getInfoPlayer(game));
        } else if(game.getState() == State.STARTED) {
            player.sendMessage(ChatColor.RED + "The game has bin started !");
            event.setCancelled(true);
            return;
        }
    }

    @EventHandler
    public void onPlayerQuitGameEvent(PlayerLeaveGameEvent event) {
        Game game = event.getGame();
        Player player = event.getPlayer();
        if (game.getState() == State.STARTED) {
            if(game.getPlayerList().size() - 1 == 0) {
                game.reset();
            }
        }
        int players = game.getPlayerList().size() - 1;
        game.broadcastMessage(player.getDisplayName() + ChatColor.BLUE + " has left (" + ChatColor.AQUA + players + ChatColor.DARK_GRAY + "/" + ChatColor.AQUA + game.getMaxPlayer()
                + ChatColor.BLUE + ")");
    }

    @EventHandler
    public void onPlayerHasDamage(EntityDamageEvent event) {
        if(event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if(getGameByPlayer(player) != null) {
                if(event.getCause() != EntityDamageEvent.DamageCause.CUSTOM) {
                    event.setCancelled(true);
                }
            } else {
                event.setCancelled(true);
            }
        }
    }

    public int getMaxMinute() {
        return maxMinute;
    }

    @EventHandler
    public void onPlayerShoot(PlayerShootEvent event) {
        Game game = event.getGame();
        Player player = event.getPlayer();
        if(event.getKill() == 2) {
            game.broadcastMessage(ChatColor.RED + "§lDouble kill !");
        } else if(event.getKill() == 3) {
            game.broadcastMessage(ChatColor.RED + "§lTriple kill !");
        } else if(event.getKill() > 3) {
            game.broadcastMessage(ChatColor.RED + "§lMultiple kill !");
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
