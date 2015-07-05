/**
 * Copyright 2015 Lo�c Nussbaumer
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you
 * may not use this file except in compliance with the License. You
 * may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License. See accompanying
 * LICENSE file.
 */
package fr.bretzel.quake.game;


import fr.bretzel.quake.Quake;
import fr.bretzel.quake.game.event.PlayerJoinGameEvent;
import fr.bretzel.quake.game.event.PlayerLeaveGameEvent;
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

/**
 * Created by MrBretzel on 19/06/2015.
 */

public class SignEvent implements Listener {

    public static String CLICK_TO_QUIT = ChatColor.RED + "Click to quit !";
    public static String CLICK_TO_JOIN = ChatColor.GREEN + "Click to join !";
    private GameManager manager;

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

                    if(block.getType() == Material.WALL_SIGN || block.getType() == Material.SIGN_POST && block != null && block.hasMetadata("join") && block.hasMetadata("game") && block.hasMetadata("name")) {
                        Sign sign = getSignByLocation(block.getLocation());
                        boolean isJoin = sign.getMetadata("join").get(0).asBoolean();
                        Game game = getManager().getGameByName(sign.getMetadata("game").get(0).asString());
                        if(isJoin) {
                            PlayerJoinGameEvent e = new PlayerJoinGameEvent(player, game);
                            Bukkit.getPluginManager().callEvent(e);
                            if(e.isCancelled()) {
                                return;
                            }
                            player.teleport(game.getSpawn());
                            game.addPlayer(player);
                            actualiseJoinSignForGame(game);
                            game.getScoreboardManager().getObjective().getScore(getInfoPlayer(game)).setScore(7);
                            break;
                        } else if(!isJoin) {
                            Bukkit.getPluginManager().callEvent(new PlayerLeaveGameEvent(player, game));
                            if (event.isCancelled()) {
                                return;
                            }
                            player.teleport(getManager().getLobby());
                            game.getPlayerList().remove(player.getUniqueId());
                            actualiseJoinSignForGame(game);
                            player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
                            break;
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
                if (lines[1].equals("join") && getManager().getGameByName(lines[2]) != null) {
                    Game game = getManager().getGameByName(lines[2]);
                    sign.setMetadata("join", new FixedMetadataValue(Quake.quake, true));
                    sign.setMetadata("game", new FixedMetadataValue(Quake.quake, game.getName()));
                    event.setLine(0, CLICK_TO_JOIN);
                    event.setLine(1, ChatColor.BLUE + game.getDisplayName());
                    event.setLine(2, getInfoPlayer(game));
                    event.setLine(3, game.getState().getName());
                    game.addSign(sign);
                    return;
                } else if (lines[1].equals("quit") && getManager().getGameByName(lines[2]) != null) {
                    Game game = getManager().getGameByName(lines[2]);
                    sign.setMetadata("join", new FixedMetadataValue(Quake.quake, false));
                    sign.setMetadata("game", new FixedMetadataValue(Quake.quake, game.getName()));
                    event.setLine(0, CLICK_TO_QUIT);
                    event.setLine(1, ChatColor.BLUE + game.getDisplayName());
                    event.setLine(2, "");
                    event.setLine(3, "");
                    game.addSign(sign);
                    return;
                } else {
                    return;
                }
            }
        }
    }

    public void actualiseJoinSignForGame(Game game) {
        for(Sign sign : game.getSignList()) {
            if(sign.getMetadata("join").get(0).asBoolean()) {
                sign.setLine(0, CLICK_TO_JOIN);
                sign.setLine(1, ChatColor.BLUE + game.getDisplayName());
                sign.setLine(2, getInfoPlayer(game));
                sign.setLine(3, game.getState().getName());
                sign.update();
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
        return (ChatColor.BLUE + "" + game.getPlayerList().size()) + ChatColor.DARK_GRAY + "/" + ChatColor.BLUE + "" + game.getMaxPlayer();
    }

    public GameManager getManager() {
        return manager;
    }

    public void setManager(GameManager manager) {
        this.manager = manager;
    }
}