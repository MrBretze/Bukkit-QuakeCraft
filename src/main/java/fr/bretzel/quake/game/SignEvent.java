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

/**
 * Created by MrBretzel on 19/06/2015.
 */

public class SignEvent implements Listener
{

    public static String CLICK_TO_QUIT = ChatColor.RED + "Click to quit !";
    public static String CLICK_TO_JOIN = ChatColor.GREEN + "Click to join !";
    public String lastPlayerInGame = "lastInGame";
    private GameManager manager;

    public SignEvent(GameManager gameManager)
    {
        setManager(gameManager);

        Quake.quake.getServer().getPluginManager().registerEvents(this, Quake.quake);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event)
    {
        Action action = event.getAction();
        Player player = event.getPlayer();
        Block block = event.getClickedBlock();
        if (action == Action.RIGHT_CLICK_BLOCK && block != null && (block.getType() == Material.OAK_WALL_SIGN || block.getType() == Material.OAK_SIGN) && getSignByLocation(block.getLocation()) != null)
        {
            Sign sign = getSignByLocation(block.getLocation());
            Game game = getGameBySign(sign);
            if (!sign.getLine(3).isEmpty() && game != null)
            {
                PlayerJoinGameEvent e = new PlayerJoinGameEvent(player, game);
                Bukkit.getPluginManager().callEvent(e);
                if (e.isCancelled())
                {
                    return;
                }
                player.teleport(game.getSpawn());
                game.addPlayer(player);
                updateSign(game);
                if (this.lastPlayerInGame.equalsIgnoreCase("lastInGame"))
                {
                    this.lastPlayerInGame = getInfoPlayer(game);
                    game.getScoreboardManager().getObjective().getScore(lastPlayerInGame).setScore(7);
                } else
                {
                    game.getScoreboardManager().getScoreboard().resetScores(lastPlayerInGame);
                    this.lastPlayerInGame = getInfoPlayer(game);
                    game.getScoreboardManager().getObjective().getScore(lastPlayerInGame).setScore(7);
                }
            } else
            {
                PlayerLeaveGameEvent e = new PlayerLeaveGameEvent(player, game);
                Bukkit.getPluginManager().callEvent(e);
                if (e.isCancelled())
                {
                    return;
                }

                player.teleport(getManager().getLobby());

                if (game != null && game.getPlayerList().contains(player.getUniqueId()))
                {
                    game.getPlayerList().remove(player.getUniqueId());
                    updateSign(game);
                }
            }
        }
    }

    @EventHandler
    public void onBlockBkreakEvent(BlockBreakEvent event)
    {
        Block block = event.getBlock();
        Player player = event.getPlayer();
        if (block.getType() == Material.OAK_WALL_SIGN || block.getType() == Material.OAK_SIGN)
        {
            if (getSignByLocation(block.getLocation()) != null)
            {
                if (player.hasPermission("quake.event.sign.break"))
                {
                    Sign sign = getSignByLocation(block.getLocation());
                    Game game = getGameBySign(sign);
                    if (player.isSneaking())
                    {
                        player.sendMessage(ChatColor.GREEN.toString() + "Remove sign for " + game.getName());
                        game.getSignList().remove(sign);
                    } else
                    {
                        player.sendMessage(ChatColor.RED.toString() + "To break this sign please sneak !");
                        event.setCancelled(true);
                    }
                } else
                {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onSignChangeEvent(SignChangeEvent event)
    {
        String[] lines = event.getLines();
        final Block block = event.getBlock();
        if (block.getType() == Material.OAK_WALL_SIGN || block.getType() == Material.OAK_SIGN)
        {
            if (getSignByLocation(block.getLocation()) != null)
            {
                Sign sign = getSignByLocation(block.getLocation());
                Game game = getGameBySign(sign);
                event.setLine(0, CLICK_TO_JOIN);
                event.setLine(1, game.getDisplayName());
                event.setLine(2, getInfoPlayer(game));
                event.setLine(3, game.getState().getName());
                return;
            }
            if (lines[0].equals("[quake]"))
            {
                Sign sign = (Sign) block.getState();
                if (lines[1].equals("join") && getManager().getGameByName(lines[2]) != null)
                {
                    Game game = getManager().getGameByName(lines[2]);
                    event.setLine(0, CLICK_TO_JOIN);
                    event.setLine(1, game.getDisplayName());
                    event.setLine(2, getInfoPlayer(game));
                    event.setLine(3, game.getState().getName());
                    game.addSign(sign);
                } else if (lines[1].equals("quit") && getManager().getGameByName(lines[2]) != null)
                {
                    Game game = getManager().getGameByName(lines[2]);
                    event.setLine(0, CLICK_TO_QUIT);
                    event.setLine(1, game.getDisplayName());
                    event.setLine(2, "");
                    event.setLine(3, "");
                    game.addSign(sign);
                }
            }
        }
    }

    public void updateSign(Game game)
    {
        for (Sign sign : game.getSignList())
        {
            if (sign != null && sign.getChunk().isLoaded())
            {
                if (!sign.getLocation().getChunk().isLoaded())
                {
                    sign.getLocation().getChunk().load();
                }

                if (sign.getLine(3).isEmpty())
                {
                    sign.setLine(1, game.getDisplayName());
                    return;
                }

                sign.setLine(0, CLICK_TO_JOIN);
                sign.setLine(1, game.getDisplayName());
                sign.setLine(2, getInfoPlayer(game));
                sign.setLine(3, game.getState().getName());
                sign.update();
            }
        }
    }

    public Sign getSignByLocation(Location location)
    {
        for (Game game : getManager().getGameLinkedList())
        {
            for (Sign sign : game.getSignList())
            {
                if (sign != null)
                {
                    if (sign.getLocation().getWorld() == location.getWorld() &&
                            sign.getLocation().getBlockX() == location.getBlockX() &&
                            sign.getLocation().getBlockY() == location.getBlockY() &&
                            sign.getLocation().getBlockZ() == location.getBlockZ())
                    {
                        return sign;
                    }
                }
            }
        }
        return null;
    }

    public Game getGameBySign(Sign location)
    {
        for (Game game : getManager().getGameLinkedList())
        {
            for (Sign sign : game.getSignList())
            {
                if (sign.getLocation().getWorld() == location.getWorld() &&
                        sign.getLocation().getBlockX() == location.getX() &&
                        sign.getLocation().getBlockY() == location.getY() &&
                        sign.getLocation().getBlockZ() == location.getZ())
                {
                    return game;
                }
            }
        }
        return null;
    }

    public String getInfoPlayer(Game game)
    {
        return (ChatColor.BLUE.toString() + game.getPlayerList().size()) + ChatColor.DARK_GRAY + "/" + ChatColor.BLUE + "" + game.getMaxPlayer();
    }

    public GameManager getManager()
    {
        return manager;
    }

    public void setManager(GameManager manager)
    {
        this.manager = manager;
    }
}
