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
package fr.bretzel.quake;

import fr.bretzel.hologram.HologramManager;
import fr.bretzel.nbt.NBTCompressedStreamTools;
import fr.bretzel.quake.game.Game;
import fr.bretzel.quake.game.GameManager;
import fr.bretzel.quake.language.LanguageManager;
import fr.bretzel.quake.reader.GameReader;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Locale;

/**
 * Created by MrBretzel on 09/06/2015.
 */
public class Quake extends JavaPlugin {

    public static PluginManager manager;
    public static GameManager gameManager;
    public static Quake quake;
    public static HologramManager holoManager;
    private static LinkedList<PlayerInfo> playerInfos = new LinkedList<>();
    private static LanguageManager languageManager;
    private static boolean debug = false;

    public static PlayerInfo getPlayerInfo(Player player) {
        for (PlayerInfo pi : playerInfos) {
            if (pi.getPlayer() == player) {
                return pi;
            }
        }
        PlayerInfo info = new PlayerInfo(player);
        logDebug("Register new player by uuid: " + info.getPlayer().getUniqueId());
        playerInfos.add(info);
        return info;
    }

    public static LinkedList<PlayerInfo> getPlayerInfos() {
        return playerInfos;
    }

    public static String getI18n(String key) {
        return languageManager.getI18n(key);
    }

    public static boolean isDebug() {
        return debug;
    }

    public static void logDebug(String msg) {
        if (isDebug())
            Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + "[DEBUG]" + ChatColor.RESET + ": " + ChatColor.WHITE + msg);
    }

    public static void logInfo(String msg) {
        Bukkit.getConsoleSender().sendMessage(ChatColor.WHITE + "[INFO]: " + msg);
    }

    @Override
    public void onEnable() {
        quake = this;
        logDebug("Starting initialing Holomanager");
        holoManager = new HologramManager(this);
        logDebug("End initialing Holomanager");

        getDataFolder().mkdir();

        manager = getServer().getPluginManager();

        logDebug("Starting initialing Gamemanager");
        gameManager = new GameManager();
        logDebug("End initialing Gamemanager");

        getCommand("quake").setExecutor(new fr.bretzel.quake.command.Command());

        saveResource("config.yml", false);

        reloadConfig();

        debug = getConfig().getBoolean("debug");

        logDebug("Starting initialing LanguageManager");
        Locale locale = new Locale(getConfig().getString("language.Language"), getConfig().getString("language.Region"));
        languageManager = new LanguageManager(locale);
        logDebug("Starting initialing LanguageManager");

        File file = new File(getDataFolder(), File.separator + "game" + File.separator);

        if (file.exists() && file.isDirectory()) {
            if (file.listFiles().length > 0) {
                initGame(file);
            }
        }
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
            if(sender instanceof Player) {
                Player player = (Player) sender;
                Util.spawnFirework(Util.getCircle(player.getLocation(), 0.4, 6));
                return true;
            } else return true;
        }
        return true;
    }

    private void initGame(File file) {
        for (File f : file.listFiles()) {
            try {
                Game game = GameReader.read(NBTCompressedStreamTools.read(new FileInputStream(f)), f);
                logDebug("Initialing game: " + game.getDisplayName());
                gameManager.getGameLinkedList().add(game);
                gameManager.signEvent.actualiseJoinSignForGame(game);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
