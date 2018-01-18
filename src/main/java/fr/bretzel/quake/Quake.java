package fr.bretzel.quake;

import fr.bretzel.quake.command.LobbyCommand;
import fr.bretzel.quake.command.QuakeCommand;
import fr.bretzel.quake.config.Config;
import fr.bretzel.quake.game.State;
import fr.bretzel.quake.hologram.HologramManager;
import fr.bretzel.nbt.NBTCompressedStreamTools;
import fr.bretzel.quake.game.Game;
import fr.bretzel.quake.game.GameManager;
import fr.bretzel.quake.language.Language;
import fr.bretzel.quake.reader.GameReader;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Objects;


public class Quake extends JavaPlugin {

    public static Config config;

    public static PluginManager manager;
    public static GameManager gameManager;
    public static Quake quake;
    public static HologramManager holoManager;

    @Override
    public ChunkGenerator getDefaultWorldGenerator(String worldName, String id) {
        return super.getDefaultWorldGenerator(worldName, id);
    }

    public static void logInfo(String msg) {
        Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_GRAY + "[INFO]: " + msg);
    }

    @Override
    public void onEnable() {
        quake = this;

        logInfo("Registering Lang");
        try {
            Language.enable();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Language.Locale locale = Language.Locale.fromString(getConfig().getString("Language") + "_" + getConfig().getString("Region"));

        Language.defaultLanguage = Language.getLanguage(locale);

        logInfo("End");



        if (!new File(getDataFolder(), "config.yml").exists()) {
            saveResource("config.yml", false);
        }

        reloadConfig();

        logInfo("Starting initialing Holomanager");
        holoManager = new HologramManager(this);
        logInfo("End initialing Holomanager");


        getDataFolder().mkdir();
        manager = getServer().getPluginManager();


        logInfo("Starting initialing Gamemanager");
        gameManager = new GameManager();
        logInfo("End initialing Gamemanager");


        logInfo("Registering QuakeCommand");
        getCommand("quake").setExecutor(new QuakeCommand());
        getCommand("lobby").setExecutor(new LobbyCommand());
        logInfo("End");


        logInfo("Registering Game");
        File file = new File(getDataFolder(), File.separator + "game" + File.separator);
        if (file.exists() && file.isDirectory()) {
            if (Objects.requireNonNull(file.listFiles()).length > 0) {
                initGame(file);
            }
        }
        logInfo("End.");

        logInfo("Start loading database.");
        boolean isMySQLconf = getConfig().getBoolean("mysql.Enabled");

        if (isMySQLconf) {
            config = new Config(getConfig().getString("mysql.Hotsname"),
                    getConfig().getString("mysql.Username"),
                    getConfig().getString("mysql.Password"),
                    getConfig().getString("mysql.Port"),
                    getConfig().getString("mysql.Database")) {
            };
        } else {
            File f = new File(getDataFolder(), "database.db");
            if (!f.exists()) {
                try {
                    f.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            config = new Config(f);
        }
        logInfo("Successfully.");
    }

    @Override
    public void onDisable() {
        for (PlayerInfo playerInfo : PlayerInfo.getAllPlayerInfo()) {
            playerInfo.save();
        }

        for (Game game : gameManager.getGameLinkedList()) {
            if (game.getState() == State.STARTED) {
                game.stop();
            }
            game.save();
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (label.equalsIgnoreCase("test")) {
            if (sender instanceof Player) {
                final Player player = (Player) sender;
                Game game = gameManager.getGameByPlayer(player);
                for (PlayerInfo info : game.getPlayerList()) {
                    logInfo("Info in game: " + info);
                }

                logInfo("Your info is: " + PlayerInfo.getPlayerInfo(player));
                return true;
            } else return true;
        }
        if (label.equalsIgnoreCase("sqlSet")) {
            if (sender instanceof Player) {
                config.setString(args[0], "Test", "", Config.Table.TEST);
                return true;
            } else return true;
        }
        if (label.equalsIgnoreCase("sqlGet")) {
            if (sender instanceof Player) {
                if (config.ifStringExist(args[0], "Test", Config.Table.TEST)) {
                    String str = config.getString("Test", "WHERE Test = '" + args[0] + "'", Config.Table.TEST);
                    sender.sendMessage("Le message % est dans la BDD".replace("%", str));
                    return true;
                } else {
                    sender.sendMessage("Ce message n'est pas enregistré dans la BDD");
                }
            } else return true;
        }
        return true;
    }

    private void initGame(File file) {
        for (File f : file.listFiles()) {
            try {
                Game game = GameReader.read(NBTCompressedStreamTools.read(new FileInputStream(f)), f);
                logInfo("Initialing game: " + game.getDisplayName());
                gameManager.getGameLinkedList().add(game);
                gameManager.signEvent.actualiseJoinSignForGame(game);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
