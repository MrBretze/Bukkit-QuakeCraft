package fr.bretzel.quake.player;

import com.evilco.mc.nbt.TagCompound;
import com.evilco.mc.nbt.stream.NbtInputStream;
import com.evilco.mc.nbt.stream.NbtOutputStream;
import fr.bretzel.quake.ParticleEffect;
import fr.bretzel.quake.Quake;
import fr.bretzel.quake.arena.Arena;
import fr.bretzel.quake.arena.Rule;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by MrBretzel on 14/06/2015.
 */

public class PlayerInfo {

    private Player player;

    private ParticleEffect effect;

    private long reload;

    private Arena arena;

    private List<Rule> rule = new ArrayList<>();

    private TagCompound compound;

    private Location firstLocation = null;

    private Location secondLocation = null;

    private File file;

    public PlayerInfo(Player player) {
        setPlayer(player);

        File file = new File(Quake.quake.getDataFolder() + File.separator + "players" + File.separator, player.getUniqueId() + ".dat");

        if(!file.exists()) {
            try {
                if(!Quake.quake.getDataFolder().exists()) {
                    Quake.quake.getDataFolder().mkdir();
                }
                if(!new File(Quake.quake.getDataFolder() + File.separator + "players" + File.separator).exists()) {
                    new File(Quake.quake.getDataFolder() + File.separator + "players" + File.separator).mkdir();
                }
                file.createNewFile();

            } catch (IOException e) {}

            //compound = new TagCompound("player", );

            try {

                compound.write(new NbtOutputStream(new FileOutputStream(file)), false);

            } catch (IOException e) {}

        }

        this.file = file;

        try {
            NbtInputStream stream = new NbtInputStream(new FileInputStream(file));

            compound = (TagCompound) stream.readTag();

        } catch (Exception e) {}
    }

    public Arena getArena() {
        return arena;
    }

    public long getReloadTime() {
        return reload;
    }

    public ParticleEffect getEffect() {
        return effect;
    }

    public Player getPlayer() {
        return player;
    }

    public List<Rule> getRule() {
        return rule;
    }

    public void setArena(Arena arena) {
        this.arena = arena;
    }

    public void setEffect(ParticleEffect effect) {
        this.effect = effect;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public void setReloadTime(long reload) {
        this.reload = reload;
    }

    public void setRule(List<Rule> rule) {
        this.rule = rule;
    }

    public TagCompound getCompound() {
        return compound;
    }

    public void setCompound(TagCompound compound) {
        this.compound = compound;
    }

    public Location getFirstLocation() {
        return firstLocation;
    }

    public Location getSecondLocation() {
        return secondLocation;
    }

    public void setReload(long reload) {
        this.reload = reload;
    }

    public void setFirstLocation(Location firstLocation) {
        this.firstLocation = firstLocation;
    }

    public void setSecondLocation(Location secondLocation) {
        this.secondLocation = secondLocation;
    }

    public PlayerInfo clone() {
        try {
            return (PlayerInfo)super.clone();
        } catch (CloneNotSupportedException var2) {
            throw new Error(var2);
        }
    }

    public void save() {
        /*compound.setString("loc1", toStringLocation(getFirstLocation()));
        compound.setString("loc2", toStringLocation(getSecondLocation()));
        compound.setLong("reload", getReloadTime());

        compound.setString("effect", getEffect().name());
        compound.setString("arena", getArena().getName());*/

        try {
            NbtOutputStream stream = new NbtOutputStream(new FileOutputStream(this.file));
            stream.write(compound);
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private String toStringLocation(Location location) {
        StringBuilder builder = new StringBuilder();
        builder.append(location.getWorld() + ";")
                .append(location.getBlockX() + ";")
                .append(location.getBlockY() + ";")
                .append(location.getBlockZ() + ";");
        return builder.toString();
    }

    private Location toLocationString(String string) {
        String[] strings = string.split(";");
        return new Location(Bukkit.getWorld(strings[0]), Double.valueOf(strings[1]), Double.valueOf(strings[2]), Double.valueOf(strings[3]));
    }
}
