package fr.bretzel.quake.arena;

import fr.bretzel.quake.Quake;
import fr.bretzel.quake.Util;
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
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

import java.util.LinkedList;

/**
 * Created by MrBretzel on 12/06/2015.
 */

public class GameManager implements Listener {

    private LinkedList<Game> gameLinkedList = new LinkedList<>();
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

    public void registerArena(Player creator, String name, Location loc1, Location loc2) {
        if(loc1 == null) {
            creator.sendMessage(ChatColor.RED + "The first is not set !");
            return;
        }
        if (loc2 == null) {
            creator.sendMessage(ChatColor.RED + "The second is not set !");
            return;
        }
        if(containsGame(name)) {
            creator.sendMessage(ChatColor.RED + "The arena is already exist !");
            return;
        } else {
            Game game = new Game(loc1, loc2, name);
            gameLinkedList.add(game);
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
        return lobby;
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

    public Game getArenaByPlayer(Player player) {
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

        if(player.hasPermission("quake.event.select") && player.getItemInHand() != null && player.getItemInHand().getType() == Material.GOLD_HOE) {
            switch (action) {
                case LEFT_CLICK_BLOCK:
                    leftClick(player, event);
                    break;
                case RIGHT_CLICK_BLOCK:
                    rightClick(player, event);
                    break;
            }
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if(getArenaByPlayer(player) != null) {
            Game game = getArenaByPlayer(player);
            if(!game.getBlocks().contains(event.getTo().getBlock())) {
                Vector vector = player.getEyeLocation().getDirection().normalize().multiply(-0.3);
                vector.setY(0);
                player.teleport(event.getFrom().add(vector));
            }
        }
    }

    public void setGameLinkedList(LinkedList<Game> gameLinkedList) {
        this.gameLinkedList = gameLinkedList;
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
