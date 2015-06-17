package fr.bretzel.quake.arena;

import fr.bretzel.quake.Quake;
import fr.bretzel.quake.arena.api.IArena;
import fr.bretzel.quake.player.PlayerInfo;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.LinkedList;

/**
 * Created by MrBretzel on 12/06/2015.
 */

public class ArenaManager implements Listener {

    private LinkedList<Arena> arenaLinkedList = new LinkedList<>();

    private Quake quake;

    public ArenaManager(Quake quake) {
        this.quake = quake;

        quake.manager.registerEvents(this, quake);
    }

    public void registerArena(Player creator, String name, Location loc1, Location loc2) {
        if(loc1 == null) {
            creator.sendMessage(ChatColor.RED + "The second is not set !");
            return;
        }
        if (loc2 == null) {
            creator.sendMessage(ChatColor.RED + "The first is not set !");
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

    public IArena getArenaByName(String name) {
        Arena arena = null;
        byte[] bytes = name.getBytes();
        for(Arena a : arenaLinkedList) {
            if(bytes == a.getNameByByte() && a.getName().equals(name)) {
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

    private void rightClick(Player player, PlayerInteractEvent event) {
        PlayerInfo info = Quake.getPlayerInfo(player);
        Location location = event.getClickedBlock().getLocation();
        info.setSecondLocation(location);
        player.sendMessage(ChatColor.GREEN + "The second point has bin set to: " + location.getBlockX() + " " + location.getBlockY() + " " + location.getBlockZ());
        event.setCancelled(true);
    }

    private void leftClick(Player player, PlayerInteractEvent event) {
        PlayerInfo info = Quake.getPlayerInfo(player);
        Location location = event.getClickedBlock().getLocation();
        info.setFirstLocation(location);
        player.sendMessage(ChatColor.GREEN + "The first point has bin set to: " + location.getBlockX() + " " + location.getBlockY() + " " + location.getBlockZ());
        event.setCancelled(true);
    }
}
