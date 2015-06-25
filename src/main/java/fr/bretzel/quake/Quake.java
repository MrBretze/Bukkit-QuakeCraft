package fr.bretzel.quake;

import com.evilco.mc.nbt.TagCompound;
import com.evilco.mc.nbt.stream.NbtInputStream;

import fr.bretzel.quake.game.Game;
import fr.bretzel.quake.game.GameManager;
import fr.bretzel.quake.reader.SignReader;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

/**
 * Created by MrBretzel on 09/06/2015.
 */

public class Quake extends JavaPlugin implements Listener {

    public static PluginManager manager;
    public static GameManager gameManager;
    public static Quake quake;
    private static LinkedList<PlayerInfo> playerInfos = new LinkedList<>();

    @Override
    public void onEnable() {
        quake = this;

        getDataFolder().mkdir();

        manager = getServer().getPluginManager();

        gameManager = new GameManager(this);

        getCommand("quake").setExecutor(new fr.bretzel.quake.command.Command());
        getCommand("quake").setTabCompleter(new fr.bretzel.quake.command.Command());

        File file = new File(getDataFolder(), File.separator + "game" + File.separator);

        if(file.exists() && file.isDirectory()) {
            if(file.listFiles().length > 0) {
                initGame(file);
            }
        }

        saveResource("config.yml", false);

        reloadConfig();
    }

    @Override
    public void onDisable() {
        saveConfig();

        for(PlayerInfo playerInfo : playerInfos) {
            playerInfo.save();
        }

        for(Game game : gameManager.getGameLinkedList()) {
            game.save();
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(label.equals("test")) {
            final Player player = (Player) sender;
            player.setHealth(0);
            BukkitRunnable runnable = new BukkitRunnable() {
                @Override
                public void run() {
                    Util.respawn(player);
                }
            };
            runnable.runTaskLater(Quake.quake, 20L);
            return true;
        }
        return true;
    }

    public static PlayerInfo getPlayerInfo(Player player) {
        for(PlayerInfo pi : playerInfos) {
            if(pi.getPlayer() == player) {
                return pi;
            }
        }
        PlayerInfo info = new PlayerInfo(player);
        return info;
    }

    private void initGame(File file) {
        for (File f : file.listFiles()) {
            try {
                Game game = new Game(this);

                String name = f.getName().replace(".dat", "");
                game.setName(name);

                game.setFile(f);

                NbtInputStream stream = new NbtInputStream(new FileInputStream(f));

                TagCompound compound = (TagCompound) stream.readTag();

                game.setCompound(compound);

                game.setFirstLocation(Util.toLocationString(compound.getString("location1")));

                game.setSecondLocation(Util.toLocationString(compound.getString("location2")));

                game.setSpawn(Util.toLocationString(compound.getString("spawn")));

                if (compound.getTag("respawn") != null) {
                    TagCompound respawn = compound.getCompound("respawn");
                    int u = respawn.getInteger("size");

                    for (int i = 0; i < u; i++) {
                        Location location = Util.toLocationString(respawn.getString(String.valueOf(i)));
                        game.addRespawn(location);
                    }
                }

                if (compound.getTag("players") != null) {
                    TagCompound players = compound.getCompound("players");
                    int u = players.getInteger("size");

                    for (int i = 0; i < u; i++) {
                        UUID uuid = UUID.fromString(players.getString(String.valueOf(i)));
                        game.addPlayer(uuid);
                    }
                }

                if (compound.getTag("signs") != null) {
                    TagCompound signs = compound.getCompound("signs");
                    int u = signs.getInteger("size");
                    for (int i = 0; i < u; i++) {
                        Sign sign = SignReader.read(signs.getCompound(String.valueOf(i)));
                        game.addSign(sign);
                    }
                }

                for (Block b : Util.blocksFromTwoPoints(game.getFirstLocation(), game.getSecondLocation())) {
                    game.addBlock(b);
                }

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
