package fr.bretzel.quake.game;

import com.evilco.mc.nbt.TagCompound;
import com.evilco.mc.nbt.TagInteger;
import com.evilco.mc.nbt.TagString;
import com.evilco.mc.nbt.stream.NbtInputStream;
import com.evilco.mc.nbt.stream.NbtOutputStream;

import fr.bretzel.quake.Quake;
import fr.bretzel.quake.Util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Beacon;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

import java.io.*;
import java.util.*;

/**
 * Created by MrBretzel on 12/06/2015.
 */

public class Game {

    private LinkedList<Location> respawn = new LinkedList<>();
    private Location firstLocation;
    private Location secondLocation;
    private Location spawn;
    private LinkedList<Block> blocks = new LinkedList<>();
    private String name;
    private TagCompound compound;
    private File file;
    private List<UUID> playerList = new ArrayList<>();
    private LinkedList<Sign> signList = new LinkedList<>();
    private Random random = new Random();
    private boolean respawnview = false;
    private int maxPlayer = 16;
    private int minPlayer = 2;
    private int maxKill = 25;
    private int secLaunch = 15;
    private State state = State.WAITING;

    public Game(Location firstLocation, Location secondLocation, String name) {
        setFirstLocation(firstLocation);
        setSecondLocation(secondLocation);
        setName(name);

        for(Block block : Util.blocksFromTwoPoints(getFirstLocation(), getSecondLocation())) {
            addBlock(block);
        }

        File mk = new File(Quake.quake.getDataFolder(), File.separator + "game" + File.separator);
        mk.mkdir();
        setFile(new File(mk, getName() + ".dat"));
        try {
            if (getFile().exists()) {
                NbtInputStream stream = new NbtInputStream(new FileInputStream(getFile()));
                setCompound(((TagCompound)stream.readTag()));
                stream.close();
            } else {
                getFile().createNewFile();
                calculeSpawnBase();
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

    public int getSecLaunch() {
        return secLaunch;
    }

    public void setSecLaunch(int secLaunch) {
        this.secLaunch = secLaunch;
    }

    public Game(Quake quake) {
        quake.gameManager.getGameLinkedList().add(this);
    }

    public int getMaxKill() {
        return maxKill;
    }

    public void setMaxKill(int maxKill) {
        this.maxKill = maxKill;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public int getMaxPlayer() {
        return maxPlayer;
    }

    public void setMaxPlayer(int maxPlayer) {
        this.maxPlayer = maxPlayer;
    }

    public int getMinPlayer() {
        return minPlayer;
    }

    public void setMinPlayer(int minPlayer) {
        this.minPlayer = minPlayer;
    }

    public void setFirstLocation(Location location) {
        this.firstLocation = location;
    }

    public List<UUID> getPlayerList() {
        return playerList;
    }

    public void setPlayerList(List<UUID> playerList) {
        this.playerList = playerList;
    }

    public void addPlayer(Player player) {
        if(!getPlayerList().contains(player.getUniqueId())) {
            getPlayerList().add(player.getUniqueId());
        }
    }

    public void addPlayer(UUID uuid) {
        if(!getPlayerList().contains(uuid)) {
            getPlayerList().add(uuid);
        }
    }

    public LinkedList<Location> getRespawn() {
        return respawn;
    }

    public void setRespawn(LinkedList<Location> respawn) {
        this.respawn = respawn;
    }


    public void addRespawn(Location location) {
        getRespawn().add(location);
    }

    public void setSecondLocation(Location location) {
        this.secondLocation = location;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public Location getFirstLocation() {
        return this.firstLocation;
    }

    public Location getSecondLocation() {
        return this.secondLocation;
    }

    public LinkedList<Block> getBlocks() {
        return blocks;
    }

    public void setBlocks(LinkedList<Block> blocks) {
        this.blocks = blocks;
    }

    public Block getBlockByLocation(Location location) {
        Block b = null;
        for(Block block : getBlocks()) {
            if(block.getWorld() == location.getWorld() && block.getX() == location.getBlockX() && block.getY() == location.getBlockY() && block.getZ() == location.getBlockZ()) {
                b = block;
                break;
            }
        }
        return b;
    }

    public void addBlock(Block block) {
        getBlocks().add(block);
    }

    public TagCompound getCompound() {
        return compound;
    }

    public LinkedList<Sign> getSignList() {
        return signList;
    }

    public void setSignList(LinkedList<Sign> signList) {
        this.signList = signList;
    }

    public void addSign(Sign sign) {
        this.signList.add(sign);
    }

    public void removeSign(Sign sign) {
        this.signList.remove(sign);
    }

    public void broadcastMessage(String msg) {
        for (UUID id : getPlayerList()) {
            Player p = Bukkit.getPlayer(id);
            if(p.isOnline()) {
                p.sendMessage(msg);
            }
        }
    }

    public void view(boolean view) {
        if(view == true) {
            this.respawnview = true;
            for(Location location : getRespawn()) {
                location.getWorld().getBlockAt(location).setType(Material.BEACON);
                for (int xPoint = location.getBlockX() - 1; xPoint <= location.getBlockX() + 1 ; xPoint++) {
                    for (int zPoint = location.getBlockZ() - 1 ; zPoint <= location.getBlockZ() + 1; zPoint++) {
                        Location l = location.getWorld().getBlockAt(xPoint, location.getBlockY() - 1, zPoint).getLocation().clone();
                        for(UUID id : getPlayerList()) {
                            Player player = Bukkit.getPlayer(id);
                            if(player.isOnline()) {
                                player.sendBlockChange(l, Material.IRON_BLOCK, (byte) 0);
                            }
                        }
                    }
                }
            }
        } else {
            this.respawnview = false;
            for(Location location : getRespawn()) {
                for (int xPoint = location.getBlockX() - 1; xPoint <= location.getBlockX() + 1 ; xPoint++) {
                    for (int zPoint = location.getBlockZ() - 1 ; zPoint <= location.getBlockZ() + 1; zPoint++) {
                        Location l = location.getWorld().getBlockAt(xPoint, location.getBlockY() - 1, zPoint).getLocation().clone();
                        for(UUID id : getPlayerList()) {
                            Player player = Bukkit.getPlayer(id);
                            if(player.isOnline()) {
                                player.sendBlockChange(l, getBlockByLocation(l).getType(), getBlockByLocation(l).getData());
                            }
                        }
                    }
                }
            }
        }
    }

    public void view() {
        if(this.respawnview) {
            view(false);
        } else {
            view(true);
        }
    }

    public boolean isView() {
        return respawnview;
    }

    public void setCompound(TagCompound compound) {
        this.compound = compound;
    }

    public Location getSpawn() {
        return spawn;
    }

    public void setSpawn(Location spawn) {
        this.spawn = spawn;
    }

    private void calculeSpawnBase() {
        int x1 = getFirstLocation().getBlockX();
        int y1 = getFirstLocation().getBlockY();
        int z1 = getFirstLocation().getBlockZ();

        int x2 = getSecondLocation().getBlockX();
        int y2 = getSecondLocation().getBlockY();
        int z2 = getSecondLocation().getBlockZ();

        int x = (x1 + x2) / 2;
        int y = (y1 + y2) / 2;
        int z = (z1 + z2) / 2;

        setSpawn(new Location(getFirstLocation().getWorld(), Double.valueOf(String.valueOf(x)), Double.valueOf(String.valueOf(y)), Double.valueOf(String.valueOf(z))));
    }

    public void reset() {
        for(UUID uuid : getPlayerList()) {
            Player p = Bukkit.getPlayer(uuid);
            if(p.isOnline()) {
                p.sendMessage(ChatColor.RED + "This game has bin annulled !");
                p.teleport(Quake.gameManager.getLobby());
            }
        }
        getPlayerList().clear();
        setState(State.WAITING);
        Quake.gameManager.signEvent.actualiseJoinSignForGame(this.clone());
    }

    public void respawn(Player p) {
        Location location = getRespawn().get(random.nextInt(getRespawn().size()));
        if(p.getWorld().getNearbyEntities(location, 10, 5, 10).size() <= 0) {
            p.teleport(location);
            p.setMetadata("respawn", new FixedMetadataValue(Quake.quake, 5));
        } else if(p.hasMetadata("respawn") && p.getMetadata("respawn").get(0).asInt() > 0) {
            p.setMetadata("respawn", new FixedMetadataValue(Quake.quake, p.getMetadata("respawn").get(0).asInt() - 1));
            respawn(p);
        } else {
            p.teleport(getRespawn().get(random.nextInt(getRespawn().size())));
        }
    }

    public void save() {

        if(!getName().equals(null) && !getName().isEmpty()) {
            getCompound().setTag(new TagString("name", getName()));
        }

        getCompound().setTag(new TagString("location1", Util.toStringLocation(getFirstLocation())));
        getCompound().setTag(new TagString("location2", Util.toStringLocation(getSecondLocation())));

        int l = 0;
        if(getRespawn().size() > 0) {

            TagCompound respawn = new TagCompound("respawn");

            for(Location location : getRespawn()) {
                respawn.setTag(new TagString(String.valueOf(l), Util.toStringLocation(location)));
                l++;
            }

            respawn.setTag(new TagInteger("size", l));

            getCompound().setTag(respawn);
        }

        int k = 0;
        if(getSignList().size() > 0) {

            TagCompound signs = new TagCompound("signs");

            for(Sign sign : getSignList()) {
                signs.setTag(SignReader.write(sign, String.valueOf(k)));
                k++;
            }

            signs.setTag(new TagInteger("size", k));

            getCompound().setTag(signs);
        }

        if(!getPlayerList().isEmpty()) {
            TagCompound players = new TagCompound("players");

            int h = 0;
            for(UUID id : getPlayerList()) {
                players.setTag(new TagString(String.valueOf(h), id.toString()));
                h++;
            }

            players.setTag(new TagInteger("size", h));

            getCompound().setTag(players);
        } else {
            TagCompound players = new TagCompound("players");

            players.setTag(new TagInteger("size", 0));

            getCompound().setTag(players);
        }

        getCompound().setTag(new TagString("spawn", Util.toStringLocation(getSpawn())));

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

    public Game clone() {
        try {
            return (Game)super.clone();
        } catch (CloneNotSupportedException var2) {
            throw new Error(var2);
        }
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
