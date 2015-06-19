package fr.bretzel.quake.arena;


import fr.bretzel.quake.Quake;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.UUID;

/**
 * Created by MrBretzel on 19/06/2015.
 */

public class SignEvent implements Listener {

    private GameManager manager;

    public static String CLICK_TO_QUIT = ChatColor.RED + "Click to quit !";
    public static String CLICK_TO_JOIN = ChatColor.GREEN + "Click to join !";

    public SignEvent(GameManager gameManager) {
        setManager(gameManager);

        getManager().getQuake().getServer().getPluginManager().registerEvents(this, Quake.quake);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Action action = event.getAction();
        Player player = event.getPlayer();

        if(player.hasPermission("quake.event.join")) {
            switch (action) {
                case RIGHT_CLICK_BLOCK:
                    Block block = event.getClickedBlock();

                    if(block.getType() == Material.WALL_SIGN || block.getType() == Material.SIGN_POST && block != null && block.hasMetadata("join") && block.hasMetadata("game")) {
                        Sign sign = getSignByLocation(block.getLocation());
                        boolean isJoin = sign.getMetadata("join").get(0).asBoolean();
                        Game game = getManager().getGameByName(sign.getMetadata("game").get(0).asString());
                        if(isJoin) {
                            player.teleport(game.getSpawn());
                            game.addPlayer(player);
                            for(UUID uuid : game.getPlayerList()) {
                                Player p = Bukkit.getPlayer(uuid);
                                if(p.isOnline()) {
                                    p.sendMessage(ChatColor.AQUA + p.getDisplayName() + ChatColor.YELLOW + " has joined (" + ChatColor.AQUA + game.getPlayerList().size() +
                                            ChatColor.YELLOW + "/" + ChatColor.AQUA + game.getMaxPlayer() + ChatColor.YELLOW + ")");
                                }
                            }
                            actualiseJoinSignForGame(game);
                        } else if(!isJoin) {
                            player.teleport(getManager().getLobby());
                            game.getPlayerList().remove(player.getUniqueId());
                            actualiseJoinSignForGame(game);
                        } else {
                            break;
                        }
                    }
                    break;
            }
        }
    }

    @EventHandler
    public void onBlockBkreakEvent(BlockBreakEvent event) {
        Block block = event.getBlock();
        Player player = event.getPlayer();
        if(block.getType() == Material.WALL_SIGN || block.getType() == Material.SIGN_POST) {
            Sign sign = getSignByLocation(block.getLocation());
            if(sign == null) {
                return;
            }
            Game game = getManager().getGameByName(sign.getMetadata("game").get(0).asString());
            game.removeSign(sign);
        }
    }

    @EventHandler
    public void onSignChangeEvent(SignChangeEvent event) {
        String[] lines = event.getLines();
        Block block = event.getBlock();

        if(block.getType() == Material.WALL_SIGN || block.getType() == Material.SIGN_POST) {
            if(lines[0].equals("[quake]")) {
                Sign sign = (Sign) block.getState();
                if(lines[1].equals("join") && getManager().getGameByName(lines[2]) != null) {
                    Game game = getManager().getGameByName(lines[2]);
                    sign.setMetadata("join", new FixedMetadataValue(Quake.quake, true));
                    sign.setMetadata("game", new FixedMetadataValue(Quake.quake, game.getName()));
                    event.setLine(0, ChatColor.RED + "" + ChatColor.BOLD + "QuakeCraft");
                    event.setLine(1, ChatColor.AQUA + lines[2]);
                    event.setLine(2, getInfoPlayer(game));
                    event.setLine(3, CLICK_TO_JOIN);
                    game.addSign(sign);
                } else if(lines[1].equals("quit") && getManager().getGameByName(lines[2]) != null) {
                    Game game = getManager().getGameByName(lines[2]);
                    sign.setMetadata("join", new FixedMetadataValue(Quake.quake, false));
                    sign.setMetadata("game", new FixedMetadataValue(Quake.quake, game.getName()));
                    event.setLine(0, ChatColor.RED + "" + ChatColor.BOLD + "QuakeCraft");
                    event.setLine(1, ChatColor.AQUA + lines[2]);
                    event.setLine(2, CLICK_TO_QUIT);
                    game.addSign(sign);
                } else {
                    return;
                }
            }
        }
    }

    public void actualiseJoinSignForGame(Game game) {
        for(Sign sign : game.getSignList()) {
            if(sign.getMetadata("join").get(0).asBoolean()) {
                sign.setLine(0, ChatColor.RED + "" + ChatColor.BOLD + "QuakeCraft");
                sign.setLine(1, ChatColor.AQUA + game.getName());
                sign.setLine(2, getInfoPlayer(game));
                sign.setLine(3, CLICK_TO_JOIN);
            }
        }
    }

    public Sign getSignByLocation(Location location) {
        for(Game game : getManager().getGameLinkedList()) {
            for(Sign sign : game.getSignList()) {
                if(sign.getLocation().getWorld() == location.getWorld() &&
                        sign.getLocation().getBlockX() == location.getBlockX() &&
                        sign.getLocation().getBlockY() == location.getBlockY() &&
                        sign.getLocation().getBlockZ() == location.getBlockZ()) {
                    return sign;
                }
            }
        }
        return null;
    }

    public String getInfoPlayer(Game game) {
        StringBuilder builder = new StringBuilder();
        builder.append(ChatColor.AQUA + "" + game.getPlayerList().size())
                .append(ChatColor.WHITE + "/")
                .append(ChatColor.AQUA + "" + game.getMaxPlayer());
        return builder.toString();
    }

    public GameManager getManager() {
        return manager;
    }

    public void setManager(GameManager manager) {
        this.manager = manager;
    }
}
