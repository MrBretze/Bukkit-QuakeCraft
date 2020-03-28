package fr.bretzel.hologram;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.Plugin;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class HologramManager implements Listener
{

    private final List<Hologram> holoList = new ArrayList<>();
    private final Plugin plugin;

    public HologramManager(Plugin plugin)
    {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);

        init();
    }

    public Hologram getHologram(World world, double x, double y, double z)
    {
        return this.getHologram(new Location(world, x, y, z));
    }

    public Hologram getHologram(World world, int x, int y, int z)
    {
        return this.getHologram(new Location(world, x, y, z));
    }

    public Hologram getHologram(Location location)
    {
        return this.getHologram(location, 0.5);
    }

    public Hologram getHologram(World world, int x, int y, int z, double range)
    {
        return getHologram(new Location(world, x, y, z), range);
    }

    public Hologram getHologram(World world, double x, double y, double z, double range)
    {
        return getHologram(new Location(world, x, y, z), range);
    }

    public Hologram getHologram(Location location, double range)
    {
        for (Hologram holo : getHologramList())
            if (holo.getLocation().getWorld() == location.getWorld() && holo.getLocation().distance(location) <= range)
                return holo;
        return null;
    }

    public void removeHologram(World world, int x, int y, int z, double range)
    {
        this.removeHologram(new Location(world, x, y, z), range);
    }

    public void removeHologram(World world, double x, double y, double z)
    {
        this.removeHologram(new Location(world, x, y, z), 0.5);
    }

    public void removeHologram(World world, int x, int y, int z)
    {
        this.removeHologram(new Location(world, x, y, z), 0.5);
    }

    public void removeHologram(World world, double x, double y, double z, double range)
    {
        this.removeHologram(new Location(world, x, y, z), range);
    }

    public void removeHologram(Location location)
    {
        this.removeHologram(location, 0.5);
    }

    public void removeHologram(Location location, double range)
    {
        if (!exist(location, range))
            return;

        Hologram hologram = getHologram(location, range);

        if (hologram != null)
            for (HoloEntity e : hologram.getHoloEntities())
                if (e.getStand() != null)
                    e.getStand().remove();
    }

    public Hologram addHologram(Location location, String... lines)
    {
        return new Hologram(location, lines, this);
    }

    public boolean exist(World world, double x, double y, double z, double range)
    {
        return exist(new Location(world, x, y, z), range);
    }

    public boolean exist(World world, double x, double y, double z)
    {
        return exist(new Location(world, x, y, z), 0.5);
    }

    public boolean exist(World world, int x, int y, int z, double range)
    {
        return exist(new Location(world, x, y, z), range);
    }

    public boolean exist(World world, int x, int y, int z)
    {
        return exist(new Location(world, x, y, z), 0.5);
    }

    public boolean exist(Location location)
    {
        return getHologram(location, 0.5) != null;
    }

    public boolean exist(Location location, double range)
    {
        return getHologram(location, range) != null;
    }

    public List<Hologram> getHologramList()
    {
        return holoList;
    }

    public Plugin getPlugin()
    {
        return plugin;
    }

    @EventHandler
    private void onPluginDisable(PluginDisableEvent event) throws IOException
    {
        if (event.getPlugin().getName().equals(getPlugin().getName()))
        {
            JsonObject hologramObject = new JsonObject();

            for (Hologram hologram : getHologramList())
            {
                JsonObject jsonHolo = new JsonObject();

                JsonArray array = new JsonArray();

                for (String line : hologram.getLines())
                {
                    array.add(line);
                }

                jsonHolo.add("lines", array);
                jsonHolo.addProperty("visible", hologram.isVisible());

                hologramObject.add(toStringLocation(hologram.getLocation()), jsonHolo);

                hologram.setVisible(false);
                hologram.remove();
            }

            File saved = new File(getPlugin().getDataFolder(), "hologram.json");

            if (!getPlugin().getDataFolder().exists())
            {
                getPlugin().getDataFolder().mkdir();
            }

            if (!saved.exists())
            {
                saved.createNewFile();
            }

            FileOutputStream obj = new FileOutputStream(saved);
            obj.write(new GsonBuilder().setPrettyPrinting().create().toJson(hologramObject).getBytes(StandardCharsets.UTF_8));
            obj.close();
        }
    }

    private void init()
    {
        File saved = new File(getPlugin().getDataFolder(), "hologram.json");

        if (saved.exists())
        {
            try
            {
                FileInputStream inputStream = new FileInputStream(saved);
                JsonObject mainObject = new JsonParser().parse(new JsonReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))).getAsJsonObject();

                for (Map.Entry<String, JsonElement> entry : mainObject.entrySet())
                {
                    Location hologramLoc = toLocationString(entry.getKey());
                    JsonObject holo = entry.getValue().getAsJsonObject();
                    ArrayList<String> lines = new ArrayList<>();

                    if (holo.has("lines"))
                    {
                        JsonArray array = holo.getAsJsonArray("lines");

                        for (JsonElement s : array)
                        {
                            lines.add(s.getAsString());
                        }
                    }

                    Collection<Entity> currentsEntity = hologramLoc.getWorld().getNearbyEntities(hologramLoc, lines.size() + 5, lines.size() + 5, lines.size() + 5);

                    if (currentsEntity.size() > 0)
                    {
                        for (Entity entity : currentsEntity)
                        {
                            if (entity instanceof ArmorStand)
                            {
                                for (String line : lines)
                                    if (entity.getCustomName() != null && !entity.getCustomName().isEmpty() && entity.getCustomName().equals(line))
                                        entity.remove();
                            }
                        }
                    }

                    Hologram hologram = addHologram(hologramLoc, lines.toArray(new String[lines.size()]));

                    if (holo.has("visible"))
                    {
                        hologram.setVisible(Boolean.parseBoolean(holo.get("visible").getAsString()));
                    }
                }

            } catch (FileNotFoundException e)
            {
                e.printStackTrace();
            }
        }
    }

    public String toStringLocation(Location location)
    {
        return location.getWorld().getName() + ":" + location.getX() + ":" + location.getY() + ":" + location.getZ();
    }

    public Location toLocationString(String location)
    {
        if (location != null && !location.isEmpty())
        {
            String[] strings = location.split(":");
            return new Location(Bukkit.getWorld(strings[0]) == null ? Bukkit.getWorlds().get(0) : Bukkit.getWorld(strings[0]), Double.valueOf(strings[1]), Double.valueOf(strings[2]), Double.valueOf(strings[3]));
        } else
        {
            return null;
        }
    }
}
