package fr.bretzel.quake.player;

import com.evilco.mc.nbt.TagCompound;
import com.evilco.mc.nbt.TagDouble;
import com.evilco.mc.nbt.TagString;
import com.evilco.mc.nbt.error.TagNotFoundException;
import com.evilco.mc.nbt.error.UnexpectedTagTypeException;
import com.evilco.mc.nbt.stream.NbtInputStream;
import com.evilco.mc.nbt.stream.NbtOutputStream;

import fr.bretzel.quake.ParticleEffect;
import fr.bretzel.quake.Quake;
import fr.bretzel.quake.Util;
import fr.bretzel.quake.arena.Game;
import fr.bretzel.quake.arena.Rule;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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

    private ParticleEffect effect = ParticleEffect.FIREWORKS_SPARK;

    private double reload = 1.5;

    private Game game;

    private List<Rule> rule = new ArrayList<>();

    private TagCompound compound;

    private Location firstLocation = null;

    private Location secondLocation = null;

    private File file;

    public PlayerInfo(Player player) {
        setPlayer(player);

        File mk = new File(Quake.quake.getDataFolder() + File.separator + "players" + File.separator);

        mk.mkdir();

        setFile(new File(mk, player.getUniqueId().toString() + ".dat"));

        try {

        if(getFile().exists()) {

            NbtInputStream stream = new NbtInputStream(new FileInputStream(getFile()));

            compound = (TagCompound) stream.readTag();

        } else {

            getFile().createNewFile();

            compound = new TagCompound(getPlayer().getUniqueId().toString());

            NbtOutputStream stream = new NbtOutputStream(new FileOutputStream(getFile()));

            compound.setTag(new TagString("effect", getEffect().name()));

            stream.write(compound);

            stream.close();
        }

        } catch (Exception e) {
            e.fillInStackTrace();
        }
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public Game getGame() {
        return game;
    }

    public double getReloadTime() {
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

    public void setGame(Game game) {
        this.game = game;
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

    private void init() {
        try {
            String loc = compound.getString("location1");

            if(!loc.equals(null) && !loc.isEmpty()) {
                setFirstLocation(Util.toLocationString(loc));
                loc = compound.getString("location2");
            }

            if(!loc.equals(null) && !loc.isEmpty()) {
                setFirstLocation(Util.toLocationString(loc));
            }

        } catch (UnexpectedTagTypeException e) {
            e.printStackTrace();
        } catch (TagNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void save() {
        if(compound != null) {
            compound.setTag(new TagDouble("reload", getReloadTime()));

            compound.setTag(new TagString("effect", getEffect().name()));

            try {
                NbtOutputStream stream = new NbtOutputStream(new FileOutputStream(this.file));
                stream.write(compound);
                stream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Bukkit.broadcastMessage(ChatColor.RED + "Error the compound was null !");
        }

    }
}
