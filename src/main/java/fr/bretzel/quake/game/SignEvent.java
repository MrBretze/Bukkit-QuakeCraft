package fr.bretzel.quake.game;


import fr.bretzel.quake.PlayerInfo;
import fr.bretzel.quake.Quake;
import fr.bretzel.quake.game.event.PlayerJoinGameEvent;
import fr.bretzel.quake.game.event.PlayerLeaveGameEvent;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class SignEvent implements Listener {

    public static String CLICK_TO_QUIT = ChatColor.RED + "Click to quit !";
    public static String CLICK_TO_JOIN = ChatColor.GREEN + "Click to join !";

    public String lastPlayerInGame = "lastInGame";
    private GameManager manager;

    public SignEvent(GameManager gameManager) {
        setManager(gameManager);

        Quake.quake.getServer().getPluginManager().registerEvents(this, Quake.quake);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Action action = event.getAction();
        Player player = event.getPlayer();
        switch (action) {
            case RIGHT_CLICK_BLOCK:
                Block block = event.getClickedBlock();
                if (block.getType() == Material.WALL_SIGN || block.getType() == Material.SIGN_POST && block instanceof org.bukkit.block.Sign && block != null) {
                    if (getSignByLocation(block.getLocation()) != null) {
                        Sign sign = getSignByLocation(block.getLocation());
                        boolean isJoin = sign.isJoin();
                        Game game = sign.getGame();
                        if (isJoin) {
                            PlayerJoinGameEvent e = new PlayerJoinGameEvent(player, game);
                            Bukkit.getPluginManager().callEvent(e);
                            if (e.isCancelled()) {
                                return;
                            }
                            player.teleport(game.getSpawn());
                            game.addPlayer(player);
                            actualiseJoinSignForGame(game);
                            if (this.lastPlayerInGame.equalsIgnoreCase("lastInGame")) {
                                this.lastPlayerInGame = getInfoPlayer(game);
                                game.getScoreboardManager().getObjective().getScore(lastPlayerInGame).setScore(7);
                                return;
                            } else {
                                game.getScoreboardManager().getScoreboard().resetScores(lastPlayerInGame);
                                this.lastPlayerInGame = getInfoPlayer(game);
                                game.getScoreboardManager().getObjective().getScore(lastPlayerInGame).setScore(7);
                                return;
                            }
                        } else {
                            PlayerLeaveGameEvent e = new PlayerLeaveGameEvent(player, game);
                            Bukkit.getPluginManager().callEvent(e);
                            if(e.isCancelled()) {
                                return;
                            }
                            player.teleport(getManager().getLobby());
                            game.getPlayerList().remove(PlayerInfo.getPlayerInfo(player));
                            actualiseJoinSignForGame(game);
                            return;
                        }
                    }
                    return;
                }
                return;
        }
    }

    @EventHandler
    public void onBlockBkreakEvent(BlockBreakEvent event) {
        Block block = event.getBlock();
        Player player = event.getPlayer();
        if (block.getType() == Material.WALL_SIGN || block.getType() == Material.SIGN_POST) {
            if(getSignByLocation(block.getLocation()) != null) {
                if(player.hasPermission("quake.event.sign.break")) {
                    Sign sign = getSignByLocation(block.getLocation());
                    Game game = getGameBySign(sign);
                    if (player.isSneaking()) {
                        player.sendMessage(ChatColor.GREEN.toString() + "Remove sign for " + game.getName());
                        game.getSignList().remove(sign);
                    } else {
                        player.sendMessage(ChatColor.RED.toString() + "To break this sign please sneak !");
                        event.setCancelled(true);
                    }
                } else {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onSignChangeEvent(SignChangeEvent event) {
        String[] lines = event.getLines();
        final Block block = event.getBlock();
        if (block.getType() == Material.WALL_SIGN || block.getType() == Material.SIGN_POST) {
            if(getSignByLocation(block.getLocation()) != null) {
                Sign sign = getSignByLocation(block.getLocation());
                Game game = getGameBySign(sign);
                event.setLine(0, CLICK_TO_JOIN);
                event.setLine(1, ChatColor.BLUE + game.getDisplayName());
                event.setLine(2, getInfoPlayer(game));
                event.setLine(3, game.getState().getName());
                return;
            }
            if (lines[0].equals("[quake]")) {
                if (lines[1].equals("join") && getManager().getGameByName(lines[2]) != null) {
                    Game game = getManager().getGameByName(lines[2]);
                    event.setLine(0, CLICK_TO_JOIN);
                    event.setLine(1, ChatColor.BLUE + game.getDisplayName());
                    event.setLine(2, getInfoPlayer(game));
                    event.setLine(3, game.getState().getName());
                    game.addSign(block.getLocation(), game);
                    return;
                } else if (lines[1].equals("quit") && getManager().getGameByName(lines[2]) != null) {
                    Game game = getManager().getGameByName(lines[2]);
                    event.setLine(0, CLICK_TO_QUIT);
                    event.setLine(1, ChatColor.BLUE + game.getDisplayName());
                    event.setLine(2, "");
                    event.setLine(3, "");
                    game.addSign(block.getLocation(), null);
                    return;
                } else {
                    return;
                }
            }
        }
    }

    public void actualiseJoinSignForGame(Game game) {
        for (Sign sign : game.getSignList()) {
            if (sign != null) {
                if (!sign.getLocation().getChunk().isLoaded()) {
                    sign.getLocation().getChunk().load();
                }
                if (sign.isJoin()) {
                    org.bukkit.block.Sign s = (org.bukkit.block.Sign) sign.getLocation().getBlock();
                    s.setLine(0, CLICK_TO_JOIN);
                    s.setLine(1, ChatColor.BLUE + game.getDisplayName());
                    s.setLine(2, getInfoPlayer(game));
                    s.setLine(3, game.getState().getName());
                    s.update();
                }
            }
        }
    }

    public Sign getSignByLocation(Location location) {
        for (Game game : getManager().getGameLinkedList()) {
            for (Sign sign : game.getSignList()) {
                if (sign != null) {
                    if (sign.getLocation().getWorld() == location.getWorld() &&
                            sign.getLocation().getBlockX() == location.getBlockX() &&
                            sign.getLocation().getBlockY() == location.getBlockY() &&
                            sign.getLocation().getBlockZ() == location.getBlockZ()) {
                        return sign;
                    }
                }
            }
        }
        return null;
    }

    public Game getGameBySign(Sign sign) {
        return sign.getGame();
    }

    public String getInfoPlayer(Game game) {
        return (ChatColor.BLUE.toString() + game.getPlayerList().size()) + ChatColor.DARK_GRAY + "/" + ChatColor.BLUE + "" + game.getMaxPlayer();
    }

    public GameManager getManager() {
        return manager;
    }

    public void setManager(GameManager manager) {
        this.manager = manager;
    }
}
