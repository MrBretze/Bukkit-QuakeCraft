package fr.bretzel.quake.arena;


import fr.bretzel.quake.Quake;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.LinkedHashMap;

/**
 * Created by MrBretzel on 19/06/2015.
 */

public class SignEvent implements Listener {

    private GameManager manager;

    private LinkedHashMap<Sign, Game> gameLinkedHashMap = new LinkedHashMap<>();

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

                    if(block.getType() == Material.WALL_SIGN || block.getType() == Material.SIGN_POST) {
                        Sign sign = getSignByLocation(block.getLocation());
                        if(sign.hasMetadata("join")) {
                            Game game = getGameLinkedHashMap().get(sign);
                            actualiseSignForGame(game);
                            player.teleport(game.getSpawn());
                        } else if(sign.hasMetadata("quit")) {
                            Game game = getGameLinkedHashMap().get(sign);
                            game.getPlayerList().remove(player.getUniqueId());

                            player.teleport(getManager().getLobby());
                        } else {
                            break;
                        }
                    }
                    break;
            }
        }
    }

    @EventHandler
    public void onSignChangeEvent(SignChangeEvent event) {
        Player player = event.getPlayer();
        String[] lines = event.getLines();
        Block block = event.getBlock();

        if(block.getType() == Material.WALL_SIGN || block.getType() == Material.SIGN_POST) {
            if(lines[0].equals("[quake]")) {
                if(lines[1].equals("join") && getManager().getGameByName(lines[2]) != null) {
                    Game game = getManager().getGameByName(lines[2]);
                    registerSign(game, lines[1], ((Sign)event.getBlock().getState()));
                    event.setLine(0, ChatColor.RED + "" + ChatColor.BOLD + "QuakeCraft");
                    event.setLine(1, ChatColor.AQUA + lines[2]);
                    event.setLine(2, getInfoPlayer(game));
                    event.setLine(3, CLICK_TO_JOIN);
                } else if(lines[1].equals("quit") && getManager().getGameByName(lines[2]) != null) {
                    Game game = getManager().getGameByName(lines[2]);
                    registerSign(game, lines[1], ((Sign)event.getBlock().getState()));
                    event.setLine(0, ChatColor.RED + "" + ChatColor.BOLD + "QuakeCraft");
                    event.setLine(1, ChatColor.AQUA + lines[2]);
                    event.setLine(2, CLICK_TO_QUIT);
                } else {
                    return;
                }
            }
        }
    }

    public void actualiseSignForGame(Game game) {
        for(Sign sign : getGameLinkedHashMap().keySet()) {
            if(getGameLinkedHashMap().get(sign).getName() == game.getName()) {
                sign.setLine(3, getInfoPlayer(game));
                sign.update();
            }
        }
    }

    public void registerSign(Game game, String string, Location location) {
        this.registerSign(game, string, (Sign)location.getBlock().getState());
    }

    public void registerSign(Game game, String string, Sign sign) {
        sign.setMetadata(string, new FixedMetadataValue(Quake.quake, ""));
        game.addSign(sign.getLocation());
        getGameLinkedHashMap().put(sign, game);
    }

    public Sign getSignByLocation(Location location) {
        for(Sign sign : getGameLinkedHashMap().keySet()) {
            if(sign.getLocation() == location) {
                return sign;
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

    public LinkedHashMap<Sign, Game> getGameLinkedHashMap() {
        return gameLinkedHashMap;
    }

    public void setGameLinkedHashMap(LinkedHashMap<Sign, Game> gameLinkedHashMap) {
        this.gameLinkedHashMap = gameLinkedHashMap;
    }

    public GameManager getManager() {
        return manager;
    }

    public void setManager(GameManager manager) {
        this.manager = manager;
    }
}
