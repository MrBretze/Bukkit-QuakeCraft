package fr.bretzel.quake.arena;

import com.evilco.mc.nbt.TagCompound;
import com.evilco.mc.nbt.TagInteger;
import com.evilco.mc.nbt.TagString;
import com.evilco.mc.nbt.stream.NbtInputStream;
import com.evilco.mc.nbt.stream.NbtOutputStream;

import fr.bretzel.quake.Quake;
import fr.bretzel.quake.Util;
import fr.bretzel.quake.arena.api.IArena;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;

import java.io.*;
import java.util.LinkedList;

/**
 * Created by MrBretzel on 12/06/2015.
 */

public class Arena implements IArena {

    private LinkedList<Rule> rules = new LinkedList<>();
    private LinkedList<Location> respawn = new LinkedList<>();
    private Location firstLocation;
    private Location secondLocation;
    private LinkedList<Block> blocks = new LinkedList<>();
    private String name;
    private TagCompound compound;
    private File file;
    private byte[] byteName;

    public Arena(Location firstLocation, Location secondLocation, String name) {
        setFirstLocation(firstLocation);
        setSecondLocation(secondLocation);
        setName(name);
        this.byteName = getName().getBytes();

        for(Block block : Util.blocksFromTwoPoints(getFirstLocation(), getSecondLocation())) {
            addBlock(block);
        }

        File mk = new File(Quake.quake.getDataFolder(), File.separator + "arena" + File.separator);

        mk.mkdir();

        setFile(new File(mk, getName() + ".dat"));

        try {
            if (getFile().exists()) {
                NbtInputStream stream = new NbtInputStream(new FileInputStream(getFile()));

                setCompound(((TagCompound)stream.readTag()));

                stream.close();
            } else {

                setCompound(new TagCompound(getName()));

                getCompound().setTag(new TagString("name", getName()));

                NbtOutputStream stream = new NbtOutputStream(new FileOutputStream(getFile()));

                stream.write(getCompound());

                stream.close();
            }
        } catch (IOException e) {
            e.fillInStackTrace();
        }
    }

    public Arena(Quake quake) {
        quake.arenaManager.getArenaLinkedList().add(this);
    }

    public File getFile() {
        return file;
    }


    public void setFile(File file) {
        this.file = file;
    }

    @Override
    public void setFirstLocation(Location location) {
        this.firstLocation = location;
    }

    public void addRules(Rule rule) {
        this.rules.add(rule);
    }

    public LinkedList<Location> getRespawn() {
        return respawn;
    }

    public void setRespawn(LinkedList<Location> respawn) {
        this.respawn = respawn;
    }

    public void setRules(LinkedList<Rule> rules) {
        this.rules = rules;
    }

    public void addRespawn(Location location) {
        getRespawn().add(location);
    }

    @Override
    public void setSecondLocation(Location location) {
        this.secondLocation = location;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public byte[] getNameByByte() {
        return getName().getBytes();
    }

    @Override
    public Location getFirstLocation() {
        return this.firstLocation;
    }

    @Override
    public Location getSecondLocation() {
        return this.secondLocation;
    }

    @Override
    public LinkedList<Rule> getRules() {
        return rules;
    }

    public LinkedList<Block> getBlocks() {
        return blocks;
    }

    public void setBlocks(LinkedList<Block> blocks) {
        this.blocks = blocks;
    }

    public void addBlock(Block block) {
        getBlocks().add(block);
    }

    public TagCompound getCompound() {
        return compound;
    }

    public void setCompound(TagCompound compound) {
        this.compound = compound;
    }

    public void save() {

        if(!getName().equals(null) && !getName().isEmpty()) {
            getCompound().setTag(new TagString("name", getName()));
        }

        getCompound().setTag(new TagString("location1", toStringLocation(getFirstLocation())));
        getCompound().setTag(new TagString("location2", toStringLocation(getSecondLocation())));

        int l = 0;
        if(getRespawn().size() > 0) {

            TagCompound respawn = new TagCompound("respawn");

            for(Location location : getRespawn()) {
                respawn.setTag(new TagString(String.valueOf(l), toStringLocation(location)));
                l++;
            }

            respawn.setTag(new TagInteger("size", l));

            getCompound().setTag(respawn);
        }

        if(getRules().size() > 0) {
            TagCompound rules = new TagCompound("rules");

            rules.setTag(new TagString("TODO", "TODO"));

            getCompound().setTag(rules);
        }

        try {
            NbtOutputStream outputStream = new NbtOutputStream(new FileOutputStream(getFile()));

            outputStream.write(getCompound());

            outputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public Arena clone() {
        try {
            return (Arena)super.clone();
        } catch (CloneNotSupportedException var2) {
            throw new Error(var2);
        }
    }

    private String toStringLocation(Location location) {
        StringBuilder builder = new StringBuilder();
        builder.append(location.getWorld().getName() + ";")
                .append(location.getBlockX() + ";")
                .append(location.getBlockY() + ";")
                .append(location.getBlockZ() + ";");
        return builder.toString();
    }

    private Location toLocationString(String string) {
        String[] strings = string.split(";");
        return new Location(Bukkit.getWorld(strings[0]), Double.valueOf(strings[1]), Double.valueOf(strings[2]), Double.valueOf(strings[3]));
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        builder.append("{");

        builder.append(getFirstLocation().toString() + ", ");

        builder.append(getSecondLocation().toString() + ", ");

        builder.append("ArenaName: " + getName());

        builder.append("}");

        return builder.toString();
    }
}
