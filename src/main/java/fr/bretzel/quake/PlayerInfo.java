package fr.bretzel.quake;

import com.evilco.mc.nbt.TagCompound;
import com.evilco.mc.nbt.TagDouble;
import com.evilco.mc.nbt.TagString;
import com.evilco.mc.nbt.error.TagNotFoundException;
import com.evilco.mc.nbt.error.UnexpectedTagTypeException;
import com.evilco.mc.nbt.stream.NbtInputStream;
import com.evilco.mc.nbt.stream.NbtOutputStream;

import fr.bretzel.quake.game.Game;

import fr.bretzel.quake.game.event.PlayerShootEvent;
import fr.bretzel.quake.game.task.ReloadTask;
import fr.bretzel.quake.inventory.BasicGun;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

import java.io.*;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by MrBretzel on 14/06/2015.
 */

public class PlayerInfo {

    private Player player;
    private ParticleEffect effect = ParticleEffect.FIREWORKS_SPARK;
    private double reload = 1.5;
    private Game game;
    private TagCompound compound;
    private Location firstLocation = null;
    private Location secondLocation = null;
    private boolean shoot = true;
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

    public boolean isInGame() {
        return Quake.gameManager.getGameByPlayer(getPlayer()) == null ? false : true;
    }

    public void give(BasicGun basicGun) {
        getPlayer().getInventory().clear();
        getPlayer().getInventory().setItem(0, basicGun.getStack());
    }

    public boolean isShoot() {
        return shoot;
    }

    public void setShoot(boolean shoot) {
        this.shoot = shoot;
    }

    public void shoot() {
        PlayerShootEvent shoot = new PlayerShootEvent(getPlayer(), Quake.gameManager.getGameByPlayer(getPlayer()));
        Bukkit.getPluginManager().callEvent(shoot);
        if(shoot.isCancelled()) {
            return;
        }

        if(!isShoot()) {
            setShoot(true);

            Bukkit.getServer().getScheduler().runTaskLater(Quake.quake, new ReloadTask(this.clone()), (long) (getReloadTime() * 20));

            LinkedList<Location> locs = Util.getLocationByDirection(getPlayer(), 200, 0.5);
            for(Location l : locs) {
                getEffect().display(0.0F, 0.0F, 0.0f, 0.0F, 1, l, 200);
            }

            List<Player> pList = Util.getPlayerListInDirection(locs, getPlayer(), 200);

            for(Player player : pList) {
                player.setMetadata("killer", new FixedMetadataValue(Quake.quake, player.getDisplayName()));
                player.setHealth(0D);
            }
        }
    }

    public PlayerInfo clone() {
        try {
            return (PlayerInfo) super.clone();
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
