package fr.bretzel.quake.arena;

import fr.bretzel.quake.Quake;
import fr.bretzel.quake.player.PlayerInfo;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

import java.util.LinkedList;

/**
 * Created by MrBretzel on 12/06/2015.
 */

public class GameManager implements Listener {

    private LinkedList<Game> gameLinkedList = new LinkedList<>();

    private Quake quake;

    public GameManager(Quake quake) {
        this.quake = quake;

        quake.manager.registerEvents(this, quake);
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
            creator.sendMessage(ChatColor.GREEN + "The game " + name + " has bin create !");
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

        if(player.hasPermission("quake.event.join")) {
            switch (action) {
                case RIGHT_CLICK_BLOCK:
                    Block block = event.getClickedBlock();

                    if(block.getType() == Material.WALL_SIGN || block.getType() == Material.SIGN_POST && block.hasMetadata("game")) {
                        Game game = getGameByName(block.getMetadata("game").get(0).asString());
                        player.teleport(game.getSpawn());
                        game.addPlayer(player);
                    }
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

    @EventHandler
    public void onSignChange(SignChangeEvent event) {
        Player player = event.getPlayer();
        String[] lines = event.getLines();

        if(lines[0].equalsIgnoreCase("[quake]") && getGameByName(lines[1]) != null) {
            event.setLine(0, ChatColor.RED + "QuakeCraft");

            Game game = getGameByName(lines[1]);

            event.getBlock().setMetadata("game", new FixedMetadataValue(Quake.quake, game.getName()));

            game.addSign(event.getBlock().getLocation());

            event.setLine(1, ChatColor.AQUA + game.getName());
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
