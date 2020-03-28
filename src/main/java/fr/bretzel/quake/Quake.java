/**
 * Copyright 2015 Lo�c Nussbaumer
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License"); you
 * may not use this file except in compliance with the License. You
 * may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License. See accompanying
 * LICENSE file.
 */
package fr.bretzel.quake;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import fr.bretzel.hologram.HoloEntity;
import fr.bretzel.hologram.Hologram;
import fr.bretzel.hologram.HologramManager;
import fr.bretzel.nbt.NBTCompressedStreamTools;
import fr.bretzel.quake.command.FixfallingCommand;
import fr.bretzel.quake.command.QuakeTabCompleter;
import fr.bretzel.quake.command.SetNoUpdate;
import fr.bretzel.quake.game.Game;
import fr.bretzel.quake.game.GameManager;
import fr.bretzel.quake.language.LanguageManager;
import fr.bretzel.quake.reader.GameReader;
import fr.bretzel.raytrace.RayTrace;
import fr.bretzel.raytrace.RayTraceResult;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.security.CodeSource;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by MrBretzel on 09/06/2015.
 */
public class Quake extends JavaPlugin
{

    public static PluginManager manager;
    public static GameManager gameManager;
    public static Quake quake;
    public static HologramManager holoManager;
    private static final HashMap<Player, PlayerInfo> playerInfos = new HashMap<>();
    private static LanguageManager languageManager;
    private static boolean debug = false;
    public HashMap<Player, BukkitTask> taskHashMap = new HashMap<>();

    public static PlayerInfo getPlayerInfo(Player player)
    {
        if (playerInfos.containsKey(player))
            return playerInfos.get(player);
        else
        {
            PlayerInfo info = new PlayerInfo(player);
            logDebug("Register new player by uuid: " + info.getPlayer().getUniqueId());
            playerInfos.put(player, info);
            return info;
        }
    }

    public static Collection<PlayerInfo> getPlayerInfos()
    {
        return playerInfos.values();
    }

    public static String getI18n(String key)
    {
        return languageManager.getI18n(key);
    }

    public static boolean isDebug()
    {
        return debug;
    }

    public static void logDebug(String msg)
    {
        if (isDebug())
            Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + "[DEBUG]" + ChatColor.RESET + ": " + ChatColor.WHITE + msg);
    }

    public static void logInfo(String msg)
    {
        Bukkit.getConsoleSender().sendMessage(ChatColor.WHITE + "[INFO]: " + msg);
    }

    @Override
    public void onEnable()
    {
        ProtocolLibrary.getProtocolManager()
                .addPacketListener(new ListenerClickDroit(this, ListenerPriority.NORMAL,
                        PacketType.Play.Client.BLOCK_PLACE, PacketType.Play.Client.USE_ITEM
                        , PacketType.Play.Client.LOOK, PacketType.Play.Client.POSITION_LOOK));

        quake = this;

        CodeSource src = getClass().getProtectionDomain().getCodeSource();
        if (src != null)
        {
            URL jar = src.getLocation();
            try
            {
                ZipInputStream zip = new ZipInputStream(jar.openStream());
                byte[] buffer = new byte[1024];
                while (true)
                {
                    ZipEntry e = zip.getNextEntry();
                    if (e == null)
                        break;
                    String name = e.getName();
                    if (name.startsWith("lang/") && name.endsWith(".json"))
                    {
                        new File(getDataFolder() + "/lang/").mkdir();
                        File f = new File(getDataFolder() + "/lang/", name.replace("lang/", ""));
                        if (!f.exists())
                            f.createNewFile();
                        FileOutputStream out = new FileOutputStream(f);

                        int len;
                        while ((len = zip.read(buffer)) > 0)
                        {
                            out.write(buffer, 0, len);
                        }
                        out.close();
                    }
                }
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        } else
        {
            logInfo("ERROR LANGUAGE NOT INITIALED !");
            setEnabled(false);
        }

        saveResource("config.yml", false);

        reloadConfig();

        debug = getConfig().getBoolean("debug");

        logDebug("Starting initialing LanguageManager");
        Locale locale = new Locale(Objects.requireNonNull(getConfig().getString("language.Language")), Objects.requireNonNull(getConfig().getString("language.Region")));
        languageManager = new LanguageManager(locale);
        logDebug("Starting initialing LanguageManager");

        logDebug("Starting initialing Holomanager");
        holoManager = new HologramManager(this);
        logDebug("End initialing Holomanager");

        getDataFolder().mkdir();

        manager = getServer().getPluginManager();

        logDebug("Starting initialing Gamemanager");
        gameManager = new GameManager();
        logDebug("End initialing Gamemanager");

        getCommand("quake").setExecutor(new fr.bretzel.quake.command.Command());
        getCommand("quake").setTabCompleter(new QuakeTabCompleter());

        File file = new File(getDataFolder(), File.separator + "game" + File.separator);

        if (file.exists() && file.isDirectory())
            if (Objects.requireNonNull(file.listFiles()).length > 0)
                Bukkit.getScheduler().runTaskLater(Quake.quake, () -> initGame(file), 10);

        getCommand("fixfalling").setExecutor(new FixfallingCommand());
        getCommand("fixfalling").setTabCompleter(new FixfallingCommand());

        getCommand("setnoupdate").setExecutor(new SetNoUpdate());
        getCommand("setnoupdate").setTabCompleter(new SetNoUpdate());
    }

    @Override
    public void onDisable()
    {
        for (PlayerInfo playerInfo : getPlayerInfos())
        {
            playerInfo.save();
        }

        for (Game game : gameManager.getGameLinkedList())
        {
            game.save();
        }

        for (Hologram hologram : holoManager.getHologramList())
            for (HoloEntity holoEntity : hologram.getHoloEntities())
                holoEntity.getStand().remove();

        holoManager.getHologramList().clear();

        getConfig().set("lobby", Util.toStringLocation(gameManager.getLobby()));
        saveConfig();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if (label.equals("test"))
        {
            if (sender instanceof Player)
            {
                Player player = (Player) sender;
                if (taskHashMap.containsKey(player))
                {
                    taskHashMap.get(player).cancel();
                    taskHashMap.remove(player);
                } else
                {
                    taskHashMap.put(player, Bukkit.getScheduler().runTaskTimer(Quake.quake, () ->
                    {
                        RayTraceResult result = RayTrace.rayTrace(player.getEyeLocation(), getPlayerInfo(player).getDirection(), 0, 200, 0.09,
                                location -> location.getBlock().getType().isAir());

                        player.sendTitle("", "Type = " + result.getHitLocation().getBlock().getType(), 0, 10, 0);

                        AtomicReference<Location> last = new AtomicReference<>();

                        for (Location location : result.getLocation().stream().filter(location ->
                        {
                            if (last.get() == null)
                                last.set(location);
                            if (last.get().distance(location) > 0.35)
                            {
                                last.set(location);
                                return true;
                            } else return false;
                        }).collect(Collectors.toList()))
                            if (location != null && location.getWorld() != null)
                                location.getWorld().spawnParticle(Particle.TOWN_AURA, location, 1, 0, 0, 0, 0, null);
                    }, 0, 0));
                }
                return true;
            } else return true;
        }
        return true;
    }

    private void initGame(File file)
    {
        for (File f : Objects.requireNonNull(file.listFiles()))
        {
            try
            {
                Game game = GameReader.read(NBTCompressedStreamTools.read(new FileInputStream(f)), f);
                logDebug("Initialing game: " + game.getDisplayName());
                gameManager.getGameLinkedList().add(game);
                gameManager.signEvent.updateSign(game);
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }
}
