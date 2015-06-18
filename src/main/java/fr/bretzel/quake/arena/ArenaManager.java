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

import java.util.LinkedList;

/**
 * Created by MrBretzel on 12/06/2015.
 */

public class ArenaManager implements Listener {

    private LinkedList<Arena> arenaLinkedList = new LinkedList<>();

    private LinkedList<Block> signLinkedList = new LinkedList<>();

    private Quake quake;

    public ArenaManager(Quake quake) {
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
        if(containsArena(name)) {
            creator.sendMessage(ChatColor.RED + "The arena is already exist !");
            return;
        } else {
            Arena arena = new Arena(loc1, loc2, name);
            arenaLinkedList.add(arena);
            creator.sendMessage(ChatColor.GREEN + "The arena " + name + " has bin create !");
        }
    }

    public Arena getArenaByName(String name) {
        Arena arena = null;
        for(Arena a : arenaLinkedList) {
            if(a.getName().equals(name)) {
                arena = a;
            }
        }
        return arena;
    }

    public boolean containsArena(Arena arena) {
        return arenaLinkedList.contains(arena);
    }

    public boolean containsArena(String arena) {
        return arenaLinkedList.contains(getArenaByName(arena));
    }

    public Quake getQuake() {
        return quake;
    }

    public LinkedList<Arena> getArenaLinkedList() {
        return arenaLinkedList;
    }

    public Arena getArenaByPlayer(Player player) {
        for(Arena a : getArenaLinkedList()) {
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

                    if(block.getType() == Material.WALL_SIGN || block.getType() == Material.SIGN_POST) {
                        BlockState blockState = block.getState();
                        Sign sign = (Sign) blockState;
                        if(getSignLinkedList().contains(block)) {
                            Arena arena = getArenaByName(sign.getLine(1));
                            player.teleport(arena.getSpawn());
                        }
                    }
                    break;
            }
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        if(getArenaByPlayer(player) != null) {
            Arena arena = getArenaByPlayer(player);
            for(Block block : arena.getBlocks()) {
                if(event.getTo().getBlockZ() != block.getLocation().getBlockZ() && event.getTo().getBlockY() != block.getLocation().getBlockY() && event.getTo().getBlockX() != block.getLocation().getBlockX()) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onSignChange(SignChangeEvent event) {
        Player player = event.getPlayer();
        String[] lines = event.getLines();

        if(lines[0].equalsIgnoreCase("[quake]") && getArenaByName(lines[1]) != null) {
            event.setLine(0, ChatColor.RED + "QuakeCraft");

            Arena arena = getArenaByName(lines[1]);

            event.setLine(1, arena.getName());

            getSignLinkedList().add(event.getBlock());
        }
    }

    public LinkedList<Block> getSignLinkedList() {
        return signLinkedList;
    }

    public void setArenaLinkedList(LinkedList<Arena> arenaLinkedList) {
        this.arenaLinkedList = arenaLinkedList;
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
