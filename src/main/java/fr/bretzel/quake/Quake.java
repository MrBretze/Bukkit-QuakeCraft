package fr.bretzel.quake;

import fr.bretzel.quake.command.LobbyCommand;
import fr.bretzel.quake.command.QuakeCommand;
import fr.bretzel.quake.config.Config;
import fr.bretzel.quake.game.State;
import fr.bretzel.quake.hologram.HologramManager;
import fr.bretzel.quake.game.Game;
import fr.bretzel.quake.game.GameManager;
import fr.bretzel.quake.language.Language;
import fr.bretzel.quake.listener.CommandListener;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class Quake extends JavaPlugin {

    public static Config database;

    public static boolean isReload = false;

    public static GameManager gameManager;
    public static Quake quake;
    public static HologramManager holoManager;

    public static void logInfo(String msg) {
        Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_GRAY + "[INFO]: " + msg);
    }

    @Override
    public void onEnable() {
        quake = this;

        Bukkit.getPluginManager().registerEvents(new CommandListener(), this);

        if (!isReload) {
            logInfo("Registering Lang");

            try {
                Language.enable();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Language.Locale locale = Language.Locale.fromString(getConfig().getString("Language") + "_" + getConfig().getString("Region"));

            Language.defaultLanguage = Language.getLanguage(locale);

            logInfo("End");
        }

        if (!new File(getDataFolder(), "config.yml").exists()) {
            saveResource("config.yml", false);
        }

        reloadConfig();

        logInfo("Starting initialing Holomanager");

        holoManager = new HologramManager(this);

        logInfo("End initialing Holomanager");


        getDataFolder().mkdir();


        logInfo("Starting initialing Gamemanager");

        gameManager = new GameManager();

        logInfo("End initialing Gamemanager");


        logInfo("Registering QuakeCommand");

        getCommand("quake").setExecutor(new QuakeCommand());
        getCommand("lobby").setExecutor(new LobbyCommand());

        logInfo("End");

        logInfo("Start loading database.");
        boolean isMySQLconf = getConfig().getBoolean("mysql.Enabled");

        if (isMySQLconf) {
            database = new Config(getConfig().getString("mysql.Hotsname"),
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
            database = new Config(f);
        }

        logInfo("Registering Game");

        loadGames();

        logInfo("End.");


        logInfo("Successfully.");

        isReload = !isReload;
    }

    @Override
    public void onDisable() {
        for (PlayerInfo playerInfo : PlayerInfo.getAllPlayerInfo()) {
            playerInfo.save();
        }

        for (Game game : gameManager.getGameLinkedList()) {
            if (game.getState() == State.STARTED && !isReload) {
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
                return true;
            } else return true;
        }
        return true;
    }

    private void loadGames() {
        //TODO
    }
}
